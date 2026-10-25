<div class="factura">
  <%-- Cabeçalho com marca + nº + data --%>
  <div class="factura-cabecalho">
    <div class="factura-marca">
      <img src="<c:url value='/assets/img/logo-marca.svg'/>" alt="AgroGest" width="48" height="48" class="factura-logo">
      <div class="factura-marca-info">
        <span class="factura-marca-nome"><span class="marca-agro">Agro</span><span class="marca-gest">Gest</span></span>
        <span class="factura-marca-subtitulo">Sistema de Gest&atilde;o Agr&iacute;cola</span>
        <span class="factura-marca-nampula muted">Nampula &middot; Mo&ccedil;ambique</span>
      </div>
    </div>
    <div class="factura-meta">
      <div class="factura-doc-tipo">Factura</div>
      <h1 class="factura-numero"><c:out value="${venda.numeroFactura}"/></h1>
      <div class="factura-data">
        <span class="muted">Data:</span> <strong><c:out value="${venda.dataFmt}"/></strong>
      </div>
    </div>
  </div>

  <hr class="factura-divisor">

  <%-- Dados do cliente --%>
  <div class="factura-seccao-cliente">
    <h2 class="factura-subtitulo">Dados do Cliente</h2>
    <div class="factura-cliente-caixa">
      <div class="factura-cliente-nome"><strong><c:out value="${venda.clienteNome}"/></strong></div>
      <div class="factura-cliente-item"><span class="muted">Email:</span> <c:out value="${venda.clienteEmail}"/></div>
      <c:if test="${not empty venda.clienteTelefone}">
        <div class="factura-cliente-item"><span class="muted">Contacto:</span> <c:out value="${venda.clienteTelefone}"/></div>
      </c:if>
      <div class="factura-cliente-item"><span class="muted">Endere&ccedil;o:</span> <c:out value="${venda.clienteEndereco}"/></div>
    </div>
  </div>

  <%-- Itens --%>
  <div class="tabela-scroll">
    <table class="tabela tabela-empilhada factura-tabela">
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
            <td data-label="Produto"><strong><c:out value="${i.produtoNome}"/></strong></td>
            <td class="num" data-label="Pre&ccedil;o unit.">
              <fmt:formatNumber value="${i.precoUnit}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT
            </td>
            <td class="num" data-label="Quantidade">
              <fmt:formatNumber value="${i.quantidade}" type="number" maxFractionDigits="2"/>
              <c:out value="${i.unidade}"/>
            </td>
            <td class="num" data-label="Subtotal">
              <strong><fmt:formatNumber value="${i.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</strong>
            </td>
          </tr>
        </c:forEach>
      </tbody>
      <tfoot>
        <tr>
          <td colspan="3" class="factura-total-label" data-label="Subtotal">Subtotal</td>
          <td class="num" data-label="Valor"><fmt:formatNumber value="${venda.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
        </tr>
        <tr>
          <td colspan="3" class="factura-total-label" data-label="IVA (16%)">IVA (16%)</td>
          <td class="num" data-label="Valor"><fmt:formatNumber value="${venda.iva}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
        </tr>
        <tr class="linha-total">
          <td colspan="3" class="factura-total-label" data-label="Total"><strong>Total</strong></td>
          <td class="num total" data-label="Valor"><fmt:formatNumber value="${venda.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
        </tr>
      </tfoot>
    </table>
  </div>

  <%-- Rodapé da factura exclusivo para impressão --%>
  <div class="factura-rodape-impresso">
    <p>Obrigado pela sua prefer&ecirc;ncia! Documento processado por computador.</p>
  </div>
</div>

<%-- Acções (fora do cartão de impressão) --%>
<div class="acoes nao-imprimir factura-acoes">
  <button type="button" class="btn" onclick="window.print()">
    <svg class="icone" aria-hidden="true"><use href="#i-recibo"/></svg>
    Imprimir factura
  </button>
  <c:choose>
    <c:when test="${not u.admin}">
      <a class="btn btn-sec" href="<c:url value='/minhas-compras'/>">
        Minhas compras
      </a>
      <a class="btn btn-sec" href="<c:url value='/catalogo'/>">
        Voltar ao cat&aacute;logo
      </a>
    </c:when>
    <c:otherwise>
      <a class="btn btn-sec" href="<c:url value='/admin/listagens/compras'/>">
        Voltar &agrave;s compras
      </a>
    </c:otherwise>
  </c:choose>
</div>
