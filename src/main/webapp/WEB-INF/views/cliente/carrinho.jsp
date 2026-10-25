<div class="pagina-topo">
  <h1>Carrinho</h1>
  <c:if test="${not empty linhas}">
    <a class="btn btn-sec" href="<c:url value='/catalogo'/>">
      <svg class="icone" aria-hidden="true"><use href="#i-caixa"/></svg>
      Continuar a comprar
    </a>
  </c:if>
</div>

<c:forEach var="a" items="${avisos}">
  <div class="alerta aviso" role="alert">
    <svg class="icone" aria-hidden="true"><use href="#i-alerta"/></svg>
    <span><c:out value="${a}"/></span>
  </div>
</c:forEach>

<c:choose>
  <c:when test="${empty linhas}">
    <div class="estado-vazio cartao">
      <svg class="icone estado-vazio-icone" aria-hidden="true"><use href="#i-carrinho"/></svg>
      <h2>O carrinho est&aacute; vazio</h2>
      <p class="muted">Ainda n&atilde;o adicionou nenhum produto ao seu carrinho de compras.</p>
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
            <th>Produto</th>
            <th class="num">Pre&ccedil;o</th>
            <th>Quantidade</th>
            <th class="num">Subtotal</th>
            <th class="nao-imprimir acoes-col">Ac&ccedil;&otilde;es</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="l" items="${linhas}">
            <tr>
              <td data-label="Produto">
                <strong><c:out value="${l.produto.nome}"/></strong>
              </td>
              <td class="num" data-label="Pre&ccedil;o">
                <fmt:formatNumber value="${l.produto.preco}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT
                <span class="muted" style="font-size:0.875rem">/ <c:out value="${l.produto.unidade}"/></span>
              </td>
              <td data-label="Quantidade">
                <form method="post" action="<c:url value='/carrinho/atualizar'/>" class="form-linha-qtd">
                  <input type="hidden" name="_t" value="${tokenForm}">
                  <input type="hidden" name="produtoId" value="${l.produto.id}">
                  <div class="carrinho-qtd-grupo">
                    <input type="text" name="quantidade" size="4" inputmode="decimal" class="campo-qtd"
                           aria-label="Quantidade de <c:out value='${l.produto.nome}'/>"
                           value="<fmt:formatNumber value='${l.quantidade}' maxFractionDigits='2' groupingUsed='false'/>">
                    <span class="unidade-texto"><c:out value="${l.produto.unidade}"/></span>
                    <button class="btn-sec btn-sm" type="submit" title="Atualizar quantidade" aria-label="Atualizar quantidade">
                      Atualizar
                    </button>
                  </div>
                </form>
              </td>
              <td class="num" data-label="Subtotal">
                <strong><fmt:formatNumber value="${l.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</strong>
              </td>
              <td class="nao-imprimir acoes-col" data-label="Ac&ccedil;&otilde;es">
                <form method="post" action="<c:url value='/carrinho/remover'/>">
                  <input type="hidden" name="_t" value="${tokenForm}">
                  <input type="hidden" name="produtoId" value="${l.produto.id}">
                  <button class="btn-perigo btn-icone" type="submit"
                          title="Remover produto do carrinho" aria-label="Remover produto"
                          data-confirm="Remover &laquo;${fn:escapeXml(l.produto.nome)}&raquo; do carrinho?">
                    <svg class="icone" aria-hidden="true"><use href="#i-lixo"/></svg>
                  </button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="3" class="factura-total-label" data-label="Subtotal">Subtotal</td>
            <td class="num" data-label="Valor"><fmt:formatNumber value="${subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
            <td class="nao-imprimir"></td>
          </tr>
          <tr>
            <td colspan="3" class="factura-total-label" data-label="IVA (16%)">IVA (16%)</td>
            <td class="num" data-label="Valor"><fmt:formatNumber value="${iva}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
            <td class="nao-imprimir"></td>
          </tr>
          <tr class="linha-total">
            <td colspan="3" class="factura-total-label" data-label="Total estimado"><strong>Total estimado</strong></td>
            <td class="num total" data-label="Valor"><fmt:formatNumber value="${total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
            <td class="nao-imprimir"></td>
          </tr>
        </tfoot>
      </table>
    </div>

    <div class="carrinho-rodape-acoes">
      <div class="acoes">
        <a class="btn" href="<c:url value='/compra'/>">
          <svg class="icone" aria-hidden="true"><use href="#i-check"/></svg>
          Finalizar compra
        </a>
        <a class="btn btn-sec" href="<c:url value='/catalogo'/>">
          Continuar a comprar
        </a>
      </div>
      <form method="post" action="<c:url value='/carrinho/esvaziar'/>" class="form-esvaziar">
        <input type="hidden" name="_t" value="${tokenForm}">
        <button class="btn-perigo btn-sec" type="submit"
                data-confirm="Esvaziar o carrinho? Todos os produtos serão removidos.">
          <svg class="icone" aria-hidden="true"><use href="#i-lixo"/></svg>
          Esvaziar carrinho
        </button>
      </form>
    </div>
  </c:otherwise>
</c:choose>
