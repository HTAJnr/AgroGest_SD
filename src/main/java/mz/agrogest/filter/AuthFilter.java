package mz.agrogest.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import mz.agrogest.model.Utilizador;

/**
 * Controlo de acesso (RN04, RN05):
 * - rotas públicas passam sem sessão;
 * - tudo o resto exige sessão (anónimo -> redirect /login);
 * - /admin/* exige perfil ADMIN (cliente -> 403).
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = caminho(req);

        if (ePublico(path)) {
            chain.doFilter(req, resp);
            return;
        }

        HttpSession sessao = req.getSession(false);
        Utilizador u = (sessao == null) ? null : (Utilizador) sessao.getAttribute("utilizador");

        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (eAdmin(path) && !u.isAdmin()) {
            req.setAttribute("titulo", "Acesso negado");
            req.setAttribute("erro", "Esta área é reservada ao administrador.");
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(req, resp);
    }

    /** Caminho dentro da aplicação, já decodificado e normalizado pelo contentor. */
    private static String caminho(HttpServletRequest req) {
        String p = req.getServletPath();
        String info = req.getPathInfo();
        if (info != null) {
            p = p + info;
        }
        return p;
    }

    private static boolean ePublico(String p) {
        return p.isEmpty()
                || p.equals("/")
                || p.equals("/login")
                || p.equals("/registo")
                || p.equals("/logout")
                || p.startsWith("/css/");
    }

    private static boolean eAdmin(String p) {
        return p.equals("/admin") || p.startsWith("/admin/");
    }
}
