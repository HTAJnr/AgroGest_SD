package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mz.agrogest.model.TipoFornecedor;
import mz.agrogest.service.FornecedorService;
import mz.agrogest.util.CampoException;
import mz.agrogest.util.NegocioException;
import mz.agrogest.util.Validacao;

/**
 * Controlador de /admin/fornecedores/*
 *   GET  /admin/fornecedores        -> lista.jsp
 *   GET  /admin/fornecedores/novo   -> form.jsp
 *   POST /admin/fornecedores        -> cria fornecedor (ou /criar)
 *   POST /admin/fornecedores/remover -> remove fornecedor
 * O acesso só para ADMIN é garantido pelo AuthFilter.
 */
@WebServlet("/admin/fornecedores/*")
public class AdminFornecedorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String LISTA = "/WEB-INF/views/admin/fornecedores/lista.jsp";
    private static final String FORM = "/WEB-INF/views/admin/fornecedores/form.jsp";
    private static final String BASE = "/admin/fornecedores";

    private final FornecedorService servico = FornecedorService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if ("/novo".equals(path)) {
            mostrarForm(req, resp);
        } else if (path == null || "/".equals(path) || path.isEmpty()) {
            mostrarLista(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // acentos certos (João Açúcar)
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();

        if ("/criar".equals(path) || "".equals(path) || "/".equals(path)) {
            criar(req, resp);
        } else if ("/remover".equals(path)) {
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
            ok(req, resp, "Fornecedor criado com sucesso.");
        } catch (NegocioException e) {
            SessaoUtil.erro(req, e);
            mostrarForm(req, resp); // o formulário mantém o que foi escrito (${param.x})
        }
    }

    private void remover(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            int id = Integer.parseInt(texto(req, "id"));
            servico.remover(id);
            ok(req, resp, "Fornecedor removido com sucesso.");
        } catch (NegocioException e) {
            erroLista(req, resp, e.getMessage());
        } catch (NumberFormatException e) {
            erroLista(req, resp, "Identificador de fornecedor inválido.");
        }
    }

    private void mostrarLista(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("titulo", "Fornecedores");
        req.setAttribute("fornecedores", servico.listar());
        req.getRequestDispatcher(LISTA).forward(req, resp);
    }

    private void mostrarForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("titulo", "Novo fornecedor");
        req.setAttribute("tipos", TipoFornecedor.values());
        req.setAttribute("provincias", Validacao.PROVINCIAS);
        req.getRequestDispatcher(FORM).forward(req, resp);
    }

    private void erroLista(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        req.getSession().setAttribute("flashErro", msg);
        resp.sendRedirect(req.getContextPath() + BASE);
    }

    private void ok(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        req.getSession().setAttribute("flashOk", msg);
        resp.sendRedirect(req.getContextPath() + BASE);
    }

    private String texto(HttpServletRequest req, String nome) {
        String v = req.getParameter(nome);
        return v == null ? "" : v.trim();
    }

    /** Converte o texto do formulário no enum; vazio -> null; inválido -> CampoException. */
    private TipoFornecedor lerTipo(String texto) {
        if (texto == null || texto.isBlank()) {
            return null; // o serviço diz "Escolha o tipo de fornecedor."
        }
        try {
            return TipoFornecedor.valueOf(texto.trim());
        } catch (IllegalArgumentException e) {
            throw new CampoException("tipo", "Tipo de fornecedor inválido.");
        }
    }
}
