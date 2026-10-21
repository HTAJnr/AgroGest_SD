package mz.agrogest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import mz.agrogest.model.Perfil;
import mz.agrogest.model.Produto;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.NegocioException;

/**
 * O DataStore é um singleton partilhado: cada teste cria os seus próprios produtos
 * (ids novos) e só olha para eles, por isso os testes não dependem uns dos outros.
 */
class VendaServiceTest {

    private final VendaService svc = VendaService.getInstance();
    private final DataStore store = DataStore.getInstance();

    private Produto novoProduto(String nome, String preco, double stock, String unidade) {
        Produto p = new Produto();
        p.setId(store.novoIdProduto());
        p.setNome(nome);
        p.setPreco(new BigDecimal(preco));
        p.setQuantidade(stock);
        p.setUnidade(unidade);
        synchronized (store.getLock()) {
            store.getProdutos().add(p);
        }
        return p;
    }

    private Utilizador novoCliente() {
        Utilizador u = new Utilizador();
        u.setId(store.novoIdUtilizador());
        u.setNome("João Açúcar");
        u.setEmail("joao" + u.getId() + "@teste.mz");
        u.setTelefone("840000000");
        u.setEndereco("Av. Eduardo Mondlane, Nampula");
        u.setPerfil(Perfil.CLIENTE);
        return u;
    }

    private static Map<Integer, Double> pedido(Object... paresIdQtd) {
        Map<Integer, Double> m = new LinkedHashMap<>();
        for (int i = 0; i < paresIdQtd.length; i += 2) {
            m.put((Integer) paresIdQtd[i], ((Number) paresIdQtd[i + 1]).doubleValue());
        }
        return m;
    }

    private int totalVendas() {
        synchronized (store.getLock()) {
            return store.getVendas().size();
        }
    }

    // ---- 1. Concorrência: a última unidade (RN01) ----

    @Test
    void duasThreadsNaUltimaUnidade_umaGanhaAOutraRecebeErro() throws Exception {
        for (int ronda = 0; ronda < 300; ronda++) {   // várias rondas: uma só pode passar por sorte
            Produto p = novoProduto("Ultima " + ronda, "100.00", 1, "un");
            Utilizador c1 = novoCliente();
            Utilizador c2 = novoCliente();
            CyclicBarrier arranque = new CyclicBarrier(2);
            ExecutorService pool = Executors.newFixedThreadPool(2);

            List<Future<Boolean>> res = new ArrayList<>();
            for (Utilizador c : new Utilizador[] {c1, c2}) {
                res.add(pool.submit(() -> {
                    arranque.await();
                    try {
                        svc.finalizarCompra(c, pedido(p.getId(), 1));
                        return true;
                    } catch (NegocioException e) {
                        return false;
                    }
                }));
            }
            int sucessos = 0;
            for (Future<Boolean> f : res) {
                if (f.get()) {
                    sucessos++;
                }
            }
            pool.shutdown();

            assertEquals(1, sucessos, "ronda " + ronda + ": só uma compra pode ganhar");
            assertEquals(0.0, p.getQuantidade(), 0.0, "ronda " + ronda + ": stock tem de ficar a 0, nunca negativo");
        }
    }

    // ---- 2. Atomicidade: 2.º item sem stock (RN02) ----

    @Test
    void segundoItemSemStock_nadaEDescontado_eNaoGastaId() {
        Produto milho = novoProduto("Milho", "45.00", 10, "kg");
        Produto adubo = novoProduto("Adubo", "850.00", 0, "saco");
        Utilizador cliente = novoCliente();
        int vendasAntes = totalVendas();

        NegocioException ex = assertThrows(NegocioException.class, () ->
                svc.finalizarCompra(cliente, pedido(milho.getId(), 3, adubo.getId(), 1)));

        assertTrue(ex.getMessage().contains("Adubo"), "a mensagem diz qual produto");
        assertTrue(ex.getMessage().contains("pediu 1") && ex.getMessage().contains("disponível 0"),
                "a mensagem diz quanto pediu e quanto há: " + ex.getMessage());
        assertEquals(10.0, milho.getQuantidade(), 0.0, "o stock do 1.º item NÃO pode ter mudado");
        assertEquals(vendasAntes, totalVendas(), "nenhuma venda criada");

        // RN06: a compra recusada não gastou id; a seguinte vem logo a seguir à última venda
        Venda ultima = svc.finalizarCompra(cliente, pedido(milho.getId(), 1));
        Venda seguinte = svc.finalizarCompra(cliente, pedido(milho.getId(), 1));
        assertEquals(ultima.getId() + 1, seguinte.getId());
    }

