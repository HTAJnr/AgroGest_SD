# AgroGest — Plano de sessões (contexto + tarefas de execução)

Uso: em cada sessão nova, anexar **este ficheiro** + os ficheiros que a tarefa pede + (nas tarefas de UI) o `DESIGN.md`. Dizer só: *"Faz T3"*.
Estado de partida: 24 Out 2026. Entrega possível a 26 Out (confirmar com o docente). **I1 concluída** (stubs removidos, `mvn clean package` e primeiro teste integrado verdes). O Helder (Pessoa A) assumiu também as tarefas da Naira (C) e do Ayko (B8, manual).

---

## 0. Regras para a IA (valem em todas as sessões)

1. Se faltar algum ficheiro listado na tarefa, **pede-o**. Não inventes nomes de classes, campos, rotas ou atributos.
2. **Lê** cada ficheiro antes de o editar. Só toca nos ficheiros listados na tarefa. No fim, lista os ficheiros alterados.
3. Este documento (secção 1) substitui o antigo contrato. Não renomeies classes, campos, métodos, rotas, atributos de sessão nem classes CSS existentes.
4. Tecnologia fixa: Java 17, Jakarta EE (`jakarta.*`, **nunca `javax.*`**), Tomcat 10.1, Servlets 6, JSP + JSTL + EL. Sem base de dados, sem frameworks, **sem scriptlets** (`<% %>`, `<%= %>`, `<%! %>`), sem `<jsp:useBean>`, sem dependências novas no `pom.xml`. JavaScript só em `assets/js/app.js` (e atributos como `onclick="window.print()"`).
5. Texto da interface e comentários em **português de Portugal**. Tudo UTF-8. Teste obrigatório: "João Açúcar".
6. Fatias pequenas: gera, confirma que compila, diz como testar. Nunca avances para a tarefa seguinte sem o utilizador confirmar.
7. O utilizador tem de **defender oralmente** cada ficheiro. No fim de cada tarefa explica, em linguagem simples e por ficheiro, o que mudou e porquê. Se uma linha for difícil de explicar, simplifica-a.
8. Não mudes campos do `model` (se mudares, avisa: é preciso apagar `/opt/tomcat/data/agrogest.ser`).
9. Fim de sessão (feito pelo utilizador): `mvn clean package && tomcat deploy`, percorrer as páginas afectadas, `git add`, `git commit`.

### 0.1 Trabalhar com um agente no IDE (Antigravity)

Sim, podes usá-lo: edita os ficheiros directamente e acaba o vaivém. Mas **o agente corre comandos no teu PC**, por isso:

1. Antes de cada tarefa: `git status` limpo e commit do estado anterior. Rede de segurança: `git diff` para ver, `git restore .` para desfazer.
2. Prompt-padrão (substitui `{X}`):
   > Lê `docs/PLANO_SESSOES.md` (secções 0, 1, 2 e a tarefa {X}) e `docs/PROGRESS.md`. Faz **só** a tarefa {X}. Não toques em nenhum ficheiro que  a tarefa nao exige. Não faças commit, apenas de-me a mensagem de commit. No fim corre `mvn clean package`, mostra `git diff --stat` e explica cada ficheiro alterado em linguagem simples. Atualiza o ficheiro `docs/PROGRESS.md`.
3. Permitir: `mvn`, `grep`, `find`, `git status/diff`. **Não** permitir em modo automático: `sudo`, `rm -rf` fora de `src/` e `target/`, `git push`, `git reset --hard`, `systemctl`. As tarefas **D3 e D5 fazem-se à mão**; o agente só explica.
4. Revê o `git diff` antes do commit (é a tua defesa). Linha que não sabes explicar → pede ao agente para a simplificar.
5. Uma tarefa por conversa; conversa nova para a seguinte (contexto limpo).
6. Se o agente reformatar ficheiros inteiros, reordenar imports ou mexer no que não foi pedido: `git checkout -- <ficheiro>` e repete a tarefa a pedir só o necessário.
7. Testar no browser continua a ser contigo: `tomcat deploy`, percorrer as páginas.
8. Tarefas marcadas **Forte** na secção 3: pede uma **segunda passagem de revisão a outro modelo** (Claude ↔ Gemini) com o prompt: *"Revê este diff. Procura: falta de `synchronized`, listas vivas expostas, `javax.*`, scriptlets, 500 por input do utilizador, `${...}` sem `<c:out>`, e regras de negócio que mudaram sem o pedirmos."*

---

## 1. Contexto mínimo (substitui enunciado e contrato)

**Projecto:** AgroGest, gestão agrícola para um agricultor de Nampula (ISCTEM, Sistemas Distribuídos). Aplicação web **sem base de dados**: dados em listas em memória, gravados num ficheiro por serialização Java. Camadas: Filter → Servlets → Services → Repo (`DataStore`, `Persistencia`) → JSP/JSTL/CSS.

**Ambiente do Helder:** Linux, Maven, Tomcat 10.1 em `/opt/tomcat`, VS Code (sem plugins), tudo por terminal. Projecto em `/home/htajnr/HTA/ISCTEM/SD/AgroGest_SD`, branch **`helder`**. Função `tomcat {start|stop|restart|deploy|log|status|ip}` no `~/.bashrc`. Em desenvolvimento a porta é **8080**; em produção (systemd) **8081**. Contexto: `/agrogest`. Ficheiro de dados: `/opt/tomcat/data/agrogest.ser`. Ciclo normal: `mvn clean package && tomcat deploy && tomcat log`. Branch da Naira (C): `origin/naira` (só tem C2, fornecedores).

**Perfis:** Administrador (`admin@agro.mz` / `admin234`, criado no arranque) e Cliente (auto-registo).

**Módulo admin:** fornecedores, produtos (repor stock), campanhas (fechar/reabrir), cultivos, colheitas (valor total calculado), listagens ordenáveis, compras detalhadas, relatórios (clientes por nome; que compraram por volume; mais assíduos; sem compras), persistência (gravar/eliminar manual).
**Módulo cliente:** registo, catálogo com filtro por categoria, carrinho em sessão, compra, factura (imprimível), histórico.

**Regras de negócio:** RN01 não vender mais que o stock · RN02 venda atómica, tudo ou nada · RN03 cliente só vê as suas facturas (admin vê todas) · RN04 `/admin/*` só admin (403) · RN05 `/catalogo`, `/carrinho/*`, `/compra`, `/factura`, `/minhas-compras` exigem sessão (senão `/login`) · RN06 ids sequenciais únicos (`AtomicInteger`), mantidos após reinício.

**Avaliação:** funcionalidades 35% · arquitectura MVC 15% · interface e usabilidade 10% · validações e RN 15% · persistência 10% · deploy systemd 5% · documentação (README + manual PDF) 10%. **Entregáveis:** código, WAR, README.md (instalação/deploy, credenciais de teste, arquitectura), manual PDF (admin e cliente), defesa ao vivo.

**Pacotes:** `mz.agrogest.{model,repo,service,servlet,filter,util,listener}`. Views em `src/main/webapp/WEB-INF/views/…` (`auth/`, `cliente/`, `admin/{produtos,fornecedores,campanhas,cultivos,colheitas,dashboard,listagens,relatorios,persistencia}/`, `erro.jsp`). Fragmentos em `WEB-INF/jspf/` (`topo.jspf`, `rodape.jspf`). CSS em `webapp/css/estilo.css`.

### Modelo (`mz.agrogest.model`, todos `Serializable`, construtor vazio, getters/setters)

| Classe | Campos | Getters calculados |
|---|---|---|
| `Utilizador` | `int id`, `String nome, email, passHash`, `Perfil perfil`, `boolean ativo`, `String telefone, endereco` | `isAdmin()` |
| `Fornecedor` | `int id`, `String nome`, `TipoFornecedor tipo`, `String contacto, provincia` | |
| `Produto` | `int id`, `String nome`, `Categoria categoria`, `int fornecedorId`, `BigDecimal preco`, `double quantidade`, `String unidade, descricao, fotoUrl`, `LocalDate validade` (nulo possível) | `getValidadeFmt()` |
| `Campanha` | `int id`, `String nome`, `LocalDate inicio, fim`, `boolean aberta` | `getInicioFmt()`, `getFimFmt()` |
| `Cultivo` | `int id, campanhaId`, `String cultura, variedade`, `double hectares`, `LocalDate plantio` | `getPlantioFmt()` |
| `Colheita` | `int id, cultivoId`, `LocalDate data`, `double kg`, `BigDecimal precoKg` | `getValorTotal()`, `getDataFmt()` |
| `VendaItem` | `int produtoId`, `String produtoNome, unidade`, `double quantidade`, `BigDecimal precoUnit, subtotal` | |
| `Venda` | `int id`, `String numeroFactura`, `LocalDateTime data`, `int clienteId`, `String clienteNome, clienteEmail, clienteTelefone, clienteEndereco`, `List<VendaItem> itens`, `BigDecimal subtotal, iva, total` | `getDataFmt()` |
| `Carrinho` | `Map<Integer,Double>`; `adicionar, atualizar, remover, esvaziar`, `isVazio()`, `getTotalItens()`, `getQuantidades()` (cópia) | |
| `LinhaCarrinho` | `Produto produto`, `double quantidade`, `BigDecimal subtotal` | |

Enums: `Perfil{ADMIN,CLIENTE}`, `TipoFornecedor{SEMENTES,MAQUINARIA,FERTILIZANTES}`, `Categoria{SEMENTE,FERTILIZANTE,MAQUINARIA,FERRAMENTA}`. Os `...Fmt()` devolvem `dd/MM/yyyy` (`Venda`: `dd/MM/yyyy HH:mm`) ou `""`, porque `<fmt:formatDate>` não funciona com `java.time`.

### `DataStore` e concorrência
`DataStore.getInstance()`; `getLock()` (**um único** monitor para todas as escritas); listas vivas `getUtilizadores/Fornecedores/Produtos/Campanhas/Cultivos/Colheitas/Vendas()`; `novoIdUtilizador/Fornecedor/Produto/Campanha/Cultivo/Colheita/Venda()` (AtomicInteger).
Regras: toda a escrita dentro de `synchronized (DataStore.getInstance().getLock()) {…}`; leituras devolvem **cópias** (`new ArrayList<>(...)`); ids só por `novoIdX()`, nunca `size()+1`.

