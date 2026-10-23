package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mz.agrogest.service.RelatorioService;

/** /admin/relatorios/{clientes|compraram|assiduos|sem-compras} */
@WebServlet("/admin/relatorios/*")
public class RelatorioServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String p = req.getPathInfo();
        if (p == null || p.equals("/")) {
            p = "/clientes";
        }

        RelatorioService rs = RelatorioService.getInstance();
        String vista;
        String titulo;
        Object lista;

        switch (p) {
            case "/clientes":
            case "/clientes-ordenados":
                vista = "clientes";
                titulo = "Clientes ordenados por nome";
                lista = rs.clientesPorNome();
                break;
            case "/compraram":
            case "/clientes-compraram":
                vista = "compraram";
                titulo = "Clientes que compraram";
                lista = rs.clientesQueCompraram();
                break;
            case "/assiduos":
            case "/mais-assiduos":
                vista = "assiduos";
                titulo = "Clientes mais assíduos";
                lista = rs.maisAssiduos();
                break;
            case "/sem-compras":
            case "/clientes-sem-compras":
                vista = "semcompras";
                titulo = "Clientes sem compras";
                lista = rs.clientesSemCompras();
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }

        req.setAttribute("vista", vista);
        req.setAttribute("lista", lista);
        req.setAttribute("titulo", titulo);
        req.getRequestDispatcher("/WEB-INF/views/admin/relatorios/index.jsp").forward(req, resp);
    }
}
