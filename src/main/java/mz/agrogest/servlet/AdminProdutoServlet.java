package mz.agrogest.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mz.agrogest.model.Categoria;
import mz.agrogest.service.FornecedorService;
import mz.agrogest.service.ListagemService;
import mz.agrogest.service.ProdutoService;
import mz.agrogest.util.NegocioException;
import mz.agrogest.util.Validacao;

@WebServlet("/admin/produtos/*")
public class AdminProdutoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String LISTA = "/WEB-INF/views/admin/produtos/lista.jsp";
	private static final String FORM = "/WEB-INF/views/admin/produtos/form.jsp";

	private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(ResolverStyle.STRICT);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();

		if ("/novo".equals(path)) {
			mostrarForm(req, resp);
		} else {
			mostrarLista(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String path = req.getPathInfo() == null ? "" : req.getPathInfo();

		switch (path) {
		case "/criar":
			criar(req, resp);
			break;
		case "/repor":
			repor(req, resp);
			break;
		case "/remover":
			remover(req, resp);
			break;
		default:
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	// ---------- Accoes ----------

	private void criar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Categoria categoria = texto(req, "categoria").isEmpty() ? null
					: Categoria.valueOf(texto(req, "categoria"));
			int fornecedorId = Integer.parseInt(texto(req, "fornecedorId"));
			BigDecimal preco = new BigDecimal(decimal(req, "preco"));
			double quantidade = Double.parseDouble(decimal(req, "quantidade"));
			LocalDate validade = parseValidade(texto(req, "validade"));

			ProdutoService.getInstance().criar(texto(req, "nome"), categoria, fornecedorId, preco, quantidade,
					texto(req, "unidade"), texto(req, "descricao"), texto(req, "fotoUrl"), validade);

			ok(req, resp, "Produto criado com sucesso.");
		} catch (NegocioException e) {
			SessaoUtil.erro(req, e);
			mostrarForm(req, resp);
		} catch (NumberFormatException e) {
			SessaoUtil.erro(req, "preco", "Preço, quantidade e fornecedor têm de ser números válidos.");
			mostrarForm(req, resp);
		} catch (DateTimeParseException e) {
			SessaoUtil.erro(req, "validade", "Data inválida. Use o formato dd/mm/aaaa (ex.: 31/03/2027).");
			mostrarForm(req, resp);
		} catch (IllegalArgumentException e) {
			SessaoUtil.erro(req, "categoria", "Categoria inválida.");
			mostrarForm(req, resp);
		}
	}

	private void repor(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			int id = Integer.parseInt(texto(req, "id"));
			double qtd = Double.parseDouble(decimal(req, "quantidade"));
			ProdutoService.getInstance().reporStock(id, qtd);
			ok(req, resp, "Stock reposto.");
		} catch (NegocioException e) {
			erroLista(req, resp, e.getMessage());
		} catch (NumberFormatException e) {
			erroLista(req, resp, "Indique uma quantidade válida (ex.: 10 ou 3,5).");
		}
	}

	private void remover(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			ProdutoService.getInstance().remover(Integer.parseInt(texto(req, "id")));
			ok(req, resp, "Produto removido.");
		} catch (NegocioException e) {
			erroLista(req, resp, e.getMessage());
		} catch (NumberFormatException e) {
			erroLista(req, resp, "Produto inválido.");
		}
	}

	// ---------- Auxiliares ----------

	private void mostrarLista(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("titulo", "Produtos");
		req.setAttribute("produtos", ProdutoService.getInstance().listar());
		req.setAttribute("nomesFornecedores", ListagemService.getInstance().nomesFornecedores());
		req.getRequestDispatcher(LISTA).forward(req, resp);
	}

	private void mostrarForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("titulo", "Novo produto");
		req.setAttribute("categorias", Categoria.values());
		req.setAttribute("fornecedores", FornecedorService.getInstance().listar());
		req.setAttribute("hoje", LocalDate.now().toString());
		req.setAttribute("unidades", Validacao.UNIDADES);
		req.setAttribute("fotosLocais", listarFotosLocais());
		req.getRequestDispatcher(FORM).forward(req, resp);
	}

	/** Devolve caminhos web das fotos em /assets/img/produtos/, ordenados. */
	private List<String> listarFotosLocais() {
		Set<String> paths = getServletContext().getResourcePaths("/assets/img/produtos/");
		if (paths == null) {
			return Collections.emptyList();
		}
		List<String> lista = new ArrayList<>();
		for (String p : paths) {
			String lower = p.toLowerCase();
			if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp")) {
				lista.add(p); // ex.: /assets/img/produtos/milho.jpg
			}
		}
		Collections.sort(lista);
		return lista;
	}

	private void erroLista(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
		req.getSession().setAttribute("flashErro", msg);
		resp.sendRedirect(req.getContextPath() + "/admin/produtos");
	}

	private void ok(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
		req.getSession().setAttribute("flashOk", msg);
		resp.sendRedirect(req.getContextPath() + "/admin/produtos");
	}

	private String texto(HttpServletRequest req, String nome) {
		String v = req.getParameter(nome);
		return v == null ? "" : v.trim();
	}

	/** Aceita "3,5" ou "3.5". */
	private String decimal(HttpServletRequest req, String nome) {
		return texto(req, nome).replace(',', '.');
	}

	private static LocalDate parseValidade(String v) {
		if (v == null || v.isBlank()) {
			return null;
		}
		v = v.trim();
		if (v.contains("/")) {
			return LocalDate.parse(v, DATA_BR);
		}
		return LocalDate.parse(v);
	}
}
