# AgroGest — Contrato do grupo

Documento partilhado pelas 3 pessoas. Define **só o que o enunciado deixa em aberto**: nomes, tipos, assinaturas, rotas, convenções e decisões. O que o enunciado já diz não se repete aqui.

Enunciado (cópia no repositório): `docs/enunciado/AgroGest_Enunciado.pdf`

---

## 0. Instruções para a IA (ler antes de fazer qualquer coisa)

1. **Se ainda não tens o enunciado no teu contexto, pede-o ao utilizador agora** (o PDF acima) e **não escrevas código antes de o leres**. Sem o enunciado vais inventar requisitos.
2. Lê este contrato inteiro e o documento da pessoa para quem trabalhas (`docs/PESSOA_A.md`, `PESSOA_B.md` ou `PESSOA_C.md`).
3. **Este contrato é lei.** Não renomeies classes, campos, métodos, rotas, pacotes nem atributos de sessão. Se achares um erro, diz ao utilizador para avisar o grupo. Não "corrijas" sozinho: o outro membro está a programar contra o mesmo nome.
4. Trabalha **só na área da pessoa** (secção 3). Nunca edites ficheiros de outra pessoa.
5. Não acrescentes dependências ao `pom.xml` (só a Pessoa A lhe toca).
6. Tecnologia fixa: Java 17, Jakarta EE (`jakarta.*`, **nunca `javax.*`**), Tomcat 10.1, Servlets 6.0, JSP + JSTL + EL. Sem base de dados, sem frameworks (Spring, Hibernate…), sem scriptlets JSP.
7. Gera em fatias pequenas (uma classe ou um servlet de cada vez) e confirma que compila antes de avançar.
8. Explica o que geras em linguagem simples: o utilizador tem de o defender oralmente, ao vivo.

---

## 1. Como o grupo trabalha

- **Git só pelo terminal**, uma branch por pessoa (`pessoa-a`, `pessoa-b`, `pessoa-c`). Ninguém faz push para `main`; **só a Pessoa A faz merge para `main`**. Guia: `docs/GIT.md`.
- **Ninguém espera por ninguém.** Cada pessoa constrói a sua parte contra este contrato. O que depende de código de outra pessoa usa **stubs** (secção 10) e, na integração, trocam-se pelo código real.
- **Marco M1 (quinta, 15 Out):** a Pessoa A publica em `main` o núcleo real (`model`, `DataStore`, `NegocioException`). Quem já o tiver pode apagar os stubs correspondentes. Os stubs de *serviços* ficam até à integração.
- Dono por área (secção 3). Se precisas de uma alteração em ficheiro de outro, pede-a por mensagem; não a faças tu.

## 2. Regras de uso de IA no grupo

1. A IA lê o enunciado e este contrato **antes** de gerar código (secção 0).
2. **Só entra no commit código que compila** (Eclipse sem erros vermelhos) e que corriste uma vez. `main` nunca pode estar partida.
3. **Se não sabes explicar uma linha, não a commites.** A defesa é ao vivo e cada um responde pela sua parte. Antes de cada commit, pede à IA para te explicar o que gerou.
4. Nada de código fora do contrato: nem classes extra com nomes diferentes nas áreas partilhadas, nem bibliotecas novas.
5. Cada pessoa lê (pelo menos) o código principal das outras duas partes antes do ensaio da defesa. Perguntas cruzadas na defesa são normais.
6. Dúvida sobre o enunciado ou o contrato: pergunta ao grupo. Não inventes e não deixes a IA inventar.

---

## 3. Estrutura e propriedade

Pacote base: `mz.agrogest` → `model`, `repo`, `service`, `servlet`, `filter`, `util`, `listener`.

