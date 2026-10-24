package mz.agrogest.filter;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.UUID;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Filtro de proteção contra duplo envio de formulários (V2).
 *
 * GET: gera um UUID, guarda-o no conjunto da sessão (máximo 50, FIFO) e coloca-o
 * no request como {@code tokenForm}. Cria sessão se não existir (para login e registo).
 *
 * POST: exige o parâmetro {@code _t}. Se existir no conjunto da sessão, consome-o
 * (remoção atómica) e prossegue; caso contrário, não executa o servlet e redireciona
 * para o Referer (ou '/') com mensagem flashErro.
 *
 * Excepção: {@code /logout}.
 */
@WebFilter("/*")
public class TokenFormFilter implements Filter {

    public static final String NOME_PARAMETRO = "_t";
    public static final String ATTR_REQUEST = "tokenForm";
    public static final String ATTR_SESSAO = "mz.agrogest.tokens";
    public static final int MAX_TOKENS = 50;
    public static final String MENSAGEM_ERRO =
            "Este pedido já foi processado ou expirou. Verifique a lista antes de repetir.";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = caminho(req);

        // Ficheiros estáticos não precisam de token nem de criação de sessão
        if (eEstatico(path)) {
            chain.doFilter(req, resp);
            return;
        }

        // /logout é uma excepção explícita
        if ("/logout".equals(path)) {
            chain.doFilter(req, resp);
            return;
        }

        String metodo = req.getMethod();

        if ("GET".equalsIgnoreCase(metodo) || "HEAD".equalsIgnoreCase(metodo)) {
            HttpSession sessao = req.getSession(true);
            String token = gerarToken(sessao);
            req.setAttribute(ATTR_REQUEST, token);
            chain.doFilter(req, resp);
            return;
        }

        if ("POST".equalsIgnoreCase(metodo)) {
            if (req.getCharacterEncoding() == null) {
                req.setCharacterEncoding("UTF-8");
            }

            String tokenParam = req.getParameter(NOME_PARAMETRO);
            HttpSession sessao = req.getSession(false);

            boolean valido = (sessao != null) && consumirToken(sessao, tokenParam);
            if (!valido) {
                HttpSession s = req.getSession(true);
                s.setAttribute("flashErro", MENSAGEM_ERRO);
                resp.sendRedirect(destinoSeguro(req));
                return;
            }

            // Token consumido com sucesso. Gerar novo token para o request e sessão,
            // garantindo que formulários reenviados após forward (ex.: erro de validação)
            // possuam um token novo e válido.
            String novoToken = gerarToken(sessao);
            req.setAttribute(ATTR_REQUEST, novoToken);

            chain.doFilter(req, resp);
            return;
        }

        // Outros métodos HTTP (se houver)
        chain.doFilter(req, resp);
    }

    private static String caminho(HttpServletRequest req) {
        String p = req.getServletPath();
        String info = req.getPathInfo();
        if (info != null) {
            p = p + info;
        }
        return p;
    }

    private static boolean eEstatico(String p) {
        return p.startsWith("/css/")
                || p.startsWith("/assets/")
                || p.endsWith(".ico");
    }

    private static String destinoSeguro(HttpServletRequest req) {
        String referer = req.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            try {
                URI uri = URI.create(referer);
                String host = uri.getHost();
                if (host == null || host.equalsIgnoreCase(req.getServerName())) {
                    return referer;
                }
            } catch (IllegalArgumentException ignored) {
                // Formato inválido de Referer
            }
        }
        return req.getContextPath() + "/";
    }

    static TokenStore obterOuCriarStore(HttpSession sessao) {
        synchronized (sessao) {
            TokenStore store = (TokenStore) sessao.getAttribute(ATTR_SESSAO);
            if (store == null) {
                store = new TokenStore();
                sessao.setAttribute(ATTR_SESSAO, store);
            }
            return store;
        }
    }

    static String gerarToken(HttpSession sessao) {
        TokenStore store = obterOuCriarStore(sessao);
        return store.gerar();
    }

    static boolean consumirToken(HttpSession sessao, String token) {
        TokenStore store = (TokenStore) sessao.getAttribute(ATTR_SESSAO);
        if (store == null) {
            return false;
        }
        return store.consumir(token);
    }

    /**
     * Armazenamento sincronizado de tokens por sessão com limite FIFO.
     */
    public static class TokenStore implements Serializable {
        private static final long serialVersionUID = 1L;
        private final LinkedHashSet<String> tokens = new LinkedHashSet<>();

        public synchronized String gerar() {
            String token = UUID.randomUUID().toString();
            tokens.add(token);
            while (tokens.size() > MAX_TOKENS) {
                Iterator<String> it = tokens.iterator();
                if (it.hasNext()) {
                    it.next();
                    it.remove();
                } else {
                    break;
                }
            }
            return token;
        }

        public synchronized boolean consumir(String token) {
            if (token == null || token.isBlank()) {
                return false;
            }
            return tokens.remove(token);
        }

        public synchronized boolean contem(String token) {
            return tokens.contains(token);
        }

        public synchronized int total() {
            return tokens.size();
        }
    }
}
