package mz.agrogest.util;

/** Erro de regra de negócio. Unchecked; a mensagem é em português e vai directa ao utilizador. */
public class NegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
