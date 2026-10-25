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

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Cultivo;
import mz.agrogest.service.CampanhaService;
import mz.agrogest.service.CultivoService;
import mz.agrogest.util.CampoException;
import mz.agrogest.util.NegocioException;

/**
 * Controlador de /admin/cultivos/*
 *   GET  /admin/cultivos                     -> sem campanhaId: lista de campanhas para escolher
 *   GET  /admin/cultivos?campanhaId={id}     -> cultivos da campanha e formulário de adição
 *   POST /admin/cultivos                     -> cria cultivo na campanha (ou /criar)
 *   POST /admin/cultivos/remover             -> remove cultivo
 * O acesso só para ADMIN é garantido pelo AuthFilter.
 */
@WebServlet(urlPatterns = {"/admin/cultivos", "/admin/cultivos/*"})
public class AdminCultivoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String VIEW_CAMPANHAS = "/WEB-INF/views/admin/cultivos/escolher-campanha.jsp";
    private static final String VIEW_CULTIVOS = "/WEB-INF/views/admin/cultivos/index.jsp";
    private static final String BASE = "/admin/cultivos";

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private final CultivoService servico = CultivoService.getInstance();
    private final CampanhaService campanhaService = CampanhaService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path != null && !path.isEmpty() && !"/".equals(path)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String campIdParam = req.getParameter("campanhaId");
        if (campIdParam == null || campIdParam.isBlank()) {
            mostrarEscolhaCampanha(req, resp);
            return;
        }

        int campanhaId;
        try {
            campanhaId = Integer.parseInt(campIdParam.trim());
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("flashErro", "Identificador de campanha inválido.");
            resp.sendRedirect(req.getContextPath() + BASE);
            return;
        }

        Campanha campanha = campanhaService.porId(campanhaId);
        if (campanha == null) {
            req.getSession().setAttribute("flashErro", "Campanha não encontrada.");
            resp.sendRedirect(req.getContextPath() + BASE);
            return;
        }

        mostrarCultivosCampanha(req, resp, campanha);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // acentos certos (João Açúcar)
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();

        switch (path) {
            case "":
            case "/":
            case "/criar":
                criar(req, resp);
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
        String campIdParam = req.getParameter("campanhaId");
        int campanhaId;
        try {
            campanhaId = Integer.parseInt(campIdParam == null ? "" : campIdParam.trim());
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("flashErro", "Identificador de campanha inválido.");
            resp.sendRedirect(req.getContextPath() + BASE);
            return;
        }

        Campanha campanha = campanhaService.porId(campanhaId);
        if (campanha == null) {
            req.getSession().setAttribute("flashErro", "Campanha não encontrada.");
            resp.sendRedirect(req.getContextPath() + BASE);
            return;
        }

        // Se a campanha estiver fechada, recusa imediatamente (D5)
        if (!campanha.isAberta()) {
            req.getSession().setAttribute("flashErro",
                    "A campanha «" + campanha.getNome() + "» está fechada e não aceita novos cultivos.");
            resp.sendRedirect(req.getContextPath() + BASE + "?campanhaId=" + campanhaId);
            return;
        }

        try {
            String cultura = texto(req, "cultura");
            String variedade = texto(req, "variedade");
            String hectaresStr = texto(req, "hectares").replace(',', '.');
            double hectares;
            try {
                hectares = Double.parseDouble(hectaresStr);
            } catch (NumberFormatException e) {
                throw new CampoException("hectares", "Introduza uma área em hectares válida.");
            }
            LocalDate plantio = parseData(req.getParameter("plantio"), "plantio", "plantio");

            servico.criar(campanhaId, cultura, variedade, hectares, plantio);
            req.getSession().setAttribute("flashOk", "Cultivo registado com sucesso.");
            resp.sendRedirect(req.getContextPath() + BASE + "?campanhaId=" + campanhaId);
        } catch (NegocioException e) {
            SessaoUtil.erro(req, e);
            mostrarCultivosCampanha(req, resp, campanha);
        } catch (DateTimeParseException e) {
            SessaoUtil.erro(req, "plantio", "Data de plantio inválida.");
            mostrarCultivosCampanha(req, resp, campanha);
        } catch (IllegalArgumentException e) {
            SessaoUtil.erro(req, "cultura", "Dados inválidos.");
            mostrarCultivosCampanha(req, resp, campanha);
        }
    }

    private void remover(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        int id;
        try {
            id = Integer.parseInt(texto(req, "id"));
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("flashErro", "Identificador de cultivo inválido.");
            resp.sendRedirect(req.getContextPath() + BASE);
            return;
        }

        int campanhaId = 0;
        Cultivo cult = servico.porId(id);
        if (cult != null) {
            campanhaId = cult.getCampanhaId();
        } else {
            try {
                campanhaId = Integer.parseInt(texto(req, "campanhaId"));
            } catch (NumberFormatException ignored) {
            }
        }

        try {
            servico.remover(id);
            req.getSession().setAttribute("flashOk", "Cultivo removido com sucesso.");
        } catch (NegocioException e) {
            req.getSession().setAttribute("flashErro", e.getMessage());
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("flashErro", "Pedido inválido.");
        }

        if (campanhaId > 0) {
            resp.sendRedirect(req.getContextPath() + BASE + "?campanhaId=" + campanhaId);
        } else {
            resp.sendRedirect(req.getContextPath() + BASE);
        }
    }

    private void mostrarEscolhaCampanha(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("titulo", "Cultivos — Selecionar campanha");
        req.setAttribute("campanhas", campanhaService.listar());
        req.getRequestDispatcher(VIEW_CAMPANHAS).forward(req, resp);
    }

    private void mostrarCultivosCampanha(HttpServletRequest req, HttpServletResponse resp, Campanha campanha)
            throws ServletException, IOException {
        req.setAttribute("titulo", "Cultivos — " + campanha.getNome());
        req.setAttribute("campanha", campanha);
        req.setAttribute("cultivos", servico.listarPorCampanha(campanha.getId()));
        req.getRequestDispatcher(VIEW_CULTIVOS).forward(req, resp);
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
