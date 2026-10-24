package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;


@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		sair(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		sair(req, resp);
	}

	private void sair(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession sessao = req.getSession(false);
		if (sessao != null) {
			sessao.invalidate(); 
		}
		
		req.getSession().setAttribute("flashOk", "Sess\u00e3o terminada.");
		resp.sendRedirect(req.getContextPath() + "/login");
	}
}