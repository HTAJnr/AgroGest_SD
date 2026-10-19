package mz.agrogest.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Colheita;
import mz.agrogest.model.Cultivo;
import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.Produto;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;

/**
 * Armazenamento em memória (singleton). Todas as ESCRITAS dos serviços correm dentro de
 * synchronized (getLock()). As listas devolvidas são as VIVAS: só se tocam dentro desse bloco.
 */
public final class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    private final Object lock = new Object();

    private final List<Utilizador> utilizadores = Collections.synchronizedList(new ArrayList<>());
    private final List<Fornecedor> fornecedores = Collections.synchronizedList(new ArrayList<>());
    private final List<Produto> produtos = Collections.synchronizedList(new ArrayList<>());
    private final List<Campanha> campanhas = Collections.synchronizedList(new ArrayList<>());
    private final List<Cultivo> cultivos = Collections.synchronizedList(new ArrayList<>());
    private final List<Colheita> colheitas = Collections.synchronizedList(new ArrayList<>());
    private final List<Venda> vendas = Collections.synchronizedList(new ArrayList<>());

    private final AtomicInteger idUtilizador = new AtomicInteger(0);
    private final AtomicInteger idFornecedor = new AtomicInteger(0);
    private final AtomicInteger idProduto = new AtomicInteger(0);
    private final AtomicInteger idCampanha = new AtomicInteger(0);
    private final AtomicInteger idCultivo = new AtomicInteger(0);
    private final AtomicInteger idColheita = new AtomicInteger(0);
    private final AtomicInteger idVenda = new AtomicInteger(0);

    private DataStore() {
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    /** Monitor ÚNICO para todas as escritas. */
    public Object getLock() {
        return lock;
    }

    public List<Utilizador> getUtilizadores() { return utilizadores; }
    public List<Fornecedor> getFornecedores() { return fornecedores; }
    public List<Produto> getProdutos() { return produtos; }
    public List<Campanha> getCampanhas() { return campanhas; }
    public List<Cultivo> getCultivos() { return cultivos; }
    public List<Colheita> getColheitas() { return colheitas; }
    public List<Venda> getVendas() { return vendas; }

    public int novoIdUtilizador() { return idUtilizador.incrementAndGet(); }
    public int novoIdFornecedor() { return idFornecedor.incrementAndGet(); }
    public int novoIdProduto() { return idProduto.incrementAndGet(); }
    public int novoIdCampanha() { return idCampanha.incrementAndGet(); }
    public int novoIdCultivo() { return idCultivo.incrementAndGet(); }
    public int novoIdColheita() { return idColheita.incrementAndGet(); }
    public int novoIdVenda() { return idVenda.incrementAndGet(); }
}
