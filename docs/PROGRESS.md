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

# Por fazer

- **V2:** Anti duplo envio (`TokenFormFilter`, token `_t`).
- **F0:** Fornecedores no padrão lista + "Novo".
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
