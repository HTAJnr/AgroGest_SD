package mz.agrogest.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mz.agrogest.model.Produto;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;
import mz.agrogest.model.VendaItem;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.NegocioException;

/**
 * Vendas. finalizarCompra é "tudo ou nada" (RN02) e nunca vende mais do que o stock (RN01):
 * corre inteira dentro do lock comum do DataStore, em duas fases.
 */
public final class VendaService {

    private static final BigDecimal TAXA_IVA = new BigDecimal("0.16");   // D4
    private static final VendaService INSTANCE = new VendaService();

    private final DataStore store = DataStore.getInstance();

    private VendaService() {
    }

    public static VendaService getInstance() {
        return INSTANCE;
    }

    /** Produto já validado + quantidade pedida. Só serve de ponte entre as duas fases. */
    private static final class Linha {
        final Produto produto;
        final double quantidade;

        Linha(Produto produto, double quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }
    }

    public Venda finalizarCompra(Utilizador cliente, Map<Integer, Double> itens) {
        if (cliente == null) {
            throw new NegocioException("É preciso iniciar sessão para comprar.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new NegocioException("O carrinho está vazio.");
        }
        Map<Integer, Double> pedido = new LinkedHashMap<>(itens);   // não depender do mapa de quem chama

        synchronized (store.getLock()) {

            // ---- FASE 1: validar TUDO, sem alterar nada ----
            List<Linha> linhas = new ArrayList<>(pedido.size());
            for (Map.Entry<Integer, Double> e : pedido.entrySet()) {
                Produto p = procurarProduto(e.getKey());
                if (p == null) {
                    throw new NegocioException("O produto com código " + e.getKey() + " já não existe.");
                }
                Double q = e.getValue();
                if (q == null || q.isNaN() || q.isInfinite() || q <= 0) {
                    throw new NegocioException("Quantidade inválida para \"" + p.getNome() + "\".");
                }
                if (p.getPreco() == null) {
                    throw new NegocioException("O produto \"" + p.getNome() + "\" não tem preço definido.");
                }
                // comparação em decimal exacto, sem erros de arredondamento do double
                if (BigDecimal.valueOf(q).compareTo(BigDecimal.valueOf(p.getQuantidade())) > 0) {
                    throw new NegocioException("Stock insuficiente para \"" + p.getNome() + "\": pediu "
                            + fmt(q) + " " + p.getUnidade() + ", disponível "
                            + fmt(p.getQuantidade()) + " " + p.getUnidade() + ".");
                }
                linhas.add(new Linha(p, q));
            }

            // ---- FASE 2a: construir a venda (ainda sem mexer no stock) ----
            Venda venda = new Venda();
            venda.setId(store.novoIdVenda());   // só aqui: compras recusadas não gastam ids (RN06)
            LocalDateTime agora = LocalDateTime.now();
            venda.setData(agora);
            venda.setNumeroFactura("FT-" + agora.getYear() + "-" + venda.getId());   // D2
            venda.setClienteId(cliente.getId());
            venda.setClienteNome(cliente.getNome());
            venda.setClienteEmail(cliente.getEmail());
            venda.setClienteTelefone(cliente.getTelefone());
            venda.setClienteEndereco(cliente.getEndereco());

            BigDecimal subtotal = BigDecimal.ZERO;
            List<VendaItem> itensVenda = new ArrayList<>(linhas.size());
            for (Linha l : linhas) {
                BigDecimal qtd = BigDecimal.valueOf(l.quantidade);
                BigDecimal subLinha = l.produto.getPreco().multiply(qtd).setScale(2, RoundingMode.HALF_UP);

                VendaItem vi = new VendaItem();   // CÓPIA: a factura não muda se o produto mudar (D7)
                vi.setProdutoId(l.produto.getId());
                vi.setProdutoNome(l.produto.getNome());
                vi.setUnidade(l.produto.getUnidade());
                vi.setQuantidade(l.quantidade);
                vi.setPrecoUnit(l.produto.getPreco());
                vi.setSubtotal(subLinha);
                itensVenda.add(vi);

                subtotal = subtotal.add(subLinha);
            }
            BigDecimal iva = subtotal.multiply(TAXA_IVA).setScale(2, RoundingMode.HALF_UP);   // D3, D4
            venda.setItens(itensVenda);
            venda.setSubtotal(subtotal);
            venda.setIva(iva);
            venda.setTotal(subtotal.add(iva));

            // ---- FASE 2b: aplicar. Só aritmética simples: já nada pode falhar a meio ----
            for (Linha l : linhas) {
                double novo = BigDecimal.valueOf(l.produto.getQuantidade())
                        .subtract(BigDecimal.valueOf(l.quantidade)).doubleValue();
                l.produto.setQuantidade(novo);
            }
            store.getVendas().add(venda);
            return venda;
        }
    }

    public Venda porId(int id) {
        synchronized (store.getLock()) {
            for (Venda v : store.getVendas()) {
                if (v.getId() == id) {
                    return v;
                }
            }
            return null;
        }
    }

    /** Mais recentes primeiro. */
    public List<Venda> porCliente(int clienteId) {
        List<Venda> resultado = new ArrayList<>();
        synchronized (store.getLock()) {
            for (Venda v : store.getVendas()) {
                if (v.getClienteId() == clienteId) {
                    resultado.add(v);
                }
            }
        }
        resultado.sort(maisRecentesPrimeiro());
        return resultado;
    }

    /** Mais recentes primeiro. */
    public List<Venda> todas() {
        List<Venda> resultado;
        synchronized (store.getLock()) {
            resultado = new ArrayList<>(store.getVendas());
        }
        resultado.sort(maisRecentesPrimeiro());
        return resultado;
    }

    // ---- auxiliares ----

    private Produto procurarProduto(int id) {   // chamar sempre com o lock tomado
        for (Produto p : store.getProdutos()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    private static Comparator<Venda> maisRecentesPrimeiro() {
        return Comparator.comparing(Venda::getData, Comparator.nullsLast(Comparator.<java.time.LocalDateTime>reverseOrder()))
                .thenComparing(Venda::getId, Comparator.reverseOrder());
    }

    /** 10.0 -> "10", 2.5 -> "2.5" (para mensagens). */
    private static String fmt(double v) {
        return BigDecimal.valueOf(v).stripTrailingZeros().toPlainString();
    }
}
