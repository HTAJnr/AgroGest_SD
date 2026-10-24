package mz.agrogest.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import mz.agrogest.model.Categoria;
import mz.agrogest.model.Produto;
import mz.agrogest.model.TipoFornecedor;

/** Testes de validação (V1). */
class ValidacaoTest {

    // ---- telefoneMz ----

    @Test
    void telefone_12345678_recusado() {
        CampoException ex = assertThrows(CampoException.class,
                () -> Validacao.telefoneMz("telefone", "12345678"));
        assertEquals("telefone", ex.getCampo());
        assertTrue(ex.getMessage().contains("moçambicano"));
    }

    @Test
    void telefone_841234567_aceite() {
        String result = Validacao.telefoneMz("telefone", "841234567");
        assertEquals("+258 84 123 4567", result);
    }

    @Test
    void telefone_comPrefixoInternacional_aceite() {
        String result = Validacao.telefoneMz("telefone", "+258 84 123 4567");
        assertEquals("+258 84 123 4567", result);
    }

    @Test
    void telefone_fixo_21123456_aceite() {
        String result = Validacao.telefoneMz("telefone", "21123456");
        assertEquals("+258 21 123 456", result);
    }

    @Test
    void telefone_841234_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.telefoneMz("telefone", "841234"));
    }

    @Test
    void telefone_82_aceite() {
        assertNotNull(Validacao.telefoneMz("telefone", "821234567"));
    }

    @Test
    void telefone_83_aceite() {
        assertNotNull(Validacao.telefoneMz("telefone", "831234567"));
    }

    @Test
    void telefone_85_aceite() {
        assertNotNull(Validacao.telefoneMz("telefone", "851234567"));
    }

    @Test
    void telefone_86_aceite() {
        assertNotNull(Validacao.telefoneMz("telefone", "861234567"));
    }

    @Test
    void telefone_87_aceite() {
        assertNotNull(Validacao.telefoneMz("telefone", "871234567"));
    }

    @Test
    void telefone_81_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.telefoneMz("telefone", "811234567"));
    }

    @Test
    void telefone_00258_prefixo_aceite() {
        String result = Validacao.telefoneMz("telefone", "00258841234567");
        assertEquals("+258 84 123 4567", result);
    }

    // ---- nomePessoa ----

    @Test
    void nome_A_recusado() {
        CampoException ex = assertThrows(CampoException.class,
                () -> Validacao.nomePessoa("nome", "A"));
        assertEquals("nome", ex.getCampo());
        assertTrue(ex.getMessage().contains("3"));
    }

    @Test
    void nome_Ana_aceite() {
        assertEquals("Ana", Validacao.nomePessoa("nome", "Ana"));
    }

    @Test
    void nome_123_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.nomePessoa("nome", "123"));
    }

    @Test
    void nome_JoaoAcucar_aceite() {
        assertEquals("João Açúcar", Validacao.nomePessoa("nome", " João  Açúcar "));
    }

    @Test
    void nome_comApostrofo_aceite() {
        assertDoesNotThrow(() -> Validacao.nomePessoa("nome", "O'Brien"));
    }

    @Test
    void nome_comHifen_aceite() {
        assertDoesNotThrow(() -> Validacao.nomePessoa("nome", "Ana-Maria"));
    }

    // ---- password ----

    @Test
    void password_123_recusada() {
        assertThrows(CampoException.class,
                () -> Validacao.password("password", "123"));
    }

    @Test
    void password_1234_aceite() {
        assertDoesNotThrow(() -> Validacao.password("password", "1234"));
    }

    @Test
    void password_nula_recusada() {
        assertThrows(CampoException.class,
                () -> Validacao.password("password", null));
    }

    // ---- email ----

    @Test
    void email_valido() {
        assertEquals("joao@teste.mz", Validacao.email("email", "Joao@Teste.MZ"));
    }

    @Test
    void email_sem_arroba_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.email("email", "sem-arroba"));
    }

    @Test
    void email_tld_curto_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.email("email", "a@b.c"));
    }

    // ---- nomeCoisa ----

    @Test
    void nomeCoisa_curto_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.nomeCoisa("nome", "AB"));
    }

    @Test
    void nomeCoisa_soDigitos_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.nomeCoisa("nome", "12345"));
    }

    @Test
    void nomeCoisa_comLetra_aceite() {
        assertEquals("Adubo X1", Validacao.nomeCoisa("nome", " Adubo  X1 "));
    }

    // ---- endereco ----

    @Test
    void endereco_curto_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.endereco("endereco", "abc"));
    }

    @Test
    void endereco_valido() {
        assertEquals("Av. Eduardo Mondlane, Nampula",
                Validacao.endereco("endereco", "Av. Eduardo Mondlane, Nampula"));
    }

    // ---- unidade ----

    @Test
    void unidade_sacp_recusada() {
        assertThrows(CampoException.class,
                () -> Validacao.unidade("unidade", "sacp"));
    }

    @Test
    void unidade_kg_aceite() {
        assertEquals("kg", Validacao.unidade("unidade", "KG"));
    }

    @Test
    void unidade_saco_aceite() {
        assertEquals("saco", Validacao.unidade("unidade", "saco"));
    }

    // ---- provincia ----

    @Test
    void provincia_valida() {
        assertDoesNotThrow(() -> Validacao.provincia("provincia", "Nampula"));
    }

    @Test
    void provincia_invalida() {
        assertThrows(CampoException.class,
                () -> Validacao.provincia("provincia", "Lisboa"));
    }

    // ---- validade ----

    @Test
    void validade_passada_recusada() {
        assertThrows(CampoException.class,
                () -> Validacao.validade("validade", LocalDate.of(2024, 1, 1)));
    }

    @Test
    void validade_futura_aceite() {
        LocalDate futuro = LocalDate.now().plusDays(30);
        assertDoesNotThrow(() -> Validacao.validade("validade", futuro));
    }

    @Test
    void validade_nula_aceite() {
        assertDoesNotThrow(() -> Validacao.validade("validade", null));
    }

    // ---- precoPositivo ----

    @Test
    void preco_zero_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.precoPositivo("preco", BigDecimal.ZERO));
    }

    @Test
    void preco_negativo_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.precoPositivo("preco", new BigDecimal("-10")));
    }

    @Test
    void preco_3casas_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.precoPositivo("preco", new BigDecimal("1.999")));
    }

    @Test
    void preco_valido() {
        assertDoesNotThrow(
                () -> Validacao.precoPositivo("preco", new BigDecimal("45.00")));
    }

    // ---- compatibilidade D13 ----

    @Test
    void compatibilidade_semente_com_maquinaria_recusada() {
        assertThrows(CampoException.class, () ->
                Validacao.compatibilidadeCategoria(
                        Categoria.SEMENTE, TipoFornecedor.MAQUINARIA, "Tractores Lda"));
    }

    @Test
    void compatibilidade_semente_com_sementes_aceite() {
        assertDoesNotThrow(() ->
                Validacao.compatibilidadeCategoria(
                        Categoria.SEMENTE, TipoFornecedor.SEMENTES, "Sementes SA"));
    }

    @Test
    void compatibilidade_ferramenta_com_maquinaria_aceite() {
        assertDoesNotThrow(() ->
                Validacao.compatibilidadeCategoria(
                        Categoria.FERRAMENTA, TipoFornecedor.MAQUINARIA, "Máquinas Lda"));
    }

    @Test
    void compatibilidade_fertilizante_com_fertilizantes_aceite() {
        assertDoesNotThrow(() ->
                Validacao.compatibilidadeCategoria(
                        Categoria.FERTILIZANTE, TipoFornecedor.FERTILIZANTES, "Fert SA"));
    }

    // ---- quantidadePositiva (reposição) ----

    @Test
    void quantidade_zero_para_reposicao_recusada() {
        assertThrows(CampoException.class,
                () -> Validacao.quantidadePositiva("quantidade", 0));
    }

    @Test
    void quantidade_negativa_para_reposicao_recusada() {
        assertThrows(CampoException.class,
                () -> Validacao.quantidadePositiva("quantidade", -5));
    }

    @Test
    void quantidade_positiva_aceite() {
        assertDoesNotThrow(
                () -> Validacao.quantidadePositiva("quantidade", 10));
    }

    // ---- contactoFornecedor ----

    @Test
    void contacto_telefone_aceite() {
        String result = Validacao.contactoFornecedor("contacto", "841234567");
        assertEquals("+258 84 123 4567", result);
    }

    @Test
    void contacto_email_aceite() {
        String result = Validacao.contactoFornecedor("contacto", "forn@teste.mz");
        assertEquals("forn@teste.mz", result);
    }

    @Test
    void contacto_invalido_recusado() {
        assertThrows(CampoException.class,
                () -> Validacao.contactoFornecedor("contacto", "12345678"));
    }

    // ---- CampoException é subclasse de NegocioException ----

    @Test
    void campoException_apanhadaPorCatchNegocio() {
        Exception ex = assertThrows(NegocioException.class,
                () -> Validacao.nomePessoa("nome", "A"));
        assertInstanceOf(CampoException.class, ex);
    }

    // ---- ProdutoService e D13 / D14 ----

    @Test
    void produtoService_compatibilidade_sementeComMaquinaria_recusada() {
        mz.agrogest.model.Fornecedor forn = mz.agrogest.service.FornecedorService.getInstance().criar(
                "Forn Maq " + java.util.UUID.randomUUID().toString().substring(0, 5),
                TipoFornecedor.MAQUINARIA,
                "841234567",
                "Nampula");

        CampoException ex = assertThrows(CampoException.class, () ->
                mz.agrogest.service.ProdutoService.getInstance().criar(
                        "Milho Branco",
                        Categoria.SEMENTE,
                        forn.getId(),
                        new BigDecimal("50.00"),
                        10,
                        "kg",
                        "Milho para semente",
                        "",
                        LocalDate.now().plusMonths(6)));

        assertEquals("fornecedorId", ex.getCampo());
        assertTrue(ex.getMessage().contains("não fornece"));
    }

    @Test
    void produtoService_produtoDuplicadoMesmoFornecedor_recusado() {
        mz.agrogest.model.Fornecedor forn = mz.agrogest.service.FornecedorService.getInstance().criar(
                "Forn Sementes " + java.util.UUID.randomUUID().toString().substring(0, 5),
                TipoFornecedor.SEMENTES,
                "841234567",
                "Nampula");

        String nomeProd = "Semente Milho " + java.util.UUID.randomUUID().toString().substring(0, 5);

        mz.agrogest.service.ProdutoService.getInstance().criar(
                nomeProd,
                Categoria.SEMENTE,
                forn.getId(),
                new BigDecimal("50.00"),
                10,
                "kg",
                "",
                "",
                LocalDate.now().plusMonths(6));

        CampoException ex = assertThrows(CampoException.class, () ->
                mz.agrogest.service.ProdutoService.getInstance().criar(
                        "  " + nomeProd.toUpperCase() + "  ",
                        Categoria.SEMENTE,
                        forn.getId(),
                        new BigDecimal("60.00"),
                        5,
                        "kg",
                        "",
                        "",
                        LocalDate.now().plusMonths(6)));

        assertEquals("nome", ex.getCampo());
        assertTrue(ex.getMessage().contains("Já existe um produto"));
    }

    @Test
    void produto_isExpirado() {
        Produto p = new Produto();
        p.setValidade(null);
        assertFalse(p.isExpirado());

        p.setValidade(LocalDate.now().plusDays(1));
        assertFalse(p.isExpirado());

        p.setValidade(LocalDate.now().minusDays(1));
        assertTrue(p.isExpirado());
    }
}
