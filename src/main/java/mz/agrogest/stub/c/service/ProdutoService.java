package mz.agrogest.stub.c.service;

/**
 * STUB temporário da Pessoa C. O ProdutoService real é da Pessoa B.
 * Só tem o método de que o FornecedorService precisa.
 * Na integração este ficheiro é apagado e o import passa a mz.agrogest.service.
 */
public final class ProdutoService {

    private static final ProdutoService INSTANCE = new ProdutoService();

    private ProdutoService() {
    }

    public static ProdutoService getInstance() {
        return INSTANCE;
    }

    /**
     * Devolve sempre false (nenhum fornecedor tem produtos).
     * Para testar a recusa de remoção, troca por: return fornecedorId == 1;
     */
    public boolean existeComFornecedor(int fornecedorId) {
        return false;
    }
}
