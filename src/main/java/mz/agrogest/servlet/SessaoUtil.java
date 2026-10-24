package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.http.*;
import mz.agrogest.model.Carrinho;
import mz.agrogest.model.Utilizador;

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
}