# Pessoa B — Módulo Cliente e gestão de Produtos

Branch: `pessoa-b` · Documentos partilhados: `docs/00_CONTRATO.md` (lê-o inteiro) e `docs/GIT.md` (Git no terminal)

## Instruções para a IA

1. **Se não tens o enunciado no contexto, pede-o ao utilizador** (`docs/enunciado/AgroGest_Enunciado.pdf`) e não escrevas código antes de o ler.
2. Lê `docs/00_CONTRATO.md` (é lei) e este documento.
3. Trabalha só nas áreas da Pessoa B (contrato, secção 3). O que depende de A ou C é **stub** (contrato, secção 10, linha **B**).
4. O utilizador tem de conseguir explicar cada ficheiro que geras: explica-o com palavras simples.

---

## 1. Instalar

| Ferramenta | Versão | Windows | Linux (Ubuntu/Debian) |
|---|---|---|---|
| JDK | **17** | `winget install EclipseAdoptium.Temurin.17.JDK` | `sudo apt update && sudo apt install -y openjdk-17-jdk` |
| Git | recente | `winget install Git.Git` (traz o Git Bash) | `sudo apt install -y git` |
| Eclipse | "**IDE for Enterprise Java and Web Developers**" | Instalador em eclipse.org/downloads (escolher **este** pacote, não o "for Java Developers") | tar.gz do mesmo pacote em eclipse.org → extrair em `~/eclipse` → `./eclipse/eclipse` |
| Apache Tomcat | **10.1.x** (não 9, não 11) | zip "64-bit Windows zip" de tomcat.apache.org → extrair para `C:\tomcat\apache-tomcat-10.1.x` (sem espaços no caminho) | tar.gz "Core" → extrair para `~/tomcat/apache-tomcat-10.1.x` e `chmod +x ~/tomcat/*/bin/*.sh` |
| Maven | opcional | O Eclipse já traz (m2e) | `sudo apt install -y maven` (opcional) |
| Conta GitHub | — | github.com | github.com |
| Capturas de ecrã + editor que exporte PDF (para o manual) | — | `Win+Shift+S`; Word ou LibreOffice | Flameshot/Spectacle/captura do sistema; LibreOffice |

### Configurar o Eclipse (uma vez)

1. Window → Preferences → **General → Workspace → Text file encoding = UTF-8** (no Windows o padrão estraga os acentos).
2. Window → Preferences → **Java → Installed JREs**: JDK 17 como padrão (JDK, não JRE).
3. Janela **Servers** → New → Apache → **Tomcat v10.1 Server** → apontar para a pasta do Tomcat → Finish. Duplo clique no servidor → **Ports → HTTP/1.1 = 8081**.
4. Faz o clone do projecto e a tua branch (`docs/GIT.md`, secções 4 e 5).
5. Eclipse: File → Import → Maven → **Existing Maven Projects** → pasta clonada.
6. Botão direito no projecto → Run As → **Run on Server**. Abrir `http://localhost:8081/agrogest/teste.jsp`. Se o contexto não for `/agrogest`: Project → Properties → Web Project Settings → Context root = `agrogest`.

### Verificar

```bash
java -version      # 17
git --version
git branch         # estás em pessoa-b
```

`teste.jsp` mostra "ambiente OK" com acentos certos e um número formatado → podes começar.

**Cuidados:** nunca `javax.*` (sempre `jakarta.*`); para testar persistência, parar o servidor com **Stop**, não com Terminate (contrato, secção 12).

---

## 2. Os teus stubs (faz isto primeiro, no máximo 1 hora)

Pede à IA: *"Gera os stubs da Pessoa B conforme a secção 10 do contrato, em `src/main/java/mz/agrogest/stub/b/...`, copiando as classes e assinaturas das secções 4, 5 e 6."* O stub de `VendaService.finalizarCompra` deve **simular**: desconta stock no stub de `ProdutoService`/lista, cria uma `Venda` com itens, subtotal, IVA 16% e total, e lança `NegocioException` se faltar stock. Assim consegues ver o fluxo completo a funcionar sozinho.

Depois do M1 (quinta 15), faz `git merge origin/main` e passa a importar `model`, `repo` e `util` reais (apaga `stub/b/model`, `stub/b/repo`, `stub/b/util` e reescreve os imports). Os stubs de **serviços** ficam até à integração.

---

## 3. Tarefas

### B1 · `ProdutoService` (real) — até quinta 15
Conforme contrato, secção 6, com as regras de concorrência da secção 5 (lock comum, devolver cópias, `novoIdProduto()`). Validações: nome obrigatório, preço > 0, quantidade ≥ 0, fornecedor existe (via `FornecedorService.porId`), `reporStock` com quantidade > 0.
**Pronto quando:** consegues criar, listar, repor e remover produtos pelas páginas do admin (B2).

