package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.http.*;
import mz.agrogest.model.Carrinho;
import mz.agrogest.model.Utilizador;
import mz.agrogest.util.CampoException;
import mz.agrogest.util.NegocioException;

final class SessaoUtil {

	private SessaoUtil() {
	}

	static Utilizador utilizador(HttpServletRequest req) {
		HttpSession sessao = req.getSession(false);
		return sessao == null ? null : (Utilizador) sessao.getAttribute("utilizador");
	}

	static Utilizador exigirCliente(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Utilizador u = utilizador(req);
		if (u == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return null;
		}
		if (u.isAdmin()) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		return u;
	}

	static Carrinho carrinho(HttpServletRequest req) {
		HttpSession sessao = req.getSession();
		synchronized (SessaoUtil.class) {
			Carrinho c = (Carrinho) sessao.getAttribute("carrinho");
			if (c == null) {
				c = new Carrinho();
				sessao.setAttribute("carrinho", c);
			}
			return c;
		}
	}

	static void semCache(HttpServletResponse resp) {
		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		resp.setHeader("Pragma", "no-cache");
		resp.setDateHeader("Expires", 0);
	}

	/**
	 * Coloca {@code erro} e {@code erroCampo} no request a partir de uma
	 * {@link NegocioException}. Se for {@link CampoException}, o campo
	 * é o {@code name} do input; caso contrário, fica vazio.
	 */
	static void erro(HttpServletRequest req, NegocioException e) {
		req.setAttribute("erro", e.getMessage());
		if (e instanceof CampoException) {
			req.setAttribute("erroCampo", ((CampoException) e).getCampo());
		} else {
			req.setAttribute("erroCampo", "");
		}
	}

	/**
	 * Coloca {@code erro} e {@code erroCampo} no request para erros
	 * de parsing (número, data, etc.) onde o servlet sabe o campo.
	 */
	static void erro(HttpServletRequest req, String campo, String mensagem) {
		req.setAttribute("erro", mensagem);
		req.setAttribute("erroCampo", campo);
	}
}
