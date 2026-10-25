package mz.agrogest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
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

class ColheitaServiceTest {

    private final ColheitaService colheitaService = ColheitaService.getInstance();
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

    private Cultivo criarCultivo(int campanhaId, String cultura) {
        return cultivoService.criar(campanhaId, cultura, "Variedade Teste", 10.0, LocalDate.of(2026, 2, 1));
    }

    @Test
    void registarColheitaValidaCalculaTotalCorretamente() {
        Campanha camp = criarCampanhaAberta("Campanha 2026");
        Cultivo cult = criarCultivo(camp.getId(), "Milho");
        LocalDate data = LocalDate.of(2026, 5, 20);

        // Caso especificado na tarefa F3: 3200 kg × 22 MT/kg = 70 400,00 MT
        Colheita c = colheitaService.registar(cult.getId(), data, 3200.0, new BigDecimal("22.00"));

        assertNotNull(c);
        assertTrue(c.getId() > 0);
        assertEquals(cult.getId(), c.getCultivoId());
        assertEquals(data, c.getData());
        assertEquals("20/05/2026", c.getDataFmt());
        assertEquals(3200.0, c.getKg());
        assertEquals(new BigDecimal("22.00"), c.getPrecoKg());
        assertEquals(new BigDecimal("70400.00"), c.getValorTotal());
    }

    @Test
    void registarDataNulaFalha() {
        Campanha camp = criarCampanhaAberta("Campanha 2026");
        Cultivo cult = criarCultivo(camp.getId(), "Milho");

        CampoException ex = assertThrows(CampoException.class, () ->
            colheitaService.registar(cult.getId(), null, 1000.0, new BigDecimal("15.00"))
        );
        assertEquals("data", ex.getCampo());
        assertTrue(ex.getMessage().contains("obrigatória"));
    }

    @Test
    void registarKgInvalidoFalha() {
        Campanha camp = criarCampanhaAberta("Campanha 2026");
        Cultivo cult = criarCultivo(camp.getId(), "Milho");
        LocalDate data = LocalDate.of(2026, 5, 20);

        CampoException exZero = assertThrows(CampoException.class, () ->
            colheitaService.registar(cult.getId(), data, 0.0, new BigDecimal("20.00"))
        );
        assertEquals("kg", exZero.getCampo());

        CampoException exNeg = assertThrows(CampoException.class, () ->
            colheitaService.registar(cult.getId(), data, -5.0, new BigDecimal("20.00"))
        );
        assertEquals("kg", exNeg.getCampo());
    }

    @Test
    void registarPrecoKgInvalidoFalha() {
        Campanha camp = criarCampanhaAberta("Campanha 2026");
        Cultivo cult = criarCultivo(camp.getId(), "Milho");
        LocalDate data = LocalDate.of(2026, 5, 20);

        CampoException exNull = assertThrows(CampoException.class, () ->
            colheitaService.registar(cult.getId(), data, 100.0, null)
        );
        assertEquals("precoKg", exNull.getCampo());

        CampoException exZero = assertThrows(CampoException.class, () ->
            colheitaService.registar(cult.getId(), data, 100.0, BigDecimal.ZERO)
        );
        assertEquals("precoKg", exZero.getCampo());

        CampoException exNeg = assertThrows(CampoException.class, () ->
            colheitaService.registar(cult.getId(), data, 100.0, new BigDecimal("-10.00"))
        );
        assertEquals("precoKg", exNeg.getCampo());
    }

    @Test
    void registarCultivoInexistenteFalha() {
        LocalDate data = LocalDate.of(2026, 5, 20);

        CampoException ex = assertThrows(CampoException.class, () ->
            colheitaService.registar(9999, data, 500.0, new BigDecimal("10.00"))
        );
        assertEquals("cultivoId", ex.getCampo());
        assertTrue(ex.getMessage().contains("não existe"));
    }

    @Test
    void registarCampanhaFechadaFalhaD5() {
        Campanha camp = criarCampanhaAberta("Campanha Fechada");
        Cultivo cult = criarCultivo(camp.getId(), "Feijão");
        campanhaService.fechar(camp.getId());

        LocalDate data = LocalDate.of(2026, 6, 1);
        NegocioException ex = assertThrows(NegocioException.class, () ->
            colheitaService.registar(cult.getId(), data, 500.0, new BigDecimal("30.00"))
        );
        assertTrue(ex.getMessage().contains("fechada"));
    }

    @Test
    void listarRetornaCopiaProtegida() {
        Campanha camp = criarCampanhaAberta("Campanha 2026");
        Cultivo cult = criarCultivo(camp.getId(), "Milho");
        LocalDate data = LocalDate.of(2026, 5, 20);

        colheitaService.registar(cult.getId(), data, 100.0, new BigDecimal("20.00"));
        colheitaService.registar(cult.getId(), data, 200.0, new BigDecimal("25.00"));

        List<Colheita> copia = colheitaService.listar();
        assertEquals(2, copia.size());

        copia.clear();
        assertEquals(2, colheitaService.listar().size(), "A lista interna não deve ser afetada");
    }

    @Test
    void listarPorCultivoFiltraCorretamente() {
        Campanha camp = criarCampanhaAberta("Campanha 2026");
        Cultivo cult1 = criarCultivo(camp.getId(), "Milho");
        Cultivo cult2 = criarCultivo(camp.getId(), "Feijão");
        LocalDate data = LocalDate.of(2026, 5, 20);

        colheitaService.registar(cult1.getId(), data, 100.0, new BigDecimal("20.00"));
        colheitaService.registar(cult1.getId(), data, 200.0, new BigDecimal("25.00"));
        colheitaService.registar(cult2.getId(), data, 50.0, new BigDecimal("50.00"));

        List<Colheita> colheitas1 = colheitaService.listarPorCultivo(cult1.getId());
        assertEquals(2, colheitas1.size());

        List<Colheita> colheitas2 = colheitaService.listarPorCultivo(cult2.getId());
        assertEquals(1, colheitas2.size());

        List<Colheita> colheitasVazia = colheitaService.listarPorCultivo(9999);
        assertTrue(colheitasVazia.isEmpty());
    }

    @Test
    void porIdRetornaColheitaOuNull() {
        Campanha camp = criarCampanhaAberta("Campanha 2026");
        Cultivo cult = criarCultivo(camp.getId(), "Milho");
        LocalDate data = LocalDate.of(2026, 5, 20);

        Colheita c = colheitaService.registar(cult.getId(), data, 100.0, new BigDecimal("20.00"));

        Colheita encontrada = colheitaService.porId(c.getId());
        assertNotNull(encontrada);
        assertEquals(c.getId(), encontrada.getId());

        assertNull(colheitaService.porId(9999));
    }
}
