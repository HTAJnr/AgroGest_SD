package mz.agrogest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import mz.agrogest.model.Utilizador;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.NegocioException;

/** O DataStore é um singleton: cada teste usa emails únicos para não depender do estado dos outros. */
class UtilizadorServiceTest {

    private final UtilizadorService svc = UtilizadorService.getInstance();

    private static String emailUnico() {
        return "t" + UUID.randomUUID() + "@teste.mz";
    }

    @Test
    void emailDuplicadoComMaiusculasEEspacosERecusado() {
        String email = emailUnico();
        svc.registarCliente("João Açúcar", email, "1234", "840000000", "Nampula");

        NegocioException ex = assertThrows(NegocioException.class, () ->
                svc.registarCliente("Outro", "  " + email.toUpperCase() + " ", "1234", "841111111", "Maputo"));
        assertTrue(ex.getMessage().contains("Já existe"));
    }

    @Test
    void passwordNuncaFicaEmClaro() {
        String email = emailUnico();
        Utilizador u = svc.registarCliente("Ana", email, "segredo123", "840000000", "Nampula");

        assertNotEquals("segredo123", u.getPassHash());
        assertFalse(u.getPassHash().contains("segredo123"));

        assertNotNull(svc.autenticar(email, "segredo123"));
        assertNotNull(svc.autenticar(email.toUpperCase(), "segredo123"));
        assertNull(svc.autenticar(email, "errada"));
        assertNull(svc.autenticar("nao-existe@teste.mz", "segredo123"));
        assertNull(svc.autenticar(null, null));
    }

    @Test
    void dadosInvalidosSaoRecusados() {
        assertThrows(NegocioException.class, () -> svc.registarCliente(" ", emailUnico(), "1234", "84", "x"));
        assertThrows(NegocioException.class, () -> svc.registarCliente("A", "sem-arroba", "1234", "84", "x"));
        assertThrows(NegocioException.class, () -> svc.registarCliente("A", emailUnico(), "123", "84", "x"));
        assertThrows(NegocioException.class, () -> svc.registarCliente("A", emailUnico(), "1234", "", "x"));
        assertThrows(NegocioException.class, () -> svc.registarCliente("A", emailUnico(), "1234", "84", " "));
    }

    @Test
    void garantirAdminEIdempotente() {
        svc.garantirAdmin();
        svc.garantirAdmin();

        long admins;
        synchronized (DataStore.getInstance().getLock()) {
            admins = DataStore.getInstance().getUtilizadores().stream()
                    .filter(u -> UtilizadorService.ADMIN_EMAIL.equals(u.getEmail()))
                    .count();
        }
        assertEquals(1, admins);

        Utilizador admin = svc.autenticar("admin@agro.mz", "admin234");
        assertNotNull(admin);
        assertTrue(admin.isAdmin());
        assertFalse(svc.listarClientes().contains(admin));
    }

    @Test
    void registosSimultaneosComOMesmoEmail_soUmGanha() throws Exception {
        final String email = emailUnico();
        final int threads = 8;
        CyclicBarrier arranque = new CyclicBarrier(threads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        List<Future<Boolean>> resultados = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            resultados.add(pool.submit(() -> {
                arranque.await();
                try {
                    svc.registarCliente("Concorrente", email, "1234", "840000000", "Nampula");
                    return true;
                } catch (NegocioException e) {
                    return false;
                }
            }));
        }
        int sucessos = 0;
        for (Future<Boolean> f : resultados) {
            if (f.get()) {
                sucessos++;
            }
        }
        pool.shutdown();

        assertEquals(1, sucessos);
    }
}
