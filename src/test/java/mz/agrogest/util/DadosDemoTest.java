package mz.agrogest.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import mz.agrogest.repo.DataStore;

class DadosDemoTest {

    @Test
    void carregarSeVazio_quandoVazio_populaDadosESegundaChamadaNaoDuplica() {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            ds.getUtilizadores().clear();
            ds.getFornecedores().clear();
            ds.getProdutos().clear();
            ds.getCampanhas().clear();
            ds.getCultivos().clear();
            ds.getColheitas().clear();
            ds.getVendas().clear();
        }

        DadosDemo.carregarSeVazio();

        synchronized (ds.getLock()) {
            assertEquals(3, ds.getFornecedores().size());
            assertEquals(15, ds.getProdutos().size());
            assertEquals(5, ds.getUtilizadores().size());
            assertEquals(1, ds.getCampanhas().size());
            assertEquals(1, ds.getCultivos().size());
            assertEquals(1, ds.getColheitas().size());
            assertEquals(4, ds.getVendas().size());

            // Cada produto tem nome, categoria, foto local em assets e preço positivo
            ds.getProdutos().forEach(p -> {
                assertNotNull(p.getNome());
                assertNotNull(p.getCategoria());
                assertNotNull(p.getFotoUrl());
                assertFalse(p.getFotoUrl().isBlank());
                assertTrue(p.getPreco().doubleValue() > 0);
            });
        }

        int totalProdutosAntes = ds.getProdutos().size();
        // Segunda chamada não pode duplicar
        DadosDemo.carregarSeVazio();
        assertEquals(totalProdutosAntes, ds.getProdutos().size());
    }
}
