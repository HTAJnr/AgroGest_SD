package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mz.agrogest.model.TipoFornecedor;
import mz.agrogest.service.FornecedorService;
import mz.agrogest.util.NegocioException;

/**
 * Controlador de /admin/fornecedores/*
 *   GET  /admin/fornecedores           -> lista + formulário de criar
 *   POST /admin/fornecedores/criar     -> cria
 *   POST /admin/fornecedores/remover   -> remove
 * O acesso só para ADMIN é garantido pelo AuthFilter.
 */
@WebServlet("/admin/fornecedores/*")
public class AdminFornecedorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String VISTA = "/WEB-INF/views/admin/fornecedores/lista.jsp";
    private static final String BASE = "/admin/fornecedores";

    private final FornecedorService servico = FornecedorService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String acao = req.getPathInfo();
        if (acao == null || "/".equals(acao)) {
            mostrarLista(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // acentos certos (João Açúcar)
        String acao = req.getPathInfo();
        if ("/criar".equals(acao)) {
            criar(req, resp);
        } else if ("/remover".equals(acao)) {
            remover(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void criar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            TipoFornecedor tipo = lerTipo(req.getParameter("tipo"));
            servico.criar(req.getParameter("nome"), tipo,
                    req.getParameter("contacto"), req.getParameter("provincia"));
            req.getSession().setAttribute("flashOk", "Fornecedor criado com sucesso.");
            resp.sendRedirect(req.getContextPath() + BASE);
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            mostrarLista(req, resp); // o formulário mantém o que foi escrito (${param.x})
        }
    }

    private void remover(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            servico.remover(id);
            req.getSession().setAttribute("flashOk", "Fornecedor removido.");
            resp.sendRedirect(req.getContextPath() + BASE);
        } catch (NumberFormatException e) {
            req.setAttribute("erro", "Identificador de fornecedor inválido.");
            mostrarLista(req, resp);
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            mostrarLista(req, resp);
        }
    }

    /** Converte o texto do formulário no enum; vazio -> null; inválido -> NegocioException. */
    private TipoFornecedor lerTipo(String texto) {
        if (texto == null || texto.isBlank()) {
            return null; // o serviço diz "Escolha o tipo de fornecedor."
        }
        try {
            return TipoFornecedor.valueOf(texto.trim());
        } catch (IllegalArgumentException e) {
            throw new NegocioException("Tipo de fornecedor inválido.");
        }
    }

    private void mostrarLista(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("titulo", "Fornecedores");
        req.setAttribute("fornecedores", servico.listar());
        req.setAttribute("tipos", TipoFornecedor.values());
        req.getRequestDispatcher(VISTA).forward(req, resp);
    }
}
