package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mz.agrogest.service.UtilizadorService;
import mz.agrogest.util.NegocioException;

@WebServlet("/registo")
public class RegistoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String FORM = "/WEB-INF/views/auth/registo.jsp";

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
		String password = req.getParameter("password") == null ? "" : req.getParameter("password");
		String confirmar = req.getParameter("confirmar") == null ? "" : req.getParameter("confirmar");

		try {
			if (!password.equals(confirmar)) {
				throw new NegocioException("As palavras-passe n\u00e3o coincidem.");
			}

			UtilizadorService.getInstance().registarCliente(valor(req, "nome"), valor(req, "email"), password,
					valor(req, "telefone"), valor(req, "endereco"));

			req.getSession().setAttribute("flashOk", "Conta criada com sucesso. J\u00e1 pode entrar.");
			resp.sendRedirect(req.getContextPath() + "/login");
		} catch (NegocioException e) {
			req.setAttribute("erro", e.getMessage());
			mostrarForm(req, resp);
		}
	}

	private void mostrarForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("titulo", "Criar conta");
		req.getRequestDispatcher(FORM).forward(req, resp);
	}

	private String valor(HttpServletRequest req, String nome) {
		String v = req.getParameter(nome);
		return v == null ? "" : v.trim();
	}
}