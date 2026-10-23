package mz.agrogest.servlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mz.agrogest.repo.Persistencia;

/** /admin/persistencia: página de estado (GET) e acções gravar/eliminar (POST). */
@WebServlet("/admin/persistencia/*")
public class PersistenciaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String p = req.getPathInfo();
        if (p != null && !p.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        mostrar(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String p = req.getPathInfo();
        if (p == null) {
            p = "";
        }
        try {
            switch (p) {
                case "/gravar":
                    Persistencia.gravar();
                    req.getSession().setAttribute("flashOk", "Dados gravados com sucesso.");
                    break;
                case "/eliminar":
                    boolean existia = Persistencia.eliminar();
                    req.getSession().setAttribute("flashOk",
                            existia ? "Ficheiro de dados eliminado." : "Não havia ficheiro para eliminar.");
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
            }
        } catch (IOException ex) {
            req.setAttribute("erro", "Falha na operação de ficheiro: " + ex.getMessage());
            mostrar(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/admin/persistencia");
    }

    private void mostrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Path f = Persistencia.ficheiro();
        boolean existe = Files.exists(f);
        req.setAttribute("ficheiro", f.toString());
        req.setAttribute("existe", existe);
        if (existe) {
            long bytes = Files.size(f);
            req.setAttribute("tamanhoKb", String.format("%.1f", bytes / 1024.0));
            LocalDateTime mod = LocalDateTime.ofInstant(Files.getLastModifiedTime(f).toInstant(),
                    ZoneId.systemDefault());
            req.setAttribute("modificado", mod.format(FMT));
        }
        req.setAttribute("titulo", "Persistência");
        req.getRequestDispatcher("/WEB-INF/views/admin/persistencia/index.jsp").forward(req, resp);
    }
}
