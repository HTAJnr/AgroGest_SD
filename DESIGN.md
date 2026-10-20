---
version: "1.0"
name: "AgroGest"
description: "Sistema de gestão agrícola. Verde-floresta sobre papel claro, texto sempre legível."
defaultMode: "light"
supportsDark: false
base: "Inspirado na estrutura do wise-design.md; paleta própria (agrícola, contraste verificado)."

consumer_contract:
  standalone: true
  goal: "Estilizar as JSP do AgroGest (css/estilo.css) a partir deste ficheiro."
  priority_order:
    - "Usar os tokens (variáveis CSS) antes de escrever cores à mão."
    - "Estilizar SÓ as classes que o contrato já define (secção 8.11). Não renomear."
    - "Nunca pôr texto e fundo da mesma cor em nenhum estado (hover, focus, active, disabled)."
  accessibility_rule: "WCAG AA: 4.5:1 em texto normal. Foco visível. Alvos >= 44px. Corpo >= 16px."

colors:
  primary: "#1f6f3a"            # botão principal, links
  primary-hover: "#17562d"
  primary-tint: "#e3f2e6"       # fundo de btn-sec em hover, linhas seleccionadas
  on-primary: "#ffffff"
  accent: "#e9b949"             # só badges/destaques; texto por cima = ink
  ink: "#1b2a1f"                # texto
  muted: "#5b6b5f"              # texto secundário
  bg: "#f4f7f2"                 # fundo da página
  surface: "#ffffff"            # cartões, tabelas, inputs
  border: "#d9e2d6"
  bar: "#14301d"                # barra de topo
  on-bar: "#ffffff"
  danger: "#b3261e"
  danger-hover: "#8c1d18"
  danger-tint: "#fdeceb"
  ok-text: "#14532d"
  ok-tint: "#e3f2e6"

contrast_verified:             # calculado, não estimado
  on-primary/primary: 6.19
  on-primary/primary-hover: 8.73
  primary/surface (btn-sec): 6.19
  primary-hover/primary-tint (btn-sec hover): 7.53
  ink/bg: 13.90
  muted/surface: 5.66
  muted/bg: 5.23
  white/danger: 6.54
  white/danger-hover: 9.11
  ok-text/ok-tint: 7.86
  danger-text/danger-tint: 7.97
  on-bar/bar: 14.28
  ink/accent: 8.23

typography:
  family: "Inter, system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif"
  mono: "ui-monospace, SFMono-Regular, Menlo, Consolas, monospace"
  h1: "32px / 1.15 / 700"
  h2: "24px / 1.25 / 600"
  h3: "18px / 1.3 / 600"
  body: "16px / 1.5 / 400"
  small: "14px / 1.4 / 400"      # só para .muted e legendas; nunca para corpo
  numeric: "tabular-nums"        # tabelas e totais (.num, .total)

spacing: { 1: 4px, 2: 8px, 3: 12px, 4: 16px, 6: 24px, 8: 32px, 12: 48px }
radius: { input: 8px, card: 12px, button: 8px, badge: 999px }
shadow: { card: "0 1px 2px rgba(20,48,29,.08)", overlay: "0 10px 30px rgba(20,48,29,.15)" }
motion: { fast: "120ms", ease: "cubic-bezier(0,0,.2,1)" }
---

## 1. Princípio que resolve o bug que viste

O botão ficou ilegível porque, em hover, **o fundo passou a ser da mesma cor do texto** (verde sobre verde, contraste 1.00:1). No Wise isto não acontece porque o texto do botão principal é escuro sobre lima e o hover não troca as cores entre si.

Regra deste sistema: **o hover muda o fundo para o tom seguinte da MESMA família e o texto mantém a sua cor.** Nunca se muda só uma das duas, e nunca se usa a cor do texto como fundo.

| Botão | Repouso | Hover | Foco | Desactivado |
|---|---|---|---|---|
| `.btn` (principal) | fundo `primary`, texto `on-primary` | fundo `primary-hover`, texto `on-primary` | anel 3px `accent` | fundo `border`, texto `muted`, sem hover |
| `.btn-sec` | fundo `surface`, texto `primary`, borda `primary` | fundo `primary-tint`, texto `primary-hover`, borda `primary-hover` | anel 3px `accent` | igual ao principal |
| `.btn-perigo` | fundo `danger`, texto branco | fundo `danger-hover`, texto branco | anel 3px `accent` | igual ao principal |

## 2. Componentes (mapeados nas classes do contrato)

- **`.barra`**: fundo `bar`, texto `on-bar`; link activo sublinhado `accent`. Hover do link: fundo branco a 12%, texto continua branco.
- **`.conteudo`**: max-width 1100px, padding 24px (12px abaixo de 640px).
- **`.cartao`**: fundo `surface`, borda 1px `border`, raio 12px, padding 16px, sombra `card`. Sem alteração em hover (só se for clicável).
- **`.grelha`**: `grid`, colunas `repeat(auto-fill, minmax(240px, 1fr))`, gap 16px. Em ecrã estreito cai para 1 coluna sozinha.
- **`.tabela`**: cabeçalho fundo `primary-tint`, texto `ink`, 600; linhas com borda inferior `border`; zebra opcional `bg`; `.num` alinhado à direita com `tabular-nums`. `.tabela-scroll` envolve com `overflow-x:auto`.
- **`.form`**: label acima do campo, 14px/600; input 44px de altura, borda `border`, raio 8px, fundo `surface`, texto `ink`; foco: borda `primary` + anel 3px `accent`.
- **`.alerta.ok`**: fundo `ok-tint`, texto `ok-text`, borda esquerda 4px `primary`. **`.alerta.erro`**: fundo `danger-tint`, texto `danger-hover`, borda esquerda 4px `danger`.
- **`.badge`**: fundo `accent`, texto `ink`, raio 999px, 12px/600, padding 2px 10px.
- **`.muted`**: cor `muted`. **`.total`**: 20px/700, `tabular-nums`.
- **`.factura`** e **`.nao-imprimir`**: em `@media print`, esconder `.nao-imprimir` e `.barra`, fundo branco, texto preto, sem sombras nem bordas coloridas.