### Serviços (classe `final`, singleton `getInstance()`, erros por `mz.agrogest.util.NegocioException` unchecked com mensagem em português; `porId` devolve `null` se não existir)
- `UtilizadorService`: `registarCliente(nome,email,password,telefone,endereco)`, `autenticar(email,password)` (null se inválido), `porId`, `listarClientes`, `garantirAdmin`.
- `VendaService`: `finalizarCompra(Utilizador, Map<Integer,Double>)`, `porId`, `porCliente(id)`, `todas()`.
- `ProdutoService`: `criar(nome,categoria,fornecedorId,preco,quantidade,unidade,descricao,fotoUrl,validade)`, `listar`, `listarEmStock`, `listarPorCategoria`, `porId`, `reporStock(id,qtd)` (soma), `remover`, `existeComFornecedor(id)`.
- `FornecedorService`: `criar(nome,tipo,contacto,provincia)`, `listar`, `porId`, `remover` (recusa se tiver produtos).
- `CampanhaService`: `criar(nome,inicio,fim)` (fim ≥ início; nasce aberta), `listar`, `porId`, `fechar`, `reabrir`, `remover` (recusa se tiver cultivos).
- `CultivoService`: `criar(campanhaId,cultura,variedade,hectares,plantio)`, `listar`, `listarPorCampanha`, `porId`, `remover` (recusa se tiver colheitas).
- `ColheitaService`: `registar(cultivoId,data,kg,precoKg)`, `listar`, `listarPorCultivo`.
- Dinheiro: `BigDecimal`, 2 casas, `HALF_UP`. IVA = subtotal × 0,16; total = subtotal + IVA. Nº factura `FT-{ano}-{id da venda}`.

### Rotas
| Rota | Servlet | Notas |
|---|---|---|
| `/` | `HomeServlet` | anónimo → `/login`; cliente → `/catalogo`; admin → `/admin/dashboard` |
| `/login`, `/logout`, `/registo` | `LoginServlet`, `LogoutServlet`, `RegistoServlet` | login faz `changeSessionId()`; erro genérico "Email ou palavra-passe incorretos" |
| `/catalogo` (`?categoria=`), `/carrinho/*`, `/compra`, `/factura?id=`, `/minhas-compras` | `Catalogo/Carrinho/Compra/Factura/MinhasComprasServlet` | factura: dono ou admin; outro → 403; inexistente → 404 |
| `/admin/produtos/*` | `AdminProdutoServlet` | |
| `/admin/dashboard`, `/admin/listagens/*` (`?ordenar=&dir=`), `/admin/relatorios/*`, `/admin/persistencia/*` | `Dashboard/Listagem/Relatorio/PersistenciaServlet` | listagens: produtos, fornecedores, clientes, armazem, compras. Relatórios: clientes, compraram, assiduos, sem-compras |
| `/admin/fornecedores/*` | `AdminFornecedorServlet` | feito pela Naira (C2) |
| `/admin/campanhas/*`, `/admin/cultivos/*` (`?campanhaId=`), `/admin/colheitas/*` (`?cultivoId=`) | a criar (F1–F3) | |

**Filtro:** `AuthFilter` em `/*`. Públicas: `/`, `/login`, `/registo`, `/logout`, `/css/*` (e `/teste.jsp` até ser apagado). **A passar a públicas também `/assets/*` (T1).** Sem sessão → redirect `/login`; não-admin em `/admin/*` → 403.
**Sessão:** atributos `utilizador` (`Utilizador`) e `carrinho` (`Carrinho`).

### Convenções web
1. Servlet → JSP por `req.getRequestDispatcher("/WEB-INF/views/…").forward(...)`.
2. `topo.jspf` e `rodape.jspf` entram **sozinhos** em todos os `*.jsp` (configurado no `web.xml`). Nos JSP **não** se escreve `<html>/<head>/<body>` nem se declaram as taglibs `c`, `fmt`, `fn` (já estão). Se for preciso declarar, os URIs são `jakarta.tags.core`, `jakarta.tags.fmt`, `jakarta.tags.functions`. Só `topo.jspf` tem `<html>`.
3. Links e recursos sempre `<c:url value="/catalogo"/>`, nunca `/agrogest/...` à mão. Texto de utilizador sempre `<c:out>`.
4. Alterações só por **POST → redirect** com `session.setAttribute("flashOk", msg)`; erro de validação → `req.setAttribute("erro", msg)` e forward ao formulário. O topo mostra `flashOk`, `flashErro`, `mensagem`, `erro` e apaga os `flash*`. Os servlets apanham `NegocioException`, `NumberFormatException`, `DateTimeParseException`, `IllegalArgumentException` e nunca devolvem 500 por input.
5. Dinheiro: `<fmt:formatNumber value="${x}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT` (locale `pt_PT`: `70 400,00`; é o esperado).
6. Classes CSS que **existem e não se renomeiam**: `barra`, `conteudo`, `grelha`, `cartao`, `tabela` (`.num`), `tabela-scroll`, `btn`, `btn-sec`, `btn-perigo`, `acoes`, `form`, `alerta ok|erro`, `badge`, `muted`, `total`, `factura`, `nao-imprimir`. Podem acrescentar-se classes novas.

### Decisões de negócio
D1 `ativo` sem lógica · D2 factura `FT-{ano}-{id}` · D3/D4 dinheiro BigDecimal, preços sem IVA · D5 campanha **fechada** não aceita cultivos nem colheitas · D6 fornecedor com produtos não se remove · D7 remover produto não afecta vendas antigas · D8 "volume" = total gasto, "assíduos" = nº de compras · D9 listagens só de leitura · D10 `${catalina.base}/data/agrogest.ser`, escrita atómica, "eliminar" apaga só o ficheiro · D11 BCrypt, admin no arranque · D12 mudou um campo do model → apagar o `.ser`.

### Decisões acrescentadas em 24 Out (a seguir ao primeiro teste integrado)
- **D13 (categoria ↔ tipo de fornecedor):** `SEMENTE→SEMENTES`, `FERTILIZANTE→FERTILIZANTES`, `MAQUINARIA→MAQUINARIA`, `FERRAMENTA→MAQUINARIA` (o enunciado não tem tipo "FERRAMENTAS"). Um produto só aceita fornecedor compatível. Validado no `ProdutoService`; a UI filtra a lista de fornecedores pela categoria.
- **D14 (unicidade, sem distinguir maiúsculas):** fornecedor por nome; produto por nome + fornecedor; campanha por nome.
- **D15 (validade):** na criação não pode ser anterior a hoje. Produtos já expirados mostram o badge "Expirado" (não bloqueia a venda).
- **D16 (unidades fixas):** `kg`, `saco`, `litro`, `tonelada`, `unidade`. Constante em `util/Validacao`, **sem mexer no model**. Nunca texto livre (já apareceu "sacp").
- **D17 (formatos):** nomes 3–80 caracteres com pelo menos uma letra (nome de pessoa: só letras, espaços, `'`, `-`, `.`); endereço 5–150; palavra-passe **mínimo 4** (os exemplos do enunciado usam `1234`; não impor mais); telefone/contacto de Moçambique (ver V1); províncias numa lista fixa das 11.
- **D18 (erros por campo):** `CampoException extends NegocioException` com o `name` do campo; os servlets põem `erroCampo` no request; a UI marca o campo e mostra a mensagem debaixo dele, além do toast.
- **D19 (anti duplo envio):** token de formulário `_t` em todos os POST (`TokenFormFilter`) + botão desactivado no submit.


---

## 2. Decisões de design (UI)

**Objectivo:** não chega ser "correcto e legível". A aplicação deve parecer **cuidada e interessante**: agrícola, moderna, calorosa. Não é a prioridade n.º 1, mas conta nos 10% de Interface e na impressão da defesa. Estas decisões **prevalecem sobre o `DESIGN.md`** quando houver conflito; em tudo o resto segue-se o `DESIGN.md` (tokens `var(--…)`, sem cores à mão, sem `!important`, foco visível, alvos ≥ 44 px, corpo ≥ 16 px, **nunca texto e fundo da mesma cor**, hover = fundo muda para o tom seguinte e o texto mantém a cor). **Tudo responsivo** (quebra a 640 px; testar a 360 px); dropdowns por toque e teclado, não só hover.

### 2.1 Direcção visual
- **Carácter:** verde-floresta sobre papel claro, toque dourado (`accent`) só em destaques, cantos 12 px, sombras suaves, muito espaço em branco, ícones de traço 2 px.
- **Tipografia:** Inter **self-hosted** (`assets/fonts/Inter-Variable.woff2`, `@font-face`, `font-display:swap`), com o fallback do `DESIGN.md`. Sem CDN (a rede da defesa pode falhar).
- **Cabeçalho:** barra `#14301d` com filete inferior de 3 px em gradiente `primary → accent`.
- **Faixa de abertura (hero)** no catálogo: gradiente `primary-hover → primary`, folha do logo como marca de água a ~8% de opacidade, texto branco.
- **Micro-interacções (120–180 ms):** botões (cor + `active` a 98%), links da barra, dropdown (fade + 4 px), toasts (slide-in). Cartões **clicáveis** (dashboard, categorias) sobem 2 px com sombra `overlay`; cartões não clicáveis **sem** hover. Linhas de tabela realçam com `primary-tint` a 50%. `prefers-reduced-motion` desliga tudo.
- **Estados vazios** com ícone, frase e acção (nunca só "Não há…").
- **Badges semânticos** (todos AA): `.badge` (accent, categoria), `.badge.ok` (Aberta, Em stock), `.badge.off` (Fechada, Indisponível, Esgotado), `.badge.aviso` (Expirado, Stock baixo).
- **Botões:** ícone + texto nos principais; só-ícone com `aria-label` e `title`.

