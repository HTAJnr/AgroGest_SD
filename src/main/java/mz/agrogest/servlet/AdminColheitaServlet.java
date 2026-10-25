package mz.agrogest.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Cultivo;
import mz.agrogest.service.CampanhaService;
import mz.agrogest.service.ColheitaService;
import mz.agrogest.service.CultivoService;
import mz.agrogest.util.CampoException;
import mz.agrogest.util.NegocioException;

/**
 * Controlador de /admin/colheitas/*
 *   GET  /admin/colheitas                     -> sem cultivoId: lista de cultivos para escolher
 *   GET  /admin/colheitas?cultivoId={id}      -> colheitas do cultivo e formulário de registo
 *   POST /admin/colheitas                     -> regista colheita no cultivo (ou /criar)
 * Sem remoção (o contrato não a prevê).
 * O acesso só para ADMIN é garantido pelo AuthFilter.
 */
@WebServlet(urlPatterns = {"/admin/colheitas", "/admin/colheitas/*"})
public class AdminColheitaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String VIEW_CULTIVOS = "/WEB-INF/views/admin/colheitas/escolher-cultivo.jsp";
    private static final String VIEW_COLHEITAS = "/WEB-INF/views/admin/colheitas/index.jsp";
    private static final String BASE = "/admin/colheitas";

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private final ColheitaService servico = ColheitaService.getInstance();
    private final CultivoService cultivoService = CultivoService.getInstance();
    private final CampanhaService campanhaService = CampanhaService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path != null && !path.isEmpty() && !"/".equals(path)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String cultIdParam = req.getParameter("cultivoId");
        if (cultIdParam == null || cultIdParam.isBlank()) {
            mostrarEscolhaCultivo(req, resp);
            return;
        }

        int cultivoId;
        try {
            cultivoId = Integer.parseInt(cultIdParam.trim());
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("flashErro", "Identificador de cultivo inválido.");
            resp.sendRedirect(req.getContextPath() + BASE);
            return;
        }

        Cultivo cultivo = cultivoService.porId(cultivoId);
        if (cultivo == null) {
            req.getSession().setAttribute("flashErro", "Cultivo não encontrado.");
            resp.sendRedirect(req.getContextPath() + BASE);
            return;
        }

        Campanha campanha = campanhaService.porId(cultivo.getCampanhaId());
        mostrarColheitasCultivo(req, resp, cultivo, campanha);
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
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void criar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String cultIdParam = req.getParameter("cultivoId");
        int cultivoId;
        try {
            cultivoId = Integer.parseInt(cultIdParam == null ? "" : cultIdParam.trim());
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("flashErro", "Identificador de cultivo inválido.");
            resp.sendRedirect(req.getContextPath() + BASE);
            return;
        }

        Cultivo cultivo = cultivoService.porId(cultivoId);
        if (cultivo == null) {
            req.getSession().setAttribute("flashErro", "Cultivo não encontrado.");
            resp.sendRedirect(req.getContextPath() + BASE);
            return;
        }

        Campanha campanha = campanhaService.porId(cultivo.getCampanhaId());
        if (campanha != null && !campanha.isAberta()) {
            req.getSession().setAttribute("flashErro",
                    "A campanha «" + campanha.getNome() + "» está fechada e não aceita novas colheitas.");
            resp.sendRedirect(req.getContextPath() + BASE + "?cultivoId=" + cultivoId);
            return;
        }

        try {
            LocalDate data = parseData(req.getParameter("data"), "data", "colheita");

            String kgStr = texto(req, "kg").replace(',', '.');
            double kg;
            try {
                kg = Double.parseDouble(kgStr);
            } catch (NumberFormatException e) {
                throw new CampoException("kg", "Introduza uma quantidade em quilogramas válida.");
            }

            String precoStr = texto(req, "precoKg").replace(',', '.').replace(" ", "").replace("\u00a0", "");
            BigDecimal precoKg;
            try {
                precoKg = new BigDecimal(precoStr);
            } catch (Exception e) {
                throw new CampoException("precoKg", "Introduza um preço por quilograma válido.");
            }

            servico.registar(cultivoId, data, kg, precoKg);
            req.getSession().setAttribute("flashOk", "Colheita registada com sucesso.");
            resp.sendRedirect(req.getContextPath() + BASE + "?cultivoId=" + cultivoId);
        } catch (NegocioException e) {
            SessaoUtil.erro(req, e);
            mostrarColheitasCultivo(req, resp, cultivo, campanha);
        } catch (DateTimeParseException e) {
            SessaoUtil.erro(req, "data", "Data de colheita inválida.");
            mostrarColheitasCultivo(req, resp, cultivo, campanha);
        } catch (IllegalArgumentException e) {
            SessaoUtil.erro(req, "kg", "Dados inválidos.");
            mostrarColheitasCultivo(req, resp, cultivo, campanha);
        }
    }

    private void mostrarEscolhaCultivo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("titulo", "Colheitas — Selecionar cultivo");
        req.setAttribute("cultivos", cultivoService.listar());
        Map<Integer, Campanha> campanhasMap = new HashMap<>();
        for (Campanha c : campanhaService.listar()) {
            campanhasMap.put(c.getId(), c);
        }
        req.setAttribute("campanhasMap", campanhasMap);
        req.getRequestDispatcher(VIEW_CULTIVOS).forward(req, resp);
    }

    private void mostrarColheitasCultivo(HttpServletRequest req, HttpServletResponse resp,
            Cultivo cultivo, Campanha campanha) throws ServletException, IOException {
        String titulo = "Colheitas — " + cultivo.getCultura();
        if (cultivo.getVariedade() != null && !cultivo.getVariedade().isBlank()) {
            titulo += " (" + cultivo.getVariedade() + ")";
        }
        req.setAttribute("titulo", titulo);
        req.setAttribute("cultivo", cultivo);
        req.setAttribute("campanha", campanha);
        req.setAttribute("colheitas", servico.listarPorCultivo(cultivo.getId()));
        req.getRequestDispatcher(VIEW_COLHEITAS).forward(req, resp);
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
