<h1>Produtos</h1>

<p><a class="btn" href="<c:url value='/admin/produtos/novo'/>">+ Novo produto</a></p>

<c:choose>
  <c:when test="${empty produtos}">
    <p class="muted">Ainda não há produtos.</p>
  </c:when>
  <c:otherwise>
    <div class="tabela-scroll">
    <table class="tabela">
      <tr>
        <th>Nome</th><th>Categoria</th><th class="num">Preço</th>
        <th class="num">Stock</th><th>Validade</th><th>Acções</th>
      </tr>
      <c:forEach var="p" items="${produtos}">
        <tr>
          <td><c:out value="${p.nome}"/></td>
          <td>${p.categoria}</td>
          <td class="num"><fmt:formatNumber value="${p.preco}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
          <td class="num">${p.quantidade} <c:out value="${p.unidade}"/></td>
          <td>${p.validadeFmt}</td>
          <td class="acoes">
            <form method="post" action="<c:url value='/admin/produtos/repor'/>">
              <input type="hidden" name="_t" value="${tokenForm}">
              <input type="hidden" name="id" value="${p.id}">
              <input type="text" name="quantidade" size="4" placeholder="qtd">
              <button class="btn-sec">Repor</button>
            </form>
            <form method="post" action="<c:url value='/admin/produtos/remover'/>">
              <input type="hidden" name="_t" value="${tokenForm}">
              <input type="hidden" name="id" value="${p.id}">
              <button class="btn-perigo">Remover</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </table>
    </div>
  </c:otherwise>
</c:choose>