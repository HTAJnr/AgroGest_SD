package mz.agrogest.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mz.agrogest.model.Carrinho;
import mz.agrogest.model.Categoria;
import mz.agrogest.model.LinhaCarrinho;
import mz.agrogest.model.Produto;
import mz.agrogest.model.Utilizador;
import mz.agrogest.service.ProdutoService;
import mz.agrogest.util.NegocioException;

@WebServlet("/carrinho/*")
public class CarrinhoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String VISTA = "/WEB-INF/views/cliente/carrinho.jsp";
	private static final BigDecimal TAXA_IVA = new BigDecimal("0.16");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (SessaoUtil.exigirCliente(req, resp) == null) {
			return;
		}
		String path = req.getPathInfo();
		if (path != null && !"/".equals(path)) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		SessaoUtil.semCache(resp);
		preparar(req, SessaoUtil.carrinho(req));
		req.setAttribute("titulo", "Carrinho");
		req.getRequestDispatcher(VISTA).forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Utilizador u = SessaoUtil.exigirCliente(req, resp);
		if (u == null) {
			return;
		}
		req.setCharacterEncoding("UTF-8");
		String path = req.getPathInfo() == null ? "" : req.getPathInfo();

		switch (path) {
		case "/adicionar":
			adicionar(req, resp);
			break;
		case "/atualizar":
			atualizar(req, resp);
			break;
		case "/remover":
			remover(req, resp);
			break;
		case "/esvaziar":
			SessaoUtil.carrinho(req).esvaziar();
			flash(req, "flashOk", "Carrinho esvaziado.");
			resp.sendRedirect(req.getContextPath() + "/carrinho");
			break;
		default:
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void adicionar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			int id = Integer.parseInt(texto(req, "produtoId"));
			double qtd = Double.parseDouble(decimal(req, "quantidade"));
			validarQuantidade(qtd);

			Produto p = ProdutoService.getInstance().porId(id);
			if (p == null) {
				throw new NegocioException("Esse produto j\u00e1 n\u00e3o existe.");
			}

			Carrinho c = SessaoUtil.carrinho(req);
			double jaNoCarrinho = c.getQuantidades().getOrDefault(id, 0.0);
			verificarStock(p, jaNoCarrinho + qtd);

			c.adicionar(id, qtd);
			flash(req, "flashOk", p.getNome() + " adicionado ao carrinho.");
		} catch (NegocioException e) {
			flash(req, "flashErro", e.getMessage());
		} catch (NumberFormatException e) {
			flash(req, "flashErro", "Indique uma quantidade v\u00e1lida (ex.: 2 ou 3,5).");
		}
		resp.sendRedirect(req.getContextPath() + voltar(req));
	}

	private void atualizar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			int id = Integer.parseInt(texto(req, "produtoId"));
			double qtd = Double.parseDouble(decimal(req, "quantidade"));
			Carrinho c = SessaoUtil.carrinho(req);

			if (qtd == 0) { // zero = tirar a linha
				c.remover(id);
				flash(req, "flashOk", "Produto removido do carrinho.");
			} else {
				validarQuantidade(qtd);
				Produto p = ProdutoService.getInstance().porId(id);
				if (p == null) {
					c.remover(id);
					throw new NegocioException("Esse produto j\u00e1 n\u00e3o existe e saiu do carrinho.");
				}
				verificarStock(p, qtd);
				c.atualizar(id, qtd);
				flash(req, "flashOk", "Carrinho atualizado.");
			}
		} catch (NegocioException e) {
			flash(req, "flashErro", e.getMessage());
		} catch (NumberFormatException e) {
			flash(req, "flashErro", "Indique uma quantidade v\u00e1lida (ex.: 2 ou 3,5).");
		}
		resp.sendRedirect(req.getContextPath() + "/carrinho");
	}

	private void remover(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			SessaoUtil.carrinho(req).remover(Integer.parseInt(texto(req, "produtoId")));
			flash(req, "flashOk", "Produto removido do carrinho.");
		} catch (NumberFormatException e) {
			flash(req, "flashErro", "Produto inv\u00e1lido.");
		}
		resp.sendRedirect(req.getContextPath() + "/carrinho");
	}

	static void preparar(HttpServletRequest req, Carrinho carrinho) {
		List<String> avisos = new ArrayList<>();
		List<LinhaCarrinho> linhas = new ArrayList<>();
		BigDecimal subtotal = BigDecimal.ZERO;

		for (Map.Entry<Integer, Double> e : carrinho.getQuantidades().entrySet()) {
			Produto p = ProdutoService.getInstance().porId(e.getKey());
			if (p == null) { 
				carrinho.remover(e.getKey());
				avisos.add("Um produto do carrinho foi retirado da loja e saiu do carrinho.");
				continue;
			}
			double qtd = e.getValue();
			if (qtd > p.getQuantidade()) {
				avisos.add("Stock de " + p.getNome() + " mudou: s\u00f3 h\u00e1 " + formatar(p.getQuantidade()) + " "
						+ p.getUnidade() + ". Ajuste a quantidade.");
			}
			LinhaCarrinho l = new LinhaCarrinho();
			l.setProduto(p);
			l.setQuantidade(qtd);
			l.setSubtotal(p.getPreco().multiply(BigDecimal.valueOf(qtd)).setScale(2, RoundingMode.HALF_UP));
			linhas.add(l);
			subtotal = subtotal.add(l.getSubtotal());
		}

		BigDecimal iva = subtotal.multiply(TAXA_IVA).setScale(2, RoundingMode.HALF_UP);
		req.setAttribute("linhas", linhas);
		req.setAttribute("avisos", avisos);
		req.setAttribute("subtotal", subtotal);
		req.setAttribute("iva", iva);
		req.setAttribute("total", subtotal.add(iva));
	}

	private void validarQuantidade(double qtd) {
		if (!(qtd > 0)) { // apanha tambem NaN
			throw new NegocioException("A quantidade tem de ser maior que zero.");
		}
	}

	private void verificarStock(Produto p, double pretendida) {
		if (pretendida > p.getQuantidade()) {
			throw new NegocioException("S\u00f3 existem " + formatar(p.getQuantidade()) + " " + p.getUnidade() + " de "
					+ p.getNome() + " em stock.");
		}
	}

	private String voltar(HttpServletRequest req) {
		if ("catalogo".equals(texto(req, "voltar"))) {
			try {
				return "/catalogo?categoria=" + Categoria.valueOf(texto(req, "categoria")).name();
			} catch (IllegalArgumentException e) {
				return "/catalogo";
			}
		}
		return "/carrinho";
	}

	private static String formatar(double v) {
		return BigDecimal.valueOf(v).stripTrailingZeros().toPlainString().replace('.', ',');
	}

	private void flash(HttpServletRequest req, String chave, String msg) {
		req.getSession().setAttribute(chave, msg);
	}

	private String texto(HttpServletRequest req, String nome) {
		String v = req.getParameter(nome);
		return v == null ? "" : v.trim();
	}

	private String decimal(HttpServletRequest req, String nome) {
		return texto(req, nome).replace(',', '.');
	}
}