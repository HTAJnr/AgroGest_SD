package mz.agrogest.util;

import org.mindrot.jbcrypt.BCrypt;

/** Hash de passwords com BCrypt (D11). A password nunca se guarda nem se regista em claro. */
public final class SenhaUtil {

    private SenhaUtil() {
    }

    public static String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password em falta.");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verificar(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, hash);
        } catch (IllegalArgumentException e) {
            // hash mal formado
            return false;
        }
    }
}