### 2.2 Estrutura das páginas (resolve "tudo encostado à esquerda")
- **Um único `.conteudo`:** `max-width:1100px; margin-inline:auto; padding:24px` (12 px em mobile). **Proibido** margem/padding próprios por página (hoje a Listagens está deslocada ~20 px face a Produtos).
- **`.pagina-topo`:** título à esquerda, **acção principal à direita** (flex, quebra em mobile). Ex.: "Produtos" … [+ Novo produto].
- **Formulários:** cartão **centrado**, `max-width:720px`, grelha de 2 colunas a partir de 720 px (campos curtos lado a lado; descrição e endereço ocupam as 2), 1 coluna em mobile; botões à direita (Cancelar `btn-sec` + Guardar `btn`).
- **Listas:** tabela a toda a largura do cartão; **nunca** formulário embutido na lista (fornecedores passam ao padrão lista + "+ Novo", como produtos).
- **Abas (`.abas`)** em Listagens e Relatórios: barra horizontal, sublinhado `primary` na activa, `aria-current="page"`, scroll horizontal em mobile. As "caixas com borda e sublinhado" actuais são um bug de estilo.
- **Acções de linha:** botões de ícone 44×44 (`mais` para repor stock, `lixo` para remover, estilo compacto `btn-sec` / `btn-perigo`), `title` + `aria-label`, alinhados ao centro da linha. "Repor" abre um `<dialog>` pequeno com a quantidade. **Nunca** `<button>`/`<input>` sem classe (hoje o "Remover" aparece com texto preto sobre vermelho).
- **Números:** quantidades sempre com `fmt:formatNumber maxFractionDigits="2"` + unidade (hoje `0.0 kg` num ecrã e `0 kg` noutro).

### 2.3 Navbar
- **Cliente (1 linha, grelha `1fr auto 1fr`):** esquerda = menu do utilizador; centro = marca (liga a `/`); direita = ícone do carrinho com badge `n` (mostrar também `0`) que **leva a `/carrinho`**. Sem link "Catálogo". Popover do carrinho: **fora do âmbito**.
- **Admin (2 linhas):** linha 1 = menu do utilizador à esquerda, **marca centrada**; linha 2 = navegação **centrada**: Dashboard · **Gestão ▾** (Produtos, Fornecedores, Campanhas, Cultivos, Colheitas) · **Listagens ▾** (Produtos, Fornecedores, Clientes, Armazém, Compras detalhadas) · **Relatórios ▾** (Clientes, Que compraram, Mais assíduos, Sem compras). Link activo com sublinhado `accent`. Mobile: a linha 2 colapsa num menu hambúrguer (ícone à direita da linha 1).
- **Menu do utilizador (ambos):** botão com ícone + nome (+ seta). Painel com cabeçalho (círculo com a inicial, nome, email, perfil num `.badge`) e itens: **cliente** → Minhas compras; **admin** → **Persistência** (sai da barra e passa para aqui); ambos → separador e **Sair**. "Sair" é **neutro** (nunca vermelho: `danger` só para erro/remoção), com ícone. Itens ≥ 44 px; fecha com Esc e clique fora; em mobile o painel ocupa a largura toda.

### 2.4 Dashboard
Cartões de contagem **clicáveis** (`<a>`): círculo `primary-tint` com ícone, número 32 px tabular, etiqueta, seta que aparece no hover. Ligações: fornecedores → `/admin/fornecedores`; produtos → `/admin/produtos`; clientes → `/admin/listagens/clientes`; vendas → `/admin/listagens/compras`; campanhas → `/admin/campanhas`.

### 2.5 Login e registo (anónimo)
Sem barra. Layout próprio, centrado, com a marca e o subtítulo "Sistema de Gestão Agrícola". Decide-se no `topo.jspf` por `empty sessionScope.utilizador`. Erro **inline** no cartão (sem empurrar o layout); email mantido após erro; mensagem genérica de login inalterada.

### 2.6 Formulários e feedback
- **Feedback no elemento:** cada erro aparece **debaixo do campo** (ícone + texto, borda `danger`, `aria-invalid`), **e** num toast; o foco vai para o primeiro campo com erro. Vale para erros do cliente (HTML5 + `app.js`) **e** do servidor (D18). Os valores escritos mantêm-se após erro.
- **Unidades:** fixas (D16), em **radio estilizado como chips**, não texto. Ao escolher a categoria sugere-se a unidade (`SEMENTE→kg`, `FERTILIZANTE→saco`, `MAQUINARIA/FERRAMENTA→unidade`).
- **Categoria filtra fornecedores (D13).** Sem fornecedores compatíveis: aviso com link "Criar fornecedor".
- **Datas:** `<input type="date">` **nativo** (o calendário é do browser; Java não faz calendários, só `LocalDate.parse` do formato ISO `yyyy-MM-dd`). Validade com `min` = hoje. Sem biblioteca externa.
- **Telefone/contacto:** `inputmode="tel"`, exemplo `84 123 4567`.
- **Província:** `<select>` das 11 províncias.
- **Foto:** escolher da galeria (miniaturas) ou colar um URL.
- **Toasts:** sucesso ~5 s; erro persistente com botão fechar; em login/registo o erro fica inline.

### 2.7 Logo e ícones
- **Logo:** a **marca** (folha dentro de anel de engrenagem) em **SVG** (cor e branca). O nome "AgroGest" é **texto HTML/CSS** ("Agro" verde, "Gest" cinzento), nunca imagem. O PNG fornecido (fundo cinzento) **não** entra na UI; serve para README/manual. Barra escura → marca branca.
- **Ícones:** SVG do Lucide, montados em sprite inline `WEB-INF/jspf/icons.jspf` (herdam `currentColor`; ficheiros-fonte em `assets/icons/`). Nomes: `utilizador`, `carrinho`, `seta-baixo`, `seta-direita`, `sair`, `recibo`, `fechar`, `check`, `alerta`, `info`, `menu`, `mais`, `lixo`, `editar`, `imagem`, `dashboard`, `caixa` (produtos), `camiao` (fornecedores), `pessoas` (clientes), `folha` (campanhas), `cofre` (persistência).
- **Rodapé:** texto actual + linha "Ayko Sambo · Naira Ornilo · Helder Vuca".

### 2.8 Fotos de produtos
Formato único em todo o lado: **4:3**, `object-fit:cover`, mesmo raio, `loading="lazy"`. Detalhe e lista de imagens em T5.

---

## 3. Ordem de execução, linha de corte e modelos

**Estado a 24 Out:** I1 concluída (stubs removidos, `mvn clean package` e primeiro teste integrado verdes). Foram encontradas lacunas de validação e de duplo envio (V1, V2) e o design precisa de trabalho (T1–T8).

| Ordem | Tarefa | Valor |
|---|---|---|
| 1 | **V1** Validações de servidor + erros por campo | RN e validações valem 15%; hoje aceita `12345678` como contacto e nomes de 1 letra |
| 2 | **V2** Anti duplo envio | evita **duas vendas** com um duplo clique em "Confirmar compra" |
| 3 | **F0** Fornecedores em lista + "Novo" | base do padrão para F1–F3 |
| 3b | **T0** Ícones (sprite + `.icone`) | 20 min; as páginas F1–F3 já nascem com ícones |
| 4 | **F1, F2, F3** Campanhas, cultivos, colheitas | requisito do módulo admin (35%) |
| 5 | **T1** Fundação visual (corrige o hover) | 45 min, defeito visível |
| 6 | **D1** README · **D3** Deploy systemd | 10% + 5% |
| 7 | **T3** Navbar · **T4** Toasts e feedback no campo · **T2** Login/registo | polimento principal |
| 8 | **T7** Admin (dashboard, listagens, relatórios, produtos, persistência) · **T5** Catálogo e fotos | onde estão as piores páginas |
| 9 | **A10** Dados de demonstração (opcional) | ajuda manual e defesa |
| 10 | **D2** Manual PDF (só com a UI estável) · **D4** Release · **D5** Limpeza · **P1** Guião | fecho |
| 11 | **T6, T8** Resto da UI e auditoria | só se sobrar tempo |

**Congelar a UI no domingo 25 à tarde.** Se até domingo às 18h não estiverem feitas V1, V2, F0–F3 e T1, **corta tudo a partir de T2**. Segunda 26: só correcções críticas, WAR final, tag, release.

### Modelo por tarefa

Não sei que versões exactas o Antigravity te oferece; escolhe pelo **nível**. **Forte** = o melhor modelo que tiveres (família Claude Opus, ou o Gemini Pro mais recente com raciocínio no máximo). **Médio** = Claude Sonnet / Gemini Pro normal. **Rápido** = Haiku / Flash, só para trabalho mecânico.

| Tarefa | Nível | Porquê |
|---|---|---|
| V1 Validações + `CampoException` | **Forte** | regras de negócio e serviços partilhados; um erro aqui estraga o registo, produtos e vendas |
| V2 Token anti duplo envio | **Forte** | filtro + sessão + concorrência; falha silenciosa é grave |
| F0 Fornecedores (padrão lista + novo) | Médio | copia o padrão de produtos |
| F1–F3 Campanhas, cultivos, colheitas | Médio, **revisão Forte do diff** | CRUD repetitivo, mas com D5, locks e regras de remoção |
| A10 Dados de demonstração | Médio | tem de passar por `VendaService` |
| T0 Ícones (sprite) | Médio | mecânico, mas um `id` errado parte todos os botões |
| T1 Fundação CSS | Médio | |
| T2 Login/registo | Médio | |
| T3 Navbar + JS de dropdown | Médio; **Forte se falhar à 2.ª** | muitas partes móveis (JSP condicional, JS, responsivo, acessibilidade) |
| T4 Toasts, feedback no campo, diálogos | **Forte** | JS + mapeamento de erros + valores mantidos em ~10 formulários |
| T5, T6, T7 UI de páginas | Médio | |
| T8 Auditoria por `grep` | Rápido | mecânico |
| D1 README | Médio | tem de descrever a arquitectura com rigor |
| D2 Manual | Rápido | texto; as capturas são tuas |
| D3 Deploy systemd | **Forte**, executado **à mão** | `sudo`, permissões, perda de dados se errar |
| D4 Release, D5 Limpeza | Médio, executado **à mão** | comandos destrutivos |
| P1 Guião da defesa | Médio | |

Regra: qualquer diff que toque em `service/`, `filter/` ou `servlet/` passa por uma revisão com modelo Forte antes do commit (0.1, ponto 8).

### 3.1 O que tens de obter (o resto é gerado)
| O quê                                                   | Onde fica                     | Notas                                                  |
| ------------------------------------------------------- | ----------------------------- | ------------------------------------------------------ |
| `Inter-Variable.woff2` (licença OFL), subconjunto latin | `webapp/assets/fonts/`        | descarregar da página oficial do Inter (rsms.me/inter) |
| **15 fotos de produtos** (lista em T5)                  | `webapp/assets/img/produtos/` | Unsplash, Pexels ou Pixabay (licença livre)            |
| `logo-agrogest.jpg` (o que já tens)                     | `webapp/assets/img/`          | só para README/manual                                  |

