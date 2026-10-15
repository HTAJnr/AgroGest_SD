# Pessoa A — Núcleo, concorrência, segurança, relatórios e integração

Branch: `pessoa-a` · Documentos partilhados: `docs/00_CONTRATO.md` (lê-o inteiro) e `docs/GIT.md`

## Instruções para a IA

1. **Se não tens o enunciado no contexto, pede-o ao utilizador** (`docs/enunciado/AgroGest_Enunciado.pdf`) e não escrevas código antes de o ler.
2. Lê `docs/00_CONTRATO.md` (é lei) e este documento.
3. Trabalha só nas áreas da Pessoa A (contrato, secção 3). Não precisas de stubs: és dono do `model`, do `DataStore` e das utilidades.

---

## 1. Instalar

| Ferramenta | Versão | Windows | Linux (Ubuntu/Debian) |
|---|---|---|---|
| JDK | **17** | `winget install EclipseAdoptium.Temurin.17.JDK` | `sudo apt update && sudo apt install -y openjdk-17-jdk` |
| Git | recente | `winget install Git.Git` (traz o Git Bash) | `sudo apt install -y git` |
| Eclipse | "**IDE for Enterprise Java and Web Developers**" | Instalador em eclipse.org/downloads (escolher **este** pacote, não o "for Java Developers") | tar.gz do mesmo pacote em eclipse.org → extrair em `~/eclipse` → `./eclipse/eclipse` |
| Apache Tomcat | **10.1.x** (não 9, não 11) | zip "64-bit Windows zip" de tomcat.apache.org → extrair para `C:\tomcat\apache-tomcat-10.1.x` (sem espaços no caminho) | tar.gz "Core" → extrair para `~/tomcat/apache-tomcat-10.1.x` e `chmod +x ~/tomcat/*/bin/*.sh` |
| Maven | opcional | O Eclipse já traz (m2e). Só se quiseres `mvn` no terminal | `sudo apt install -y maven` (opcional) |

Este Tomcat é o **de desenvolvimento**, em pasta tua. O Tomcat "de produção" com systemd (em `/opt`) é a parte do deploy (Pessoa C) e **não** deve coexistir com o do Eclipse ao mesmo tempo (mesma porta 8081).

### Configurar o Eclipse (uma vez)

1. Window → Preferences → **General → Workspace → Text file encoding = UTF-8** (no Windows o padrão é Cp1252 e estraga os acentos).
2. Window → Preferences → **Java → Installed JREs**: o JDK 17 como padrão (tem de ser JDK, não JRE).
3. Janela **Servers** → New → Apache → **Tomcat v10.1 Server** → apontar para a pasta do Tomcat → Finish. Duplo clique no servidor → **Ports → HTTP/1.1 = 8081**.
4. File → Import → Maven → **Existing Maven Projects** → escolher a pasta clonada.
5. Botão direito no projecto → Run As → **Run on Server**. Abrir `http://localhost:8081/agrogest/teste.jsp`. Se o contexto não for `/agrogest`: Project → Properties → Web Project Settings → Context root = `agrogest`.

### Verificar

```bash
java -version      # tem de dizer 17
git --version
```

E a página `teste.jsp` mostra "ambiente OK", os acentos certos e um número formatado.

## 2. Implicações para ti (dual boot e o programa)

**Dual boot — risco real de perderes dias.** Montar o ambiente "perfeito" é a forma clássica de adiar o trabalho que dá nota. Faz o dual boot **depois** de A2 estar em `main` (quinta) ou na véspera do deploy, com cópia de segurança e limite de 3 horas. Se falhar, usa WSL2 com systemd ou uma VM e segue em frente. Antes de particionar: desactivar o *Fast Startup*/hibernação do Windows, suspender o BitLocker se estiver activo, encolher a partição pelo Gestor de Discos do Windows (≥ 60 GB livres para o Linux).

**Dois sistemas = dois ambientes separados:**
- Dois clones do repositório (um em cada SO). **Nunca** abras a pasta do projecto do Windows a partir do Linux (partição NTFS): estraga permissões, fins de linha e o Eclipse.
- Duas chaves SSH (uma por SO) ou credenciais separadas; dois workspaces do Eclipse.
- Os dois SO usam a mesma `main` no GitHub; é só `git pull` / `git fetch`.

