package mz.agrogest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Cultivo;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.CampoException;
import mz.agrogest.util.NegocioException;

class CampanhaServiceTest {

    private final CampanhaService servico = CampanhaService.getInstance();
    private final DataStore ds = DataStore.getInstance();

    @BeforeEach
    void limpar() {
        synchronized (ds.getLock()) {
            ds.getCampanhas().clear();
            ds.getCultivos().clear();
        }
    }

    @Test
    void criarCampanhaValida() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 6, 30);
        Campanha c = servico.criar("Campanha de Milho João Açúcar", inicio, fim);

        assertNotNull(c);
        assertTrue(c.getId() > 0);
        assertEquals("Campanha de Milho João Açúcar", c.getNome());
        assertEquals(inicio, c.getInicio());
        assertEquals(fim, c.getFim());
        assertTrue(c.isAberta());
        assertEquals("01/01/2026", c.getInicioFmt());
        assertEquals("30/06/2026", c.getFimFmt());
    }

    @Test
    void criarCampanhaFimAntesDeInicioFalha() {
        LocalDate inicio = LocalDate.of(2026, 6, 30);
        LocalDate fim = LocalDate.of(2026, 1, 1);

        CampoException ex = assertThrows(CampoException.class, () ->
            servico.criar("Campanha Inválida", inicio, fim)
        );
        assertEquals("fim", ex.getCampo());
        assertTrue(ex.getMessage().contains("não pode ser anterior"));
    }

    @Test
    void criarCampanhaFimIgualAInicioAceite() {
        LocalDate data = LocalDate.of(2026, 5, 10);
        Campanha c = servico.criar("Campanha de Um Dia", data, data);
        assertNotNull(c);
        assertTrue(c.isAberta());
    }

    @Test
    void criarCampanhaDatasNulasFalha() {
        LocalDate data = LocalDate.of(2026, 5, 10);

        CampoException ex1 = assertThrows(CampoException.class, () ->
            servico.criar("Campanha Sem Início", null, data)
        );
        assertEquals("inicio", ex1.getCampo());

        CampoException ex2 = assertThrows(CampoException.class, () ->
            servico.criar("Campanha Sem Fim", data, null)
        );
        assertEquals("fim", ex2.getCampo());
    }

    @Test
    void criarCampanhaNomeInvalidoFalha() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 6, 30);

        // Vazio
        CampoException exVazio = assertThrows(CampoException.class, () ->
            servico.criar("   ", inicio, fim)
        );
        assertEquals("nome", exVazio.getCampo());

        // Curto (< 3)
        CampoException exCurto = assertThrows(CampoException.class, () ->
            servico.criar("AB", inicio, fim)
        );
        assertEquals("nome", exCurto.getCampo());

        // Só dígitos (sem letras)
        CampoException exDigitos = assertThrows(CampoException.class, () ->
            servico.criar("12345", inicio, fim)
        );
        assertEquals("nome", exDigitos.getCampo());
    }

    @Test
    void criarCampanhaNomeDuplicadoFalhaD14() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 6, 30);

        servico.criar("Campanha Agrícola Sul", inicio, fim);

        CampoException ex = assertThrows(CampoException.class, () ->
            servico.criar("campanha agrícola sul", inicio, fim)
        );
        assertEquals("nome", ex.getCampo());
        assertTrue(ex.getMessage().contains("Já existe uma campanha com o nome"));
    }

    @Test
    void fecharEReabrirCampanha() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 6, 30);
        Campanha c = servico.criar("Campanha Ciclo", inicio, fim);

        assertTrue(c.isAberta());

        servico.fechar(c.getId());
        assertFalse(c.isAberta());

        servico.reabrir(c.getId());
        assertTrue(c.isAberta());
    }

    @Test
    void fecharOuReabrirInexistenteFalha() {
        assertThrows(NegocioException.class, () -> servico.fechar(9999));
        assertThrows(NegocioException.class, () -> servico.reabrir(9999));
    }

    @Test
    void removerCampanhaSemCultivos() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 6, 30);
        Campanha c = servico.criar("Campanha Para Remover", inicio, fim);

        assertEquals(1, servico.listar().size());
        servico.remover(c.getId());
        assertEquals(0, servico.listar().size());
        assertNull(servico.porId(c.getId()));
    }

    @Test
    void removerCampanhaComCultivosBloqueado() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 6, 30);
        Campanha c = servico.criar("Campanha Com Cultivos", inicio, fim);

        // Associar cultivo à campanha
        Cultivo cultivo = new Cultivo();
        cultivo.setId(ds.novoIdCultivo());
        cultivo.setCampanhaId(c.getId());
        cultivo.setCultura("Milho");
        synchronized (ds.getLock()) {
            ds.getCultivos().add(cultivo);
        }

        NegocioException ex = assertThrows(NegocioException.class, () ->
            servico.remover(c.getId())
        );
        assertTrue(ex.getMessage().contains("tem cultivos associados"));
        assertNotNull(servico.porId(c.getId()));
    }

    @Test
    void listarDevolveCopiaNaoListaViva() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 6, 30);
        servico.criar("Campanha 1", inicio, fim);

        List<Campanha> lista = servico.listar();
        assertEquals(1, lista.size());

        // Modificar a cópia não altera o DataStore
        lista.clear();
        assertEquals(1, servico.listar().size());
    }
}
