<%-- Catálogo de Produtos (Área do Cliente) --%>
<c:set var="utilizadorNome" value="${not empty sessionScope.utilizador.nome ? sessionScope.utilizador.nome : (not empty u.nome ? u.nome : '')}"/>
<c:set var="primeiroNome" value="${not empty utilizadorNome ? fn:split(utilizadorNome, ' ')[0] : 'Cliente'}"/>

<%-- Faixa de abertura (Hero) --%>
<section class="hero" aria-label="Boas-vindas">
  <div class="hero-conteudo">
    <h1>Ol&aacute;, <c:out value="${primeiroNome}"/></h1>
    <p>Encontre sementes, fertilizantes, maquinaria e ferramentas de qualidade para a sua produ&ccedil;&atilde;o agr&iacute;cola.</p>
  </div>
</section>

<%-- Chips de categoria --%>
<nav class="categoria-chips" aria-label="Filtrar produtos por categoria">
  <a class="btn ${empty param.categoria ? '' : 'btn-sec'}"
     href="<c:url value='/catalogo'/>"
     <c:if test="${empty param.categoria}">aria-current="page"</c:if>>
    Todas
  </a>
  <c:forEach var="c" items="${categorias}">
    <a class="btn ${param.categoria == c.name() ? '' : 'btn-sec'}"
       href="<c:url value='/catalogo'><c:param name='categoria' value='${c.name()}'/></c:url>"
       <c:if test="${param.categoria == c.name()}">aria-current="page"</c:if>>
      <c:out value="${c}"/>
    </a>
  </c:forEach>
</nav>

<%-- Lista de produtos ou estado vazio --%>
<c:choose>
  <c:when test="${empty produtos}">
    <div class="estado-vazio cartao">
      <svg class="icone estado-vazio-icone" aria-hidden="true"><use href="#i-caixa"/></svg>
      <h2>Nenhum produto encontrado</h2>
      <c:choose>
        <c:when test="${not empty param.categoria}">
          <p class="muted">N&atilde;o foram encontrados produtos para a categoria selecionada.</p>
        </c:when>
        <c:otherwise>
          <p class="muted">De momento n&atilde;o existem produtos dispon&iacute;veis no cat&aacute;logo.</p>
        </c:otherwise>
      </c:choose>
      <a class="btn" href="<c:url value='/catalogo'/>">
        <svg class="icone" aria-hidden="true"><use href="#i-fechar"/></svg>
        Limpar filtro
      </a>
    </div>
  </c:when>
  <c:otherwise>
    <div class="grelha">
      <c:forEach var="p" items="${produtos}">
        <div class="cartao cartao-produto">
          <c:set var="fotoSrc">
            <c:choose>
              <c:when test="${empty p.fotoUrl}">
                <c:url value='/assets/img/produto-placeholder.svg'/>
              </c:when>
              <c:when test="${fn:startsWith(p.fotoUrl, '/')}">
                <c:url value='${p.fotoUrl}'/>
              </c:when>
              <c:when test="${fn:startsWith(p.fotoUrl, 'http://') or fn:startsWith(p.fotoUrl, 'https://')}">
                <c:out value='${p.fotoUrl}'/>
              </c:when>
              <c:otherwise>
                <c:url value='/${p.fotoUrl}'/>
              </c:otherwise>
            </c:choose>
          </c:set>
          <img class="produto-foto"
               src="${fotoSrc}"
               alt="<c:out value='${p.nome}'/>"
               width="400"
               height="300"
               loading="lazy"
               data-placeholder="<c:url value='/assets/img/produto-placeholder.svg'/>">

          <div class="produto-corpo">
            <h2 class="produto-titulo"><c:out value="${p.nome}"/></h2>
            <div>
              <span class="badge"><c:out value="${p.categoria}"/></span>
            </div>

            <c:if test="${not empty p.descricao}">
              <p class="produto-desc"><c:out value="${p.descricao}"/></p>
            </c:if>

            <p class="muted produto-meta">
              Fornecedor: <span><c:out value="${fornecedores[p.fornecedorId].nome}"/></span>
            </p>

            <c:if test="${not empty p.validadeFmt}">
              <p class="muted produto-meta">
                Validade: <span><c:out value="${p.validadeFmt}"/></span>
                <c:if test="${p.expirado}">
                  <span class="badge aviso">Expirado</span>
                </c:if>
              </p>
            </c:if>

            <div class="produto-preco-stock">
              <p class="total produto-preco">
                <fmt:formatNumber value="${p.preco}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT
                <span class="produto-unidade">/ <c:out value="${p.unidade}"/></span>
              </p>

              <p class="produto-stock">
                <c:choose>
                  <c:when test="${p.quantidade > 0}">
                    Dispon&iacute;vel: <strong><fmt:formatNumber value="${p.quantidade}" type="number" maxFractionDigits="2"/> <c:out value="${p.unidade}"/></strong>
                  </c:when>
                  <c:otherwise>
                    <span class="badge off">Indispon&iacute;vel</span>
                  </c:otherwise>
                </c:choose>
              </p>
            </div>

            <form method="post" action="<c:url value='/carrinho/adicionar'/>" class="produto-form-add">
              <input type="hidden" name="_t" value="${tokenForm}">
              <input type="hidden" name="produtoId" value="${p.id}">
              <input type="hidden" name="voltar" value="catalogo">
              <input type="hidden" name="categoria" value="<c:out value='${param.categoria}'/>">
              <div class="produto-add-acoes">
                <c:choose>
                  <c:when test="${p.quantidade > 0}">
                    <input type="text" name="quantidade" value="1" size="3" inputmode="decimal" aria-label="Quantidade de <c:out value='${p.nome}'/>" class="produto-qtd-input">
                    <button class="btn produto-btn-add" type="submit">
                      <svg class="icone" aria-hidden="true"><use href="#i-carrinho"/></svg>
                      Adicionar
                    </button>
                  </c:when>
                  <c:otherwise>
                    <input type="text" name="quantidade" value="0" size="3" inputmode="decimal" disabled aria-disabled="true" aria-label="Quantidade esgotada" class="produto-qtd-input">
                    <button class="btn produto-btn-add" type="button" disabled aria-disabled="true">
                      <svg class="icone" aria-hidden="true"><use href="#i-carrinho"/></svg>
                      Indispon&iacute;vel
                    </button>
                  </c:otherwise>
                </c:choose>
              </div>
            </form>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>
