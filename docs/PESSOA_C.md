# Pessoa C — Módulo agrícola (CRUDs), aspecto visual, deploy e README

Branch: `pessoa-c` · Documentos partilhados: `docs/00_CONTRATO.md` (lê-o inteiro) e `docs/GIT.md` (Git no terminal)

## Instruções para a IA

1. **Se não tens o enunciado no contexto, pede-o ao utilizador** (`docs/enunciado/AgroGest_Enunciado.pdf`) e não escrevas código antes de o ler.
2. Lê `docs/00_CONTRATO.md` (é lei) e este documento.
3. Trabalha só nas áreas da Pessoa C (contrato, secção 3). O que depende de A ou B é **stub** (contrato, secção 10, linha **C**).
4. O utilizador tem de conseguir explicar cada ficheiro que geras: explica-o com palavras simples.

---

## 1. Instalar

| Ferramenta | Versão | Windows | Linux (Ubuntu/Debian) |
|---|---|---|---|
| JDK | **17** | `winget install EclipseAdoptium.Temurin.17.JDK` | `sudo apt update && sudo apt install -y openjdk-17-jdk` |
| Git | recente | `winget install Git.Git` (traz o Git Bash) | `sudo apt install -y git` |
| Eclipse | "**IDE for Enterprise Java and Web Developers**" | Instalador em eclipse.org/downloads (escolher **este** pacote, não o "for Java Developers") | tar.gz do mesmo pacote em eclipse.org → extrair em `~/eclipse` → `./eclipse/eclipse` |
| Apache Tomcat (desenvolvimento) | **10.1.x** (não 9, não 11) | zip "64-bit Windows zip" de tomcat.apache.org → extrair para `C:\tomcat\apache-tomcat-10.1.x` (sem espaços) | tar.gz "Core" → extrair para `~/tomcat/apache-tomcat-10.1.x` e `chmod +x ~/tomcat/*/bin/*.sh` |
| Maven | opcional | O Eclipse já traz (m2e) | `sudo apt install -y maven` (opcional) |
| Conta GitHub | — | github.com | github.com |
| **Linux para o deploy** (só se usas Windows) | Ubuntu 22.04/24.04 | WSL2: `wsl --install -d Ubuntu` num PowerShell de administrador; depois activar o systemd (abaixo) | já tens |

**WSL2 com systemd (só Windows):** dentro do Ubuntu do WSL, `sudo nano /etc/wsl.conf` e escrever:

```
[boot]
systemd=true
```

Depois, no PowerShell: `wsl --shutdown` e voltar a abrir o Ubuntu. Confirma com `systemctl is-system-running` (deve dizer `running` ou `degraded`). O WSL2 serve para treinar o deploy; a validação final faz-se numa máquina Linux a sério (Pessoa A com dual boot, ou o colega que usa Linux).

### Configurar o Eclipse (uma vez)

1. Window → Preferences → **General → Workspace → Text file encoding = UTF-8**.
2. Window → Preferences → **Java → Installed JREs**: JDK 17 como padrão.
3. Janela **Servers** → New → Apache → **Tomcat v10.1 Server** → apontar para a pasta do Tomcat. Duplo clique → **Ports → HTTP/1.1 = 8081**.
4. Clone e a tua branch (`docs/GIT.md`, secções 4 e 5). File → Import → Maven → **Existing Maven Projects**.
5. Run As → **Run on Server** → `http://localhost:8081/agrogest/teste.jsp`. Se o contexto não for `/agrogest`: Project → Properties → Web Project Settings → Context root = `agrogest`.

### Verificar

```bash
java -version      # 17
git --version
git branch         # estás em pessoa-c
```

**Cuidados:** nunca `javax.*` (sempre `jakarta.*`); para testar persistência, **Stop**, não Terminate (contrato, secção 12).

---

## 2. Os teus stubs (faz isto primeiro, no máximo 1 hora)

Pede à IA: *"Gera os stubs da Pessoa C conforme a secção 10 do contrato, em `src/main/java/mz/agrogest/stub/c/...`, copiando classes e assinaturas das secções 4, 5 e 6."* O stub de `ProdutoService.existeComFornecedor` pode devolver sempre `false` (ou `true` para um fornecedor de exemplo, para testares a recusa).

Depois do M1 (quinta 15): `git merge origin/main` e passa a importar `model`, `repo` e `util` reais (apaga `stub/c/model`, `stub/c/repo`, `stub/c/util` e reescreve os imports). O stub de serviço fica até à integração.

---

## 3. Tarefas

### C1 · **Deploy do esqueleto — começa já (esta semana)**
É a tarefa que mais pode falhar e a que menos depende do código dos outros: faz-a **cedo**, com o WAR do esqueleto (só tem `teste.jsp`).
1. Gerar o WAR: no Eclipse, botão direito no projecto → Run As → **Maven build…** → Goals: `clean package`. Fica em `target/agrogest.war`. (Com `mvn` no terminal: `mvn clean package`.)
2. Num Linux (ou WSL2 com systemd): Tomcat 10.1 em `/opt/tomcat`, utilizador de sistema `tomcat` sem login, porta **8081** no `server.xml`, WAR em `webapps/agrogest.war`.
3. Unidade systemd em `/etc/systemd/system/tomcat.service`; guardar uma cópia em `deploy/tomcat.service` no repositório.
4. `sudo systemctl daemon-reload && sudo systemctl enable --now tomcat`; abrir `http://localhost:8081/agrogest/teste.jsp`.
5. Guardar os passos, por ordem e testados, em `deploy/DEPLOY.md`.