| Área | Dono |
|---|---|
| `pom.xml`, `web.xml`, `.gitignore`, `.gitattributes`, `docs/00_CONTRATO.md` | **A** |
| `model/*` (**excepto** `Carrinho`, `LinhaCarrinho`), `repo/*`, `util/*`, `filter/*`, `listener/*` | **A** |
| `service/UtilizadorService`, `VendaService`, `ListagemService`, `RelatorioService` | **A** |
| Servlets `DashboardServlet`, `ListagemServlet`, `RelatorioServlet`, `PersistenciaServlet` + `views/admin/{dashboard,listagens,relatorios,persistencia}/` | **A** |
| `model/Carrinho`, `model/LinhaCarrinho`, `service/ProdutoService` | **B** |
| Servlets `HomeServlet`, `LoginServlet`, `LogoutServlet`, `RegistoServlet`, `CatalogoServlet`, `CarrinhoServlet`, `CompraServlet`, `FacturaServlet`, `MinhasComprasServlet`, `AdminProdutoServlet` + `views/auth/`, `views/cliente/`, `views/admin/produtos/` | **B** |
| `service/FornecedorService`, `CampanhaService`, `CultivoService`, `ColheitaService` | **C** |
| Servlets `AdminFornecedorServlet`, `AdminCampanhaServlet`, `AdminCultivoServlet`, `AdminColheitaServlet` + `views/admin/{fornecedores,campanhas,cultivos,colheitas}/` | **C** |
| `css/estilo.css`, `WEB-INF/jspf/*`, `views/erro.jsp`, `README.md`, `deploy/` | **C** |
| `src/test/java/**` (testes JUnit) | **A** |
| `src/main/webapp/teste.jsp` (teste de ambiente) | **A** (apaga na integração) |
| `docs/GIT.md`, `docs/PESSOA_*.md`, `entrega/` (WAR final) | **A** |
| `docs/manual/` (manual PDF) | **B** |
| `stub/b/**` | **B** (temporário) |
| `stub/c/**` | **C** (temporário) |

Views (JSP) ficam em `src/main/webapp/WEB-INF/views/…` (nunca acessíveis directamente por URL).

---

## 4. Contrato do modelo (`mz.agrogest.model`)

Regras para **todas** as classes: `implements Serializable` com `serialVersionUID = 1L`; construtor público **sem argumentos**; getter e setter de **cada campo** (boolean: `isX`/`setX`). **Fora do `model`, objectos criam-se com `new X()` + setters** (nunca com construtores com argumentos: assim o stub e a classe real são intermutáveis).

Enums: `Perfil {ADMIN, CLIENTE}` · `TipoFornecedor {SEMENTES, MAQUINARIA, FERTILIZANTES}` · `Categoria {SEMENTE, FERTILIZANTE, MAQUINARIA, FERRAMENTA}`

| Classe | Campos (tipo nome) | Getters calculados (só leitura, não são campos) |
|---|---|---|
| `Utilizador` | `int id`, `String nome`, `String email`, `String passHash`, `Perfil perfil`, `boolean ativo`, `String telefone`, `String endereco` | `boolean isAdmin()` |
| `Fornecedor` | `int id`, `String nome`, `TipoFornecedor tipo`, `String contacto`, `String provincia` | — |
| `Produto` | `int id`, `String nome`, `Categoria categoria`, `int fornecedorId`, `BigDecimal preco`, `double quantidade`, `String unidade`, `String descricao`, `String fotoUrl`, `LocalDate validade` (pode ser `null`) | `String getValidadeFmt()` |
| `Campanha` | `int id`, `String nome`, `LocalDate inicio`, `LocalDate fim`, `boolean aberta` | `getInicioFmt()`, `getFimFmt()` |
| `Cultivo` | `int id`, `int campanhaId`, `String cultura`, `String variedade`, `double hectares`, `LocalDate plantio` | `getPlantioFmt()` |
| `Colheita` | `int id`, `int cultivoId`, `LocalDate data`, `double kg`, `BigDecimal precoKg` | `BigDecimal getValorTotal()`, `getDataFmt()` |
| `VendaItem` | `int produtoId`, `String produtoNome`, `String unidade`, `double quantidade`, `BigDecimal precoUnit`, `BigDecimal subtotal` | — |
| `Venda` | `int id`, `String numeroFactura`, `LocalDateTime data`, `int clienteId`, `String clienteNome`, `String clienteEmail`, `String clienteTelefone`, `String clienteEndereco`, `List<VendaItem> itens` (inicializada vazia), `BigDecimal subtotal`, `BigDecimal iva`, `BigDecimal total` | `String getDataFmt()` |

- Os getters `...Fmt()` devolvem `dd/MM/yyyy` (a `Venda` devolve `dd/MM/yyyy HH:mm`) e `""` se o valor for `null`. Existem porque **`<fmt:formatDate>` não funciona com `java.time`**.
- `Venda` e `VendaItem` guardam **cópia** (nome do produto, dados do cliente): a factura não muda se o produto ou o cliente mudarem depois.

**Só da Pessoa B** (vivem em `model` mas ninguém mais as toca):

