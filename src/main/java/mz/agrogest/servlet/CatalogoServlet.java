package mz.agrogest.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mz.agrogest.model.Categoria;
import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.Produto;
import mz.agrogest.service.FornecedorService;
import mz.agrogest.service.ProdutoService;

@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String VISTA = "/WEB-INF/views/cliente/catalogo.jsp";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		SessaoUtil.semCache(resp);

		String filtro = req.getParameter("categoria");
		List<Produto> produtos;
		if (filtro == null || filtro.isBlank()) {
			produtos = ProdutoService.getInstance().listar();
		} else {
			try {
				produtos = ProdutoService.getInstance().listarPorCategoria(Categoria.valueOf(filtro.trim()));
			} catch (IllegalArgumentException e) {
				req.setAttribute("erro", "Categoria inv\u00e1lida.");
				produtos = ProdutoService.getInstance().listar();
			}
		}

		Map<Integer, Fornecedor> fornecedores = new HashMap<>();
		for (Fornecedor f : FornecedorService.getInstance().listar()) {
			fornecedores.put(f.getId(), f);
		}

		req.setAttribute("titulo", "Cat\u00e1logo");
		req.setAttribute("produtos", produtos);
		req.setAttribute("categorias", Categoria.values());
		req.setAttribute("fornecedores", fornecedores);
		req.getRequestDispatcher(VISTA).forward(req, resp);
	}
}