    // ---- 3. IVA e total (D3, D4) ----
    // O enunciado não traz preços do Milho/Adubo; valores abaixo escolhidos por nós.

    @Test
    void ivaETotalCertos() {
        Produto milho = novoProduto("Milho", "45.00", 100, "kg");
        Produto adubo = novoProduto("Adubo", "850.00", 10, "saco");
        Utilizador cliente = novoCliente();

        Venda v = svc.finalizarCompra(cliente, pedido(milho.getId(), 3, adubo.getId(), 1));

        // 3 x 45,00 = 135,00 ; 1 x 850,00 = 850,00 ; subtotal 985,00 ; IVA 16% = 157,60 ; total 1142,60
        assertEquals(new BigDecimal("985.00"), v.getSubtotal());
        assertEquals(new BigDecimal("157.60"), v.getIva());
        assertEquals(new BigDecimal("1142.60"), v.getTotal());
        assertEquals(2, v.getItens().size());
        assertEquals(new BigDecimal("135.00"), v.getItens().get(0).getSubtotal());
        assertEquals(97.0, milho.getQuantidade(), 0.0);
        assertEquals(9.0, adubo.getQuantidade(), 0.0);
        assertTrue(v.getNumeroFactura().matches("FT-\\d{4}-" + v.getId()), v.getNumeroFactura());
        assertEquals(v, svc.porId(v.getId()));
    }

    @Test
    void arredondamentoHalfUp() {
        Produto p = novoProduto("Semente", "33.33", 100, "kg");
        Venda v = svc.finalizarCompra(novoCliente(), pedido(p.getId(), 2.5));
        // 2,5 x 33,33 = 83,325 -> 83,33 ; IVA 83,33 x 0,16 = 13,3328 -> 13,33 ; total 96,66
        assertEquals(new BigDecimal("83.33"), v.getSubtotal());
        assertEquals(new BigDecimal("13.33"), v.getIva());
        assertEquals(new BigDecimal("96.66"), v.getTotal());
    }

    // ---- Extras que protegem a defesa ----

    @Test
    void facturaGuardaCopia_mudarOProdutoNaoAlteraAVenda() {
        Produto p = novoProduto("Milho", "45.00", 10, "kg");
        Venda v = svc.finalizarCompra(novoCliente(), pedido(p.getId(), 2));

        p.setNome("Milho Renomeado");
        p.setPreco(new BigDecimal("999.00"));

        assertEquals("Milho", v.getItens().get(0).getProdutoNome());
        assertEquals(new BigDecimal("45.00"), v.getItens().get(0).getPrecoUnit());
        assertEquals("João Açúcar", v.getClienteNome());
    }

    @Test
    void entradasInvalidasSaoRecusadasSemMexerNoStock() {
        Produto p = novoProduto("Milho", "45.00", 10, "kg");
        Utilizador c = novoCliente();

        assertThrows(NegocioException.class, () -> svc.finalizarCompra(null, pedido(p.getId(), 1)));
        assertThrows(NegocioException.class, () -> svc.finalizarCompra(c, null));
        assertThrows(NegocioException.class, () -> svc.finalizarCompra(c, new LinkedHashMap<>()));
        assertThrows(NegocioException.class, () -> svc.finalizarCompra(c, pedido(p.getId(), 0)));
        assertThrows(NegocioException.class, () -> svc.finalizarCompra(c, pedido(p.getId(), -2)));
        assertThrows(NegocioException.class, () -> svc.finalizarCompra(c, pedido(p.getId(), Double.NaN)));
        assertThrows(NegocioException.class, () -> svc.finalizarCompra(c, pedido(999999, 1)));
        assertThrows(NegocioException.class, () -> svc.finalizarCompra(c, pedido(p.getId(), 10.5)));
        assertEquals(10.0, p.getQuantidade(), 0.0);
    }

    @Test
    void historicoMaisRecentesPrimeiro_eSoDoCliente() {
        Produto p = novoProduto("Milho", "45.00", 100, "kg");
        Utilizador a = novoCliente();
        Utilizador b = novoCliente();
        Venda v1 = svc.finalizarCompra(a, pedido(p.getId(), 1));
        svc.finalizarCompra(b, pedido(p.getId(), 1));
        Venda v3 = svc.finalizarCompra(a, pedido(p.getId(), 1));

        List<Venda> doA = svc.porCliente(a.getId());
        assertEquals(2, doA.size());
        assertEquals(v3.getId(), doA.get(0).getId());
        assertEquals(v1.getId(), doA.get(1).getId());
        assertNotNull(svc.todas());
        assertTrue(svc.todas().size() >= 3);
    }
}
