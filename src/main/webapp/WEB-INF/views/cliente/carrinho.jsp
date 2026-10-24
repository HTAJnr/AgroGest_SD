<h1>Carrinho</h1>

<c:forEach var="a" items="${avisos}">
  <div class="alerta erro"><c:out value="${a}"/></div>
</c:forEach>

<c:choose>
  <c:when test="${empty linhas}">
    <p class="muted">O carrinho est&aacute; vazio.</p>
    <p><a class="btn" href="<c:url value='/catalogo'/>">Ver cat&aacute;logo</a></p>
  </c:when>
  <c:otherwise>
    <div class="tabela-scroll">
      <table class="tabela">
        <thead>
          <tr>
            <th>Produto</th>S
            <th class="num">Pre&ccedil;o</th>
            <th>Quantidade</th>
            <th class="num">Subtotal</th>
            <th>Ac&ccedil;&otilde;es</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="l" items="${linhas}">
            <tr>
              <td><c:out value="${l.produto.nome}"/></td>
              <td class="num">
                <fmt:formatNumber value="${l.produto.preco}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT
                / <c:out value="${l.produto.unidade}"/>
              </td>
              <td>
                <form method="post" action="<c:url value='/carrinho/atualizar'/>">
                  <input type="hidden" name="produtoId" value="${l.produto.id}">
                  <input type="text" name="quantidade" size="5" inputmode="decimal" aria-label="Quantidade"
                         value="<fmt:formatNumber value='${l.quantidade}' maxFractionDigits='2' groupingUsed='false'/>">
                  <c:out value="${l.produto.unidade}"/>
                  <button class="btn btn-sec" type="submit">Atualizar</button>
                </form>
              </td>
              <td class="num"><fmt:formatNumber value="${l.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
              <td>
                <form method="post" action="<c:url value='/carrinho/remover'/>">
                  <input type="hidden" name="produtoId" value="${l.produto.id}">
                  <button class="btn btn-perigo" type="submit">Remover</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
        <tfoot>
          <tr><td colspan="3">Subtotal</td>
              <td class="num"><fmt:formatNumber value="${subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td><td></td></tr>
          <tr><td colspan="3">IVA (16%)</td>
              <td class="num"><fmt:formatNumber value="${iva}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td><td></td></tr>
          <tr><td colspan="3">Total estimado</td>
              <td class="num"><fmt:formatNumber value="${total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td><td></td></tr>
        </tfoot>
      </table>
    </div>

    <div class="acoes">
      <a class="btn" href="<c:url value='/compra'/>">Finalizar compra</a>
      <a class="btn btn-sec" href="<c:url value='/catalogo'/>">Continuar a comprar</a>
      <form method="post" action="<c:url value='/carrinho/esvaziar'/>" onsubmit="return confirm('Esvaziar o carrinho?');">
        <button class="btn btn-perigo" type="submit">Esvaziar carrinho</button>
      </form>
    </div>
  </c:otherwise>
</c:choose>