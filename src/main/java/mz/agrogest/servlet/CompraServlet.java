package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mz.agrogest.model.Carrinho;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;
import mz.agrogest.service.VendaService;
import mz.agrogest.util.NegocioException;

@WebServlet("/compra")
public class CompraServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String VISTA = "/WEB-INF/views/cliente/compra.jsp";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (SessaoUtil.exigirCliente(req, resp) == null) {
			return;
		}
		SessaoUtil.semCache(resp);
		Carrinho carrinho = SessaoUtil.carrinho(req);
		if (carrinho.isVazio()) {
			carrinhoVazio(req, resp);
			return;
		}
		mostrarResumo(req, resp, carrinho);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Utilizador cliente = SessaoUtil.exigirCliente(req, resp);
		if (cliente == null) {
			return;
		}
		req.setCharacterEncoding("UTF-8");
		Carrinho carrinho = SessaoUtil.carrinho(req);

		synchronized (carrinho) {
			if (carrinho.isVazio()) {
				carrinhoVazio(req, resp);
				return;
			}
			try {
				Venda venda = VendaService.getInstance().finalizarCompra(cliente, carrinho.getQuantidades());
				carrinho.esvaziar();
				req.getSession().setAttribute("flashOk", "Compra conclu\u00edda com sucesso.");
				resp.sendRedirect(req.getContextPath() + "/factura?id=" + venda.getId());
			} catch (NegocioException e) {
				req.setAttribute("erro", e.getMessage());
				mostrarResumo(req, resp, carrinho);
			}
		}
	}

	private void mostrarResumo(HttpServletRequest req, HttpServletResponse resp, Carrinho carrinho)
			throws ServletException, IOException {
		CarrinhoServlet.preparar(req, carrinho);
		req.setAttribute("titulo", "Finalizar compra");
		req.getRequestDispatcher(VISTA).forward(req, resp);
	}

	private void carrinhoVazio(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		req.getSession().setAttribute("flashErro", "O carrinho est\u00e1 vazio.");
		resp.sendRedirect(req.getContextPath() + "/carrinho");
	}
}