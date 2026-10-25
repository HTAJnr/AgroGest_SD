package mz.agrogest.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import mz.agrogest.repo.Persistencia;
import mz.agrogest.service.UtilizadorService;
import mz.agrogest.util.DadosDemo;

/** Arranque: restaura dados e garante o admin. Encerramento: grava. */
@WebListener
public class ContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(ContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        boolean restaurou = Persistencia.restaurar(); // nunca lança excepção
        try {
            UtilizadorService.getInstance().garantirAdmin();
        } catch (RuntimeException ex) {
            LOG.log(Level.SEVERE, "Falha ao garantir o administrador", ex);
        }
        if (!restaurou) {
            DadosDemo.carregarSeVazio();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            Persistencia.gravar();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Falha ao gravar os dados no encerramento", ex);
        }
    }
}