| Classe | Contrato |
|---|---|
| `Carrinho` | Guarda `Map<Integer, Double>` (produtoId → quantidade). Métodos: `void adicionar(int produtoId, double quantidade)` (soma se já existir), `void atualizar(int produtoId, double quantidade)`, `void remover(int produtoId)`, `void esvaziar()`, `boolean isVazio()`, `int getTotalItens()` (nº de linhas; usado pelo topo), `Map<Integer, Double> getQuantidades()` (**cópia**). |
| `LinhaCarrinho` | `Produto produto`, `double quantidade`, `BigDecimal subtotal` (para mostrar o carrinho). |

---

## 5. Contrato do `DataStore` (`mz.agrogest.repo`)

Singleton com listas em memória e contadores. Todos os serviços usam-no; ninguém cria listas próprias.

```java
public final class DataStore {
    public static DataStore getInstance();
    public Object getLock();                      // monitor ÚNICO para TODAS as escritas

    public List<Utilizador> getUtilizadores();    // Collections.synchronizedList (lista VIVA)
    public List<Fornecedor> getFornecedores();
    public List<Produto>    getProdutos();
    public List<Campanha>   getCampanhas();
    public List<Cultivo>    getCultivos();
    public List<Colheita>   getColheitas();
    public List<Venda>      getVendas();

    public int novoIdUtilizador();                // AtomicInteger.incrementAndGet()
    public int novoIdFornecedor();
    public int novoIdProduto();
    public int novoIdCampanha();
    public int novoIdCultivo();
    public int novoIdColheita();
    public int novoIdVenda();
}
```

Regras de concorrência (valem para **todos** os serviços):

1. Toda a operação que **altera** estado (criar, remover, repor stock, vender, fechar…) corre dentro de `synchronized (DataStore.getInstance().getLock()) { … }`. É um lock **comum** de propósito: `VendaService` (A) e `ProdutoService.reporStock` (B) mexem na mesma quantidade e têm de se excluir mutuamente.
2. As listas "vivas" só se tocam **dentro** desse bloco. Os métodos de leitura dos serviços devolvem **cópias** (`new ArrayList<>(...)`), nunca a lista viva.
3. IDs só se obtêm com `novoIdX()`. Nunca `size()+1`, nunca `new AtomicInteger`.

---

## 6. Contrato dos serviços (`mz.agrogest.service`)

Todos os serviços: classe `final`, singleton com `public static XService getInstance()`. Validam os dados e, se algo estiver errado, lançam `mz.agrogest.util.NegocioException` (**unchecked**, `extends RuntimeException`, construtor `(String mensagem)`, mensagem em português pronta para mostrar ao utilizador). `porId` devolve `null` se não existir.

**`UtilizadorService` (A)**
```java
Utilizador registarCliente(String nome, String email, String password, String telefone, String endereco);
Utilizador autenticar(String email, String password);   // null se credenciais inválidas
Utilizador porId(int id);
List<Utilizador> listarClientes();
void garantirAdmin();                                    // chamado pelo listener no arranque
```

**`VendaService` (A)**
```java
Venda finalizarCompra(Utilizador cliente, Map<Integer, Double> itens);  // produtoId -> quantidade
Venda porId(int id);
List<Venda> porCliente(int clienteId);                  // mais recentes primeiro
List<Venda> todas();                                    // mais recentes primeiro
```

**`ProdutoService` (B)**
```java
Produto criar(String nome, Categoria categoria, int fornecedorId, BigDecimal preco,
              double quantidade, String unidade, String descricao, String fotoUrl, LocalDate validade);
List<Produto> listar();
List<Produto> listarEmStock();                           // quantidade > 0
List<Produto> listarPorCategoria(Categoria categoria);
Produto porId(int id);
void reporStock(int id, double quantidade);              // SOMA ao stock; quantidade > 0
void remover(int id);
boolean existeComFornecedor(int fornecedorId);
```

**`FornecedorService` (C)**
```java
Fornecedor criar(String nome, TipoFornecedor tipo, String contacto, String provincia);
List<Fornecedor> listar();
Fornecedor porId(int id);
void remover(int id);                                    // NegocioException se tiver produtos
```

**`CampanhaService` (C)**
```java
Campanha criar(String nome, LocalDate inicio, LocalDate fim);   // fim >= inicio; nasce aberta
List<Campanha> listar();
Campanha porId(int id);
void fechar(int id);
void reabrir(int id);
void remover(int id);                                    // NegocioException se tiver cultivos
```