Pontos onde isto costuma falhar (verifica um a um):
- `Type=forking`, `User=tomcat`, `Environment=JAVA_HOME=...` (o caminho real do JDK 17), `CATALINA_HOME` e `CATALINA_BASE`, `ExecStart=.../startup.sh`, `ExecStop=.../shutdown.sh`.
- `TimeoutStopSec` ≥ 30 s: a paragem **grava os dados** (contrato, D10); se o systemd matar o Tomcat cedo, perde-se tudo.
- A pasta `data/` dentro de `CATALINA_BASE` tem de pertencer ao utilizador `tomcat` (`chown`), senão a gravação falha em silêncio.
- Ficheiros `.sh` e `.service` com fim de linha **LF** (o `.gitattributes` já trata disso; se editares no Windows, confirma).
- Firewall: `sudo ufw allow 8081` se o `ufw` estiver activo.
- Logs: `journalctl -u tomcat -e` e `/opt/tomcat/logs/catalina.out`.

**Pronto quando:** `systemctl start/stop/restart/status tomcat` funcionam, o serviço sobe sozinho após reiniciar a máquina, e (depois da integração) `systemctl restart tomcat` mantém os dados.

### C2 · Fornecedores — `/admin/fornecedores/*` — até quinta 15
`FornecedorService` real (contrato, secção 6, com as regras de concorrência da secção 5) e `AdminFornecedorServlet`: criar, listar, remover. Remover fornecedor com produtos → `NegocioException` (D6, via `ProdutoService.existeComFornecedor`).
**Pronto quando:** campos vazios dão erro de formulário, e a recusa de remoção mostra uma mensagem clara.

### C3 · Campanhas — `/admin/campanhas/*` — até quinta 15
`CampanhaService` + `AdminCampanhaServlet`: criar (fim ≥ início), listar, fechar, reabrir, remover (recusa se tiver cultivos).
**Pronto quando:** fechar/reabrir alterna o estado visível e a remoção bloqueada explica o motivo.

### C4 · Cultivos — `/admin/cultivos/*` — até sábado 17
`CultivoService` + `AdminCultivoServlet`. Sem `?campanhaId=` mostra a lista de campanhas para escolher; com ele, os cultivos dessa campanha e o formulário para registar. Campanha fechada não aceita novos cultivos (D5). Remover cultivo com colheitas → recusado.

### C5 · Colheitas — `/admin/colheitas/*` — até sábado 17
`ColheitaService` + `AdminColheitaServlet`. Por `?cultivoId=`: listar e registar (data, kg, preço por kg); o **valor total** é o `getValorTotal()` do modelo (não o calcules no JSP). Valores positivos obrigatórios.
**Pronto quando:** o exemplo do enunciado (3200 kg × 22 MT/kg) mostra 70 400,00 MT (em pt_PT o separador de milhares é um espaço).

### C6 · Aspecto visual e responsivo — até domingo 18
És o dono de `css/estilo.css`, `WEB-INF/jspf/topo.jspf` e `rodape.jspf`, e `views/erro.jsp`. Melhora o que quiseres, mas **não renomeies classes nem atributos** que A e B já usam (contrato, secção 8). Verifica ecrã largo e telemóvel (largura ≤ 640 px).
**Pronto quando:** todas as páginas dos três módulos ficam coerentes e a impressão da factura esconde menu e botões.

### C7 · Deploy final — sex 23 a sáb 24
Depois da integração: voltar a fazer o passo C1 com o WAR final (`entrega/agrogest.war`) e correr o teste de persistência no serviço: criar dados → `systemctl restart tomcat` → os dados continuam.

### C8 · README.md — sex 23
Substitui o esqueleto por: instruções de instalação e deploy (resumo do `deploy/DEPLOY.md`), **credenciais de teste** (admin: `admin@agro.mz` / `admin234`), e descrição da arquitectura (a Pessoa A dá-te o texto). Enunciado §XI.

### C9 · Defesa
Sabes explicar, sem ajuda: o que é um WAR, o que a unidade systemd faz linha a linha, porque a paragem do Tomcat grava os dados, e como se restaura o serviço se parar.

---

## 4. Auto-teste antes de cada push

- [ ] Eclipse sem erros vermelhos; a aplicação arranca.
- [ ] Acentos ("João Açúcar") certos em formulário e página.
- [ ] Nenhum JSP com scriptlets (`<% `, `<%=`, `<%!`), nem `javax`, nem `<html>` (excepto o `topo.jspf`, que é teu). (`<%@ page` e `<%--` são permitidos.)
- [ ] `git status` só mostra ficheiros das tuas áreas (e de `stub/c/`).
