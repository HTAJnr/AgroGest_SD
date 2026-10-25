package mz.agrogest.service;

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
 * Regras de negócio dos cultivos agrícolas (criar, listar, procurar, remover).
 * Toda a escrita corre dentro do lock único do DataStore (contrato, secção 5).
 */
public final class CultivoService {

    private static final CultivoService INSTANCE = new CultivoService();

    private CultivoService() {
    }

    public static CultivoService getInstance() {
        return INSTANCE;
    }

    /**
     * Cria um novo cultivo agrícola associado a uma campanha aberta (D5).
     * Validações:
     * - campanha existe e está aberta;
     * - cultura (3–80 caracteres, pelo menos uma letra);
     * - variedade (opcional; se indicada, 3–80 caracteres e pelo menos uma letra);
     * - hectares > 0;
     * - data de plantio obrigatória.
     */
    public Cultivo criar(int campanhaId, String cultura, String variedade, double hectares, LocalDate plantio) {
        String cult = Validacao.nomeCoisa("cultura", cultura);
        String var = Validacao.normalizar(variedade);
        if (!var.isEmpty()) {
            var = Validacao.nomeCoisa("variedade", var);
        }
        Validacao.hectares("hectares", hectares);
        if (plantio == null) {
            throw new CampoException("plantio", "A data de plantio é obrigatória.");
        }

        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            Campanha camp = null;
            for (Campanha c : ds.getCampanhas()) {
                if (c.getId() == campanhaId) {
                    camp = c;
                    break;
                }
            }
            if (camp == null) {
                throw new CampoException("campanhaId", "A campanha selecionada não existe.");
            }
            if (!camp.isAberta()) {
                throw new NegocioException("Não é possível associar cultivos a uma campanha fechada.");
            }

            Cultivo cultivo = new Cultivo();
            cultivo.setId(ds.novoIdCultivo());
            cultivo.setCampanhaId(campanhaId);
            cultivo.setCultura(cult);
            cultivo.setVariedade(var);
            cultivo.setHectares(hectares);
            cultivo.setPlantio(plantio);

            ds.getCultivos().add(cultivo);
            return cultivo;
        }
    }

    /** Devolve uma CÓPIA da lista de todos os cultivos (nunca a lista viva). */
    public List<Cultivo> listar() {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            return new ArrayList<>(ds.getCultivos());
        }
    }

    /** Devolve uma CÓPIA da lista de cultivos associados à campanha especificada. */
    public List<Cultivo> listarPorCampanha(int campanhaId) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            List<Cultivo> res = new ArrayList<>();
            for (Cultivo c : ds.getCultivos()) {
                if (c.getCampanhaId() == campanhaId) {
                    res.add(c);
                }
            }
            return res;
        }
    }

    /** Devolve o cultivo com o ID indicado, ou null se não existir. */
    public Cultivo porId(int id) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            for (Cultivo c : ds.getCultivos()) {
                if (c.getId() == id) {
                    return c;
                }
            }
            return null;
        }
    }

    /**
     * Remove o cultivo se não tiver colheitas associadas.
     * Recusa com mensagem explicativa se existir alguma colheita com cultivoId == id.
     */
    public void remover(int id) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            Cultivo c = porId(id);
            if (c == null) {
                throw new NegocioException("Cultivo não encontrado.");
            }
            for (Colheita col : ds.getColheitas()) {
                if (col.getCultivoId() == id) {
                    throw new NegocioException(
                            "Não é possível remover: este cultivo tem colheitas associadas. "
                            + "Remova primeiro essas colheitas.");
                }
            }
            ds.getCultivos().removeIf(cult -> cult.getId() == id);
        }
    }
}
