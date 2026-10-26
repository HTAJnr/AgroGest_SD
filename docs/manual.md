# AgroGest — Manual de Utilização da Aplicação
**Sistema Integrado de Gestão Agrícola e Venda de Insumos**  
*Documentação Oficial de Apoio ao Utilizador — Versão 1.0.0*

---

<div align="center">
  <img src="img/logo-agrogest.png" alt="Logótipo AgroGest" width="220"/>
  <br><br>
  <strong>Engenharia Informática · Sistemas Distribuídos</strong><br>
  <strong>ISCTEM — Instituto Superior de Ciências e Tecnologia de Moçambique</strong><br>
  <em>Nampula / Maputo, Moçambique · Outubro de 2026</em>
</div>

<br>

| Informação | Detalhe |
| :--- | :--- |
| **Aplicação** | AgroGest — Gestão Agrícola e Loja Virtual |
| **Versão** | 1.0.0 (Release de Produção) |
| **Autores** | Ayko Sambo · Naira Ornilo · Hélder Vuca |
| **Docente / Disciplina** | Sistemas Distribuídos (Avaliação Prática) |
| **Ambiente de Execução** | Apache Tomcat 10.1 · Java 17 LTS · Linux Systemd (Porta 8081) |
| **Ficheiro de Persistência** | `/opt/tomcat/data/agrogest.ser` |

---

<div style="page-break-after: always;"></div>

## Índice Geral

