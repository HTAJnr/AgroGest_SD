package mz.agrogest.repo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Colheita;
import mz.agrogest.model.Cultivo;
import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.Produto;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;

/**
 * Grava e restaura o estado completo do DataStore (listas + contadores de ids)
 * num único ficheiro, com escrita atómica (contrato D10).
 */
public final class Persistencia {

    private static final Logger LOG = Logger.getLogger(Persistencia.class.getName());
    private static final int NUM_CONTADORES = 7;

    private Persistencia() {
    }

    /** Tudo o que vai para o ficheiro, num só objecto. */
    private static final class Estado implements Serializable {
        private static final long serialVersionUID = 1L;
        ArrayList<Utilizador> utilizadores;
        ArrayList<Fornecedor> fornecedores;
        ArrayList<Produto> produtos;
        ArrayList<Campanha> campanhas;
        ArrayList<Cultivo> cultivos;
        ArrayList<Colheita> colheitas;
        ArrayList<Venda> vendas;
        int[] contadores;
    }

    /** ${catalina.base}/data/agrogest.ser (se não houver catalina.base, usa a pasta actual). */
    public static Path ficheiro() {
        String base = System.getProperty("catalina.base");
        if (base == null || base.isBlank()) {
            base = System.getProperty("user.dir");
        }
        return Paths.get(base, "data", "agrogest.ser");
    }

    /**
     * Grava o estado. Escreve em .ser.tmp, força para disco e só depois troca
     * atomicamente pelo ficheiro final: se o processo morrer a meio, o ficheiro
     * anterior continua intacto.
     */
    public static void gravar() throws IOException {
        Path destino = ficheiro();
        Path tmp = destino.resolveSibling(destino.getFileName() + ".tmp");
        DataStore ds = DataStore.getInstance();

        synchronized (ds.getLock()) {
            Files.createDirectories(destino.getParent());

            Estado e = new Estado();
            e.utilizadores = new ArrayList<>(ds.getUtilizadores());
            e.fornecedores = new ArrayList<>(ds.getFornecedores());
            e.produtos = new ArrayList<>(ds.getProdutos());
            e.campanhas = new ArrayList<>(ds.getCampanhas());
            e.cultivos = new ArrayList<>(ds.getCultivos());
            e.colheitas = new ArrayList<>(ds.getColheitas());
            e.vendas = new ArrayList<>(ds.getVendas());
            e.contadores = ds.getContadores();

            try (FileOutputStream fos = new FileOutputStream(tmp.toFile());
                 ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos))) {
                oos.writeObject(e);
                oos.flush();
                fos.getFD().sync();
            } catch (IOException | RuntimeException ex) {
                try {
                    Files.deleteIfExists(tmp);
                } catch (IOException ignorado) {
                    // nada a fazer
                }
                throw ex;
            }

            Files.move(tmp, destino, StandardCopyOption.ATOMIC_MOVE);
        }
        LOG.info("Dados gravados em " + destino);
    }

    /**
     * Restaura o estado. Devolve true se restaurou, false se não havia ficheiro
     * ou se estava corrompido (nesse caso renomeia-o para .ser.bak e o sistema
     * arranca vazio). Nunca lança excepção: não pode impedir o arranque.
     */
    public static boolean restaurar() {
        Path f = ficheiro();
        if (!Files.exists(f)) {
            LOG.info("Sem ficheiro de dados em " + f + ": arranque vazio.");
            return false;
        }

        // Lê tudo para um objecto à parte; só toca no DataStore se a leitura correr bem.
        Estado e;
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(f)))) {
            e = (Estado) ois.readObject();
            if (e == null || e.utilizadores == null || e.fornecedores == null || e.produtos == null
                    || e.campanhas == null || e.cultivos == null || e.colheitas == null
                    || e.vendas == null || e.contadores == null || e.contadores.length != NUM_CONTADORES) {
                throw new IOException("Ficheiro de dados incompleto");
            }
        } catch (IOException | ClassNotFoundException | RuntimeException ex) {
            LOG.log(Level.SEVERE, "Ficheiro de dados corrompido ou incompatível; a arrancar vazio.", ex);
            quarentena(f);
            return false;
        }

        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            ds.getUtilizadores().clear();
            ds.getUtilizadores().addAll(e.utilizadores);
            ds.getFornecedores().clear();
            ds.getFornecedores().addAll(e.fornecedores);
            ds.getProdutos().clear();
            ds.getProdutos().addAll(e.produtos);
            ds.getCampanhas().clear();
            ds.getCampanhas().addAll(e.campanhas);
            ds.getCultivos().clear();
            ds.getCultivos().addAll(e.cultivos);
            ds.getColheitas().clear();
            ds.getColheitas().addAll(e.colheitas);
            ds.getVendas().clear();
            ds.getVendas().addAll(e.vendas);
            ds.setContadores(e.contadores);
        }
        LOG.info("Dados restaurados de " + f);
        return true;
    }

    /** Apaga o ficheiro (não limpa a memória: contrato D10). Devolve true se existia. */
    public static boolean eliminar() throws IOException {
        Path f = ficheiro();
        synchronized (DataStore.getInstance().getLock()) {
            Files.deleteIfExists(f.resolveSibling(f.getFileName() + ".tmp"));
            return Files.deleteIfExists(f);
        }
    }

    private static void quarentena(Path f) {
        Path bak = f.resolveSibling(f.getFileName() + ".bak");
        try {
            Files.move(f, bak, StandardCopyOption.REPLACE_EXISTING);
            LOG.warning("Ficheiro corrompido movido para " + bak);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Não consegui renomear o ficheiro corrompido.", ex);
        }
    }
}
