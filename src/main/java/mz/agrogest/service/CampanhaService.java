package mz.agrogest.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Cultivo;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.CampoException;
import mz.agrogest.util.NegocioException;
import mz.agrogest.util.Validacao;

/**
 * Regras de negócio das campanhas agrícolas (criar, listar, procurar, fechar, reabrir, remover).
 * Toda a escrita corre dentro do lock único do DataStore (contrato, secção 5).
 */
public final class CampanhaService {

    private static final CampanhaService INSTANCE = new CampanhaService();

    private CampanhaService() {
    }

    public static CampanhaService getInstance() {
        return INSTANCE;
    }

    /**
     * Cria uma nova campanha agrícola (nasce aberta com ID sequencial).
     * Valida nome (3–80 caracteres, pelo menos uma letra), datas obrigatórias,
     * fim >= início e unicidade de nome (D14).
     */
    public Campanha criar(String nome, LocalDate inicio, LocalDate fim) {
        String n = Validacao.nomeCoisa("nome", nome);
        if (inicio == null) {
            throw new CampoException("inicio", "A data de início é obrigatória.");
        }
        if (fim == null) {
            throw new CampoException("fim", "A data de fim é obrigatória.");
        }
        if (fim.isBefore(inicio)) {
            throw new CampoException("fim", "A data de fim não pode ser anterior à data de início.");
        }

        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            // D14: unicidade por nome (sem distinguir maiúsculas)
            for (Campanha c : ds.getCampanhas()) {
                if (n.equalsIgnoreCase(c.getNome())) {
                    throw new CampoException("nome",
                            "Já existe uma campanha com o nome «" + c.getNome() + "».");
                }
            }
            Campanha c = new Campanha();
            c.setId(ds.novoIdCampanha());
            c.setNome(n);
            c.setInicio(inicio);
            c.setFim(fim);
            c.setAberta(true);
            ds.getCampanhas().add(c);
            return c;
        }
    }

    /** Devolve uma CÓPIA da lista (nunca a lista viva). */
    public List<Campanha> listar() {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            return new ArrayList<>(ds.getCampanhas());
        }
    }

    /** Devolve null se não existir. */
    public Campanha porId(int id) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            for (Campanha c : ds.getCampanhas()) {
                if (c.getId() == id) {
                    return c;
                }
            }
            return null;
        }
    }

    /** Fecha a campanha dentro do lock. */
    public void fechar(int id) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            Campanha c = porId(id);
            if (c == null) {
                throw new NegocioException("Campanha não encontrada.");
            }
            c.setAberta(false);
        }
    }

    /** Reabre a campanha dentro do lock. */
    public void reabrir(int id) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            Campanha c = porId(id);
            if (c == null) {
                throw new NegocioException("Campanha não encontrada.");
            }
            c.setAberta(true);
        }
    }

    /**
     * Remove a campanha se não tiver cultivos associados.
     * Recusa com mensagem explicativa se existir algum cultivo com campanhaId == id.
     */
    public void remover(int id) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            Campanha c = porId(id);
            if (c == null) {
                throw new NegocioException("Campanha não encontrada.");
            }
            for (Cultivo cult : ds.getCultivos()) {
                if (cult.getCampanhaId() == id) {
                    throw new NegocioException(
                            "Não é possível remover: esta campanha tem cultivos associados. "
                            + "Remova primeiro esses cultivos.");
                }
            }
            ds.getCampanhas().removeIf(camp -> camp.getId() == id);
        }
    }
}
