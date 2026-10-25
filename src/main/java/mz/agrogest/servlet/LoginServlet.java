package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mz.agrogest.model.Utilizador;
import mz.agrogest.service.UtilizadorService;
import mz.agrogest.util.NegocioException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String FORM = "/WEB-INF/views/auth/login.jsp";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession sessao = req.getSession(false);
		if (sessao != null && sessao.getAttribute("utilizador") != null) {
			resp.sendRedirect(req.getContextPath() + "/");
			return;
		}
		mostrarForm(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String email = valor(req, "email");
		String password = req.getParameter("password") == null ? "" : req.getParameter("password");

		try {
			Utilizador u = UtilizadorService.getInstance().autenticar(email, password);

			if (u == null) {
				erroForm(req, resp, email, "Email ou palavra-passe incorretos.");
				return;
			}

			HttpSession sessao = req.getSession();
			req.changeSessionId();
			sessao.setAttribute("utilizador", u);

			resp.sendRedirect(req.getContextPath() + "/");
		} catch (NegocioException e) {
			erroForm(req, resp, email, e.getMessage());
		}
	}

	private void erroForm(HttpServletRequest req, HttpServletResponse resp, String email, String msg)
			throws ServletException, IOException {
		req.setAttribute("email", email);
		req.setAttribute("erro", msg);
		mostrarForm(req, resp);
	}

	private void mostrarForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("titulo", "Entrar");
		req.getRequestDispatcher(FORM).forward(req, resp);
	}

	private String valor(HttpServletRequest req, String nome) {
		String v = req.getParameter(nome);
		return v == null ? "" : v.trim();
	}
}