### B2 · Admin: gestão de Produtos — `/admin/produtos/*`
`AdminProdutoServlet`: listar (com botões), formulário de criação (escolher categoria e fornecedor de listas), repor stock, remover. Tudo por POST + redirect + `flashOk`. Enunciado §IV "Gestão de Produtos".
**Pronto quando:** campos inválidos (preço negativo, nome vazio, data mal escrita) mostram erro no formulário e nunca dão 500.

### B3 · Login, logout, registo, home — até quinta 15
`LoginServlet`, `LogoutServlet`, `RegistoServlet`, `HomeServlet` (contrato, secção 7). No login: autenticar, `request.changeSessionId()`, guardar `utilizador` na sessão, redirecionar por perfil. Logout: `session.invalidate()`.
**Pronto quando:** registo com email repetido mostra erro; login errado mostra erro (sem dizer se foi o email ou a password); depois do logout não se volta atrás ao catálogo.

### B4 · Catálogo — até sábado 17
`CatalogoServlet`: lista produtos com nome, descrição, foto, preço, validade, quantidade disponível, fornecedor, categoria; filtro `?categoria=`. Em `<c:out>` para texto de utilizador; foto por `<img src>` com tratamento se vier vazia. Para o nome do fornecedor, o servlet põe um `Map<Integer, Fornecedor>` no request e o JSP faz `${fornecedores[p.fornecedorId].nome}`.
**Pronto quando:** o filtro funciona, produto sem stock aparece como indisponível, e a página fica bem num ecrã estreito.

### B5 · Carrinho em sessão — até sábado 17
`Carrinho` e `LinhaCarrinho` (os teus, contrato secção 4) e `CarrinhoServlet` em `/carrinho/*`: ver, adicionar (com quantidade), actualizar, remover, esvaziar. O carrinho vive na sessão (atributo `carrinho`; cria-o se não existir). Ao adicionar/actualizar, valida contra o stock actual e mostra aviso se exceder (a validação **definitiva** é a do `VendaService`).
**Pronto quando:** o contador do menu ("Carrinho (n)") actualiza, quantidades decimais (3,5 kg) funcionam e o carrinho sobrevive a navegar entre páginas.

### B6 · Compra / checkout — até domingo 18
`CompraServlet`: GET mostra o resumo (linhas + total estimado); POST chama `VendaService.finalizarCompra(cliente, carrinho.getQuantidades())`. Sucesso → esvazia o carrinho e redirecciona para `/factura?id={venda.id}`. `NegocioException` (falta de stock, produto removido…) → volta ao resumo com a mensagem **e o carrinho intacto**.
**Pronto quando:** testado com stock a mais, a menos, e com produto removido por outro utilizador entretanto.

### B7 · Factura e Minhas compras (RN03) — até domingo 18
`FacturaServlet`: carrega a venda; **só o dono (ou admin) a vê**; qualquer outro → `resp.sendError(403)`; id inexistente → 404. Mostra número, dados do cliente, itens, subtotal, IVA 16%, total, com botão "Imprimir" (`onclick="window.print()"`, dentro de um elemento com classe `nao-imprimir`) e a factura dentro de `.factura`. `MinhasComprasServlet`: histórico do utilizador com ligação para cada factura.
**Pronto quando:** mudando o `?id=` na barra de endereços para o de outro cliente dá 403.

### B8 · Manual de utilização (PDF) — sex 23 a sáb 24
Passo a passo, com capturas, das operações principais de **admin** e **cliente** (siga o "Exemplo de fluxo" do enunciado). Guardar o `.md`/`.docx` e o **PDF** em `docs/manual/`. Só fazer com a aplicação integrada a correr.

### B9 · Defesa
Sabes explicar, sem ajuda: como o carrinho se mantém em sessão, o que faz `changeSessionId`, por que a factura só aparece ao dono, o que acontece se dois clientes compram o último saco ao mesmo tempo.

---

## 4. Auto-teste antes de cada push

- [ ] Eclipse sem erros vermelhos; a aplicação arranca.
- [ ] Acentos ("João Açúcar") certos em formulário e página.
- [ ] Nenhum JSP com scriptlets (`<% `, `<%=`, `<%!`), nem `javax`, nem `<html>`. (`<%@ page` e `<%--` são permitidos.)
- [ ] `git status` só mostra ficheiros das tuas áreas (e de `stub/b/`).