**Do programa:**
- O ficheiro `agrogest.ser` fica **fora** do Git e **não é partilhável** entre máquinas/SO. Se mudares um campo do `model`, apaga-o (contrato, D12).
- Sob o Eclipse, `${catalina.base}` aponta para uma pasta temporária dentro do workspace (`.metadata/.plugins/org.eclipse.wst.server.core/tmp0`), por isso o ficheiro de dados vive lá. **Stop** grava; **Terminate** perde tudo.
- O Tomcat do Eclipse e o Tomcat do systemd usam a mesma porta (8081): nunca ligados ao mesmo tempo.
- Java: se tiveres JDK 21 instalado, o `pom.xml` compila na mesma com `release 17`. O Tomcat tem de ser 10.1.

---

## 3. Tarefas (por ordem de impacto)

### A1 · Publicar o esqueleto em `main` — hoje, 12 Out
Extrair o ZIP do kit **para dentro** do clone, `git add .`, commit, `git push origin main`. Convidar B e C como colaboradores (GitHub → Settings → Collaborators). Enviar-lhes `docs/PESSOA_B.md` / `PESSOA_C.md` e `docs/GIT.md` (já vão no repositório; basta dizer-lhes onde estão). Criar a tua branch `pessoa-a`.
**Pronto quando:** B e C clonam e abrem `teste.jsp` na porta 8081.

### A2 · Núcleo real em `main` — até quinta 15 Out (M1)
`model` completo conforme contrato secção 4 (todas as classes e enums, **excepto** `Carrinho` e `LinhaCarrinho`), `util/NegocioException`, `util/SenhaUtil` (BCrypt: `hash` e `verificar`), `repo/DataStore` conforme secção 5. Faz merge para `main` e **avisa o grupo**.
**Pronto quando:** compila, B e C fazem `git merge origin/main` e apagam os seus stubs de `model`, `repo`, `util`. É a tarefa que mais desbloqueia: faz-a **antes** das outras.

### A3 · `UtilizadorService` + admin no arranque
Registo (validar campos, email único e normalizado, hash BCrypt), autenticação, listagem de clientes, `garantirAdmin()` (D11).
**Pronto quando:** dois registos com o mesmo email (maiúsculas diferentes) são recusados e a password nunca aparece em claro em lado nenhum.

### A4 · `VendaService.finalizarCompra` (aqui está a nota: RN01, RN02, RN06)
Dentro do lock comum: validar **todos** os itens (existem, quantidade > 0, stock suficiente) **antes** de descontar qualquer um; só depois descontar tudo, criar `VendaItem`s com cópia, calcular subtotal/IVA/total (D3, D4), `novoIdVenda()`, `numeroFactura` (D2). Qualquer falha → `NegocioException` com mensagem clara (qual produto, quanto pedido, quanto há) e **nenhum** stock alterado.
**Pronto quando** um teste JUnit (`src/test/java`) prova:
1. Duas threads a comprar a última unidade: uma ganha, a outra recebe erro, stock nunca negativo.
2. Carrinho com 2 itens em que o 2.º não tem stock: o stock do 1.º **não** mudou.
3. IVA e total certos com os números do exemplo do enunciado.

### A5 · Persistência e ciclo de vida
`repo/Persistencia`: gravar/restaurar o estado completo (listas **e** contadores) num único `ObjectOutputStream`, escrita atómica (D10), tudo sob o lock. `listener/ContextListener`: no arranque restaura (e chama `garantirAdmin()`), ao encerrar grava. `PersistenciaServlet`: `gravar` e `eliminar` por POST.
**Pronto quando:**
- Parar o servidor com **Stop** grava; voltar a arrancar restaura tudo e os ids **continuam** (não repetem).
- Um ficheiro corrompido **não** impede o arranque (regista o erro, renomeia o ficheiro para `.ser.bak`, arranca vazio).
- Se matares o processo a meio da escrita, o ficheiro anterior continua válido.

