package mz.agrogest.service;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;

/**
 * Relatórios de clientes (enunciado §IV). Decisão D8:
 * "por volume" = total gasto (decrescente); "mais assíduos" = nº de compras (decrescente).
 */
public final class RelatorioService {

    private static final RelatorioService INSTANCE = new RelatorioService();
    private static final Collator COL = Collator.getInstance(new Locale("pt", "PT"));

    private RelatorioService() {
    }

    public static RelatorioService getInstance() {
        return INSTANCE;
    }

    /** Uma linha dos relatórios: cliente + nº de compras + total gasto. */
    public static final class LinhaCliente {
        private final Utilizador cliente;
        private final int compras;
        private final BigDecimal total;

        public LinhaCliente(Utilizador cliente, int compras, BigDecimal total) {
            this.cliente = cliente;
            this.compras = compras;
            this.total = total;
        }

        public Utilizador getCliente() {
            return cliente;
        }

        public int getCompras() {
            return compras;
        }

        public BigDecimal getTotal() {
            return total;
        }
    }

    private static int texto(String a, String b) {
        return COL.compare(a == null ? "" : a, b == null ? "" : b);
    }

    /** Todos os clientes com o seu nº de compras e total gasto (uma passagem pelas vendas). */
    private List<LinhaCliente> resumos() {
        Map<Integer, Integer> numero = new HashMap<>();
        Map<Integer, BigDecimal> gasto = new HashMap<>();
        for (Venda v : VendaService.getInstance().todas()) {
            BigDecimal t = v.getTotal() == null ? BigDecimal.ZERO : v.getTotal();
            numero.merge(v.getClienteId(), 1, Integer::sum);
            gasto.merge(v.getClienteId(), t, BigDecimal::add);
        }
        List<LinhaCliente> r = new ArrayList<>();
        for (Utilizador c : UtilizadorService.getInstance().listarClientes()) {
            r.add(new LinhaCliente(c,
                    numero.getOrDefault(c.getId(), 0),
                    gasto.getOrDefault(c.getId(), BigDecimal.ZERO)));
        }
        return r;
    }

    /** Clientes ordenados por nome (crescente). */
    public List<Utilizador> clientesPorNome() {
        List<Utilizador> r = UtilizadorService.getInstance().listarClientes();
        r.sort((a, b) -> texto(a.getNome(), b.getNome()));
        return r;
    }

    /** Clientes que compraram, ordenados por volume (total gasto, decrescente). */
    public List<LinhaCliente> clientesQueCompraram() {
        List<LinhaCliente> r = new ArrayList<>();
        for (LinhaCliente l : resumos()) {
            if (l.getCompras() > 0) {
                r.add(l);
            }
        }
        r.sort((a, b) -> {
            int c = b.getTotal().compareTo(a.getTotal());
            return c != 0 ? c : texto(a.getCliente().getNome(), b.getCliente().getNome());
        });
        return r;
    }

    /** Ranking por número de compras (decrescente; desempate por total gasto). */
    public List<LinhaCliente> maisAssiduos() {
        List<LinhaCliente> r = new ArrayList<>();
        for (LinhaCliente l : resumos()) {
            if (l.getCompras() > 0) {
                r.add(l);
            }
        }
        r.sort((a, b) -> {
            int c = Integer.compare(b.getCompras(), a.getCompras());
            if (c != 0) {
                return c;
            }
            c = b.getTotal().compareTo(a.getTotal());
            return c != 0 ? c : texto(a.getCliente().getNome(), b.getCliente().getNome());
        });
        return r;
    }

    /** Extra do enunciado: clientes sem nenhuma compra. */
    public List<Utilizador> clientesSemCompras() {
        List<Utilizador> r = new ArrayList<>();
        for (LinhaCliente l : resumos()) {
            if (l.getCompras() == 0) {
                r.add(l.getCliente());
            }
        }
        r.sort((a, b) -> texto(a.getNome(), b.getNome()));
        return r;
    }
}
