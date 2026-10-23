package mz.agrogest.service;

import java.text.Collator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.Produto;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;
import mz.agrogest.repo.DataStore;

/**
 * Listagens do administrador (só leitura). Lê do DataStore / serviços e devolve
 * sempre cópias ordenadas por Comparator. Chaves de ordenação desconhecidas
 * caem no critério por omissão (nome, ou data nas compras).
 */
public final class ListagemService {

    private static final ListagemService INSTANCE = new ListagemService();
    private static final Collator COL = Collator.getInstance(new Locale("pt", "PT"));

    private ListagemService() {
    }

    public static ListagemService getInstance() {
        return INSTANCE;
    }

    /** Comparação de texto que respeita acentos (João, Açúcar...). */
    private static int texto(String a, String b) {
        return COL.compare(a == null ? "" : a, b == null ? "" : b);
    }

    private static <T> List<T> ordenado(List<T> origem, Comparator<T> cmp, boolean asc) {
        List<T> r = new ArrayList<>(origem);
        r.sort(asc ? cmp : cmp.reversed());
        return r;
    }

    // ---------- Produtos e armazém ----------

    private static Comparator<Produto> comparadorProduto(String ordenar) {
        if ("preco".equals(ordenar)) {
            return Comparator.comparing(Produto::getPreco);
        }
        if ("stock".equals(ordenar)) {
            return Comparator.comparingDouble(Produto::getQuantidade);
        }
        if ("validade".equals(ordenar)) {
            return Comparator.comparing(Produto::getValidade,
                    Comparator.nullsLast(Comparator.<LocalDate>naturalOrder()));
        }
        if ("categoria".equals(ordenar)) {
            return (a, b) -> texto(String.valueOf(a.getCategoria()), String.valueOf(b.getCategoria()));
        }
        return (a, b) -> texto(a.getNome(), b.getNome());
    }

    public List<Produto> produtos(String ordenar, boolean asc) {
        List<Produto> todos = new ArrayList<>(DataStore.getInstance().getProdutos());
        return ordenado(todos, comparadorProduto(ordenar), asc);
    }

    /** Produtos em armazém: stock > 0. */
    public List<Produto> armazem(String ordenar, boolean asc) {
        List<Produto> emStock = new ArrayList<>();
        for (Produto p : new ArrayList<>(DataStore.getInstance().getProdutos())) {
            if (p.getQuantidade() > 0) {
                emStock.add(p);
            }
        }
        return ordenado(emStock, comparadorProduto(ordenar), asc);
    }

    /** id do fornecedor -> nome, para mostrar o fornecedor nas listagens de produtos. */
    public Map<Integer, String> nomesFornecedores() {
        Map<Integer, String> m = new HashMap<>();
        for (Fornecedor f : new ArrayList<>(DataStore.getInstance().getFornecedores())) {
            m.put(f.getId(), f.getNome());
        }
        return m;
    }

    // ---------- Fornecedores ----------

    public List<Fornecedor> fornecedores(String ordenar, boolean asc) {
        Comparator<Fornecedor> c;
        if ("tipo".equals(ordenar)) {
            c = (a, b) -> texto(String.valueOf(a.getTipo()), String.valueOf(b.getTipo()));
        } else if ("provincia".equals(ordenar)) {
            c = (a, b) -> texto(a.getProvincia(), b.getProvincia());
        } else {
            c = (a, b) -> texto(a.getNome(), b.getNome());
        }
        return ordenado(new ArrayList<>(DataStore.getInstance().getFornecedores()), c, asc);
    }

    // ---------- Clientes ----------

    public List<Utilizador> clientes(String ordenar, boolean asc) {
        Comparator<Utilizador> c;
        if ("email".equals(ordenar)) {
            c = (a, b) -> texto(a.getEmail(), b.getEmail());
        } else {
            c = (a, b) -> texto(a.getNome(), b.getNome());
        }
        return ordenado(UtilizadorService.getInstance().listarClientes(), c, asc);
    }

    // ---------- Compras detalhadas ----------

    public List<Venda> compras(String ordenar, boolean asc) {
        Comparator<Venda> c;
        if ("valor".equals(ordenar)) {
            c = Comparator.comparing(Venda::getTotal);
        } else if ("cliente".equals(ordenar)) {
            c = (a, b) -> texto(a.getClienteNome(), b.getClienteNome());
        } else {
            c = Comparator.comparing(Venda::getData);
        }
        return ordenado(VendaService.getInstance().todas(), c, asc);
    }
}
