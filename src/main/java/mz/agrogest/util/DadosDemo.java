package mz.agrogest.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mz.agrogest.model.Categoria;
import mz.agrogest.model.Cultivo;
import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.Produto;
import mz.agrogest.model.TipoFornecedor;
import mz.agrogest.model.Utilizador;
import mz.agrogest.repo.DataStore;
import mz.agrogest.service.CampanhaService;
import mz.agrogest.service.ColheitaService;
import mz.agrogest.service.CultivoService;
import mz.agrogest.service.FornecedorService;
import mz.agrogest.service.ProdutoService;
import mz.agrogest.service.UtilizadorService;
import mz.agrogest.service.VendaService;

/**
 * Carrega dados de demonstração (A10) no primeiro arranque da aplicação,
 * caso o DataStore esteja vazio (sem fornecedores, produtos ou vendas).
 * Passa sempre pelos serviços e pelas regras de negócio.
 */
public final class DadosDemo {

    private static final Logger LOG = Logger.getLogger(DadosDemo.class.getName());

    private DadosDemo() {
    }

    public static void carregarSeVazio() {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            if (!ds.getFornecedores().isEmpty() || !ds.getProdutos().isEmpty() || !ds.getVendas().isEmpty()) {
                return;
            }
        }

        try {
            LOG.info("A inicializar carregamento de dados de demonstração (A10)...");

            // 1. Fornecedores compatíveis com D13
            FornecedorService fornSvc = FornecedorService.getInstance();
            Fornecedor fSementes = fornSvc.criar("Sementes do Sul, Lda", TipoFornecedor.SEMENTES, "+258 84 111 2233", "Nampula");
            Fornecedor fAdubos = fornSvc.criar("AgroNutri, Lda", TipoFornecedor.FERTILIZANTES, "+258 82 222 3344", "Manica");
            Fornecedor fMaq = fornSvc.criar("MaqAgro, Lda", TipoFornecedor.MAQUINARIA, "+258 86 333 4455", "Maputo");

            // 2. Produtos com as fotos locais de assets/img/produtos/
            ProdutoService prodSvc = ProdutoService.getInstance();
            LocalDate val1Ano = LocalDate.now().plusYears(1);
            LocalDate val2Anos = LocalDate.now().plusYears(2);

            // Sementes
            Produto pMilho = prodSvc.criar("Semente de milho", Categoria.SEMENTE, fSementes.getId(),
                    new BigDecimal("95.00"), 500.0, "kg",
                    "Semente de milho certificada de alto rendimento.",
                    "/assets/img/produtos/milho.jpg", val1Ano);

            Produto pFeijao = prodSvc.criar("Semente Feijão nhemba", Categoria.SEMENTE, fSementes.getId(),
                    new BigDecimal("320.00"), 400.0, "kg",
                    "Feijão nhemba resistente à seca, ideal para o norte.",
                    "/assets/img/produtos/feijao.jpg", val1Ano);

            Produto pArroz = prodSvc.criar("Semente de arroz", Categoria.SEMENTE, fSementes.getId(),
                    new BigDecimal("110.00"), 350.0, "kg",
                    "Semente de arroz de regadio selecionada.",
                    "/assets/img/produtos/arroz.jpg", val1Ano);

            Produto pAmendoim = prodSvc.criar("Amendoim para semente", Categoria.SEMENTE, fSementes.getId(),
                    new BigDecimal("180.00"), 250.0, "kg",
                    "Amendoim de ciclo curto com boa taxa de germinação.",
                    "/assets/img/produtos/amendoim.jpg", val1Ano);

            // Fertilizantes
            Produto pAdubo = prodSvc.criar("Adubo NPK 50 kg", Categoria.FERTILIZANTE, fAdubos.getId(),
                    new BigDecimal("1450.00"), 50.0, "saco",
                    "Fertilizante composto NPK 12-24-12 em sacos de 50 kg.",
                    "/assets/img/produtos/adubo-saco.jpg", val2Anos);

            Produto pUreia = prodSvc.criar("Ureia granulada", Categoria.FERTILIZANTE, fAdubos.getId(),
                    new BigDecimal("1800.00"), 40.0, "saco",
                    "Ureia 46% azoto para adubação de cobertura.",
                    "/assets/img/produtos/fertilizante-granulado.jpg", val2Anos);

            Produto pComposto = prodSvc.criar("Composto orgânico", Categoria.FERTILIZANTE, fAdubos.getId(),
                    new BigDecimal("450.00"), 60.0, "saco",
                    "Matéria orgânica enriquecida para melhoria do solo.",
                    "/assets/img/produtos/composto.jpg", val1Ano);

            // Maquinaria e Ferramentas (fornecedor MaqAgro)
            Produto pTrator = prodSvc.criar("Trator 60 CV", Categoria.MAQUINARIA, fMaq.getId(),
                    new BigDecimal("1250000.00"), 2.0, "unidade",
                    "Trator agrícola 4x4 robusto e económico.",
                    "/assets/img/produtos/trator.jpg", null);

            Produto pPulv = prodSvc.criar("Pulverizador costal", Categoria.MAQUINARIA, fMaq.getId(),
                    new BigDecimal("4500.00"), 10.0, "unidade",
                    "Pulverizador manual costal de 16 litros com lança de latão.",
                    "/assets/img/produtos/pulverizador.jpg", null);

            prodSvc.criar("Bomba de rega", Categoria.MAQUINARIA, fMaq.getId(),
                    new BigDecimal("18500.00"), 5.0, "unidade",
                    "Motobomba a gasolina de alta vazão para irrigação.",
                    "/assets/img/produtos/bomba_rega.jpg", null);

            Produto pEnxada = prodSvc.criar("Enxada", Categoria.FERRAMENTA, fMaq.getId(),
                    new BigDecimal("450.00"), 80.0, "unidade",
                    "Enxada em aço temperado com cabo de madeira resistente.",
                    "/assets/img/produtos/enxada.jpg", null);

            Produto pRegador = prodSvc.criar("Regador 10 L", Categoria.FERRAMENTA, fMaq.getId(),
                    new BigDecimal("350.00"), 40.0, "unidade",
                    "Regador em plástico resistente com crivo removível.",
                    "/assets/img/produtos/regador.jpg", null);

            prodSvc.criar("Pá de bico", Categoria.FERRAMENTA, fMaq.getId(),
                    new BigDecimal("520.00"), 35.0, "unidade",
                    "Pá em aço carbono com punho anatómico.",
                    "/assets/img/produtos/pa.jpg", null);

            Produto pCatana = prodSvc.criar("Catana agrícola", Categoria.FERRAMENTA, fMaq.getId(),
                    new BigDecimal("380.00"), 50.0, "unidade",
                    "Catana para corte de mato e colheita de culturas.",
                    "/assets/img/produtos/catana.jpg", null);

            Produto pLuvas = prodSvc.criar("Luvas de trabalho", Categoria.FERRAMENTA, fMaq.getId(),
                    new BigDecimal("150.00"), 100.0, "unidade",
                    "Luvas de proteção reforçadas para trabalho de campo.",
                    "/assets/img/produtos/luvas.jpg", null);

            // 3. Clientes de teste
            UtilizadorService userSvc = UtilizadorService.getInstance();
            Utilizador uJoao = userSvc.registarCliente("João Açúcar", "joao@teste.mz", "1234",
                    "+258 84 123 4567", "Av. Eduardo Mondlane, Nampula");
            Utilizador uAna = userSvc.registarCliente("Ana Silva", "ana@teste.mz", "1234",
                    "+258 82 234 5678", "Bairro de Muhala, Nampula");
            Utilizador uCarlos = userSvc.registarCliente("Carlos Tembe", "carlos@teste.mz", "1234",
                    "+258 86 345 6789", "Estrada Nacional 1, Nampula");
            Utilizador uMaria = userSvc.registarCliente("Maria Macuácua", "maria@teste.mz", "1234",
                    "+258 87 456 7890", "Rua dos Semedo, Nampula");
            userSvc.registarCliente("Pedro Cossa", "pedro@teste.mz", "1234",
                    "+258 85 567 8901", "Zona Industrial, Nampula"); // Sem compras (para relatório de clientes sem compras)

            // 4. Campanhas, Cultivos e Colheitas
            CampanhaService campSvc = CampanhaService.getInstance();
            CultivoService cultSvc = CultivoService.getInstance();
            ColheitaService colhSvc = ColheitaService.getInstance();

            var camp = campSvc.criar("Campanha Agrícola 2026/2027",
                    LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(6));
            Cultivo cult = cultSvc.criar(camp.getId(), "Milho", "PAN 53", 15.5,
                    LocalDate.now().minusMonths(1));
            // 3200 kg x 22 MT = 70 400 MT (exemplo do enunciado)
            colhSvc.registar(cult.getId(), LocalDate.now().minusDays(5), 3200.0, new BigDecimal("22.00"));

            // 5. Vendas de demonstração (via VendaService.finalizarCompra)
            VendaService vendaSvc = VendaService.getInstance();

            // Venda 1: João Açúcar compra 5 kg de milho + 1 saco de adubo
            Map<Integer, Double> ped1 = new LinkedHashMap<>();
            ped1.put(pMilho.getId(), 5.0);
            ped1.put(pAdubo.getId(), 1.0);
            vendaSvc.finalizarCompra(uJoao, ped1);

            // Venda 2: Ana Silva compra 2 enxadas + 1 regador
            Map<Integer, Double> ped2 = new LinkedHashMap<>();
            ped2.put(pEnxada.getId(), 2.0);
            ped2.put(pRegador.getId(), 1.0);
            vendaSvc.finalizarCompra(uAna, ped2);

            // Venda 3: Carlos Tembe compra 10 kg de feijão
            Map<Integer, Double> ped3 = new LinkedHashMap<>();
            ped3.put(pFeijao.getId(), 10.0);
            vendaSvc.finalizarCompra(uCarlos, ped3);

            // Venda 4: Maria Macuácua compra 1 pulverizador + 1 catana + 2 luvas
            Map<Integer, Double> ped4 = new LinkedHashMap<>();
            ped4.put(pPulv.getId(), 1.0);
            ped4.put(pCatana.getId(), 1.0);
            ped4.put(pLuvas.getId(), 2.0);
            vendaSvc.finalizarCompra(uMaria, ped4);

            LOG.info("dados de demonstração carregados");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro ao carregar dados de demonstração", ex);
        }
    }
}
