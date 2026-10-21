package mz.agrogest.stub.b.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import mz.agrogest.model.Produto;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;
import mz.agrogest.model.VendaItem;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.NegocioException;

// STUB da Pessoa B (temporário). O real é da Pessoa A.
// Simula a venda: valida stock de TODOS os itens, só depois desconta (tudo ou nada),
// e calcula subtotal, IVA 16% e total.
public final class VendaService {

    private static final VendaService INSTANCIA = new VendaService();
    private static final BigDecimal TAXA_IVA = new BigDecimal("0.16");

    private final List<Venda> vendas = new ArrayList<>();
    private final AtomicInteger seq = new AtomicInteger();

    private VendaService() { }

    public static VendaService getInstance() { return INSTANCIA; }

    public Venda finalizarCompra(Utilizador cliente, Map<Integer, Double> itens) {
        if (cliente == null) throw new NegocioException("Sessão inválida.");
        if (itens == null || itens.isEmpty()) throw new NegocioException("O carrinho está vazio.");

        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            // 1.ª passagem: valida tudo (não altera nada)
            for (Map.Entry<Integer, Double> e : itens.entrySet()) {
                Produto p = procurar(ds, e.getKey());
                if (p == null) throw new NegocioException("Um produto do carrinho já não existe.");
                if (e.getValue() <= 0) throw new NegocioException("Quantidade inválida para " + p.getNome() + ".");
                if (e.getValue() > p.getQuantidade())
                    throw new NegocioException("Stock insuficiente para " + p.getNome()
                            + " (disponível: " + p.getQuantidade() + " " + p.getUnidade() + ").");
            }

            // 2.ª passagem: desconta o stock e monta a venda
            Venda v = new Venda();
            BigDecimal subtotal = BigDecimal.ZERO;
            for (Map.Entry<Integer, Double> e : itens.entrySet()) {
                Produto p = procurar(ds, e.getKey());
                double qtd = e.getValue();
                p.setQuantidade(p.getQuantidade() - qtd);

                VendaItem it = new VendaItem();
                it.setProdutoId(p.getId());
                it.setProdutoNome(p.getNome());
                it.setUnidade(p.getUnidade());
                it.setQuantidade(qtd);
                it.setPrecoUnit(p.getPreco());
                it.setSubtotal(p.getPreco().multiply(BigDecimal.valueOf(qtd)).setScale(2, RoundingMode.HALF_UP));
                v.getItens().add(it);
                subtotal = subtotal.add(it.getSubtotal());
            }

            int id = seq.incrementAndGet();
            LocalDateTime agora = LocalDateTime.now();
            v.setId(id);
            v.setNumeroFactura("FT-" + agora.getYear() + "-" + id);
            v.setData(agora);
            v.setClienteId(cliente.getId());
            v.setClienteNome(cliente.getNome());
            v.setClienteEmail(cliente.getEmail());
            v.setClienteTelefone(cliente.getTelefone());
            v.setClienteEndereco(cliente.getEndereco());
            v.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
            v.setIva(subtotal.multiply(TAXA_IVA).setScale(2, RoundingMode.HALF_UP));
            v.setTotal(v.getSubtotal().add(v.getIva()));
            vendas.add(v);
            return v;
        }
    }

    private Produto procurar(DataStore ds, int id) {
        for (Produto p : ds.getProdutos()) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public Venda porId(int id) {
        synchronized (DataStore.getInstance().getLock()) {
            for (Venda v : vendas) {
                if (v.getId() == id) return v;
            }
            return null;
        }
    }

    // Mais recentes primeiro
    public List<Venda> porCliente(int clienteId) {
        synchronized (DataStore.getInstance().getLock()) {
            List<Venda> r = new ArrayList<>();
            for (int i = vendas.size() - 1; i >= 0; i--) {
                if (vendas.get(i).getClienteId() == clienteId) r.add(vendas.get(i));
            }
            return r;
        }
    }

    // Mais recentes primeiro
    public List<Venda> todas() {
        synchronized (DataStore.getInstance().getLock()) {
            List<Venda> r = new ArrayList<>(vendas);
            java.util.Collections.reverse(r);
            return r;
        }
    }
}
