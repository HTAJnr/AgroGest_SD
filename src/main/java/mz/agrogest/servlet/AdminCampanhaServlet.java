package mz.agrogest.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mz.agrogest.service.CampanhaService;
import mz.agrogest.util.CampoException;
import mz.agrogest.util.NegocioException;

/**
 * Controlador de /admin/campanhas/*
 *   GET  /admin/campanhas         -> lista.jsp
 *   GET  /admin/campanhas/novo    -> form.jsp
 *   POST /admin/campanhas         -> cria campanha (ou /criar)
 *   POST /admin/campanhas/fechar  -> fecha campanha
 *   POST /admin/campanhas/reabrir -> reabre campanha
 *   POST /admin/campanhas/remover -> remove campanha
 * O acesso só para ADMIN é garantido pelo AuthFilter.
 */
@WebServlet("/admin/campanhas/*")
public class AdminCampanhaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String LISTA = "/WEB-INF/views/admin/campanhas/lista.jsp";
    private static final String FORM = "/WEB-INF/views/admin/campanhas/form.jsp";
    private static final String BASE = "/admin/campanhas";

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private final CampanhaService servico = CampanhaService.getInstance();

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

        switch (path) {
            case "/criar":
            case "":
            case "/":
                criar(req, resp);
                break;
            case "/fechar":
                fechar(req, resp);
                break;
            case "/reabrir":
                reabrir(req, resp);
                break;
            case "/remover":
                remover(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void criar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String nome = texto(req, "nome");
            LocalDate inicio = parseData(req.getParameter("inicio"), "inicio", "início");
            LocalDate fim = parseData(req.getParameter("fim"), "fim", "fim");
            servico.criar(nome, inicio, fim);
            ok(req, resp, "Campanha criada com sucesso.");
        } catch (NegocioException e) {
            SessaoUtil.erro(req, e);
            mostrarForm(req, resp); // o formulário mantém o que foi escrito (${param.x})
        } catch (DateTimeParseException e) {
            SessaoUtil.erro(req, "inicio", "Data inválida.");
            mostrarForm(req, resp);
        } catch (IllegalArgumentException e) {
            SessaoUtil.erro(req, "nome", "Dados inválidos.");
            mostrarForm(req, resp);
        }
    }

    private void fechar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            int id = Integer.parseInt(texto(req, "id"));
            servico.fechar(id);
            ok(req, resp, "Campanha fechada com sucesso.");
        } catch (NegocioException e) {
            erroLista(req, resp, e.getMessage());
        } catch (NumberFormatException e) {
            erroLista(req, resp, "Identificador de campanha inválido.");
        } catch (IllegalArgumentException e) {
            erroLista(req, resp, "Pedido inválido.");
        }
    }

    private void reabrir(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            int id = Integer.parseInt(texto(req, "id"));
            servico.reabrir(id);
            ok(req, resp, "Campanha reaberta com sucesso.");
        } catch (NegocioException e) {
            erroLista(req, resp, e.getMessage());
        } catch (NumberFormatException e) {
            erroLista(req, resp, "Identificador de campanha inválido.");
        } catch (IllegalArgumentException e) {
            erroLista(req, resp, "Pedido inválido.");
        }
    }

    private void remover(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            int id = Integer.parseInt(texto(req, "id"));
            servico.remover(id);
            ok(req, resp, "Campanha removida com sucesso.");
        } catch (NegocioException e) {
            erroLista(req, resp, e.getMessage());
        } catch (NumberFormatException e) {
            erroLista(req, resp, "Identificador de campanha inválido.");
        } catch (IllegalArgumentException e) {
            erroLista(req, resp, "Pedido inválido.");
        }
    }

    private void mostrarLista(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("titulo", "Campanhas");
        req.setAttribute("campanhas", servico.listar());
        req.getRequestDispatcher(LISTA).forward(req, resp);
    }

    private void mostrarForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("titulo", "Nova campanha");
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

    private static LocalDate parseData(String v, String campo, String nomeAmigavel) {
        if (v == null || v.isBlank()) {
            throw new CampoException(campo, "A data de " + nomeAmigavel + " é obrigatória.");
        }
        v = v.trim();
        try {
            if (v.contains("/")) {
                return LocalDate.parse(v, DATA_BR);
            }
            return LocalDate.parse(v);
        } catch (DateTimeParseException e) {
            throw new CampoException(campo, "Data de " + nomeAmigavel + " inválida.");
        }
    }
}
