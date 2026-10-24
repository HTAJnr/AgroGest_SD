package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;
import mz.agrogest.service.VendaService;

@WebServlet("/factura")
public class FacturaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String VISTA = "/WEB-INF/views/cliente/factura.jsp";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Utilizador u = SessaoUtil.utilizador(req);
		if (u == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}
		SessaoUtil.semCache(resp);

		String idTexto = req.getParameter("id");
		int id;
		try {
			id = Integer.parseInt(idTexto == null ? "" : idTexto.trim());
		} catch (NumberFormatException e) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Venda venda = VendaService.getInstance().porId(id);
		if (venda == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND); 
			return;
		}
		if (!u.isAdmin() && venda.getClienteId() != u.getId()) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		req.setAttribute("titulo", "Factura " + venda.getNumeroFactura());
		req.setAttribute("venda", venda);
		req.getRequestDispatcher(VISTA).forward(req, resp);
	}
}