**Já feitos (entregues no chat):** `logo-marca.svg`, `logo-marca-branca.svg`, `favicon.svg`, `favicon-32.png`, `apple-touch-icon.png`, `produto-placeholder.svg` → `webapp/assets/img/`.

**Ícones: descarregar do Lucide** (lucide.dev, licença ISC; botão *Download SVG* de cada um) para `webapp/assets/icons/`. Mapeamento (confirma o nome no site, alguns foram renomeados):

Atualmente estao em assets/, mover para pasta correcta na execusao de alguma tarefa relacionada com isso.

| Ficheiro                     | Categoria    | Produto de demo       | Unidade | Preço (MT) | Stock |
| ---------------------------- | ------------ | --------------------- | ------- | ---------- | ----- |
| `milho.jpg`                  | SEMENTE      | Semente de milho      | kg      | 95         | 500   |
| `feijao.jpg`                 | SEMENTE      | Semente Feijão nhemba | kg      | 320        | 400   |
| `adubo-saco.jpg`             | FERTILIZANTE | Adubo NPK 50 kg       | saco    | 1 450      | 50    |
| `fertilizante-granulado.jpg` | FERTILIZANTE | Ureia granulada       | saco    | 1 800      | 40    |
| `trator.jpg`                 | MAQUINARIA   | Trator 60 CV          | unidade | 1 250 000  | 2     |
| `pulverizador.jpg`           | MAQUINARIA   | Pulverizador costal   | unidade | 4 500      | 10    |
| `enxada.jpg`                 | FERRAMENTA   | Enxada                | unidade | 450        | 80    |
| `regador.jpg`                | FERRAMENTA   | Regador 10 L          | unidade | 350        | 40    |
**Fornecedores de demo (3, como no enunciado).** A D13 obriga a que o tipo do fornecedor seja compatível com a categoria:

- **Sementes do Sul, Lda**, tipo SEMENTES: milho e feijão.
- **AgroNutri, Lda**, tipo FERTILIZANTES: adubo e ureia.
- **MaqAgro, Lda**, tipo MAQUINARIA: trator, pulverizador, enxada e regador, porque FERRAMENTA usa fornecedor MAQUINARIA.

| Nosso nome     | Lucide           | Nosso nome  | Lucide             |
| -------------- | ---------------- | ----------- | ------------------ |
| `utilizador`   | `user`           | `lixo`      | `trash-2`          |
| `carrinho`     | `shopping-cart`  | `editar`    | `pencil`           |
| `seta-baixo`   | `chevron-down`   | `imagem`    | `image`            |
| `seta-direita` | `arrow-right`    | `dashboard` | `layout-dashboard` |
| `sair`         | `log-out`        | `caixa`     | `package`          |
| `recibo`       | `receipt`        | `camiao`    | `truck`            |
| `fechar`       | `x`              | `pessoas`   | `users`            |
| `check`        | `check`          | `folha`     | `leaf`             |
| `alerta`       | `triangle-alert` | `cofre`     | `hard-drive`       |
| `info`         | `info`           | `mais`      | `plus`             |
| `menu`         | `menu`           |             |                    |

---

## 4. Tarefas

### I1 · Integração e QA (A8) — CONCLUÍDA em 24 Out (manter como checklist de regressão)
**Enviar:** `git log --oneline -5 origin/naira`, `find src -path '*stub*' -name '*.java'`, saída de `mvn -q compile`, `AuthFilter.java`.
**Fazer:**
1. `git switch helder && git switch -c teste-integracao && git fetch origin && git merge origin/naira`.
2. Stubs: com a `naira` mergeada o `FornecedorService` real existe e o `ProdutoService` real é da B, por isso serve o comando completo:
   `rm -rf src/main/java/mz/agrogest/stub && grep -rlE 'mz\.agrogest\.stub\.' src | xargs -r sed -i -E 's/mz\.agrogest\.stub\.[bc]\./mz.agrogest./g'`.
   Se a `naira` **não** estiver mergeada, não apagar `stub/b/service/FornecedorService`.
3. Apagar `src/main/webapp/teste.jsp` e retirar `/teste.jsp` das rotas públicas do `AuthFilter`.
4. `mvn clean package` (os testes JUnit correm e têm de passar), `tomcat deploy`, `tomcat log`.
5. Percorrer o checklist (cada ponto: esperado → resultado):
   - Anónimo em `/catalogo`, `/carrinho`, `/compra`, `/factura?id=1`, `/minhas-compras`, `/admin/dashboard` → `/login` (RN05). `/css/estilo.css` abre sem login.
   - Registo "João Açúcar" `joao@teste.mz`/`1234`; repetir com `JOAO@teste.mz` → erro; campos vazios → erro, nunca 500; login errado → mensagem genérica; `JSESSIONID` muda após login.
   - Cliente em `/admin/*` → 403 (RN04). Logout + botão "voltar" não reabre o catálogo.
   - Admin: criar fornecedor, 3 produtos (um com stock 0, um com stock 1), repor stock (0 e −5 recusados), campos inválidos dão erro no formulário.
   - Catálogo: filtro, indisponível, sem foto. Carrinho: `3,5` e `3.5`, actualizar/remover/esvaziar, contador, excesso de stock.
   - Compra: caminho feliz → `FT-2026-1`, IVA = subtotal × 0,16. **RN01** com dois clientes (janela anónima): o segundo recebe erro e o carrinho fica intacto. **RN02**: item 1 com stock + item 2 sem stock → stock do 1 não muda. Produto removido entretanto → erro limpo.
   - Factura: `?id=` de outro cliente → 403 (RN03); `9999`, `abc`, sem id → 404/400, **nunca 500**; admin vê todas; Ctrl+P sem menu nem botões.
   - Admin A: dashboard, 5 listagens com ordenação por todos os campos, compras detalhadas completas, 4 relatórios, "Gravar agora".
   - Persistência (RN06): `tomcat stop` → `ls /opt/tomcat/data/` → `tomcat start` → dados iguais e próxima factura = n+1. Ficheiro corrompido (`echo lixo > …agrogest.ser`) → arranca vazio e aparece `.ser.bak`. `strings agrogest.ser | grep 1234` e `grep 1234 /opt/tomcat/logs/*` → nada.
6. Se tudo passar: `git switch helder && git merge teste-integracao`; só depois `main` (secção D4).
**Pronto quando:** checklist inteiro verde. Nome de getter errado só rebenta ao abrir a página (`PropertyNotFoundException` no log).

### V1 · Validações de servidor (regras de negócio) e erros por campo
**Modelo:** Forte (e 2.ª revisão por outro modelo).
**Problemas observados no teste:** contacto `12345678` aceite no registo e nos fornecedores; nome com 1 carácter aceite; unidade livre ("sacp"); validade de 2024 aceite; fornecedor de qualquer tipo para qualquer categoria; produto repetido.
**Enviar:** `UtilizadorService.java`, `ProdutoService.java`, `FornecedorService.java`, `NegocioException.java`, `Produto.java`, `Fornecedor.java`, `SessaoUtil.java`, `RegistoServlet.java`, `AdminProdutoServlet.java`, `AdminFornecedorServlet.java`, o teste JUnit do `VendaService` (para não o partir).
**Fazer:**
1. `util/CampoException extends NegocioException` com `(String campo, String mensagem)` e `getCampo()` (o `campo` é o atributo `name` do input). Sendo subclasse, todos os `catch (NegocioException)` actuais continuam a funcionar.
2. `util/Validacao` (métodos estáticos que lançam `CampoException`; texto normalizado com `trim` e espaços internos colapsados):
   - `nomePessoa(campo,v)`: 3–80; só letras (com acentos), espaços, `'`, `-`, `.`; não pode ser só dígitos.
   - `nomeCoisa(campo,v)` (produto, fornecedor, campanha, cultura, variedade): 3–80, pelo menos uma letra.
   - `email(campo,v)`: `^[^@\s]+@[^@\s]+\.[^@\s]{2,}$`, ≤ 120, minúsculas.
   - `telefoneMz(campo,v)`: remover espaços, `-`, `(`, `)`; aceitar prefixo `+258` ou `00258` opcional; depois **telemóvel** `8[2-7]\d{7}` (9 dígitos: 82/83 Tmcel, 84/85 Vodacom, 86/87 Movitel) ou **fixo** `2\d{7}` (8 dígitos). Guardar normalizado (ex.: `+258 84 123 4567`). Mensagem: *"Introduza um número moçambicano válido, por exemplo 84 123 4567."* (`12345678` tem de ser recusado.) Regras do plano de numeração; se o docente der exemplos diferentes, ajustar.
   - `contactoFornecedor(campo,v)`: aceita telefone MZ **ou** email.
   - `endereco(campo,v)`: 5–150. `password(campo,v)`: **4**–72 (BCrypt limita a 72; os exemplos do enunciado usam `1234`).
   - `precoPositivo` (≤ 2 casas, teto 10 000 000), `quantidadeNaoNegativa`, `quantidadePositiva` (reposição: > 0 e ≤ 1 000 000), `hectares > 0`, `kg > 0`, `precoKg > 0`.
   - `validade(campo, LocalDate)`: não anterior a hoje (D15). `unidade(campo,v)`: dentro de `Validacao.UNIDADES = {"kg","saco","litro","tonelada","unidade"}`. `provincia(campo,v)`: dentro de `Validacao.PROVINCIAS` (Maputo Cidade, Maputo, Gaza, Inhambane, Sofala, Manica, Tete, Zambézia, Nampula, Cabo Delgado, Niassa).
3. Aplicar nos serviços, **dentro do lock quando houver unicidade**: `UtilizadorService.registarCliente` (nome, email, password, telefone, endereço); `FornecedorService.criar` (nome, contacto, província, nome único D14); `ProdutoService.criar` (nome, preço, quantidade, unidade, validade, fornecedor existe, **compatibilidade categoria ↔ tipo D13**, unicidade nome+fornecedor D14); `ProdutoService.reporStock`.
   - D13: `SEMENTE→SEMENTES`, `FERTILIZANTE→FERTILIZANTES`, `MAQUINARIA→MAQUINARIA`, `FERRAMENTA→MAQUINARIA`. Erro: `new CampoException("fornecedorId", "O fornecedor «X» é do tipo SEMENTES e não fornece produtos da categoria MAQUINARIA.")`.
