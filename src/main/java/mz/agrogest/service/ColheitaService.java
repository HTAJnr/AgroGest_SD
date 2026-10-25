package mz.agrogest.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Colheita;
import mz.agrogest.model.Cultivo;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.CampoException;
import mz.agrogest.util.NegocioException;
import mz.agrogest.util.Validacao;

/**
 * Regras de negócio das colheitas agrícolas (registar, listar, procurar).
 * Sem remoção (o contrato não a prevê).
 * Toda a escrita corre dentro do monitor único do DataStore (contrato, secção 5).
 */
public final class ColheitaService {

    private static final ColheitaService INSTANCE = new ColheitaService();

    private ColheitaService() {
    }

    public static ColheitaService getInstance() {
        return INSTANCE;
    }

    /**
     * Regista uma nova colheita associada a um cultivo de uma campanha aberta (D5).
     * Validações:
     * - cultivo existe;
     * - campanha do cultivo existe e está aberta (D5);
     * - data obrigatória;
     * - kg > 0;
     * - precoKg > 0.
     */
    public Colheita registar(int cultivoId, LocalDate data, double kg, BigDecimal precoKg) {
        if (data == null) {
            throw new CampoException("data", "A data da colheita é obrigatória.");
        }
        Validacao.kg("kg", kg);
        Validacao.precoKg("precoKg", precoKg);

        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            Cultivo cultivo = null;
            for (Cultivo c : ds.getCultivos()) {
                if (c.getId() == cultivoId) {
                    cultivo = c;
                    break;
                }
            }
            if (cultivo == null) {
                throw new CampoException("cultivoId", "O cultivo selecionado não existe.");
            }

            Campanha camp = null;
            for (Campanha c : ds.getCampanhas()) {
                if (c.getId() == cultivo.getCampanhaId()) {
                    camp = c;
                    break;
                }
            }
            if (camp == null) {
                throw new NegocioException("A campanha associada ao cultivo não foi encontrada.");
            }
            if (!camp.isAberta()) {
                throw new NegocioException("Não é possível registar colheitas numa campanha fechada.");
            }

            Colheita colheita = new Colheita();
            colheita.setId(ds.novoIdColheita());
            colheita.setCultivoId(cultivoId);
            colheita.setData(data);
            colheita.setKg(kg);
            colheita.setPrecoKg(precoKg.setScale(2, RoundingMode.HALF_UP));

            ds.getColheitas().add(colheita);
            return colheita;
        }
    }

    /** Devolve uma CÓPIA da lista de todas as colheitas (nunca a lista viva). */
    public List<Colheita> listar() {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            return new ArrayList<>(ds.getColheitas());
        }
    }

    /** Devolve uma CÓPIA da lista de colheitas associadas ao cultivo especificado. */
    public List<Colheita> listarPorCultivo(int cultivoId) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            List<Colheita> res = new ArrayList<>();
            for (Colheita c : ds.getColheitas()) {
                if (c.getCultivoId() == cultivoId) {
                    res.add(c);
                }
            }
            return res;
        }
    }

    /** Devolve a colheita com o ID indicado, ou null se não existir. */
    public Colheita porId(int id) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            for (Colheita c : ds.getColheitas()) {
                if (c.getId() == id) {
                    return c;
                }
            }
            return null;
        }
    }
}
