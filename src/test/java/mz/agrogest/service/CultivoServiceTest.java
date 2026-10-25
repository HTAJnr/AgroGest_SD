package mz.agrogest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Colheita;
import mz.agrogest.model.Cultivo;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.CampoException;
import mz.agrogest.util.NegocioException;

class CultivoServiceTest {

    private final CultivoService cultivoService = CultivoService.getInstance();
    private final CampanhaService campanhaService = CampanhaService.getInstance();
    private final DataStore ds = DataStore.getInstance();

    @BeforeEach
    void limpar() {
        synchronized (ds.getLock()) {
            ds.getCampanhas().clear();
            ds.getCultivos().clear();
            ds.getColheitas().clear();
        }
    }

    private Campanha criarCampanhaAberta(String nome) {
        return campanhaService.criar(nome, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
    }

    @Test
    void criarCultivoValido() {
        Campanha camp = criarCampanhaAberta("Campanha Principal 2026");
        LocalDate plantio = LocalDate.of(2026, 2, 15);

        Cultivo c = cultivoService.criar(camp.getId(), "Milho João Açúcar", "PAN 53", 15.5, plantio);

        assertNotNull(c);
        assertTrue(c.getId() > 0);
        assertEquals(camp.getId(), c.getCampanhaId());
        assertEquals("Milho João Açúcar", c.getCultura());
        assertEquals("PAN 53", c.getVariedade());
        assertEquals(15.5, c.getHectares());
        assertEquals(plantio, c.getPlantio());
        assertEquals("15/02/2026", c.getPlantioFmt());
    }

    @Test
    void criarCultivoSemVariedadeAceite() {
        Campanha camp = criarCampanhaAberta("Campanha Aberta");
        LocalDate plantio = LocalDate.of(2026, 3, 1);

        Cultivo c = cultivoService.criar(camp.getId(), "Feijão nhemba", null, 5.0, plantio);
        assertNotNull(c);
        assertEquals("", c.getVariedade());

        Cultivo c2 = cultivoService.criar(camp.getId(), "Mandioca", "   ", 8.0, plantio);
        assertNotNull(c2);
        assertEquals("", c2.getVariedade());
    }

    @Test
    void criarCultivoComVariedadeInvalidaFalha() {
        Campanha camp = criarCampanhaAberta("Campanha Aberta");
        LocalDate plantio = LocalDate.of(2026, 3, 1);

        // Curta (< 3 caracteres)
        CampoException ex1 = assertThrows(CampoException.class, () ->
            cultivoService.criar(camp.getId(), "Milho", "AB", 5.0, plantio)
        );
        assertEquals("variedade", ex1.getCampo());

        // Só dígitos (sem letras)
        CampoException ex2 = assertThrows(CampoException.class, () ->
            cultivoService.criar(camp.getId(), "Milho", "12345", 5.0, plantio)
        );
        assertEquals("variedade", ex2.getCampo());
    }

    @Test
    void criarCultivoCampanhaInexistenteFalha() {
        LocalDate plantio = LocalDate.of(2026, 2, 1);

        CampoException ex = assertThrows(CampoException.class, () ->
            cultivoService.criar(9999, "Milho", "Matuba", 10.0, plantio)
        );
        assertEquals("campanhaId", ex.getCampo());
        assertTrue(ex.getMessage().contains("não existe"));
    }

    @Test
    void criarCultivoCampanhaFechadaFalhaD5() {
        Campanha camp = criarCampanhaAberta("Campanha Fechada");
        campanhaService.fechar(camp.getId());

        LocalDate plantio = LocalDate.of(2026, 2, 1);

        NegocioException ex = assertThrows(NegocioException.class, () ->
            cultivoService.criar(camp.getId(), "Milho", "Matuba", 10.0, plantio)
        );
        assertTrue(ex.getMessage().contains("fechada"));
    }

    @Test
    void criarCultivoCulturaInvalidaFalha() {
        Campanha camp = criarCampanhaAberta("Campanha Aberta");
        LocalDate plantio = LocalDate.of(2026, 2, 1);

        // Vazia
        CampoException exVazia = assertThrows(CampoException.class, () ->
            cultivoService.criar(camp.getId(), "   ", "Matuba", 10.0, plantio)
        );
        assertEquals("cultura", exVazia.getCampo());

        // Curta (< 3)
        CampoException exCurta = assertThrows(CampoException.class, () ->
            cultivoService.criar(camp.getId(), "AB", "Matuba", 10.0, plantio)
        );
        assertEquals("cultura", exCurta.getCampo());

        // Só números
        CampoException exDigitos = assertThrows(CampoException.class, () ->
            cultivoService.criar(camp.getId(), "12345", "Matuba", 10.0, plantio)
        );
        assertEquals("cultura", exDigitos.getCampo());
    }

    @Test
    void criarCultivoHectaresInvalidosFalha() {
        Campanha camp = criarCampanhaAberta("Campanha Aberta");
        LocalDate plantio = LocalDate.of(2026, 2, 1);

        // Zero
        CampoException exZero = assertThrows(CampoException.class, () ->
            cultivoService.criar(camp.getId(), "Milho", "Matuba", 0.0, plantio)
        );
        assertEquals("hectares", exZero.getCampo());

        // Negativo
        CampoException exNegativo = assertThrows(CampoException.class, () ->
            cultivoService.criar(camp.getId(), "Milho", "Matuba", -2.5, plantio)
        );
        assertEquals("hectares", exNegativo.getCampo());
    }

    @Test
    void criarCultivoPlantioNuloFalha() {
        Campanha camp = criarCampanhaAberta("Campanha Aberta");

        CampoException ex = assertThrows(CampoException.class, () ->
            cultivoService.criar(camp.getId(), "Milho", "Matuba", 10.0, null)
        );
        assertEquals("plantio", ex.getCampo());
        assertTrue(ex.getMessage().contains("obrigatória"));
    }

    @Test
    void listarDevolveCopiaNaoListaViva() {
        Campanha camp = criarCampanhaAberta("Campanha Aberta");
        cultivoService.criar(camp.getId(), "Milho", "Matuba", 10.0, LocalDate.of(2026, 2, 1));

        List<Cultivo> lista = cultivoService.listar();
        assertEquals(1, lista.size());

        lista.clear();
        assertEquals(1, cultivoService.listar().size());
    }

    @Test
    void listarPorCampanhaFiltraCorretamente() {
        Campanha camp1 = criarCampanhaAberta("Campanha 1");
        Campanha camp2 = criarCampanhaAberta("Campanha 2");

        cultivoService.criar(camp1.getId(), "Milho", "Matuba", 10.0, LocalDate.of(2026, 2, 1));
        cultivoService.criar(camp1.getId(), "Feijão", "", 5.0, LocalDate.of(2026, 2, 5));
        cultivoService.criar(camp2.getId(), "Algodão", "", 20.0, LocalDate.of(2026, 3, 1));

        assertEquals(2, cultivoService.listarPorCampanha(camp1.getId()).size());
        assertEquals(1, cultivoService.listarPorCampanha(camp2.getId()).size());
        assertEquals(0, cultivoService.listarPorCampanha(9999).size());
    }

    @Test
    void porIdDevolveCorretoOuNull() {
        Campanha camp = criarCampanhaAberta("Campanha Aberta");
        Cultivo c = cultivoService.criar(camp.getId(), "Milho", "Matuba", 10.0, LocalDate.of(2026, 2, 1));

        Cultivo encontrado = cultivoService.porId(c.getId());
        assertNotNull(encontrado);
        assertEquals("Milho", encontrado.getCultura());

        assertNull(cultivoService.porId(9999));
    }

    @Test
    void removerCultivoSemColheitas() {
        Campanha camp = criarCampanhaAberta("Campanha Aberta");
        Cultivo c = cultivoService.criar(camp.getId(), "Milho", "Matuba", 10.0, LocalDate.of(2026, 2, 1));

        assertEquals(1, cultivoService.listar().size());
        cultivoService.remover(c.getId());
        assertEquals(0, cultivoService.listar().size());
        assertNull(cultivoService.porId(c.getId()));
    }

    @Test
    void removerCultivoComColheitasBloqueado() {
        Campanha camp = criarCampanhaAberta("Campanha Aberta");
        Cultivo c = cultivoService.criar(camp.getId(), "Milho", "Matuba", 10.0, LocalDate.of(2026, 2, 1));

        // Adicionar colheita associada ao cultivo
        Colheita col = new Colheita();
        col.setId(ds.novoIdColheita());
        col.setCultivoId(c.getId());
        col.setData(LocalDate.of(2026, 6, 1));
        col.setKg(1000);
        synchronized (ds.getLock()) {
            ds.getColheitas().add(col);
        }

        NegocioException ex = assertThrows(NegocioException.class, () ->
            cultivoService.remover(c.getId())
        );
        assertTrue(ex.getMessage().contains("tem colheitas associadas"));
        assertNotNull(cultivoService.porId(c.getId()));
    }

    @Test
    void removerCultivoInexistenteFalha() {
        assertThrows(NegocioException.class, () -> cultivoService.remover(9999));
    }
}
