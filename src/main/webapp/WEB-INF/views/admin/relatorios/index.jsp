  <div class="pagina-topo">
    <h1>Relatórios</h1>
  </div>

  <%-- Abas de navegação --%>
  <nav class="abas nao-imprimir" aria-label="Relatórios">
    <a href="<c:url value='/admin/relatorios/clientes'/>"
       aria-current="${vista == 'clientes' ? 'page' : 'false'}">Clientes por nome</a>
    <a href="<c:url value='/admin/relatorios/compraram'/>"
       aria-current="${vista == 'compraram' ? 'page' : 'false'}">Clientes que compraram</a>
    <a href="<c:url value='/admin/relatorios/assiduos'/>"
       aria-current="${vista == 'assiduos' ? 'page' : 'false'}">Mais assíduos</a>
    <a href="<c:url value='/admin/relatorios/sem-compras'/>"
       aria-current="${vista == 'semcompras' ? 'page' : 'false'}">Sem compras</a>
  </nav>

  <c:choose>

    <%-- Clientes por nome e Clientes sem compras: lista simples de clientes --%>
    <c:when test="${vista == 'clientes' || vista == 'semcompras'}">
      <c:choose>
        <c:when test="${empty lista}">
          <c:choose>
            <c:when test="${vista == 'semcompras'}">
              <div class="estado-vazio cartao">
                <svg class="icone estado-vazio-icone" aria-hidden="true"><use href="#i-check"/></svg>
                <p><strong>Excelente!</strong> Todos os clientes já fizeram pelo menos uma compra.</p>
              </div>
            </c:when>
            <c:otherwise>
              <div class="estado-vazio cartao">
                <svg class="icone estado-vazio-icone" aria-hidden="true"><use href="#i-pessoas"/></svg>
                <p>Ainda não há clientes registados.</p>
              </div>
            </c:otherwise>
          </c:choose>
        </c:when>
        <c:otherwise>
          <div class="tabela-scroll">
            <table class="tabela">
              <thead>
                <tr>
                  <th class="num">#</th>
                  <th>Nome</th>
                  <th>Email</th>
                  <th>Telefone</th>
                  <th>Endereço</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="c" items="${lista}" varStatus="s">
                  <tr>
                    <td class="num">${s.count}</td>
                    <td><c:out value="${c.nome}"/></td>
                    <td><c:out value="${c.email}"/></td>
                    <td><c:out value="${c.telefone}"/></td>
                    <td><c:out value="${c.endereco}"/></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:otherwise>
      </c:choose>
    </c:when>

    <%-- Clientes que compraram (por volume) e Mais assíduos (por nº de compras) --%>
    <c:otherwise>
      <c:if test="${empty lista}">
        <div class="estado-vazio cartao">
          <svg class="icone estado-vazio-icone" aria-hidden="true"><use href="#i-recibo"/></svg>
          <p>Ainda não há compras registadas.</p>
        </div>
      </c:if>
      <c:if test="${not empty lista}">
        <div class="tabela-scroll">
          <table class="tabela">
            <thead>
              <tr>
                <th class="num">#</th>
                <th>Cliente</th>
                <th>Email</th>
                <th class="num">Nº de compras</th>
                <th class="num">Total gasto</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="l" items="${lista}" varStatus="s">
                <tr class="${s.index == 0 ? 'podio-1' : (s.index == 1 ? 'podio-2' : (s.index == 2 ? 'podio-3' : ''))}">
                  <td class="num">
                    <c:choose>
                      <c:when test="${s.index == 0}"><span class="badge ok podio-badge" title="1.º lugar">🥇</span></c:when>
                      <c:when test="${s.index == 1}"><span class="badge podio-badge" title="2.º lugar">🥈</span></c:when>
                      <c:when test="${s.index == 2}"><span class="badge aviso podio-badge" title="3.º lugar">🥉</span></c:when>
                      <c:otherwise>${s.count}</c:otherwise>
                    </c:choose>
                  </td>
                  <td><c:out value="${l.cliente.nome}"/></td>
                  <td><c:out value="${l.cliente.email}"/></td>
                  <td class="num">${l.compras}</td>
                  <td class="num"><fmt:formatNumber value="${l.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:if>
    </c:otherwise>

  </c:choose>