**`CultivoService` (C)**
```java
Cultivo criar(int campanhaId, String cultura, String variedade, double hectares, LocalDate plantio);
List<Cultivo> listar();
List<Cultivo> listarPorCampanha(int campanhaId);
Cultivo porId(int id);
void remover(int id);                                    // NegocioException se tiver colheitas
```

**`ColheitaService` (C)**
```java
Colheita registar(int cultivoId, LocalDate data, double kg, BigDecimal precoKg);
List<Colheita> listar();
List<Colheita> listarPorCultivo(int cultivoId);
```

`ListagemService` e `RelatorioService` são internos da Pessoa A (ninguém mais os chama). `Persistencia` (`repo`) idem.

---

## 7. Rotas e donos

Cada pessoa é dona de **tudo o que está debaixo do seu prefixo** e decide os sub-caminhos (via `pathInfo`). Dois servlets com a mesma rota = o Tomcat **não arranca**; por isso esta tabela é exclusiva.

| Rota | Servlet | Dono | Notas |
|---|---|---|---|
| `""` (raiz) | `HomeServlet` | B | Redirecciona: anónimo → `/login`; cliente → `/catalogo`; admin → `/admin/dashboard` |
| `/login` | `LoginServlet` | B | |
| `/logout` | `LogoutServlet` | B | Invalida a sessão |
| `/registo` | `RegistoServlet` | B | |
| `/catalogo` | `CatalogoServlet` | B | Parâmetro `categoria` (nome do enum) |
| `/carrinho/*` | `CarrinhoServlet` | B | |
| `/compra` | `CompraServlet` | B | |
| `/factura` | `FacturaServlet` | B | Parâmetro `id` (id da venda) |
| `/minhas-compras` | `MinhasComprasServlet` | B | |
| `/admin/produtos/*` | `AdminProdutoServlet` | B | |
| `/admin/dashboard` | `DashboardServlet` | A | |
| `/admin/listagens/*` | `ListagemServlet` | A | Ordenação por `?ordenar=nome&dir=asc` |
| `/admin/relatorios/*` | `RelatorioServlet` | A | |
| `/admin/persistencia/*` | `PersistenciaServlet` | A | `gravar`, `eliminar` (POST) |
| `/admin/fornecedores/*` | `AdminFornecedorServlet` | C | |
| `/admin/campanhas/*` | `AdminCampanhaServlet` | C | |
| `/admin/cultivos/*` | `AdminCultivoServlet` | C | Parâmetro `campanhaId` |
| `/admin/colheitas/*` | `AdminColheitaServlet` | C | Parâmetro `cultivoId` |

O menu (`topo.jspf`) já aponta para todas estas rotas. A listagem "Compras detalhadas" (A) liga cada venda a `/factura?id={id}`; o `FacturaServlet` (B) deixa ver a factura ao dono ou ao admin (RN03) e responde 403 aos restantes.

**Filtro (A):** `AuthFilter` em `/*`. Públicas: `/`, `/login`, `/registo`, `/logout`, `/css/*` e, **só até à integração**, `/teste.jsp`. Tudo o resto exige sessão. Anónimo → redirect para `/login`; autenticado **não-admin** em `/admin/*` → **403**.

**Sessão:** atributo `utilizador` (`Utilizador`) e `carrinho` (`Carrinho`). O login (B) deve chamar `request.changeSessionId()` antes de guardar o utilizador.

---

## 8. Convenções web (valem para todos)