4. Servlets: nos `catch (NegocioException e)` acrescentar `erroCampo` ao request (idealmente um método `SessaoUtil.erro(req, e)` que põe `erro` e `erroCampo`, este último vazio se não for `CampoException`). `NumberFormatException` e `DateTimeParseException`: o servlet sabe o campo e preenche `erroCampo`. Aplicar em Registo, AdminProduto, AdminFornecedor (e nos novos de F1–F3).
5. `Produto.isExpirado()` (getter calculado; não altera campos nem serialização) para o badge "Expirado".
6. JUnit para `Validacao`: telefone `12345678` ✗, `841234567` ✓, `+258 84 123 4567` ✓, `21123456` ✓, `841234` ✗; nome `A` ✗, `Ana` ✓, `123` ✗; password `123` ✗, `1234` ✓; compatibilidade SEMENTE+MAQUINARIA ✗; produto duplicado ✗. E o `VendaService` continua verde.
**Pronto quando:** os casos acima falham no servidor com mensagem clara e `erroCampo` correcto; `mvn clean package` passa. **Não mudar campos do model** (sem D12). Dados já gravados no `.ser` não são revalidados: para testar, cria dados novos ou apaga o `.ser`.

### V2 · Anti duplo envio (servidor)
**Modelo:** Forte (e 2.ª revisão).
**Problema:** o duplo clique criou o produto duas vezes. POST+redirect não protege contra dois POST quase simultâneos. Em `/compra` seriam **duas vendas** com o stock descontado duas vezes.
**Enviar:** `AuthFilter.java`, `web.xml`, `SessaoUtil.java`, `CompraServlet.java`, `topo.jspf`, e todos os JSP com formulário (`grep -rln 'method="post"' src/main/webapp`).
**Fazer:**
1. `filter/TokenFormFilter` (`/*`, depois do `AuthFilter`). **GET:** gera `UUID`, guarda-o num conjunto da sessão (máx. 50, descarta o mais antigo) e põe-no no request como `tokenForm` (criar sessão se não existir, para login/registo). **POST:** exige o parâmetro `_t`; se existir no conjunto, **remove-o** (consome) e segue; se não, **não executa** o servlet e redirecciona para o Referer (ou `/`) com `flashErro` *"Este pedido já foi processado ou expirou. Verifique a lista antes de repetir."* Excepção: `/logout`. A remoção do token tem de ser atómica (`synchronized` na sessão ou `ConcurrentHashMap.remove`) para que dois POST simultâneos com o mesmo token deixem passar **um só**.
2. Em **cada** `<form method="post">` acrescentar `<input type="hidden" name="_t" value="${tokenForm}">`. Falha fechado: formulário esquecido = POST recusado, fácil de apanhar nos testes.
3. Vários formulários na mesma página (botões "Remover" numa tabela) partilham o token da página; depois do redirect recebe-se um novo.
4. O cliente (T4) desactiva o botão no submit: as duas camadas juntas.
**Pronto quando:** duplo clique rápido em "Guardar produto" cria **um** produto; duplo clique em "Confirmar compra" cria **uma** venda (conferir em `/admin/listagens/compras` e no stock); F5 na página de resultado não repete a operação; duas abas abertas, submeter em ambas, funciona; todos os formulários continuam a funcionar.
**Corte:** se faltar tempo, aplicar **só** a `/compra`. Não deixar a compra sem protecção.

### F0 · Fornecedores no padrão lista + "Novo" (código da Naira)
**Modelo:** Médio.
**Enviar:** `AdminProdutoServlet.java`, `views/admin/produtos/lista.jsp` e `form.jsp`, `AdminFornecedorServlet.java`, `views/admin/fornecedores/*.jsp`, `estilo.css`.
**Fazer:** copiar o padrão de produtos. `GET /admin/fornecedores` → `lista.jsp` (cabeçalho de página com "+ Novo fornecedor" à direita, tabela, «Remover» em texto por agora: passa a ícone na T0); `GET /admin/fornecedores/novo` → `form.jsp` (cartão centrado, 2 colunas, `tipo` em `<select>`, `provincia` em `<select>` das 11 províncias, contacto com `inputmode="tel"`, botões Cancelar/Guardar); `POST /admin/fornecedores` cria → redirect + `flashOk`; erro → forward ao form com os valores mantidos e `erroCampo`. Este é o padrão de F1–F3.
**Pronto quando:** não há formulário dentro da lista; "+ Novo fornecedor" abre o formulário; Cancelar volta à lista; remover fornecedor com produtos continua recusado.

### T0 · Ícones (sprite) — logo a seguir à F0, antes de F1–F3
**Modelo:** Médio.
**Pré-requisito:** os SVG do Lucide descarregados para `src/main/webapp/assets/icons/` (tabela da secção 3.1), com o nome original do Lucide (`trash-2.svg`, `plus.svg`, …).
**Enviar:** `topo.jspf`, `estilo.css`, a lista de ficheiros de `assets/icons/` e um deles como exemplo (`user.svg`).
**Fazer:**
1. Com um script descartável (fora do repositório), gerar `WEB-INF/jspf/icons.jspf`: um `<svg xmlns="http://www.w3.org/2000/svg" style="display:none" aria-hidden="true">` com um `<symbol id="i-{nome nosso}" viewBox="0 0 24 24">` por ícone (conteúdo interior do ficheiro Lucide, sem o `<svg>` exterior), pelo mapeamento da 3.1 (`trash-2.svg` → `i-lixo`, `plus.svg` → `i-mais`, …). Ícone em falta → avisar, não inventar.
2. No `topo.jspf`, logo a seguir a `<body>`: `<%@ include file="/WEB-INF/jspf/icons.jspf" %>` (directiva; não é scriptlet).
3. CSS: `.icone { width:1.25em; height:1.25em; flex:none; fill:none; stroke:currentColor; stroke-width:2; stroke-linecap:round; stroke-linejoin:round; vertical-align:-.2em; }` e `.btn-icone { min-width:44px; padding:8px; }`.
4. Uso nos JSP: `<svg class="icone" aria-hidden="true"><use href="#i-lixo"/></svg>`. Botão só-ícone: `<button class="btn-perigo btn-icone" aria-label="Remover" title="Remover">…</button>`. Botão com texto: ícone antes do texto (o `.btn` já tem `gap`).
5. Aplicar já nas páginas da F0: "+ Novo fornecedor" com `mais`, "Remover" com `lixo`.
**Pronto quando:** o ícone aparece com a cor do texto e **acompanha o hover** do botão; não há pedidos de rede extra; nenhuma página dá erro de JSP; botões só-ícone têm `aria-label`.

### F1 · Campanhas — `/admin/campanhas/*`
**Modelo:** Médio, com revisão Forte do diff. **Pré-requisitos:** V1, V2, F0.
**Enviar:** `FornecedorService.java`, `AdminFornecedorServlet.java` e JSPs de fornecedores (**já no padrão lista + "Novo" da F0**), `Validacao.java`, `CampoException.java`, `Campanha.java`, `DataStore.java`.
**Fazer:** `CampanhaService` (padrão do `FornecedorService`; validações por `Validacao`; nome único D14): `criar` (nome 3–80, datas obrigatórias, fim ≥ início, `aberta=true`, `novoIdCampanha()`), `listar` (cópia), `porId`, `fechar`/`reabrir` (dentro do lock), `remover` (recusa com mensagem se existir `Cultivo` com `campanhaId`; verificar em `DataStore.getCultivos()` **dentro do lock**). `AdminCampanhaServlet` (`@WebServlet("/admin/campanhas/*")`): `GET` lista, `GET /novo` formulário (padrão da F0, valores mantidos e `erroCampo` em caso de erro); POST `criar`, `fechar`, `reabrir`, `remover` com POST+redirect+`flashOk`; apanhar as 4 excepções. JSP `lista.jsp`: tabela (`.tabela-scroll`), badge Aberta/Fechada, botões Fechar/Reabrir/Remover, link "Cultivos" → `/admin/cultivos?campanhaId={id}`.
**Datas:** usar a **mesma convenção de input de data** que a Naira/B já usam (confirmar nos ficheiros); `LocalDate.parse` do valor `yyyy-MM-dd` se for `type="date"`.
**Pronto quando:** fim < início dá erro; fechar/reabrir alterna o estado visível; remover campanha com cultivos mostra o motivo; mensagem "João Açúcar" ok; sem 500.

### F2 · Cultivos — `/admin/cultivos/*`
**Enviar:** os de F1 já feitos (`CampanhaService`, servlet e JSPs de campanhas), `Cultivo.java`.
**Fazer:** `CultivoService.criar` (campanha existe, **aberta** (D5), cultura 3–80 por `Validacao`, hectares > 0, plantio obrigatório), `listar`, `listarPorCampanha`, `porId`, `remover` (recusa se existir `Colheita` com `cultivoId`, verificar em `DataStore.getColheitas()` no lock). `AdminCultivoServlet`: **sem** `?campanhaId=` mostra a lista de campanhas para escolher; **com** ele mostra os cultivos dessa campanha e o formulário (desactivado com aviso se a campanha estiver fechada). Link para `/admin/colheitas?cultivoId={id}`.
**Pronto quando:** campanha fechada recusa novo cultivo; cultivo com colheitas não se remove; id de campanha inexistente → erro limpo.

### F3 · Colheitas — `/admin/colheitas/*`
**Enviar:** `CultivoService`, `AdminCultivoServlet`, JSPs de cultivos, `Colheita.java`.
**Fazer:** `ColheitaService.registar` (cultivo existe, campanha do cultivo **aberta** (D5), data obrigatória, kg > 0, precoKg > 0; sem remoção, o contrato não a prevê), `listar`, `listarPorCultivo`. `AdminColheitaServlet`: sem `?cultivoId=` lista cultivos para escolher; com ele lista colheitas e formulário. O **valor total** é `${c.valorTotal}` (`getValorTotal()` do modelo), **não se calcula no JSP**; formato `fmt:formatNumber … MT`.
**Pronto quando:** 3200 kg × 22 MT/kg mostra `70 400,00 MT`; valores ≤ 0 recusados; campanha fechada recusa.

