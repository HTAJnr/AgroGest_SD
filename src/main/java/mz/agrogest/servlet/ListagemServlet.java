package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mz.agrogest.service.ListagemService;

/**
 * /admin/listagens/{produtos|fornecedores|clientes|armazem|compras}
 * Ordenação por ?ordenar=campo&dir=asc|desc. Só leitura.
 */
@WebServlet("/admin/listagens/*")
public class ListagemServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String p = req.getPathInfo();
        if (p == null || p.equals("/")) {
            p = "/produtos";
        }

        String ordenar = req.getParameter("ordenar");
        String dirParam = req.getParameter("dir");
        boolean asc = !"desc".equalsIgnoreCase(dirParam);

        ListagemService ls = ListagemService.getInstance();
        String vista;
        String titulo;
        Object lista;

        switch (p) {
            case "/produtos":
                vista = "produtos";
                titulo = "Listagem de produtos";
                lista = ls.produtos(ordenar, asc);
                req.setAttribute("nomesFornecedores", ls.nomesFornecedores());
                break;
            case "/armazem":
                vista = "armazem";
                titulo = "Produtos em armazém";
                lista = ls.armazem(ordenar, asc);
                req.setAttribute("nomesFornecedores", ls.nomesFornecedores());
                break;
            case "/fornecedores":
                vista = "fornecedores";
                titulo = "Listagem de fornecedores";
                lista = ls.fornecedores(ordenar, asc);
                break;
            case "/clientes":
                vista = "clientes";
                titulo = "Listagem de clientes";
                lista = ls.clientes(ordenar, asc);
                break;
            case "/compras":
            case "/compras-detalhadas":
                vista = "compras";
                titulo = "Compras detalhadas";
                if (ordenar == null) {
                    ordenar = "data";
                    if (dirParam == null) {
                        asc = false; // mais recentes primeiro por omissão
                    }
                }
                lista = ls.compras(ordenar, asc);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }

        if (ordenar == null) {
            ordenar = "nome";
        }

        req.setAttribute("vista", vista);
        req.setAttribute("lista", lista);
        req.setAttribute("ordenar", ordenar);
        req.setAttribute("dir", asc ? "asc" : "desc");
        req.setAttribute("titulo", titulo);
        req.getRequestDispatcher("/WEB-INF/views/admin/listagens/index.jsp").forward(req, resp);
    }
}
