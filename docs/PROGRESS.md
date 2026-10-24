# Feito

- **I1 · Integração e QA:** Remoção dos stubs, merge de branches, compilação limpa e testes integrados.
- **V1 · Validações de servidor e erros por campo:**
  - Criação de `mz.agrogest.util.CampoException` (subclasse de `NegocioException`) para mapear erros diretamente ao campo do formulário.
  - Criação de `mz.agrogest.util.Validacao` com validações centralizadas e normalização (nomes, email, telefone moçambicano, contacto, endereço, password, preço, quantidades, validade, unidades e províncias).
  - Regra D13: Compatibilidade entre categoria de produto e tipo de fornecedor.
  - Regra D14: Unicidade de fornecedor por nome e de produto por nome + fornecedor (case-insensitive dentro do lock).
  - Regra D15: Validade futura na criação e método auxiliar `Produto.isExpirado()`.
  - Integração nos serviços (`UtilizadorService`, `FornecedorService`, `ProdutoService`) e nos servlets (`RegistoServlet`, `AdminFornecedorServlet`, `AdminProdutoServlet`) via `SessaoUtil.erro(...)`.
  - Testes unitários JUnit para `Validacao`, `ProdutoService` e `FornecedorService` adicionados e aprovados.
- **V2 · Anti duplo envio (servidor):**
  - Criação de `mz.agrogest.filter.TokenFormFilter` (`/*`, executado após o `AuthFilter`).
  - Geração de UUID em requisições GET, armazenamento em conjunto de sessão com limite FIFO de 50 tokens e disponibilização via request attribute `tokenForm`.
  - Consumo atómico do parâmetro `_t` em requisições POST com sincronização na coleção da sessão (`synchronized boolean consumir(String token)`).
  - Bloqueio de duplo envio: pedidos POST concorrentes ou repetidos com o mesmo token deixam passar apenas um, redirecionando os restantes com `flashErro` sem executar o servlet.
  - Exceção explícita de validação para `/logout` e passagem direta de recursos estáticos (`/css/*`, `/assets/*`).
  - Garantia de UTF-8 no filtro antes da leitura de parâmetros para proteger acentuação ("João Açúcar").
  - Geração de token de substituição no request após consumo em POST para suportar formulários reenviados após forward em erros de validação.
  - Adição de `<input type="hidden" name="_t" value="${tokenForm}">` em todos os formulários POST das JSPs.
  - Suite de testes unitários `TokenFormFilterTest` com testes de concorrência multithread, FIFO, isolamento de abas e ciclo de vida HTTP do filtro.
- **F0 · Fornecedores no padrão lista + "Novo":**
  - Separação da criação e listagem de fornecedores seguindo o padrão de produtos (base para F1–F3).
  - `AdminFornecedorServlet`: suporte a rotas `GET /admin/fornecedores` (lista), `GET /admin/fornecedores/novo` (formulário), `POST /admin/fornecedores` (criação via POST-redirect-GET com `flashOk`, ou forward com `erroCampo` e preservação de campos em erro de validação) e `POST /admin/fornecedores/remover` (remoção com `flashOk` ou `flashErro` em caso de fornecedor com produtos associados).
  - `views/admin/fornecedores/lista.jsp`: remoção do formulário embutido; adição de cabeçalho `.pagina-topo` com botão "+ Novo fornecedor" e botão de remoção por ícone SVG (`.btn-icone`).
  - `views/admin/fornecedores/form.jsp`: criação de página com cartão centrado (`.form-centrado`), grelha a 2 colunas (`.form-grelha`), selects para tipo e província (11 províncias de Moçambique), `inputmode="tel"` no contacto, token anti duplo envio `_t` e botões Cancelar/Guardar.
  - `css/estilo.css`: introdução das classes de suporte `.pagina-topo`, `.form-centrado`, `.form-grelha`, `.campo-cheio`, `.form-acoes`, `.btn-icone` e estilos base padronizados para `.btn`, `.btn-sec` e `.btn-perigo`.
- **T0 · Ícones (sprite SVG inline e organização de assets):**
  - Migração de todos os recursos da pasta raiz `assets/` para a estrutura padronizada da webapp (`src/main/webapp/assets/icons/`, `src/main/webapp/assets/img/` e `src/main/webapp/assets/img/produtos/`).
  - Geração de `src/main/webapp/WEB-INF/jspf/icons.jspf` com sprite SVG (`<symbol id="i-{nome}" viewBox="0 0 24 24">`) cobrindo todos os ícones da secção 3.1 do plano (incluindo `i-sair`, `i-cofre` e ações de persistência).
  - Inclusão estática do sprite em `src/main/webapp/WEB-INF/jspf/topo.jspf` logo após o `<body>` via `<%@ include file="/WEB-INF/jspf/icons.jspf" %>`.
  - Estilização em `src/main/webapp/css/estilo.css` para a classe `.icone` (`1.25em`, `stroke: currentColor`, `fill: none`), `.btn-icone` com dimensões mínimas de 44x44px e padding de 8px, e botões base (`.btn`, `.btn-sec`, `.btn-perigo`) com `inline-flex` e `gap: 8px` entre ícone e texto.
  - Atualização de `views/admin/fornecedores/lista.jsp` aplicando o ícone `i-mais` no link "+ Novo fornecedor" e o ícone `i-lixo` no botão de remoção `.btn-icone`.

# Por fazer

- **F1:** Campanhas (`/admin/campanhas/*`).
- **F2:** Cultivos (`/admin/cultivos/*`).
- **F3:** Colheitas (`/admin/colheitas/*`).
- **T1:** Fundação visual (CSS, variáveis, hover, botões).
- **T2–T8:** Interface, navegação, toasts, catálogo, fotos e páginas administrativas.
- **D1–D5:** README, manual PDF, deploy systemd, release e limpeza.

# Decisões tomadas

- **D13:** Mapeamento fixo de categorias a tipos de fornecedor (`SEMENTE` → `SEMENTES`, `FERTILIZANTE` → `FERTILIZANTES`, `MAQUINARIA`/`FERRAMENTA` → `MAQUINARIA`).
- **D14:** Validações de unicidade executadas dentro do lock global `DataStore.getLock()`.
- **D15:** Validade no modelo não altera campos nem serialização (`isExpirado()` é getter calculado).
- **D16:** Lista fechada de unidades (`Validacao.UNIDADES`).
- **D17:** Validações de telefone MZ (prefixos 82-88 e fixo 2) com formatação normalizada `+258 XX XXX XXXX`.
- **D18:** Atributo `erroCampo` colocado na requisição pelos servlets em caso de erro para suporte a feedback por campo.
- **D19:** Mecanismo anti duplo envio com token de formulário `_t` gerado por GET, armazenado na sessão (máx. 50, FIFO) e consumido atomicamente no POST via `TokenFormFilter`.
