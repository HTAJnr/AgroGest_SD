<div class="factura">
  <h1>Factura <c:out value="${venda.numeroFactura}"/></h1>
  <p>Data: ${venda.dataFmt}</p>

  <h3>Cliente</h3>
  <p>
    <c:out value="${venda.clienteNome}"/><br>
    <c:out value="${venda.clienteEmail}"/><br>
    <c:if test="${not empty venda.clienteTelefone}"><c:out value="${venda.clienteTelefone}"/><br></c:if>
    <c:out value="${venda.clienteEndereco}"/>
  </p>

  <div class="tabela-scroll">
    <table class="tabela">
      <thead>
        <tr>
          <th>Produto</th>
          <th class="num">Pre&ccedil;o unit.</th>
          <th class="num">Quantidade</th>
          <th class="num">Subtotal</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="i" items="${venda.itens}">
          <tr>
            <td><c:out value="${i.produtoNome}"/></td>
            <td class="num"><fmt:formatNumber value="${i.precoUnit}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
            <td class="num">
              <fmt:formatNumber value="${i.quantidade}" type="number" maxFractionDigits="2"/>
              <c:out value="${i.unidade}"/>
            </td>
            <td class="num"><fmt:formatNumber value="${i.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
          </tr>
        </c:forEach>
      </tbody>
      <tfoot>
        <tr><td colspan="3">Subtotal</td>
            <td class="num"><fmt:formatNumber value="${venda.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td></tr>
        <tr><td colspan="3">IVA (16%)</td>
            <td class="num"><fmt:formatNumber value="${venda.iva}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td></tr>
        <tr><td colspan="3">Total</td>
            <td class="num"><fmt:formatNumber value="${venda.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td></tr>
      </tfoot>
    </table>
  </div>
</div>

<div class="acoes nao-imprimir">
  <button type="button" class="btn" onclick="window.print()">Imprimir</button>
  <c:if test="${not u.admin}">
    <a class="btn btn-sec" href="<c:url value='/minhas-compras'/>">Minhas compras</a>
  </c:if>
</div>