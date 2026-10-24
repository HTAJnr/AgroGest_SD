package mz.agrogest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.TipoFornecedor;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.CampoException;
import mz.agrogest.util.NegocioException;
import mz.agrogest.util.Validacao;

/**
 * Regras de negócio dos fornecedores (criar, listar, procurar, remover).
 * Toda a escrita corre dentro do lock único do DataStore (contrato, secção 5).
 */
public final class FornecedorService {

    private static final FornecedorService INSTANCE = new FornecedorService();

    private FornecedorService() {
    }

    public static FornecedorService getInstance() {
        return INSTANCE;
    }

    public Fornecedor criar(String nome, TipoFornecedor tipo, String contacto, String provincia) {
        String n = Validacao.nomeCoisa("nome", nome);
        if (tipo == null) {
            throw new CampoException("tipo", "Escolha o tipo de fornecedor.");
        }
        String c = Validacao.contactoFornecedor("contacto", contacto);
        String p = Validacao.provincia("provincia", provincia);

        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            // D14: unicidade por nome (sem distinguir maiúsculas)
            for (Fornecedor f : ds.getFornecedores()) {
                if (n.equalsIgnoreCase(f.getNome())) {
                    throw new CampoException("nome",
                            "Já existe um fornecedor com o nome «" + f.getNome() + "».");
                }
            }
            Fornecedor f = new Fornecedor();
            f.setId(ds.novoIdFornecedor());
            f.setNome(n);
            f.setTipo(tipo);
            f.setContacto(c);
            f.setProvincia(p);
            ds.getFornecedores().add(f);
            return f;
        }
    }

    /** Devolve uma CÓPIA da lista (nunca a lista viva). */
    public List<Fornecedor> listar() {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            return new ArrayList<>(ds.getFornecedores());
        }
    }

    /** Devolve null se não existir. */
    public Fornecedor porId(int id) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            for (Fornecedor f : ds.getFornecedores()) {
                if (f.getId() == id) {
                    return f;
                }
            }
            return null;
        }
    }

    /** Regra D6: não se remove um fornecedor que ainda tenha produtos. */
    public void remover(int id) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            if (porId(id) == null) {
                throw new NegocioException("Fornecedor não encontrado.");
            }
            if (ProdutoService.getInstance().existeComFornecedor(id)) {
                throw new NegocioException(
                        "Não é possível remover: este fornecedor tem produtos associados. "
                        + "Remova primeiro esses produtos.");
            }
            ds.getFornecedores().removeIf(f -> f.getId() == id);
        }
    }
}
