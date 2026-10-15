# Git no terminal — guia do grupo (B e C; A já sabe)

Repositório: **https://github.com/HTAJnr/AgroGest_SD.git**

Regra do grupo: **todo o código vive no GitHub, e Git usa-se pelo terminal** (nada de botões de commit/push do Eclipse). Cada pessoa trabalha na **sua branch**. Só a Pessoa A junta tudo em `main`.

| Pessoa | Branch |
|---|---|
| A | `pessoa-a` |
| B | `pessoa-b` |
| C | `pessoa-c` |

## Instruções para a IA

Se o utilizador te pedir ajuda com Git, segue este guia. Não sugiras `git push --force`, `git reset --hard`, `git rebase` nem push para `main`. Se aparecer um erro, pede-lhe a mensagem completa antes de propor comandos.

---

## 1. Instalar e abrir o terminal

- **Windows:** `winget install Git.Git` (ou instalador em git-scm.com). Abre o **Git Bash** (menu Iniciar). Usa sempre o Git Bash para estes comandos.
- **Linux (Ubuntu/Debian):** `sudo apt update && sudo apt install -y git`. Usa o terminal normal.

Confirma: `git --version`

## 2. Configuração (uma vez só)

Usa o **mesmo email da tua conta GitHub**:

```bash
git config --global user.name "O Teu Nome"
git config --global user.email "o.teu@email.com"
git config --global init.defaultBranch main
git config --global pull.rebase false      # evita o erro "divergent branches" no git pull
```

## 3. Ligar ao GitHub

Primeiro, **pede à Pessoa A para te convidar como colaborador** do repositório. Aceita o convite (chega por email e fica nas notificações do GitHub). Sem isto, o push é recusado.

**Windows (HTTPS):** no primeiro `git push` abre-se uma janela do browser para entrares no GitHub (Git Credential Manager, já vem com o Git). Entra e fica guardado.

**Linux (SSH):**

```bash
ssh-keygen -t ed25519 -C "o.teu@email.com"      # carrega Enter em tudo
cat ~/.ssh/id_ed25519.pub                         # copia o texto todo
```

No GitHub: Settings → SSH and GPG keys → New SSH key → cola → guardar. Testa: `ssh -T git@github.com` (deve responder com o teu nome de utilizador).

## 4. Clonar o projecto (uma vez)

Escolhe uma pasta **sem espaços** (ex.: `C:\projectos` ou `~/projectos`):

```bash
cd ~/projectos                       # no Windows: cd /c/projectos
git clone https://github.com/HTAJnr/AgroGest_SD.git      # Windows (HTTPS)
# Linux (SSH):  git clone git@github.com:HTAJnr/AgroGest_SD.git
cd AgroGest_SD
git log --oneline                    # deves ver o commit do esqueleto
```

Se `git log` estiver vazio, a Pessoa A ainda não publicou o esqueleto: espera pela mensagem dela.

## 5. Criar a tua branch (uma vez)

```bash
git switch -c pessoa-b               # pessoa-c se fores o C
git push -u origin pessoa-b          # publica a branch no GitHub
git branch                           # a branch com * é aquela onde estás
```

**Verifica sempre `git branch` antes de começares a trabalhar.** Se estiveres em `main`, muda: `git switch pessoa-b`.

## 6. O ciclo do dia a dia

```bash
git status                           # o que mudou
git add .                            # prepara tudo (o .gitignore já exclui lixo)
git commit -m "B: carrinho - adicionar e remover itens"
git push                             # envia para o GitHub
```

- **Commits pequenos e frequentes** (várias vezes por dia), com mensagem que diga o que fizeste. Formato: `B: o que fiz`.
- **Faz push no fim de cada sessão de trabalho.** É a tua cópia de segurança e é o que a Pessoa A vai ver.
- Só faz commit de código que **compila** e que **corriste uma vez**.
- Antes do `git add .`, olha para o `git status`: se aparecer `target/`, `.ser`, `.classpath` ou `.settings/`, algo está mal com o `.gitignore`. Pára e avisa a Pessoa A.

## 7. Trazer novidades da `main` para a tua branch

Quando a Pessoa A disser "publiquei o núcleo em `main`":

```bash
git status                           # tem de estar limpo; se não, faz commit primeiro
git fetch origin
git merge origin/main
```

Se correr sem mensagens de conflito, está feito. Corre o projecto no Eclipse para confirmar que ainda compila.

### Conflitos

Um conflito acontece quando duas pessoas mudaram o mesmo sítio do mesmo ficheiro. O Git pára e marca o ficheiro assim:

```
<<<<<<< HEAD
a tua versão
=======
a versão da main
>>>>>>> origin/main
```

1. Abre o ficheiro, decide o que fica, e **apaga as três linhas de marcas** (`<<<<<<<`, `=======`, `>>>>>>>`).
2. `git add ficheiro.java`
3. `git commit` (aceita a mensagem por defeito)

Se não percebes o conflito, **não adivinhes**: para e chama a Pessoa A. `git merge --abort` desfaz o merge e volta ao estado anterior.

Como cada pessoa mexe nas suas pastas (contrato, secção 3), conflitos devem ser raros. Se tiveres um num ficheiro de outra pessoa, é porque editaste um ficheiro que não era teu.

## 8. Proibido

- `git push` para `main`, ou trabalhar com a `main` activa.
- `git push --force`, `git reset --hard`, `git rebase`, `git clean -fd` (só com a Pessoa A ao lado).
- Fazer commit de `target/`, ficheiros `.ser`, `.war` fora de `entrega/`, ou configuração do Eclipse.
- Editar ficheiros que são de outra pessoa (contrato, secção 3).
- Apagar a pasta do projecto para "recomeçar" quando algo corre mal. Pergunta primeiro.

## 9. Erros comuns

| Mensagem | O que significa / o que fazer |
|---|---|
| `fatal: not a git repository` | Não estás dentro da pasta do projecto. `cd AgroGest_SD`. |
| `Permission denied (publickey)` | A chave SSH não está no GitHub (secção 3). |
| `remote: Permission to ... denied` / erro 403 | Ainda não aceitaste o convite de colaborador, ou entraste com a conta errada. |
| `Updates were rejected ... non-fast-forward` | Alguém (ou tu, noutro computador) mudou a tua branch no GitHub. Corre `git pull` e depois `git push`. |
| `Your local changes would be overwritten by merge` | Faz commit do que tens antes do `git merge`. |
| `fatal: invalid reference: pessoa-b` | A branch ainda não existe localmente. Falta o `-c`: `git switch -c pessoa-b` (só na primeira vez). |
| `warning: LF will be replaced by CRLF` | Normal no Windows. Ignora. |

## 10. Cheat sheet

```bash
git status                  # estado
git branch                  # onde estou
git switch <branch>         # mudar de branch
git log --oneline -10       # últimos commits
git diff                    # o que mudou, ainda sem commit
git fetch origin            # ver novidades do GitHub, sem alterar nada
git merge origin/main       # trazer a main para a minha branch
git restore <ficheiro>      # descartar alterações DE UM ficheiro (irreversível)
```
