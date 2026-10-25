<div class="pagina-topo">
  <h1>Minhas compras</h1>
  <a class="btn btn-sec" href="<c:url value='/catalogo'/>">
    <svg class="icone" aria-hidden="true"><use href="#i-caixa"/></svg>
    Ir ao cat&aacute;logo
  </a>
</div>

<c:choose>
  <c:when test="${empty compras}">
    <div class="estado-vazio cartao">
      <svg class="icone estado-vazio-icone" aria-hidden="true"><use href="#i-recibo"/></svg>
      <h2>Ainda n&atilde;o efectuou nenhuma compra</h2>
      <p class="muted">Assim que finalizar a sua primeira encomenda, as suas facturas aparecer&atilde;o aqui.</p>
      <a class="btn" href="<c:url value='/catalogo'/>">
        <svg class="icone" aria-hidden="true"><use href="#i-caixa"/></svg>
        Ver cat&aacute;logo
      </a>
    </div>
  </c:when>
  <c:otherwise>
    <div class="tabela-scroll">
      <table class="tabela tabela-empilhada">
        <thead>
          <tr>
            <th>Factura</th>
            <th>Data</th>
            <th class="num">Total</th>
            <th class="nao-imprimir acoes-col">Ac&ccedil;&otilde;es</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="v" items="${compras}">
            <tr>
              <td data-label="Factura">
                <strong><c:out value="${v.numeroFactura}"/></strong>
              </td>
              <td data-label="Data">
                <c:out value="${v.dataFmt}"/>
              </td>
              <td class="num" data-label="Total">
                <span class="total"><fmt:formatNumber value="${v.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</span>
              </td>
              <td class="nao-imprimir acoes-col" data-label="Ac&ccedil;&otilde;es">
                <a class="btn btn-sec btn-sm" href="<c:url value='/factura'><c:param name='id' value='${v.id}'/></c:url>">
                  <svg class="icone" aria-hidden="true"><use href="#i-recibo"/></svg>
                  Ver factura
                </a>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </c:otherwise>
</c:choose>
