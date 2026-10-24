package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mz.agrogest.model.Utilizador;
import mz.agrogest.service.VendaService;

@WebServlet("/minhas-compras")
public class MinhasComprasServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String VISTA = "/WEB-INF/views/cliente/minhas-compras.jsp";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Utilizador u = SessaoUtil.exigirCliente(req, resp);
		if (u == null) {
			return;
		}
		SessaoUtil.semCache(resp);
		req.setAttribute("titulo", "Minhas compras");
		req.setAttribute("compras", VendaService.getInstance().porCliente(u.getId())); // mais recentes primeiro
		req.getRequestDispatcher(VISTA).forward(req, resp);
	}
}