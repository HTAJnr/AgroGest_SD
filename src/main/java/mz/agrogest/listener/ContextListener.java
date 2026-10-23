package mz.agrogest.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import mz.agrogest.repo.Persistencia;
import mz.agrogest.service.UtilizadorService;

/** Arranque: restaura dados e garante o admin. Encerramento: grava. */
@WebListener
public class ContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(ContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Persistencia.restaurar(); // nunca lança excepção
        try {
            UtilizadorService.getInstance().garantirAdmin();
        } catch (RuntimeException ex) {
            LOG.log(Level.SEVERE, "Falha ao garantir o administrador", ex);
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
