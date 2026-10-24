<h1>Minhas compras</h1>

<c:choose>
  <c:when test="${empty compras}">
    <p class="muted">Ainda n&atilde;o fez nenhuma compra.</p>
    <p><a class="btn" href="<c:url value='/catalogo'/>">Ver cat&aacute;logo</a></p>
  </c:when>
  <c:otherwise>
    <div class="tabela-scroll">
      <table class="tabela">
        <thead>
          <tr>
            <th>Factura</th>
            <th>Data</th>
            <th class="num">Total</th>
            <th>Ac&ccedil;&otilde;es</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="v" items="${compras}">
            <tr>
              <td><c:out value="${v.numeroFactura}"/></td>
              <td>${v.dataFmt}</td>
              <td class="num"><fmt:formatNumber value="${v.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
              <td><a class="btn btn-sec" href="<c:url value='/factura'/>?id=${v.id}">Ver factura</a></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </c:otherwise>
</c:choose>