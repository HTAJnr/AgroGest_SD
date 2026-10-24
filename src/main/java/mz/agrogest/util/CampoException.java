package mz.agrogest.util;

/**
 * Erro de validação associado a um campo específico do formulário.
 * O {@code campo} corresponde ao atributo {@code name} do input HTML,
 * permitindo ao servlet marcar o campo com erro na interface.
 */
public class CampoException extends NegocioException {
    private static final long serialVersionUID = 1L;

    private final String campo;

    public CampoException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    /** Atributo {@code name} do input que causou o erro. */
    public String getCampo() {
        return campo;
    }
}
