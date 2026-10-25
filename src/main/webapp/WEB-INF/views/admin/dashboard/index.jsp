<div class="conteudo">
  <div class="pagina-topo">
    <h1>Dashboard</h1>
  </div>

  <div class="grelha dash-grelha">

    <a href="<c:url value='/admin/fornecedores'/>" class="cartao cartao-link dash-cartao" aria-label="Fornecedores">
      <div class="dash-icone-circulo">
        <svg class="icone" aria-hidden="true"><use href="#i-camiao"/></svg>
      </div>
      <p class="dash-numero">${totalFornecedores}</p>
      <p class="dash-etiqueta">Fornecedores</p>
      <svg class="icone dash-seta" aria-hidden="true"><use href="#i-seta-direita"/></svg>
    </a>

    <a href="<c:url value='/admin/produtos'/>" class="cartao cartao-link dash-cartao" aria-label="Produtos">
      <div class="dash-icone-circulo">
        <svg class="icone" aria-hidden="true"><use href="#i-caixa"/></svg>
      </div>
      <p class="dash-numero">${totalProdutos}</p>
      <p class="dash-etiqueta">Produtos</p>
      <svg class="icone dash-seta" aria-hidden="true"><use href="#i-seta-direita"/></svg>
    </a>

    <a href="<c:url value='/admin/listagens/clientes'/>" class="cartao cartao-link dash-cartao" aria-label="Clientes">
      <div class="dash-icone-circulo">
        <svg class="icone" aria-hidden="true"><use href="#i-pessoas"/></svg>
      </div>
      <p class="dash-numero">${totalClientes}</p>
      <p class="dash-etiqueta">Clientes</p>
      <svg class="icone dash-seta" aria-hidden="true"><use href="#i-seta-direita"/></svg>
    </a>

    <a href="<c:url value='/admin/listagens/compras'/>" class="cartao cartao-link dash-cartao" aria-label="Vendas">
      <div class="dash-icone-circulo">
        <svg class="icone" aria-hidden="true"><use href="#i-recibo"/></svg>
      </div>
      <p class="dash-numero">${totalVendas}</p>
      <p class="dash-etiqueta">Vendas</p>
      <svg class="icone dash-seta" aria-hidden="true"><use href="#i-seta-direita"/></svg>
    </a>

    <a href="<c:url value='/admin/campanhas'/>" class="cartao cartao-link dash-cartao" aria-label="Campanhas">
      <div class="dash-icone-circulo">
        <svg class="icone" aria-hidden="true"><use href="#i-folha"/></svg>
      </div>
      <p class="dash-numero">${totalCampanhas}</p>
      <p class="dash-etiqueta">Campanhas</p>
      <svg class="icone dash-seta" aria-hidden="true"><use href="#i-seta-direita"/></svg>
    </a>

  </div>
</div>
