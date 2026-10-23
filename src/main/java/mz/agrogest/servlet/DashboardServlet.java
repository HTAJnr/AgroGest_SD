package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mz.agrogest.repo.DataStore;
import mz.agrogest.service.UtilizadorService;

/** /admin/dashboard: contagens gerais lidas do DataStore. */
@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DataStore ds = DataStore.getInstance();
        req.setAttribute("totalFornecedores", ds.getFornecedores().size());
        req.setAttribute("totalProdutos", ds.getProdutos().size());
        req.setAttribute("totalClientes", UtilizadorService.getInstance().listarClientes().size());
        req.setAttribute("totalVendas", ds.getVendas().size());
        req.setAttribute("totalCampanhas", ds.getCampanhas().size());
        req.setAttribute("titulo", "Dashboard");
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard/index.jsp").forward(req, resp);
    }
}
