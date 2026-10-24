package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mz.agrogest.model.Utilizador;


@WebServlet("")
public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession sessao = req.getSession(false);
		Utilizador u = sessao == null ? null : (Utilizador) sessao.getAttribute("utilizador");

		String destino;
		if (u == null) {
			destino = "/login";
		} else if (u.isAdmin()) {
			destino = "/admin/dashboard";
		} else {
			destino = "/catalogo";
		}
		resp.sendRedirect(req.getContextPath() + destino);
	}
}