1. O servlet encaminha para o JSP com `req.getRequestDispatcher("/WEB-INF/views/…").forward(req, resp)`.
2. **O topo e o rodapé entram sozinhos em todos os `*.jsp`** (configurado no `web.xml`). Nos teus JSP **não** escrevas `<html>`, `<head>`, `<body>`, **nem** declares as taglibs `c`, `fmt`, `fn`: já estão declaradas. Fragmentos reutilizáveis têm extensão `.jspf`.
3. **Sem scriptlets** (`<% %>`, `<%= %>`): o `web.xml` dá erro. Só EL e JSTL. Nada de `<jsp:useBean>`.
4. Se alguma vez declarares uma taglib, os URIs são `jakarta.tags.core`, `jakarta.tags.fmt`, `jakarta.tags.functions` (a IA tende a escrever os antigos `http://java.sun.com/…`, que **não** funcionam aqui).
5. Links, actions e recursos: `<c:url value="/catalogo"/>`. Nunca `/agrogest/...` escrito à mão.
6. Texto escrito por utilizadores (nome, endereço, descrição, cultura…) mostra-se com `<c:out value="${...}"/>`, nunca `${...}` cru (evita XSS). `${...}` cru só para números e para os `...Fmt`.
7. Alterações só por **POST**. Depois de um POST com sucesso: **redirect** (`resp.sendRedirect(req.getContextPath() + "/…")`) com `session.setAttribute("flashOk", "mensagem")`; o topo mostra-a e apaga-a. Erro de validação: `req.setAttribute("erro", mensagem)` e forward de volta ao formulário. O topo mostra `flashOk`, `flashErro`, `mensagem` e `erro`.
8. Os servlets apanham `NegocioException`, `NumberFormatException`, `DateTimeParseException` e `IllegalArgumentException` (enum inválido) e mostram `erro`. Nunca deixar rebentar com 500 por input do utilizador.
9. `titulo` (atributo de request, opcional) define o título da página.
10. Dinheiro: `<fmt:formatNumber value="${x}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT`. O topo fixa o locale `pt_PT`, por isso aparece `70 400,00` (espaço nos milhares, vírgula nos decimais). É o esperado; não "corrijam" para pontos.
11. Classes CSS que já existem (usem-nas): `barra`, `conteudo`, `grelha`, `cartao`, `tabela` (`.num` nas células numéricas), `tabela-scroll`, `btn`, `btn-sec`, `btn-perigo`, `acoes`, `form`, `alerta ok|erro`, `badge`, `muted`, `total`, `factura`, `nao-imprimir`. A Pessoa C pode acrescentar e melhorar, **sem renomear**.
12. Tudo em UTF-8. Teste obrigatório com "João Açúcar" num formulário.

## 9. Decisões que o enunciado deixa em aberto

| # | Decisão |
|---|---|
| D1 | `Utilizador.ativo` existe no modelo, é sempre `true` e **não tem lógica** (não se implementa activar/desactivar contas). |
| D2 | Nº de factura = `FT-{ano}-{id da venda}` (ex.: `FT-2026-1`). O id da venda é global e sequencial. |
| D3 | Dinheiro = `BigDecimal` com 2 casas, `RoundingMode.HALF_UP`. Stock, quantidades, kg e hectares = `double`. |
| D4 | Os preços **não** incluem IVA: `iva = subtotal × 0,16`; `total = subtotal + iva`. |
| D5 | Campanha **fechada** não aceita novos cultivos nem colheitas. |
| D6 | Fornecedor com produtos associados não pode ser removido (mesma lógica das campanhas). |
| D7 | Remover um produto não afecta vendas antigas (têm cópia do nome, preço e unidade). |
| D8 | Relatório "por volume" = soma dos totais gastos (decrescente). "Mais assíduos" = nº de compras (decrescente). |
| D9 | Listagens do admin são só de leitura, com ordenação. Gestão (criar/remover) fica nas páginas de cada módulo. |
| D10 | Ficheiro de dados: `${catalina.base}/data/agrogest.ser` (se não existir a pasta, cria-se). Escrita atómica: grava em `.ser.tmp` e `Files.move(..., ATOMIC_MOVE)`. "Eliminar" apaga o **ficheiro**, não limpa a memória. |
| D11 | Passwords com BCrypt (`org.mindrot:jbcrypt`), guardadas em `passHash`. Admin criado no arranque: `admin@agro.mz` / `admin234`. Emails normalizados (`trim` + minúsculas) e únicos. |
| D12 | Quando se altera um campo de uma classe do `model`, **apagar o `agrogest.ser` antigo**: a desserialização falha ou mistura dados. |

## 10. Stubs: como funcionam e como se trocam

**Quem faz stub de quê** (só do que **não é teu**; a Pessoa A não precisa de nenhum):