### A10 · Dados de demonstração (opcional; só com I1 e F1–F3 verdes)
**Enviar:** `ContextListener.java`, `Persistencia.java`, `UtilizadorService.java`, `VendaService.java`.
**Fazer:** classe `util/DadosDemo` chamada pelo `ContextListener` **só se não houve restauro** e as listas estão vazias: 3 fornecedores, 3 produtos, 5 clientes (`joao@teste.mz`/`1234`, …), 1 campanha, 4 vendas **criadas via `VendaService.finalizarCompra`** (nunca a mexer nas listas à mão). Log "dados de demonstração carregados".
**Pronto quando:** primeiro arranque sem `.ser` mostra o dashboard do enunciado (3 | 3 | 5 | 4 | 1); segundo arranque (com `.ser`) **não** duplica.

### T1 · Fundação visual
**Enviar:** `estilo.css`, `topo.jspf`, `rodape.jspf`, `erro.jsp`, `web.xml`, `AuthFilter.java`, `DESIGN.md`, a imagem do logo.
**Fazer:**
1. `estilo.css`: bloco `:root` e componentes do `DESIGN.md` (§6); `.barra` com `--bar`; corrigir `.btn`, `.btn-sec`, `.btn-perigo` (repouso, hover, active, disabled, `:focus-visible`), `min-height:44px`. **Localizar e remover** a regra actual que torna o fundo igual ao texto no hover (provavelmente `.btn-sec:hover`, `a:hover` ou `.barra a`). Inputs 44 px, foco com anel `accent`. `@media print` e `@media (max-width:640px)` do `DESIGN.md`.
2. `AuthFilter`: tornar `/assets/*` público (mesmo mecanismo que `/css/*`).
3. `topo.jspf` (`<head>`): `<link rel="icon" type="image/svg+xml" href="<c:url value='/assets/img/favicon.svg'/>">`, `favicon-32.png`, `apple-touch-icon.png`, `<meta name="theme-color" content="#14301d">`, confirmar `viewport`.
4. `rodape.jspf`: acrescentar "Ayko Sambo · Naira Ornilo · Helder Vuca" por baixo do texto actual.
5. **Os SVG e PNG da marca já estão feitos** (não desenhar): copiar para `src/main/webapp/assets/img/` os ficheiros `logo-marca.svg`, `logo-marca-branca.svg`, `favicon.svg`, `favicon-32.png`, `apple-touch-icon.png` e `produto-placeholder.svg` que o utilizador fornece. Só ligá-los (`<link rel="icon">`, marca no topo, etc.).
6. Copiar o PNG original para `assets/img/logo-agrogest.png`.
7. Inter self-hosted: `@font-face` para `assets/fonts/Inter-Variable.woff2` (`font-display:swap`) com o fallback do `DESIGN.md`.
8. **Componentes de CSS novos** (secção 2): `.pagina-topo`, `.abas`, `.badge.ok/.off/.aviso`, `.campo-erro` e `.invalido`, `.chips` (radio como chip), `.toast`/`.avisos`, `dialog`, `.produto-foto`/`.produto-mini`, `.cartao-link` (cartão clicável com elevação), filete em gradiente na barra, `.conteudo` único centrado (sem margens por página). Corrigir: `a.btn*` sem sublinhado, `button` sempre com classe, `.btn-perigo` com texto branco.
**Modelo:** Médio.
**Pronto quando:** em repouso e em hover/foco/active/disabled nenhum botão tem texto e fundo iguais (testar `.btn`, `.btn-sec`, `.btn-perigo` e os chips do catálogo); separador do browser mostra o favicon mesmo sem sessão; rodapé com os três nomes; nada quebrado nas páginas existentes.

### T2 · Login e registo sem navbar
**Enviar:** `estilo.css`, `topo.jspf`, `rodape.jspf`, `login.jsp`, `registo.jsp`, `LoginServlet.java`, `RegistoServlet.java`, `UtilizadorService.java` (só ler), `DESIGN.md`.
**Fazer:**
1. `topo.jspf`/`rodape.jspf`: `<c:choose>` por `empty sessionScope.utilizador`. Anónimo → **sem `.barra`**, `body.pagina-auth`, `<main class="auth">` centrado (aberto no topo, fechado no rodapé) com a marca (SVG + "AgroGest" em texto + subtítulo). Autenticado → como está.
2. `login.jsp`: cartão centrado (máx. 420 px), labels acima, inputs 44 px com `autocomplete="email"` / `"current-password"`, `autofocus`, botão principal 100% de largura, link "Ainda não tem conta? Registe-se". Erro **inline** dentro do cartão (`role="alert"`), **sem empurrar o layout**; manter o email escrito após erro (confirmar/ajustar no servlet); mensagem genérica inalterada.
3. `registo.jsp`: mesmos padrões; campos nome, email, palavra-passe (`autocomplete="new-password"`), telefone, endereço; link "Já tem conta? Entre". Atributos HTML5 só com regras que o `UtilizadorService` realmente impõe.
**Pronto quando:** `/login` e `/registo` não mostram barra; logo e favicon aparecem sem sessão; erro não faz saltar o layout; tudo usável a 360 px; login e registo continuam a funcionar (incluindo email repetido).

### T3 · Navbar (cliente e admin), menu do utilizador e ícones
**Modelo:** Médio (sobe para Forte se falhar à 2.ª tentativa).
**Enviar:** `estilo.css`, `topo.jspf`, `rodape.jspf`, `SessaoUtil.java`, `Carrinho.java`, `LogoutServlet.java`, `DESIGN.md`.
**Fazer:**
1. **(Já feito na T0: confirmar que o sprite existe e só usar os ícones.)** Referência: `WEB-INF/jspf/icons.jspf` (sprite `<svg style="display:none"><symbol id="i-…">`) a partir dos SVG do **Lucide** que o utilizador pôs em `assets/icons/` (ver 3.1): para cada ficheiro, `<symbol id="i-{nome}" viewBox="0 0 24 24">` com o conteúdo interior, `stroke="currentColor"`, `fill="none"`, traço 2 px. **Não desenhar ícones à mão.** O `id` usa o nome da secção 2.7 (ex.: `i-lixo` ← `trash-2.svg`). Incluir no `topo.jspf` com `<%@ include file="/WEB-INF/jspf/icons.jspf" %>` (directiva, não é scriptlet).
2. Criar `src/main/webapp/assets/js/app.js`: dropdowns (clique/toque, Esc, clique fora, um aberto de cada vez, `aria-expanded`) e hambúrguer. Carregar com `<script defer src="<c:url value='/assets/js/app.js'/>">`.
3. **Cliente** (secção 2.3): grelha `1fr auto 1fr`. Esquerda: menu do utilizador. Centro: marca (SVG branco + "AgroGest" em texto) a ligar a `/`. Direita: ícone carrinho → `/carrinho` com badge `${empty sessionScope.carrinho ? 0 : sessionScope.carrinho.totalItens}` e `aria-label="Carrinho, n itens"`. **Remover** o link "Catálogo".
4. **Admin** (`sessionScope.utilizador.admin`): 2 linhas. Linha 1: menu do utilizador à esquerda + marca centrada. Linha 2: navegação centrada com Dashboard e os três dropdowns da secção 2.3; link activo sublinhado `accent` (usar `requestScope['jakarta.servlet.forward.servlet_path']`). Abaixo de 640 px a linha 2 colapsa num hambúrguer.
5. **Menu do utilizador** (ambos): botão ícone + nome; painel com cabeçalho (círculo com a inicial: `${fn:toUpperCase(fn:substring(utilizador.nome,0,1))}`, nome, email `.muted`, perfil num `.badge`), itens conforme perfil (cliente: Minhas compras; admin: Persistência), separador, **Sair** (neutro, ícone `sair`, hover `primary-tint`; manter o método que o `LogoutServlet` aceita).
6. CSS: hover dos links da barra = fundo branco 12%, texto branco; transições 120 ms; alvos ≥ 44 px; painéis com sombra `overlay` e raio 12 px; `prefers-reduced-motion`; `.barra` escondida em impressão; filete inferior em gradiente.
**Pronto quando:** cliente vê utilizador | marca | carrinho; o badge actualiza ao adicionar/remover; dropdowns abrem por toque e por teclado e fecham com Esc/clique fora; a marca leva ao catálogo; admin chega a todas as rotas pelos grupos e a Persistência pelo menu do utilizador; tudo usável a 360 px; hover/foco legíveis.

### T4 · Toasts, feedback no campo, validação e confirmações
**Modelo:** **Forte** (e 2.ª revisão).
**Pré-requisitos:** V1 (devolve `erroCampo`) e V2 (campo `_t`).
**Enviar:** `estilo.css`, `topo.jspf`, `rodape.jspf`, `app.js`, todos os JSP de formulário (`login`, `registo`, `admin/produtos/form`, `admin/fornecedores/form`, `admin/campanhas/*`, `admin/cultivos/*`, `admin/colheitas/*`, `cliente/carrinho`, `cliente/compra`), `SessaoUtil.java`, `DESIGN.md`.
**Fazer:**
1. **Toasts.** O topo continua a renderizar os `.alerta` (fallback sem JS) dentro de `<div class="avisos" data-campo="${fn:escapeXml(erroCampo)}">`; o `app.js` converte-os em toasts num contentor `aria-live` (topo-direita em desktop, largura total em telemóvel, máx. 3). `ok` desaparece em ~5 s (pausa em hover/foco); `erro` fica até fechar (botão `fechar`, `role="alert"`). Usar `textContent`. **Excepção:** em `body.pagina-auth` o erro fica inline no cartão.
2. **Feedback no elemento (servidor).** Se `data-campo` não for vazio: localizar `[name="…"]` no formulário da página → `aria-invalid="true"`, classe `.invalido` (borda `danger`), inserir `<small class="campo-erro" id="…">` com ícone `alerta` + a mesma mensagem, `aria-describedby`, `focus()` e `scrollIntoView({block:'center'})`; **e** mostrar o toast. Se o campo não existir, só o toast. Para `radio` (unidade) e `select` marcar o grupo/contentor.
3. **Valores mantidos.** Em todos os formulários: `value="<c:out value='${param.x}'/>"`; `textarea` com o conteúdo; `select`/`radio` com `selected`/`checked` por comparação com `param`. Palavra-passe nunca se repõe.
4. **Validação no cliente** (`form[data-validar]` com `novalidate`): em `blur` e `submit` usar `checkValidity()` e mostrar a mensagem debaixo do campo pelo **mesmo mecanismo** do ponto 2. Espelhar **só** o que o servidor impõe (D17): `minlength`/`maxlength`, `pattern` do telefone MZ e do nome, `required`, `min` de preço/quantidade, `min=hoje` na validade. Mensagens em português: vazio *"Preencha este campo."*, email *"Introduza um email válido."*, mínimo *"O valor tem de ser maior que {min}."*, telefone *"Introduza um número moçambicano válido, por exemplo 84 123 4567."*.
5. **Decimais:** não passar a `type="number"` os campos de quantidade/preço. Usar `type="text" inputmode="decimal"` com `pattern` e **normalizar `,` → `.` no servlet** antes do parse.
6. **Duplo clique:** no `submit` o botão fica `disabled` com o texto "A guardar…" (e spinner). Complementa o V2.
7. **Confirmações:** `button[data-confirm="mensagem"]` abre um `<dialog>` nativo (Cancelar / Confirmar; o Confirmar de remoção usa `.btn-perigo`). Aplicar a todos os "Remover" e ao "Eliminar ficheiro" da persistência.
**Pronto quando:** criar/remover/comprar mostram toast; um erro de servidor mostra toast **e** a mensagem debaixo do campo certo com o foco nele; campo inválido mostra a mensagem sem round-trip; valores escritos mantêm-se após erro; duplo clique não duplica; com JS desligado tudo continua a funcionar.