## 3. Foco e teclado

Todo o elemento interactivo tem `:focus-visible` com `outline: 3px solid var(--accent); outline-offset: 2px`. Nunca `outline: none` sem substituto.

## 4. Responsivo

Abaixo de 640px: padding do `.conteudo` a 12px, `.grelha` a 1 coluna, `.barra` empilha, botões ocupam 100% da largura dentro de `.form`, tabelas dentro de `.tabela-scroll`.

## 5. Proibido

- Texto e fundo da mesma cor em qualquer estado.
- Cor escrita à mão (`#1f6f3a`) em vez de `var(--primary)`.
- `!important` para "resolver" hover (é o que costuma causar o bug).
- Usar `danger` para outra coisa que não erro/remoção.
- Corpo de texto abaixo de 16px.

## 6. CSS pronto a colar (topo do `estilo.css`)

```css
:root{
  --primary:#1f6f3a; --primary-hover:#17562d; --primary-tint:#e3f2e6; --on-primary:#fff;
  --accent:#e9b949; --ink:#1b2a1f; --muted:#5b6b5f;
  --bg:#f4f7f2; --surface:#fff; --border:#d9e2d6;
  --bar:#14301d; --on-bar:#fff;
  --danger:#b3261e; --danger-hover:#8c1d18; --danger-tint:#fdeceb;
  --ok-text:#14532d; --ok-tint:#e3f2e6;
  --radius:8px; --radius-card:12px;
}
body{background:var(--bg);color:var(--ink);font:16px/1.5 Inter,system-ui,-apple-system,"Segoe UI",Roboto,sans-serif}

.btn,.btn-sec,.btn-perigo{
  display:inline-flex;align-items:center;justify-content:center;gap:8px;
  min-height:44px;padding:8px 16px;border:1px solid transparent;border-radius:var(--radius);
  font:600 16px/1.25 inherit;cursor:pointer;text-decoration:none;
  transition:background-color .12s ease,color .12s ease,border-color .12s ease;
}
.btn{background:var(--primary);color:var(--on-primary);border-color:var(--primary)}
.btn:hover{background:var(--primary-hover);border-color:var(--primary-hover);color:var(--on-primary)}

.btn-sec{background:var(--surface);color:var(--primary);border-color:var(--primary)}
.btn-sec:hover{background:var(--primary-tint);color:var(--primary-hover);border-color:var(--primary-hover)}

.btn-perigo{background:var(--danger);color:#fff;border-color:var(--danger)}
.btn-perigo:hover{background:var(--danger-hover);border-color:var(--danger-hover);color:#fff}

.btn:active,.btn-sec:active,.btn-perigo:active{transform:scale(.98)}
.btn:disabled,.btn-sec:disabled,.btn-perigo:disabled{
  background:var(--border);color:var(--muted);border-color:var(--border);cursor:not-allowed;transform:none}
:is(a,button,input,select,textarea):focus-visible{outline:3px solid var(--accent);outline-offset:2px}

.cartao{background:var(--surface);border:1px solid var(--border);border-radius:var(--radius-card);padding:16px;box-shadow:0 1px 2px rgba(20,48,29,.08)}
.form input,.form select,.form textarea{min-height:44px;width:100%;padding:8px 12px;border:1px solid var(--border);border-radius:var(--radius);background:var(--surface);color:var(--ink);font:inherit}
.form input:focus,.form select:focus,.form textarea:focus{border-color:var(--primary)}
.alerta{padding:12px 16px;border-radius:var(--radius);border-left:4px solid}
.alerta.ok{background:var(--ok-tint);color:var(--ok-text);border-color:var(--primary)}
.alerta.erro{background:var(--danger-tint);color:var(--danger-hover);border-color:var(--danger)}
.badge{background:var(--accent);color:var(--ink);border-radius:999px;padding:2px 10px;font:600 12px/1.25 inherit}
.muted{color:var(--muted)}
.num{text-align:right;font-variant-numeric:tabular-nums}
.total{font:700 20px/1.25 inherit;font-variant-numeric:tabular-nums}

@media print{.nao-imprimir,.barra{display:none!important}body{background:#fff;color:#000}}
```

## 7. Como entra no projecto sem partir o contrato

`css/estilo.css` e `WEB-INF/jspf/*` são da **Pessoa C**. Não mexas neles na tua branch: dá-lhe este ficheiro e o bloco da secção 6 e pede-lhe para o aplicar na branch dela. Se editares tu, tens conflito de merge na integração, que é o que menos precisas esta semana.