### A6 · `AuthFilter` (RN04, RN05)
Conforme contrato, secção 7.
**Pronto quando:** anónimo em `/catalogo` → redirect `/login`; cliente em `/admin/dashboard` → **403** (página `erro.jsp`); admin passa em tudo; `/css/*` é público; `/teste.jsp` também é público (para a Pessoa C poder testar o deploy), e só deixa de existir quando o apagares na integração.

### A7 · Dashboard, Listagens e Relatórios (admin)
`DashboardServlet` (contagens lendo o `DataStore`), `ListagemServlet` (produtos, fornecedores, clientes, armazém com stock > 0, compras detalhadas; ordenação por `?ordenar=&dir=`), `RelatorioServlet` (as quatro vistas do enunciado §IV, com D8). Contagem por `DataStore` e ordenação por `Comparator`; sem duplicar regras que já estão nos serviços.
**Pronto quando:** cada listagem ordena por pelo menos os campos que o enunciado lista e a "compra detalhada" mostra todos os campos pedidos.

### A8 · Integração e QA (tu és o dono do resultado) — ter 20 a qui 22 Out
1. `git switch main && git pull && git fetch origin && git switch -c integracao`
2. `git merge origin/pessoa-b` e `git merge origin/pessoa-c` (conflitos em `stub/` não devem existir: cada um tem a sua subpasta).
3. Trocar stubs por código real (contrato, secção 10):
   ```bash
   rm -rf src/main/java/mz/agrogest/stub
   grep -rlE 'mz\.agrogest\.stub\.' src | xargs -r sed -i -E 's/mz\.agrogest\.stub\.[bc]\./mz.agrogest./g'
   ```
   (Git Bash no Windows.)
4. Apagar `src/main/webapp/teste.jsp`. Compilar. Corrigir erros.
5. Correr o fluxo completo (checklist abaixo). Os erros de nome de getter só aparecem **ao abrir a página**: percorre todas.
6. Quando estiver estável: merge de `integracao` para `main`, tag, e `mvn clean package` → copiar o WAR para `entrega/`.

Faz review do código principal de B e C: tens de conseguir explicá-lo também.

### A9 · Defesa
Guião da demonstração ao vivo (ordem: admin cria dados → cliente compra → factura → relatórios → parar e arrancar o serviço e mostrar que os dados voltam). Ensaiar duas vezes com o grupo. Escrever a secção "Arquitectura" do README (para a Pessoa C colar).

### A10 · Extras (só se sobrar tempo; nunca antes de A8)
Dados de demonstração no primeiro arranque (o exemplo do enunciado mostra 3 fornecedores, 3 produtos, 5 clientes, 4 vendas); mais testes JUnit.

---

## 4. Checklist de aceitação (para o teu QA)

Regras do enunciado §VI (RN01 a RN06 = alíneas a–f):

- [ ] RN01: comprar mais do que o stock → recusado com mensagem.
- [ ] RN02: compra com 2 itens, 2.º sem stock → nada descontado.
- [ ] RN03: cliente X não abre a factura do cliente Y (por URL com `?id=`); admin abre todas.
- [ ] RN04: cliente em qualquer `/admin/*` → 403.
- [ ] RN05: anónimo em `/catalogo`, `/carrinho`, `/compra`, `/factura`, `/minhas-compras` → `/login`.
- [ ] RN06: ids sequenciais e únicos, mesmo após reiniciar.

Resto:

- [ ] "João Açúcar" num formulário aparece bem em listagens e factura.
- [ ] Nenhuma password em claro no `.ser` nem nos logs.
- [ ] Parar com Stop → arrancar → tudo igual. Ficheiro corrompido não bloqueia.
- [ ] Remover campanha com cultivos / cultivo com colheitas / fornecedor com produtos → recusado com mensagem.
- [ ] Factura imprime sem menu nem botões.
- [ ] Layout aceitável num ecrã estreito (telemóvel).
- [ ] `mvn clean package` gera `target/agrogest.war` e corre num Tomcat limpo.
