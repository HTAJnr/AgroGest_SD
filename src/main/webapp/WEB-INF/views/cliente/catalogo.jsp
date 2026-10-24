<h1>Cat&aacute;logo</h1>

<div class="acoes">
  <a class="btn ${empty param.categoria ? '' : 'btn-sec'}" href="<c:url value='/catalogo'/>">Todas</a>
  <c:forEach var="c" items="${categorias}">
    <a class="btn ${param.categoria == c.name() ? '' : 'btn-sec'}" href="<c:url value='/catalogo'/>?categoria=${c.name()}">${c}</a>
  </c:forEach>
</div>

<c:choose>
  <c:when test="${empty produtos}">
    <p class="muted">N&atilde;o h&aacute; produtos para mostrar.</p>
  </c:when>
  <c:otherwise>
    <div class="grelha">
      <c:forEach var="p" items="${produtos}">
        <div class="cartao">
          <c:choose>
            <c:when test="${not empty p.fotoUrl}">
              <img src="<c:out value='${p.fotoUrl}'/>" alt="<c:out value='${p.nome}'/>" onerror="this.style.display='none'">
            </c:when>
            <c:otherwise>
              <p class="muted">Sem foto</p>
            </c:otherwise>
          </c:choose>

          <h3><c:out value="${p.nome}"/></h3>
          <p><span class="badge">${p.categoria}</span></p>
          <p><c:out value="${p.descricao}"/></p>
          <p class="total">
            <fmt:formatNumber value="${p.preco}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT
            / <c:out value="${p.unidade}"/>
          </p>
          <p class="muted">Fornecedor: <c:out value="${fornecedores[p.fornecedorId].nome}"/></p>
          <c:if test="${not empty p.validadeFmt}">
            <p class="muted">Validade: ${p.validadeFmt}</p>
          </c:if>

          <c:choose>
            <c:when test="${p.quantidade > 0}">
              <p>Dispon&iacute;vel:
                <fmt:formatNumber value="${p.quantidade}" type="number" maxFractionDigits="2"/>
                <c:out value="${p.unidade}"/>
              </p>
              <form method="post" action="<c:url value='/carrinho/adicionar'/>">
                <input type="hidden" name="_t" value="${tokenForm}">
                <input type="hidden" name="produtoId" value="${p.id}">
                <input type="hidden" name="voltar" value="catalogo">
                <input type="hidden" name="categoria" value="<c:out value='${param.categoria}'/>">
                <input type="text" name="quantidade" value="1" size="5" inputmode="decimal" aria-label="Quantidade">
                <button class="btn" type="submit">Adicionar ao carrinho</button>
              </form>
            </c:when>
            <c:otherwise>
              <p><span class="badge">Indispon&iacute;vel</span></p>
            </c:otherwise>
          </c:choose>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>