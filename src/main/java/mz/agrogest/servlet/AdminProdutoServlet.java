package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mz.agrogest.service.ProdutoService;

@WebServlet("/admin/produtos/*")
public class AdminProdutoServlet extends HttpServlet {
	//Constant
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("titulo", "Produtos");
		req.setAttribute("produtos", ProdutoService.getInstance().listar());
		req.getRequestDispatcher("/WEB-INF/views/admin/produtos/lista.jsp").forward(req, resp);
		
	}
}
