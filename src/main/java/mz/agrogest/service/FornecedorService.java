package mz.agrogest.service;

import java.util.ArrayList;
import java.util.List;

import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.TipoFornecedor;
import mz.agrogest.repo.DataStore;
import mz.agrogest.service.ProdutoService; // na integração passa a mz.agrogest.service.ProdutoService
import mz.agrogest.util.NegocioException;

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
        nome = limpar(nome);
        contacto = limpar(contacto);
        provincia = limpar(provincia);

        if (nome.isEmpty()) {
            throw new NegocioException("O nome do fornecedor é obrigatório.");
        }
        if (tipo == null) {
            throw new NegocioException("Escolha o tipo de fornecedor.");
        }
        if (contacto.isEmpty()) {
            throw new NegocioException("O contacto do fornecedor é obrigatório.");
        }
        if (provincia.isEmpty()) {
            throw new NegocioException("A província do fornecedor é obrigatória.");
        }

        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            Fornecedor f = new Fornecedor();
            f.setId(ds.novoIdFornecedor());
            f.setNome(nome);
            f.setTipo(tipo);
            f.setContacto(contacto);
            f.setProvincia(provincia);
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

    private static String limpar(String s) {
        return s == null ? "" : s.trim();
    }
}
