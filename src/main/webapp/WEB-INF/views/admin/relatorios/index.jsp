<div class="conteudo">
  <h1>Relatórios</h1>

  <div class="acoes nao-imprimir">
    <a class="${vista == 'clientes' ? 'btn' : 'btn-sec'}" href="<c:url value='/admin/relatorios/clientes'/>">Clientes por nome</a>
    <a class="${vista == 'compraram' ? 'btn' : 'btn-sec'}" href="<c:url value='/admin/relatorios/compraram'/>">Clientes que compraram</a>
    <a class="${vista == 'assiduos' ? 'btn' : 'btn-sec'}" href="<c:url value='/admin/relatorios/assiduos'/>">Mais assíduos</a>
    <a class="${vista == 'semcompras' ? 'btn' : 'btn-sec'}" href="<c:url value='/admin/relatorios/sem-compras'/>">Sem compras</a>
  </div>

  <h2><c:out value="${titulo}"/></h2>

  <c:if test="${empty lista}">
    <p class="muted">Sem registos.</p>
  </c:if>

  <c:if test="${not empty lista}">
    <c:choose>

      <%-- Clientes por nome e Clientes sem compras: lista simples de clientes --%>
      <c:when test="${vista == 'clientes' || vista == 'semcompras'}">
        <div class="tabela-scroll">
          <table class="tabela">
            <thead>
              <tr>
                <th>#</th>
                <th>Nome</th>
                <th>Email</th>
                <th>Telefone</th>
                <th>Endereço</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="c" items="${lista}" varStatus="s">
                <tr>
                  <td>${s.count}</td>
                  <td><c:out value="${c.nome}"/></td>
                  <td><c:out value="${c.email}"/></td>
                  <td><c:out value="${c.telefone}"/></td>
                  <td><c:out value="${c.endereco}"/></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:when>

      <%-- Clientes que compraram (por volume) e Mais assíduos (por nº de compras) --%>
      <c:otherwise>
        <div class="tabela-scroll">
          <table class="tabela">
            <thead>
              <tr>
                <th>#</th>
                <th>Cliente</th>
                <th>Email</th>
                <th class="num">Nº de compras</th>
                <th class="num">Total gasto</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="l" items="${lista}" varStatus="s">
                <tr>
                  <td>${s.count}</td>
                  <td><c:out value="${l.cliente.nome}"/></td>
                  <td><c:out value="${l.cliente.email}"/></td>
                  <td class="num">${l.compras}</td>
                  <td class="num"><fmt:formatNumber value="${l.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:otherwise>

    </c:choose>
  </c:if>
</div>
