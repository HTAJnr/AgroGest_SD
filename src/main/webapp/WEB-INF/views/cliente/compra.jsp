<h1>Finalizar compra</h1>

<c:forEach var="a" items="${avisos}">
  <div class="alerta erro"><c:out value="${a}"/></div>
</c:forEach>

<div class="tabela-scroll">
  <table class="tabela">
    <thead>
      <tr>
        <th>Produto</th>
        <th class="num">Pre&ccedil;o</th>
        <th class="num">Quantidade</th>
        <th class="num">Subtotal</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="l" items="${linhas}">
        <tr>
          <td><c:out value="${l.produto.nome}"/></td>
          <td class="num"><fmt:formatNumber value="${l.produto.preco}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
          <td class="num">
            <fmt:formatNumber value="${l.quantidade}" type="number" maxFractionDigits="2"/>
            <c:out value="${l.produto.unidade}"/>
          </td>
          <td class="num"><fmt:formatNumber value="${l.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
        </tr>
      </c:forEach>
    </tbody>
    <tfoot>
      <tr><td colspan="3">Subtotal</td>
          <td class="num"><fmt:formatNumber value="${subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td></tr>
      <tr><td colspan="3">IVA (16%)</td>
          <td class="num"><fmt:formatNumber value="${iva}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td></tr>
      <tr><td colspan="3">Total estimado</td>
          <td class="num"><fmt:formatNumber value="${total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td></tr>
    </tfoot>
  </table>
</div>

<p class="muted">O valor final &eacute; calculado ao confirmar, com o stock e os pre&ccedil;os desse momento.</p>

<div class="acoes">
  <form method="post" action="<c:url value='/compra'/>">
    <input type="hidden" name="_t" value="${tokenForm}">
    <button class="btn" type="submit">Confirmar compra</button>
  </form>
  <a class="btn btn-sec" href="<c:url value='/carrinho'/>">Voltar ao carrinho</a>
</div>