### T5 · Catálogo (a "home" do cliente) e fotos de produtos
**Modelo:** Médio.
**Enviar:** `catalogo.jsp`, `CatalogoServlet.java` (só ler), `estilo.css`, `app.js`, `DESIGN.md`, e a lista de ficheiros em `assets/img/produtos/`.
**Fazer:**
1. **Faixa de abertura (hero):** "Olá, {primeiro nome}" + subtítulo, gradiente e folha em marca de água (secção 2.1).
2. **Chips de categoria** (manter `btn`/`btn-sec`; em telemóvel uma só linha com scroll horizontal; `aria-current` no activo).
3. **Cartões** numa `.grelha`: foto, nome, badge de categoria, preço `MT` com `/ unidade`, "Disponível: n unidade" (formatado), validade (com badge "Expirado" se `isExpirado()`), fornecedor (`${fornecedores[p.fornecedorId].nome}` em `<c:out>`), formulário de adicionar (quantidade + botão com ícone carrinho; desactivado e badge "Indisponível" se stock 0). Sem hover nos cartões. Estado vazio com ícone e "Limpar filtro".
4. **Fotos — formato único:**
   - `.produto-foto { width:100%; aspect-ratio:4/3; object-fit:cover; border-radius:12px 12px 0 0; background:var(--primary-tint); }`; miniatura de admin `.produto-mini { width:48px; height:48px; object-fit:cover; border-radius:8px; }`. Atributos `width`/`height` e `loading="lazy"`.
   - `fotoUrl` pode ser caminho local (`/assets/img/produtos/milho.jpg`, que se escreve com `<c:url>`) ou URL `https://…`. No JSP: `<c:choose>` com `fn:startsWith(p.fotoUrl,'/')`.
   - Foto vazia ou que falha (`error`, tratado no `app.js`) → `assets/img/produto-placeholder.svg` (já feito).
5. **Imagens a descarregar** (**offline, recomendado**; a rede da defesa pode falhar). Fontes de licença livre: Unsplash, Pexels, Pixabay. Guardar o autor em `assets/img/produtos/CREDITOS.txt`. Nomes em minúsculas, sem acentos:

   | Ficheiro | Pesquisar (inglês) |
   |---|---|
   | `milho.jpg`, `feijao.jpg`, `arroz.jpg`, `amendoim.jpg` | maize corn kernels · cowpea beans · rice grains · peanuts |
   | `adubo-saco.jpg`, `fertilizante-granulado.jpg`, `composto.jpg` | fertilizer bag · granular fertilizer · compost |
   | `trator.jpg`, `pulverizador.jpg`, `bomba-rega.jpg` | tractor field · crop sprayer · irrigation water pump |
   | `enxada.jpg`, `pa.jpg`, `catana.jpg`, `regador.jpg`, `luvas.jpg` | garden hoe · shovel · machete · watering can · work gloves |

   Normalizar todas (ImageMagick): `mogrify -resize 800x600^ -gravity center -extent 800x600 -quality 80 *.jpg` → 800×600, ≤ ~120 KB cada.
   **Alternativa online (não recomendada para a demo):** serviços que montam a imagem por palavra-chave no URL (p. ex. `loremflickr.com/800/600/maize`) devolvem fotos aleatórias e podem estar em baixo no dia; `source.unsplash.com` já não é de confiança. Ficheiros do Wikimedia Commons (`https://commons.wikimedia.org/wiki/Special:FilePath/<Ficheiro>?width=800`) são estáveis, mas exigem o nome exacto do ficheiro. Não verifiquei nenhum destes hoje.
**Pronto quando:** filtro e adicionar funcionam; produto sem stock claramente indisponível; **todas as fotos com o mesmo tamanho e proporção**, sem imagem partida; 1 coluna a 360 px.

### T6 · Carrinho, compra, factura, minhas compras
**Enviar:** `carrinho.jsp`, `compra.jsp`, `factura.jsp`, `minhas-compras.jsp`, `estilo.css`, `app.js`.
**Fazer:** carrinho em tabela (`.tabela-scroll`), em ecrã estreito linhas empilhadas (`td::before{content:attr(data-label)}`), quantidade editável por linha, remover, totais com `.total`, estado vazio com link ao catálogo; compra: resumo e "Confirmar compra" (desactiva ao submeter); **factura**: cabeçalho com marca + nº + data, dados do cliente, itens, subtotal/IVA 16%/total à direita, `@page{margin:15mm}`, em impressão esconder `.barra`, `.nao-imprimir`, toasts e rodapé, fundo branco/texto preto, linhas sem quebra a meio; minhas compras: nº, data, total, "Ver factura".
**Pronto quando:** Ctrl+P (pré-visualização) mostra só a factura; tabelas legíveis a 360 px; fluxo de compra intacto.

### T7 · Admin (dashboard, listagens, relatórios, produtos, persistência)
**Modelo:** Médio. Dividir em **T7a**, **T7b**, **T7c** (uma conversa cada).
**Problemas observados:** abas das Listagens são caixas sublinhadas e desiguais; conteúdo da Listagens deslocado face a Produtos; tabela de Produtos com botões sem estilo (Remover com texto preto sobre vermelho, campo "qtd" minúsculo, linha de "Acções" desalinhada); `0.0 kg` contra `0 kg`; muito espaço vazio à direita.

**T7a · Dashboard, Listagens, Relatórios**
- **Enviar:** `dashboard/*.jsp`, `listagens/*.jsp`, `relatorios/*.jsp`, `estilo.css`, `icons.jspf`.
- **Fazer:** dashboard com os cartões clicáveis da secção 2.4. Listagens e Relatórios com `.abas`, mesmo `.conteudo` das outras páginas, `.pagina-topo`. Cabeçalhos ordenáveis como links com `aria-sort` e ícone de seta no campo activo (a coluna "Fornecedor" também ordenável, se o servlet o permitir). Quantidades e dinheiro formatados. Miniatura da foto na listagem de produtos. **Compras detalhadas:** um cartão por compra (nº da factura, data, cliente, telefone, endereço, tabela de itens com unitário/subtotal, total, link "Ver factura") em vez de uma tabela larga. Relatório "mais assíduos": 1.º/2.º/3.º destacados. "Sem compras" vazio com mensagem positiva.
- **Pronto quando:** as 5 listagens e os 4 relatórios mantêm o comportamento e a ordenação, com layout consistente e legível a 360 px.

**T7b · Produtos (lista e formulário)**
- **Pré-requisitos:** V1, F0 (padrão), T5 (fotos).
- **Enviar:** `admin/produtos/lista.jsp`, `form.jsp`, `AdminProdutoServlet.java`, `estilo.css`, `app.js`.
- **Fazer lista:** miniatura, nome, badge de categoria, fornecedor, preço, stock com unidade (badge "Esgotado" a 0), validade (badge "Expirado"), acções por ícone (**repor** abre `<dialog>` com a quantidade; **remover** com `data-confirm`), alinhadas ao centro da linha. Cabeçalho com "+ Novo produto" à direita.
- **Fazer formulário** (cartão centrado, 2 colunas): categoria (`<select>`) que **filtra os fornecedores** compatíveis (cada `<option>` leva `data-categorias`: `SEMENTES→SEMENTE`, `FERTILIZANTES→FERTILIZANTE`, `MAQUINARIA→MAQUINARIA,FERRAMENTA`); sem compatíveis, aviso com link "Criar fornecedor"; unidade em **chips** (D16) com sugestão por categoria; preço `inputmode="decimal"`; quantidade; validade `type="date"` com `min` = hoje (o servlet põe `hoje` no request em ISO); descrição `textarea` (máx. 300, contador); **foto**: galeria de miniaturas (radio) listada com `getServletContext().getResourcePaths("/assets/img/produtos/")` **ou** campo de URL (aceitar `https://…` ou `/assets/…`). Botões Cancelar / Guardar à direita. O servidor continua a validar tudo (V1).
- **Pronto quando:** escolher SEMENTE só mostra fornecedores do tipo SEMENTES; unidade só pelas 5 opções; não se escolhe validade passada; erros de servidor aparecem no campo certo; a foto aparece na lista e no catálogo.

**T7c · Persistência**
- **Enviar:** `persistencia/*.jsp`, `PersistenciaServlet.java`, `estilo.css`.
- **Fazer:** página acessível pelo menu do utilizador (admin). Cartão com "Gravar agora" (`.btn`) e "Eliminar ficheiro" (`.btn-perigo` + `data-confirm`), texto a explicar que **eliminar só apaga o ficheiro** (D10) e que os dados em memória continuam até parar o serviço.
- **Pronto quando:** gravar mostra toast de sucesso; eliminar pede confirmação.