1. [Introdução e Visão Geral](#1-introdução-e-visão-geral)
   - [1.1 Sobre o AgroGest](#11-sobre-o-agrogest)
   - [1.2 Perfis de Utilizador](#12-perfis-de-utilizador)
   - [1.3 Requisitos de Sistema e Navegador](#13-requisitos-de-sistema-e-navegador)
2. [Acesso e Autenticação](#2-acesso-e-autenticação)
   - [2.1 Início de Sessão (Login)](#21-início-de-sessão-login)
   - [2.2 Registo de Nova Conta de Cliente](#22-registo-de-nova-conta-de-cliente)
   - [2.3 Encerramento de Sessão (Logout)](#23-encerramento-de-sessão-logout)
3. [Manual do Administrador (Módulo de Gestão)](#3-manual-do-administrador-módulo-de-gestão)
   - [3.1 Painel Principal (Dashboard)](#31-painel-principal-dashboard)
   - [3.2 Gestão de Fornecedores](#32-gestão-de-fornecedores)
   - [3.3 Gestão de Produtos e Reposição de Stock](#33-gestão-de-produtos-e-reposição-de-stock)
   - [3.4 Campanhas Agrícolas](#34-campanhas-agrícolas)
   - [3.5 Parcelas de Cultivo](#35-parcelas-de-cultivo)
   - [3.6 Registo de Colheitas e Valor Total](#36-registo-de-colheitas-e-valor-total)
   - [3.7 Listagens Ordenáveis e Armazém](#37-listagens-ordenáveis-e-armazém)
   - [3.8 Auditoria de Compras Detalhadas](#38-auditoria-de-compras-detalhadas)
   - [3.9 Relatórios Analíticos de Gestão](#39-relatórios-analíticos-de-gestão)
   - [3.10 Salvaguarda e Persistência Manual de Dados](#310-salvaguarda-e-persistência-manual-de-dados)
4. [Manual do Cliente (Loja Online e Faturação)](#4-manual-do-cliente-loja-online-e-faturação)
   - [4.1 Catálogo de Produtos e Filtro por Categoria](#41-catálogo-de-produtos-e-filtro-por-categoria)
   - [4.2 Carrinho de Compras e Gestão de Quantidades](#42-carrinho-de-compras-e-gestão-de-quantidades)
   - [4.3 Finalização e Confirmação da Compra](#43-finalização-e-confirmação-da-compra)
   - [4.4 Emissão, Consulta e Impressão de Fatura Oficial](#44-emissão-consulta-e-impressão-de-fatura-oficial)
   - [4.5 Consulta do Histórico de Compras](#45-consulta-do-histórico-de-compras)
5. [Resumo das Regras de Negócio](#5-resumo-das-regras-de-negócio)
6. [Resolução de Problemas Frequentes (FAQ)](#6-resolução-de-problemas-frequentes-faq)

---

<div style="page-break-after: always;"></div>

## 1. Introdução e Visão Geral

### 1.1 Sobre o AgroGest

O **AgroGest** é uma solução computacional web concebida para modernizar e integrar as operações agrícolas e comerciais de produtores e cooperativas da província de Nampula, em Moçambique. O sistema unifica num único ambiente:

- A gestão operacional de fornecedores moçambicanos de sementes, fertilizantes e maquinaria;
- O controlo de inventário e reposição de produtos do armazém;
- O acompanhamento do ciclo produtivo agrícola através de campanhas sazonais, parcelas de cultivo e registo analítico de colheitas;
- Uma plataforma de comércio eletrónico (e-commerce) para clientes locais adquirirem insumos agrícolas com emissão instantânea de faturas comerciais fiscalmente estruturadas.

A aplicação foi desenvolvida segundo a arquitetura MVC (Model-View-Controller) em conformidade com as especificações Jakarta EE / Servlets 6.0 e Tomcat 10.1, garantindo processamento transacional seguro, controlo concorrente de stock e persistência de dados tolerante a falhas.

### 1.2 Perfis de Utilizador

O sistema contempla dois perfis distintos com níveis estritos de autorização (Filtro `AuthFilter`):

1. **Administrador (`ADMIN`)**:
   - Gestão completa de inventário, parceiros comerciais e ciclo agrícola;
   - Acesso a métricas globais, relatórios avançados de desempenho e listagens operacionais;
   - Consulta irrestrita a todas as faturas e operações de persistência atómica.
2. **Cliente (`CLIENTE`)**:
   - Consulta interativa do catálogo de produtos disponíveis em stock;
   - Gestão de carrinho de compras individual em sessão;
   - Aquisição de insumos agrícolas com emissão de fatura personalizada;
   - Acesso exclusivo ao seu histórico pessoal de faturas.

### 1.3 Requisitos de Sistema e Navegador

Por ser uma aplicação web responsiva orientada a padrões modernos (HTML5, CSS3 com variáveis semânticas e JavaScript assíncrono nativo):

- **Dispositivos:** Computadores de secretária, computadores portáteis, tablets e telemóveis inteligentes (otimizado até à resolução de 360 px de largura).
- **Navegadores Homologados:** Google Chrome (versão 100+), Mozilla Firefox (versão 100+), Microsoft Edge (versão 100+) e Safari (versão 15+).
- **Resolução de Ecrã Recomendada:** 1280 × 800 píxeis ou superior para tarefas administrativas de análise tabular; adaptável automaticamente a ecrãs móveis.
- **Conectividade:** Conexão à rede local ou internet onde se encontra publicado o servidor Tomcat (ex.: `http://localhost:8081/agrogest/`).

---

<div style="page-break-after: always;"></div>

## 2. Acesso e Autenticação

### 2.1 Início de Sessão (Login)

Todos os acessos à plataforma têm início na página de autenticação. Para proteger a integridade dos dados, as rotas do catálogo, compras e administração exigem uma sessão ativa.

![Ecrã de Login e Autenticação](img/01-login.png)
*Figura 1: Ecrã de autenticação com cartão centrado e proteção de credenciais.*

#### Procedimento de Entrada:
1. Abra o navegador e aceda ao endereço: `http://localhost:8081/agrogest/login` (ou à raiz `/agrogest/`, que redireciona automaticamente).
2. Introduza o seu **endereço de e-mail** registado.
3. Introduza a sua **palavra-passe**.
4. Prima o botão **Entrar**.
5. O sistema valida os dados de forma segura (palavra-passe encriptada com algoritmo BCrypt) e renova o identificador de sessão (`changeSessionId()`), encaminhando o utilizador:
   - Administrador → `/admin/dashboard`
   - Cliente → `/catalogo`

> [!TIP]
> **Credenciais Padrão para Demonstração e Testes:**
> - **Administrador:** `admin@agro.mz` · Palavra-passe: `admin234`
> - **Cliente Registado:** `joao@teste.mz` · Palavra-passe: `1234`

> [!NOTE]
> Se introduzir credenciais incorretas, o sistema apresenta a mensagem amigável *"Email ou palavra-passe incorretos"* diretamente no cartão de autenticação, preservando o e-mail introduzido para agilizar a correção sem recarregar a estrutura visual da página.

---

### 2.2 Registo de Nova Conta de Cliente

Qualquer novo agricultor ou cliente pode criar uma conta sem necessidade de intervenção do administrador.

![Formulário de Registo de Cliente](img/02-registo.png)
*Figura 2: Formulário de auto-registo de novo cliente com validação de contacto de Moçambique.*

#### Procedimento de Registo:
1. No ecrã de início de sessão, clique na ligação **"Ainda não tem conta? Registe-se"** (ou aceda a `/agrogest/registo`).
2. Preencha os campos obrigatórios do formulário:
   - **Nome Completo:** Introduza o seu nome (mínimo 3 caracteres alfabéticos, ex.: `João Açúcar`).
   - **Endereço de E-mail:** E-mail de contacto válido e único no sistema.
   - **Palavra-passe:** Mínimo de 4 caracteres.
   - **Telefone:** Número moçambicano válido das operadoras nacionais (prefixos 82/83 Tmcel, 84/85 Vodacom, 86/87 Movitel ou rede fixa 2x, ex.: `84 123 4567`). O sistema formata automaticamente para o padrão internacional `+258 84 123 4567`.
   - **Endereço:** Morada física ou localização da exploração agrícola (5 a 150 caracteres).
3. Prima o botão **Criar conta**.
4. Uma vez concluído o registo com sucesso, a conta é criada com o perfil `CLIENTE` e a sessão é iniciada de imediato, sendo o utilizador encaminhado para o catálogo de produtos.

---

### 2.3 Encerramento de Sessão (Logout)

Por razões de segurança e privacidade em terminais partilhados:
1. Clique no **Menu do Utilizador** localizado no canto superior direito da barra de navegação (botão com o avatar e o seu primeiro nome).
2. No menu suspenso, clique na opção **Sair** (acompanhada pelo ícone de logout).
3. A sessão é invalidada no servidor e o navegador regressa ao ecrã de início de sessão. O uso do botão "Retroceder" do navegador não permite reabrir páginas protegidas.

---

<div style="page-break-after: always;"></div>

## 3. Manual do Administrador (Módulo de Gestão)

O perfil de Administrador dispõe de privilégios globais para gerir toda a infraestrutura física, comercial e de produção da empresa agrícola.

### 3.1 Painel Principal (Dashboard)

Ao autenticar-se como administrador, o primeiro ecrã apresentado é o **Dashboard**, que sintetiza o pulso operacional da organização.

![Painel de Controlo do Administrador](img/03-admin-dashboard.png)
*Figura 3: Dashboard com cartões de contagem interativos e resumo das áreas principais.*

#### Elementos do Dashboard:
- **Barra de Navegação Superior Unificada:** Inclui o logótipo da marca, o menu do perfil do utilizador com botão de persistência de dados e os grupos de navegação com indicador dourado ativo (`.nav-ativo`):
  - **Dashboard:** Regresso imediato ao resumo geral.
  - **Gestão ▾:** Produtos, Fornecedores, Campanhas, Cultivos e Colheitas.
  - **Listagens ▾:** Produtos, Fornecedores, Clientes, Armazém e Compras detalhadas.
  - **Relatórios ▾:** Clientes, Que compraram, Mais assíduos e Sem compras.
- **Cartões Interativos com Métricas:**
  - **Fornecedores:** Total de parceiros ativos cadastrados no sistema. Clique para aceder à gestão de fornecedores.
  - **Produtos em Catálogo:** Total de insumos comercializados.
  - **Clientes Registados:** Base de agricultores e empresas clientes.
  - **Vendas Concluídas:** Total de encomendas emitidas com sucesso.
  - **Campanhas Agrícolas:** Número de campanhas de produção registadas.

---

### 3.2 Gestão de Fornecedores

A gestão rigorosa dos fornecedores assegura que os produtos adquiridos provêm de entidades idóneas e com especialização técnica comprovada.

![Listagem de Fornecedores](img/04-admin-fornecedores-lista.png)
*Figura 4: Listagem tabular de fornecedores com ações e dados de província.*

#### Consultar e Filtrar Fornecedores:
1. Aceda ao menu **Gestão ▾ → Fornecedores** (rota `/admin/fornecedores`).
2. A tabela exibe o **Nome do Fornecedor**, **Tipo de Especialização** (`SEMENTES`, `FERTILIZANTES`, `MAQUINARIA`), **Contacto** e a **Província** de Moçambique onde opera.
3. Para remover um parceiro, prima o botão com o ícone de lixo (`.btn-perigo.btn-icone`). Uma caixa de confirmação nativa solicita aprovação prévia.

> [!IMPORTANT]
> **Regra de Negócio D06:** O sistema recusa a eliminação de qualquer fornecedor que possua produtos associados no catálogo, impedindo registos órfãos e mantendo o histórico de compras íntegro.

![Formulário de Cadastro de Fornecedor](img/05-admin-fornecedor-novo.png)
*Figura 5: Cadastro de novo fornecedor com campos normalizados.*

#### Criar Novo Fornecedor:
1. Na página de fornecedores, prima o botão **+ Novo fornecedor** no canto superior direito.
2. Preencha os campos estruturados:
   - **Nome:** Designação social da empresa (3 a 80 caracteres; único no sistema de acordo com a regra D14).
   - **Tipo de Fornecedor:** Selecione entre `Sementes`, `Fertilizantes` ou `Maquinaria`.
   - **Contacto:** Telefone moçambicano ou e-mail corporativo.
   - **Província:** Seleção entre as 11 províncias de Moçambique (ex.: Nampula, Cabo Delgado, Sofala, Maputo Cidade, etc.).
3. Prima o botão **Guardar**. O sistema emite uma notificação (*toast*) verde de confirmação e regressa à listagem.

---

### 3.3 Gestão de Produtos e Reposição de Stock

O armazém do AgroGest centraliza os produtos disponíveis tanto para consumo interno nas explorações como para venda aos clientes.

![Listagem de Produtos e Stock](img/06-admin-produtos-lista.png)
*Figura 6: Inventário de produtos com fotos em miniatura 4:3, badges de stock e ações.*

#### Ações na Tabela de Produtos:
- **Miniatura Fotográfica:** Visualização rápida do insumo com proporção 4:3.
- **Badges Semânticos de Estado:**
  - `.badge` dourado: Categoria do produto (`SEMENTE`, `FERTILIZANTE`, `MAQUINARIA`, `FERRAMENTA`).
  - `.badge.off` cinzento/vermelho: Indica **Esgotado** quando a quantidade atinge 0.
  - `.badge.aviso` amarelo: Indica **Expirado** caso a data de validade tenha sido ultrapassada.
- **Repor Stock:** Clique no botão com o ícone **+** para abrir o diálogo modal interativo. Introduza a quantidade adicional a creditar no armazém e confirme. O valor é imediatamente somado ao stock existente.
- **Remover Produto:** Clique no ícone do caixote do lixo para retirar o produto do catálogo. O produto é removido de futuras vendas, mas as vendas antigas mantêm o histórico intacto (Regra D07).

![Cadastro de Novo Produto](img/07-admin-produto-novo.png)
*Figura 7: Formulário de novo produto com seleção de unidade em chips e galeria de fotos.*

#### Registar Novo Produto:
1. Prima **+ Novo produto** no cabeçalho da página de produtos (`/admin/produtos/novo`).
2. Indique os atributos do insumo:
   - **Nome do Produto:** Nome comercial claro (3 a 80 caracteres).
   - **Categoria:** Escolha a categoria pretendida.
   - **Fornecedor:** A seleção é automaticamente filtrada segundo a **Regra D13**:
     - Categoria `SEMENTE` exige fornecedor de `SEMENTES`.
     - Categoria `FERTILIZANTE` exige fornecedor de `FERTILIZANTES`.
     - Categorias `MAQUINARIA` e `FERRAMENTA` exigem fornecedor de `MAQUINARIA`.
   - **Preço Unitário (MT):** Preço de venda em Meticais (positivo, com até 2 casas decimais).
   - **Quantidade Inicial:** Quantidade física disponível para arranque.
   - **Unidade de Medida:** Escolha amigável em chips estilizados (`kg`, `saco`, `litro`, `tonelada`, `unidade`).
   - **Data de Validade:** Data futura de expiração (opcional para ferramentas/máquinas).
   - **Descrição:** Resumo técnico e especificações de aplicação.
   - **Fotografia:** Selecione uma imagem da galeria local integrada ou insira o endereço de uma fotografia.
3. Prima **Guardar**. Em caso de inconformidade de campos, o erro específico é realçado a vermelho por baixo do elemento com foco automático.

---

### 3.4 Campanhas Agrícolas

As campanhas representam os ciclos temporais de cultivo agrícola (ex.: época das chuvas, época de sequeiro ou safras anuais).

![Listagem de Campanhas Agrícolas](img/08-admin-campanhas-lista.png)
*Figura 8: Listagem de campanhas com controlo de estado Aberta / Fechada.*

#### Operações com Campanhas:
- **Ver Cultivos:** Clique na hiperligação com o nome da campanha para inspecionar as parcelas de terra ativas associadas ao ciclo.
- **Fechar Campanha:** Ao concluir a época agrícola, prima **Fechar**. A campanha passa ao estado **Fechada** (`.badge.off`).
- **Reabrir Campanha:** Permite reativar uma campanha encerrada caso seja necessário registar acertos operacionais.
- **Remover Campanha:** Só é permitida a remoção se a campanha não contiver nenhum cultivo associado.

> [!WARNING]
> **Regra de Negócio D05:** Campanhas no estado **Fechada** bloqueiam terminantemente a abertura de novos cultivos e o registo de novas colheitas, salvaguardando o encerramento do exercício agrícola.

![Formulário de Nova Campanha](img/09-admin-campanha-novo.png)
*Figura 9: Registo de nova campanha agrícola com datas de vigência.*

#### Criar Nova Campanha:
1. No menu superior selecione **Gestão ▾ → Campanhas** e clique em **+ Nova campanha**.
2. Indique a **Designação da Campanha** (ex.: `Campanha Agrícola 2026/2027`).
3. Defina a **Data de Início** e a **Data de Fim** (a data final tem obrigatoriamente de ser igual ou posterior à data inicial).
4. Prima **Guardar**. A campanha é criada no estado **Aberta** por defeito.

---

### 3.5 Parcelas de Cultivo

Cada campanha é dividida em parcelas de cultivo específicas, onde se plantam diferentes espécies agrícolas em áreas delimitadas.

![Gestão de Cultivos por Campanha](img/10-admin-cultivos-lista.png)
*Figura 10: Parcelas de cultivo instaladas com especificação de hectares e variedade.*

#### Gerir Cultivos:
1. Aceda a **Gestão ▾ → Cultivos** (ou através da ligação direta de uma campanha).
2. O topo apresenta a campanha selecionada, a sua vigência e o badge de estado.
3. A tabela discrimina:
   - **Cultura:** Espécie vegetal semeada (ex.: Milho, Feijão, Arroz).
   - **Variedade:** Linhagem agronómica (ex.: Matuba, Nhemba, Pan 53).
   - **Área (Hectares):** Dimensão do talhão produtivo.
   - **Data de Plantio:** Data de sementeira em campo.
   - **Ações:** Atalho direto para **Colheitas** e botão de remoção da parcela (recusada se já existirem colheitas registadas).
4. **Adicionar Cultivo:** Utilize o formulário posicionado por baixo da tabela para lançar novas parcelas sem sair do contexto da campanha.

---

### 3.6 Registo de Colheitas e Valor Total

O módulo de colheitas contabiliza os rendimentos físicos obtidos no campo e calcula automaticamente o valor monetário bruto correspondente à produção.

![Registo de Colheitas](img/11-admin-colheitas-lista.png)
*Figura 11: Colheitas apuradas com destaque do valor total monetário.*

#### Registar Rendimento de Colheita:
1. Aceda a **Gestão ▾ → Colheitas** e selecione a parcela de cultivo em exploração.
2. No formulário **Registar nova colheita**, preencha:
   - **Data da Colheita:** Dia em que se efetuou o corte/apanha.
   - **Quantidade Colhida (kg):** Peso total aferido em quilogramas (ex.: `3200`).
   - **Preço de Referência por kg (MT):** Cotação de mercado por quilograma (ex.: `22,00`).
3. Prima **Registar colheita**.
4. O sistema insere a linha na tabela e calcula o **Valor Total** através da fórmula do modelo de domínio:
   $$\text{Valor Total} = \text{Quantidade (kg)} \times \text{Preço/kg} = 3\,200 \times 22{,}00\text{ MT} = 70\,400{,}00\text{ MT}$$

---

### 3.7 Listagens Ordenáveis e Armazém

Para efeitos de auditoria e conferência física, o módulo de **Listagens** oferece grelhas de dados configuráveis em tempo real.

![Listagens Ordenáveis do Sistema](img/12-admin-listagens.png)
*Figura 12: Listagem de produtos com cabeçalhos ordenáveis interativos.*

#### Recursos das Listagens:
- **Abas de Navegação (`.abas`):** Alterne confortavelmente entre **Produtos**, **Fornecedores**, **Clientes**, **Armazém** e **Compras detalhadas**.
- **Ordenação Bidirecional:** Clique em qualquer cabeçalho de coluna (ex.: *Nome*, *Categoria*, *Preço*, *Stock*) para alternar entre ordenação ascendente (▲) e descendente (▼).
- **Aba de Armazém:** Exibe o balanço de stocks críticos, sinalizando produtos com necessidade urgente de reposição.

---

### 3.8 Auditoria de Compras Detalhadas

Ao contrário de uma simples tabela de totais, a secção de **Compras Detalhadas** expõe cada transação em cartões individuais completos.

![Compras Detalhadas dos Clientes](img/13-admin-compras-detalhadas.png)
*Figura 13: Cartões individuais de compras detalhadas com discriminação fiscal.*

#### Informação Apresentada em Cada Cartão:
- Número da fatura comercial e data/hora de emissão;
- Identificação fiscal do cliente: nome completo, telefone moçambicano e morada de entrega;
- Discriminação linha a linha de cada insumo faturado (designação, quantidade, unidade, preço unitário e subtotal);
- Linhas de apuramento financeiro: Subtotal, Imposto sobre o Valor Acrescentado (**IVA a 16%**) e Valor Total Pago;
- Ligação direta **"Ver factura"** para consulta do duplicado oficial.

---

### 3.9 Relatórios Analíticos de Gestão

O AgroGest disponibiliza quatro relatórios estratégicos para apoiar a tomada de decisão comercial e fidelização de clientes.

![Relatórios Analíticos com Pódio](img/14-admin-relatorios.png)
*Figura 14: Relatório de clientes mais assíduos com identificação de pódio.*

#### Relatórios Disponíveis:
1. **Mais Assíduos:** Classificação dos clientes com maior número de transações efetuadas. As três primeiras posições recebem destaque de pódio de honra (🥇 1.º Lugar, 🥈 2.º Lugar, 🥉 3.º Lugar).
2. **Que Compraram:** Ordenação pelo volume financeiro global gasto (maior volume de negócios gerado para o AgroGest).
3. **Clientes:** Listagem alfabética de todos os agricultores e entidades registadas.
4. **Sem Compras:** Identificação dos utilizadores que criaram conta mas ainda não efetuaram qualquer aquisição, permitindo ações de contacto ou apoio comercial.

---

### 3.10 Salvaguarda e Persistência Manual de Dados

O AgroGest não utiliza sistemas relacionais pesados externos; os dados residem na memória do servidor e são salvaguardados em ficheiro binário através de serialização Java atómica (`/opt/tomcat/data/agrogest.ser`).

![Painel de Persistência em Disco](img/15-admin-persistencia.png)
*Figura 15: Painel de persistência com ações de gravação atómica e proteção.*

#### Procedimentos de Salvaguarda:
- **Gravação Automática:** Ocorre de forma transparente e segura no fecho regular do servidor Tomcat (via *Shutdown Hook* e `ContextListener`).
- **Gravar Agora (Manual):** No menu do utilizador clique em **Persistência** e prima o botão **Gravar agora**. Uma cópia atómica e íntegra é gravada de imediato no disco (`.tmp` seguido de substituição atómica com salvaguarda de `.ser.bak`).
- **Eliminar Ficheiro:** Apaga unicamente o ficheiro físico no disco.
  > [!IMPORTANT]
  > **Regra de Negócio D10:** A eliminação do ficheiro `.ser` limpa o ficheiro em disco mas **não apaga os dados ativos em memória**. Caso deseje recomeçar a aplicação completamente vazia, elimine o ficheiro e reinicie o serviço Tomcat (`systemctl restart tomcat`).

---

<div style="page-break-after: always;"></div>

## 4. Manual do Cliente (Loja Online e Faturação)

O módulo do Cliente oferece uma experiência de compra ágil, intuitiva e segura, adaptada tanto a ecrãs de computador como a telemóveis.

### 4.1 Catálogo de Produtos e Filtro por Categoria

Após a autenticação, o cliente é recebido na montra virtual do AgroGest.

![Catálogo Virtual de Produtos](img/16-cliente-catalogo.png)
*Figura 16: Catálogo de produtos com faixa hero, filtro em chips e grelha de insumos.*

#### Como Explorar o Catálogo:
1. **Faixa de Saudação (Hero):** Dá as boas-vindas personalizadas (*"Olá, João"*) com fundo em verde-floresta e grafismo estilizado.
2. **Filtro Rápido em Chips:** Clique numa das categorias no topo para filtrar a lista instantaneamente:
   - **Todas** (Catálogo completo)
   - **Sementes** (Milho, Feijão nhemba, Arroz, Amendoim)
   - **Fertilizantes** (Adubos NPK, Ureia granulada, Composto orgânico)
   - **Maquinaria** (Tratores agrícolas, Pulverizadores, Bombas de rega)
   - **Ferramentas** (Enxadas, Regadores, Pás, Catanas, Luvas de trabalho)
3. **Cartão de Produto:** Cada cartão detalha a fotografia real do insumo em proporção 4:3, nome comercial, fornecedor, validade, preço unitário por medida e stock disponível.
4. **Adicionar ao Carrinho:**
   - Indique a quantidade pretendida no campo numérico (ex.: `3` kg de semente de milho).
   - Prima o botão com o ícone de carrinho. O produto é imediatamente incorporado no seu carrinho pessoal e o contador no topo da página atualiza-se.

---

### 4.2 Carrinho de Compras e Gestão de Quantidades

O carrinho de compras permite rever, afinar e validar os artigos selecionados antes de assumir o compromisso financeiro.

![Carrinho de Compras do Cliente](img/17-cliente-carrinho.png)
*Figura 17: Carrinho de compras com tabela responsiva e apuramento financeiro.*

#### Gestão de Artigos no Carrinho:
- **Alterar Quantidade:** Modifique o número na caixa da linha respetiva e prima **Atualizar**. O subtotal da linha e os totais da página são recalculados na hora.
- **Remover Artigo:** Prima o botão vermelho com o ícone do caixote do lixo. Uma confirmação solicita certeza antes de retirar o item.
- **Esvaziar Carrinho:** O botão inferior permite limpar todos os artigos de uma só vez.
- **Apuramento Financeiro Transparente:**
  - **Subtotal:** Soma dos preços dos produtos.
  - **IVA (16%):** Imposto legal sobre o valor acrescentado calculado com arredondamento comercial a 2 casas decimais (`HALF_UP`).
  - **Total Estimado:** Montante final a pagar em Meticais (MT).
- Prima **Finalizar compra** para avançar para o pagamento.

---

### 4.3 Finalização e Confirmação da Compra

O ecrã de checkout consolida os dados de faturação e previne erros de duplo pagamento ou quebra de stock concorrente.

![Resumo e Confirmação de Compra](img/18-cliente-confirmar-compra.png)
*Figura 18: Confirmação de compra com dados fiscais e proteção anti duplo envio.*

#### Passo-a-passo da Confirmação:
1. Verifique os **Dados de Faturação e Entrega** (nome, e-mail, telefone normalizado e endereço da sua exploração).
2. Reveja a tabela resumo dos produtos e o montante global a debitar.
3. Prima o botão **Confirmar compra**.
4. **Proteção Transacional Ativa (RN01, RN02 e D19):**
   - O botão desativa-se instantaneamente, apresentando o texto *"A confirmar compra…"*, impedindo que um duplo clique gere duas vendas ou debite stock duplicado.
   - O servidor processa a venda de forma atómica dentro do monitor global (`DataStore.getLock()`). Se outro cliente tiver acabado de comprar o último saco de adubo, o sistema recusa a venda e mantém o seu carrinho intacto sem debitar valores parciais (Princípio do Tudo ou Nada).

---

### 4.4 Emissão, Consulta e Impressão de Fatura Oficial

Assim que a compra é confirmada com sucesso, o sistema emite e apresenta de imediato a **Fatura Comercial** oficial.

![Fatura Comercial Emitida](img/19-cliente-factura.png)
*Figura 19: Fatura comercial oficial (FT-2026-X) pronta para impressão e arquivo.*

#### Características da Fatura Comercial:
- **Numeração Sequencial Única (D02):** Identificador padronizado no formato `FT-{ano}-{id}` (ex.: `FT-2026-1`).
- **Cabeçalho Formal:** Marca AgroGest, data e hora de emissão e domicílio legal em Nampula, Moçambique.
- **Dados do Cliente:** Informação fiscal completa para dedução de despesas operacionais.
- **Quadro de Itens:** Preço unitário, quantidade, unidade física e subtotais.
- **Quadro de Totais:** Subtotal, IVA discriminado a 16% e Valor Total em Meticais.
- **Impressão Pronta:** Clique no botão **Imprimir fatura** (ou utilize o atalho de teclado `Ctrl+P`). A aplicação aciona a folha de estilo de impressão especializada (`@media print`), que:
  - Oculta a barra de navegação, botões e elementos web;
  - Ajusta o documento com margens exatas para papel A4;
  - Converte o fundo para branco com texto preto nítido de alta legibilidade.

> [!NOTE]
> **Privacidade de Dados (Regra RN03):** O cliente só consegue consultar e visualizar as suas próprias faturas. Se tentar aceder à fatura de outro agricultor alterando o identificador na barra de endereço, o servidor bloqueia o acesso com o código `403 Proibido`. O administrador tem autorização para visualizar todas as faturas emitidas.

---

### 4.5 Consulta do Histórico de Compras

Para consultar encomendas passadas e reimprimir faturas anteriores:
1. Abra o menu do utilizador e selecione **Minhas compras** (rota `/minhas-compras`).
2. A lista apresenta todas as faturas emitidas em seu nome, ordenadas cronologicamente.

![Histórico de Minhas Compras](img/20-cliente-minhas-compras.png)
*Figura 20: Histórico de encomendas passadas com acesso direto às faturas.*

3. Prima o botão **Ver fatura** para aceder ao duplicado oficial da transação a qualquer momento.

---

<div style="page-break-after: always;"></div>

## 5. Resumo das Regras de Negócio

Para consulta rápida durante a utilização ou defesa oral da aplicação, a tabela abaixo sintetiza as regras e decisões implementadas no AgroGest:

| Código | Descrição da Regra | Impacto Prático na Aplicação |
| :--- | :--- | :--- |
| **RN01** | **Respeito Estrito de Stock** | O sistema nunca vende acima da quantidade física disponível em armazém. |
| **RN02** | **Venda Atómica (Tudo ou Nada)** | Se um único item da encomenda falhar em stock, toda a venda é abortada; o stock dos outros itens não se altera. |
| **RN03** | **Privacidade de Faturação** | O cliente só pode abrir as suas faturas (erro 403 em caso de intrusão). O administrador pode auditar todas. |
| **RN04** | **Restrição de Administração** | Todas as rotas `/admin/*` exigem perfil de administrador; clientes recebem 403 imediato. |
| **RN05** | **Sessão Obrigatória** | Rotas de catálogo, carrinho, compra e faturas exigem sessão ativa; utilizadores anónimos são direcionados para o login. |
| **RN06** | **Identificadores Sequenciais Atómicos** | Chaves primárias mantêm-se sequenciais únicas mesmo após múltiplos reinícios do servidor (`AtomicInteger`). |
| **D01** | **Utilizador Ativo** | O estado de ativação de conta é preservado no modelo para conformidade estrutural. |
| **D02** | **Numeração de Fatura** | Formato fiscal uniforme: `FT-{ano}-{id}` (ex.: `FT-2026-1`). |
| **D03 / D04** | **Precisão Monetária com BigDecimal** | Dinheiro calculado com `BigDecimal` a 2 casas decimais (`HALF_UP`). Preços de catálogo expressos sem IVA; IVA a 16% apurado no checkout. |
| **D05** | **Bloqueio de Campanha Fechada** | Campanhas fechadas não aceitam abertura de novos cultivos nem registo de colheitas. |
| **D06** | **Proteção de Fornecedor com Produtos** | Não é possível eliminar fornecedores que tenham insumos associados no catálogo. |
| **D07** | **Preservação de Histórico de Venda** | A eliminação de um produto do catálogo não apaga nem afeta as faturas emitidas no passado. |
| **D08** | **Métricas de Relatório** | "Volume" corresponde ao total de Meticais gastos; "Assíduos" corresponde ao número absoluto de compras. |
| **D10** | **Persistência Atómica em Disco** | Escrita segura com gravação temporária (`.tmp`) e cópia de segurança (`.ser.bak`). "Eliminar" apaga o ficheiro físico, mantendo a memória até reiniciar. |
| **D11** | **Criptografia BCrypt** | Palavras-passe nunca são gravadas em texto limpo; o administrador padrão é garantido no arranque. |
| **D13** | **Compatibilidade Insumo / Parceiro** | Produtos de `SEMENTE` só aceitam fornecedores de `SEMENTES`; `FERTILIZANTE` com `FERTILIZANTES`; `MAQUINARIA`/`FERRAMENTA` com `MAQUINARIA`. |
| **D14** | **Unicidade de Registos** | Nomes de fornecedores, produtos e campanhas são únicos (insensíveis a maiúsculas/minúsculas). |
| **D15** | **Controlo de Validade** | A criação de produtos exige data futura; produtos com validade expirada recebem badge amarelo sem travar venda de emergência. |
| **D16** | **Unidades de Medida Fixas** | Seleção estrita por chips: `kg`, `saco`, `litro`, `tonelada`, `unidade`. |
| **D17** | **Normalização de Contactos MZ** | Validação estrita de prefixos móveis (82/83, 84/85, 86/87) e fixos (2x) com gravação normalizada `+258 XX XXX XXXX`. |
| **D18** | **Feedback Erro no Campo** | Mensagens de validação surgem exatamente por baixo do campo com falha, com foco e realce visual. |
| **D19** | **Token Anti Duplo Envio** | Token `_t` de consumo único no request POST impede duplicação de produtos ou faturas por duplo clique rápido. |

---

<div style="page-break-after: always;"></div>

## 6. Resolução de Problemas Frequentes (FAQ)

### 1. Esqueci-me da palavra-passe do administrador. Como posso aceder?
O administrador padrão do sistema é automaticamente garantido no arranque com as credenciais:
- **E-mail:** `admin@agro.mz`
- **Palavra-passe:** `admin234`
Caso os dados tenham sido alterados ou corrompidos, elimine o ficheiro `/opt/tomcat/data/agrogest.ser` e reinicie o servidor Tomcat. O utilizador padrão será recriado no próximo arranque.

### 2. Ao tentar eliminar um fornecedor, recebo uma mensagem de erro. Porquê?
Em cumprimento da **Regra D06**, o sistema proíbe a remoção de fornecedores que tenham produtos registados. Para conseguir remover o fornecedor, deve primeiro remover os produtos associados a ele ou atribuí-los a outro parceiro.

### 3. Porque é que não consigo registar um novo cultivo na minha campanha?
Verifique se a campanha agrícola não se encontra no estado **Fechada**. Apenas campanhas com o badge verde **Aberta** aceitam novas parcelas de cultivo e novos registos de colheita (Regra D05). Caso necessário, prima o botão **Reabrir** na listagem de campanhas.

### 4. Um produto que pretendo comprar diz "Indisponível". O que significa?
Significa que o stock do armazém atingiu o valor zero (`0`). O botão de adição ao carrinho é automaticamente bloqueado para evitar transações que violem a regra de negócio RN01. Contacte o administrador do armazém para proceder à reposição do produto.

### 5. O que acontece se o computador desligar repentinamente por falha de energia?
O sistema grava periodicamente os dados e guarda sempre uma cópia de segurança (`agrogest.ser.bak`). Quando o sistema for religado, a persistência atómica deteta e recupera o último estado válido conhecido. Recomenda-se que o administrador utilize a funcionalidade **Gravar agora** no painel de persistência após operações de grande volume.

---

<br>

<div align="center">
  <hr>
  <strong>AgroGest — Inovação e Eficiência na Agricultura de Moçambique</strong><br>
  <em>Desenvolvido por Ayko Sambo, Naira Ornilo e Hélder Vuca · ISCTEM 2026</em>
</div>
