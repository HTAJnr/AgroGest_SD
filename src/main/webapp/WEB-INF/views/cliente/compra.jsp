<div class="pagina-topo">
  <h1>Finalizar compra</h1>
</div>

<c:forEach var="a" items="${avisos}">
  <div class="alerta erro" role="alert">
    <svg class="icone" aria-hidden="true"><use href="#i-alerta"/></svg>
    <span><c:out value="${a}"/></span>
  </div>
</c:forEach>

<div class="cartao" style="margin-bottom: 24px;">
  <h2 style="font-size: 1.1rem; margin-bottom: 12px;">Dados de factura&ccedil;&atilde;o e entrega</h2>
  <div class="grelha-dados">
    <div>
      <span class="muted" style="font-size: 0.875rem;">Cliente</span>
      <div><strong><c:out value="${u.nome}"/></strong></div>
    </div>
    <div>
      <span class="muted" style="font-size: 0.875rem;">Email</span>
      <div><c:out value="${u.email}"/></div>
    </div>
    <c:if test="${not empty u.telefone}">
      <div>
        <span class="muted" style="font-size: 0.875rem;">Contacto</span>
        <div><c:out value="${u.telefone}"/></div>
      </div>
    </c:if>
    <div>
      <span class="muted" style="font-size: 0.875rem;">Endere&ccedil;o de entrega</span>
      <div><c:out value="${u.endereco}"/></div>
    </div>
  </div>
</div>

<div class="tabela-scroll">
  <table class="tabela tabela-empilhada">
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
          <td data-label="Produto">
            <strong><c:out value="${l.produto.nome}"/></strong>
          </td>
          <td class="num" data-label="Pre&ccedil;o">
            <fmt:formatNumber value="${l.produto.preco}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT
            <span class="muted" style="font-size: 0.85em;">/ <c:out value="${l.produto.unidade}"/></span>
          </td>
          <td class="num" data-label="Quantidade">
            <fmt:formatNumber value="${l.quantidade}" type="number" maxFractionDigits="2"/>
            <c:out value="${l.produto.unidade}"/>
          </td>
          <td class="num" data-label="Subtotal">
            <strong><fmt:formatNumber value="${l.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</strong>
          </td>
        </tr>
      </c:forEach>
    </tbody>
    <tfoot>
      <tr>
        <td colspan="3" class="factura-total-label" data-label="Subtotal">Subtotal</td>
        <td class="num" data-label="Valor"><fmt:formatNumber value="${subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
      </tr>
      <tr>
        <td colspan="3" class="factura-total-label" data-label="IVA (16%)">IVA (16%)</td>
        <td class="num" data-label="Valor"><fmt:formatNumber value="${iva}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
      </tr>
      <tr class="linha-total">
        <td colspan="3" class="factura-total-label" data-label="Total estimado"><strong>Total estimado</strong></td>
        <td class="num total" data-label="Valor"><fmt:formatNumber value="${total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
      </tr>
    </tfoot>
  </table>
</div>

<p class="muted" style="margin-top: 16px;">
  <svg class="icone" aria-hidden="true"><use href="#i-info"/></svg>
  O valor final &eacute; calculado ao confirmar, com o stock e os pre&ccedil;os desse momento.
</p>

<div class="acoes form-acoes" style="margin-top: 24px;">
  <form method="post" action="<c:url value='/compra'/>" data-validar novalidate>
    <input type="hidden" name="_t" value="${tokenForm}">
    <button class="btn" type="submit" id="btn-confirmar-compra" data-submetendo="A confirmar compra…">
      <svg class="icone" aria-hidden="true"><use href="#i-check"/></svg>
      Confirmar compra
    </button>
  </form>
  <a class="btn btn-sec" href="<c:url value='/carrinho'/>">
    Voltar ao carrinho
  </a>
</div>