### T8 · Auditoria final de UI
**Enviar:** saída de:
`grep -rnE 'javax|<%[^@-]|<html|/agrogest/|java\.sun\.com|style=|#[0-9a-fA-F]{6}' src/main/webapp` e `find src/main/webapp -name '*.jsp'`.
**Fazer:** corrigir cada ocorrência (cores à mão → `var(--…)`, `style=` → classe); verificar que **todas** as páginas (incluindo as da Naira: fornecedores) usam `.tabela-scroll` nas tabelas, `.num` em números, `data-confirm` nas remoções, botão "voltar" no fluxo campanha → cultivo → colheita, badges consistentes (Aberta/Fechada), mesmo formato de data nos formulários; contraste AA; navegação só por teclado; 360 px sem scroll horizontal da página.
**Pronto quando:** o grep não devolve nada indevido e todas as páginas passaram a mesma checklist visual.

### D1 · README.md (na raiz)
**Enviar:** `README.md` actual, `pom.xml`, `deploy/DEPLOY.md` (quando existir).
**Conteúdo (português de Portugal):** descrição; stack (Java 17, Tomcat 10.1, Servlets 6, JSP/JSTL, Maven); como compilar (`mvn clean package` → `target/agrogest.war`); instalar e fazer deploy (Tomcat 10.1, WAR em `webapps/`, porta 8081, contexto `/agrogest`, serviço systemd — resumo do `DEPLOY.md`); **credenciais de teste** (admin `admin@agro.mz` / `admin234`; cliente `joao@teste.mz` / `1234` se houver dados de demonstração); **arquitectura** (diagrama em camadas do enunciado, pacotes, lock único, fluxo de `finalizarCompra`, persistência atómica, AuthFilter); rotas principais; regras RN01–RN06; decisões D1–D12 resumidas; localização do ficheiro de dados; como correr os testes (`mvn test`); autores (Ayko Sambo, Naira Ornilo, Helder Vuca); ligação ao manual em `docs/manual/`. Inserir o logo (`assets/img/logo-agrogest.png`) no topo.
**Pronto quando:** alguém que nunca viu o projecto instala e entra com as credenciais só com o README.

### D3 · Deploy com systemd (porta 8081)
**Enviar:** `ls -la /opt/tomcat`, `grep -n 'port=' /opt/tomcat/conf/server.xml`, `readlink -f "$(which java)"`, `cat ~/.bashrc | sed -n '/tomcat()/,/^}/p'`.
**Atenção:** o `/opt/tomcat` actual é gerido pela função do `~/.bashrc` (como o teu utilizador, porta 8080). Passar a systemd com o utilizador `tomcat` **muda as permissões**; depois disso não uses a função `tomcat` ao mesmo tempo. Ou a adaptas para `sudo systemctl …` ou deixas de a usar.
**Fazer:**
1. `tomcat stop`.
2. `sudo useradd -r -U -d /opt/tomcat -s /usr/sbin/nologin tomcat`; `sudo chown -R tomcat:tomcat /opt/tomcat`; `sudo chmod +x /opt/tomcat/bin/*.sh`; `sudo mkdir -p /opt/tomcat/data && sudo chown tomcat:tomcat /opt/tomcat/data`.
3. `server.xml`: Connector de `8080` para `8081`.
4. Criar `deploy/tomcat.service` (ajustar `JAVA_HOME` ao caminho real do JDK 17):
```
[Unit]
Description=Apache Tomcat 10.1 (AgroGest)
After=network.target

[Service]
Type=forking
User=tomcat
Group=tomcat
Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
Environment=CATALINA_HOME=/opt/tomcat
Environment=CATALINA_BASE=/opt/tomcat
Environment=CATALINA_PID=/opt/tomcat/temp/tomcat.pid
PIDFile=/opt/tomcat/temp/tomcat.pid
ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh 30
TimeoutStopSec=60
Restart=on-failure
RestartSec=5
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```
5. `sudo cp deploy/tomcat.service /etc/systemd/system/ && sudo systemctl daemon-reload && sudo systemctl enable --now tomcat`.
6. WAR: `sudo rm -rf /opt/tomcat/webapps/agrogest*; sudo cp entrega/agrogest.war /opt/tomcat/webapps/; sudo chown tomcat:tomcat /opt/tomcat/webapps/agrogest.war; sudo systemctl restart tomcat`. Abrir `http://localhost:8081/agrogest/`.
7. **Teste de persistência no serviço:** criar dados → `sudo systemctl restart tomcat` → dados continuam e ids não repetem. `sudo ls -la /opt/tomcat/data/`.
8. Se houver `ufw` activa: `sudo ufw allow 8081/tcp`. Logs: `journalctl -u tomcat -e` e `/opt/tomcat/logs/catalina.out`.
9. Escrever `deploy/DEPLOY.md` com os passos **tal como foram testados**.
**Pronto quando:** `systemctl start/stop/restart/status tomcat` funcionam; o serviço sobe sozinho após reiniciar a máquina; o restart mantém os dados. Explicar linha a linha a unidade (para a defesa) e porque `TimeoutStopSec` ≥ 30 s (a paragem grava os dados).

### D2 · Manual de utilização (PDF)
**Pré-requisito:** UI congelada e aplicação a correr (idealmente com A10 carregado).
**Enviar:** lista das páginas e as capturas que já existam.
**Fazer:** seguir o "Exemplo de fluxo" do enunciado. **Admin:** login → dashboard → criar fornecedor → criar produto → campanha → cultivo → colheita (3200 kg × 22 = 70 400,00 MT) → listagens (com ordenação) → compras detalhadas → relatórios → persistência (gravar). **Cliente:** registo → catálogo e filtro → adicionar ao carrinho (3 kg de milho, 1 saco de adubo) → alterar/remover → finalizar → factura `FT-2026-1` → imprimir → minhas compras. Capturas a ~1280 px, nomes `docs/manual/img/NN-descricao.png`, legenda de uma linha por captura. Escrever `docs/manual/manual.md`, exportar para `docs/manual/Manual_AgroGest.pdf` (LibreOffice, ou `pandoc` se instalado). Capa com o logo e os três nomes.
**Pronto quando:** o PDF existe em `docs/manual/` e uma pessoa nova repete os dois fluxos só com ele.

### D4 · Release no GitHub
**Fazer (só com tabela I1 verde e UI congelada):**
```bash
git switch main && git pull
git merge helder            # ou o fluxo de PR que o grupo usa
mvn clean package
mkdir -p entrega && cp target/agrogest.war entrega/
git check-ignore -v entrega/agrogest.war   # se aparecer regra, está ignorado
git add -f entrega/agrogest.war            # -f só se estiver ignorado
git commit -m "entrega: WAR v1.0.0"
git tag -a v1.0.0 -m "AgroGest v1.0.0 - entrega"
git push origin main && git push origin v1.0.0
```
Depois: GitHub → **Releases → Draft a new release** → tag `v1.0.0` → **Generate release notes** → anexar `agrogest.war` e `docs/manual/Manual_AgroGest.pdf` → Publish. Ou: `gh release create v1.0.0 entrega/agrogest.war docs/manual/*.pdf --title "AgroGest v1.0.0" --generate-notes` (requer `gh auth login`).
- O ficheiro tem de chamar-se **`agrogest.war`** (com versão no nome, o contexto mudaria).
- Usar `v1.0.0-rc1` como *pre-release* para ensaio. Bug depois de publicar → `v1.0.1`, não mover a tag.
- Nas notas: Java 17, Tomcat 10.1, copiar o WAR para `webapps/`, porta, credenciais.
- Repositório privado → adicionar o docente como colaborador. Nunca anexar `agrogest.ser`.

### D5 · Limpeza final do repositório
A pasta `docs/` **não se apaga toda**: `docs/manual/` fica (é entregável). Apagar só o que era interno, **no último dia, antes da tag**, e guardar cópia fora do repositório:
```bash
mkdir -p ~/HTA/ISCTEM/SD/docs-internos
cp -r docs/00_CONTRATO.md docs/GIT.md docs/PESSOA_*.md docs/enunciado ~/HTA/ISCTEM/SD/docs-internos/ 2>/dev/null
cp TAREFAS_EXPLICADAS.md PLANO_SESSOES.md DESIGN.md ~/HTA/ISCTEM/SD/docs-internos/ 2>/dev/null
git rm -r --ignore-unmatch docs/00_CONTRATO.md docs/GIT.md docs/PESSOA_A.md docs/PESSOA_B.md docs/PESSOA_C.md docs/enunciado
git rm --ignore-unmatch TAREFAS_EXPLICADAS.md PLANO_SESSOES.md DESIGN.md
find . -path ./target -prune -o -name 'teste.jsp' -print
find src -path '*stub*' -name '*.java'
```
O que ficou nas versões antigas continua no histórico do Git (não vale a pena reescrever o histórico). Confirmar que `teste.jsp` e `stub/` já não existem. Ficam: `README.md`, `docs/manual/`, `deploy/`, `entrega/agrogest.war`, `src/`, `pom.xml`.

### P1 · Guião da defesa (ficheiro interno, fora do repositório)
**Fazer:** ordem da demonstração ao vivo — admin cria fornecedor, produto, campanha, cultivo, colheita → cliente regista-se, compra → factura → listagens e relatórios → `sudo systemctl restart tomcat` → mostrar que os dados voltam. Duas passagens de ensaio. Para cada módulo, 3 perguntas prováveis com resposta de 2 linhas (lock único e `synchronized` (RN01/RN02), `AuthFilter` e 403, `changeSessionId`, factura só do dono (RN03), escrita atómica e `.ser.bak`, BCrypt, `BigDecimal`, POST+redirect, o que é um WAR, a unidade systemd). Cada membro tem de saber explicar as partes dos outros dois.

---

## 5. Checklist final antes de entregar

- [ ] Tabela da I1 verde numa instalação limpa (`systemctl`, porta 8081).
- [ ] `mvn clean package` sem falhas; `entrega/agrogest.war` actualizado.
- [ ] Sem `teste.jsp`, sem `stub/`, sem `javax`, sem scriptlets.
- [ ] README e manual PDF em `docs/manual/`.
- [ ] Release publicada com o WAR.
- [ ] Ensaio da defesa feito duas vezes.
- [ ] Duplo clique rápido em guardar e em confirmar compra: **um** registo / **uma** venda.
- [ ] `12345678` recusado como contacto; nome de 1 letra recusado; SEMENTE com fornecedor MAQUINARIA recusada; unidade só das 5; validade passada recusada.
- [ ] Erros aparecem debaixo do campo certo (e em toast) em todos os formulários.
- [ ] Todas as fotos com o mesmo tamanho; nenhuma partida.