| Pessoa | Stubs (mesmo nome, mesmo pacote relativo, mesmas assinaturas do contrato) |
|---|---|
| **B** | `model`: `Utilizador`, `Perfil`, `Produto`, `Categoria`, `Fornecedor`, `TipoFornecedor`, `Venda`, `VendaItem` · `repo.DataStore` (só `getInstance`, `getLock`, `getProdutos`, `novoIdProduto`) · `util.NegocioException` · `service`: `UtilizadorService` (`registarCliente`, `autenticar`, `porId`), `VendaService` (as 4 métodos), `FornecedorService` (`listar`, `porId`) |
| **C** | `model`: `Fornecedor`, `TipoFornecedor`, `Campanha`, `Cultivo`, `Colheita` · `repo.DataStore` (`getInstance`, `getLock`, listas e `novoId` das suas entidades) · `util.NegocioException` · `service.ProdutoService` (só `existeComFornecedor`) |

**Onde ficam:** `src/main/java/mz/agrogest/stub/<pessoa>/<mesmo sub-pacote>/Classe.java`, com `package mz.agrogest.stub.b.model;` (ou `.c.`). Exemplos: `mz.agrogest.stub.b.model.Produto`, `mz.agrogest.stub.c.repo.DataStore`.

**Regras dos stubs:**
- Copiam o contrato **à letra** (campos, getters calculados, assinaturas). Não inventes métodos.
- Devolvem dados plausíveis (uma lista de exemplo) para poderes ver as páginas a funcionar.
- O teu código real **importa os stubs** (`import mz.agrogest.stub.b.model.Produto;`). Nunca ponhas stubs nos pacotes reais.
- Os JSP **não importam nada**: usam só EL, por isso não mudam na integração. Mas **os nomes dos getters têm de ser exactos**, ou o JSP rebenta em execução.

**Troca na integração (feita pela Pessoa A, num só comando):** depois de fazer merge das branches, apagar `stub/` e reescrever os imports:

```bash
rm -rf src/main/java/mz/agrogest/stub
grep -rlE 'mz\.agrogest\.stub\.' src | xargs -r sed -i -E 's/mz\.agrogest\.stub\.[bc]\./mz.agrogest./g'
```

(No Windows corre-se no **Git Bash**.) Por isso é obrigatório o nome do pacote do stub ter a forma `mz.agrogest.stub.<b|c>.<model|repo|util|service>`.

Depois do M1, B e C fazem a troca **só** de `model`, `repo` e `util` (os serviços continuam stub). Em vez do comando acima, usam este (troca `b` por `c` se fores o C):

```bash
rm -rf src/main/java/mz/agrogest/stub/b/{model,repo,util}
grep -rlE 'mz\.agrogest\.stub\.b\.(model|repo|util)\.' src | xargs -r sed -i -E 's/mz\.agrogest\.stub\.b\.(model|repo|util)\./mz.agrogest.\1./g'
```

---

## 11. Calendário (hoje é segunda, 12 Out; entrega até 26 Out)

| Quando | Marco |
|---|---|
| Seg 12 – Ter 13 | Todos: ambiente instalado e `teste.jsp` a abrir. A: esqueleto em `main`, convites enviados. |
| Qui 15 | **M1:** A publica `model` + `DataStore` + `NegocioException` em `main`. B e C com a primeira entrega (login/produtos; fornecedores/campanhas). |
| Sáb 17 | Funcionalidades principais prontas em cada branch (com stubs). |
| Dom 18 – Seg 19 | **Congelamento:** só correcções. Push final de cada branch na segunda à noite. |
| Ter 20 – Qui 22 | **Integração** (A). Testes cruzados do fluxo completo. B e C disponíveis para corrigir. |
| Sex 23 – Sáb 24 | Deploy com systemd (C), README, manual PDF (B), ensaio da defesa (todos). |
| Dom 25 | Folga de segurança. Nada novo. |
| Seg 26 | Entrega. |

Confirma a data real da defesa com o docente: o "26" é o limite que o grupo tem.

## 12. Armadilhas conhecidas

- **`javax.*` vs `jakarta.*`**: a IA escreve `javax.servlet` por hábito. Aqui tem de ser `jakarta.servlet`. Se vires `javax`, está errado.
- **Tomcat 10.1** (não 9, não 11). Em Tomcat 9 nada do `jakarta` funciona.
- **Persistência no Eclipse:** o ficheiro só é gravado se o servidor parar com **Stop**. O botão **Terminate** mata o processo e perde os dados.
- No Windows, define o Eclipse para UTF-8: Window → Preferences → General → Workspace → *Text file encoding* = UTF-8.
- Mudou um campo do `model`? Apaga o `.ser` antigo (D12).
- Porta **8081**; se estiver ocupada, fecha o outro Tomcat.
