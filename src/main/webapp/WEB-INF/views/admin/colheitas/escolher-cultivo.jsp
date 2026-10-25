<div class="pagina-topo">
  <h1>Colheitas — Selecionar cultivo</h1>
</div>

<p class="muted">As colheitas pertencem a um cultivo agrícola. Selecione um cultivo para registar ou consultar as suas colheitas.</p>

<c:choose>
  <c:when test="${empty cultivos}">
    <p class="muted">Ainda não existem cultivos registados. <a href="<c:url value='/admin/cultivos'/>">Registar um cultivo</a> primeiro.</p>
  </c:when>
  <c:otherwise>
    <div class="tabela-scroll">
      <table class="tabela">
        <thead>
          <tr>
            <th>Cultura</th>
            <th>Variedade</th>
            <th>Campanha</th>
            <th class="num">Área (ha)</th>
            <th>Data de plantio</th>
            <th>Estado da campanha</th>
            <th class="nao-imprimir">Acções</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="c" items="${cultivos}">
            <c:set var="camp" value="${campanhasMap[c.campanhaId]}"/>
            <tr>
              <td><strong><c:out value="${c.cultura}"/></strong></td>
              <td>
                <c:choose>
                  <c:when test="${not empty c.variedade}">
                    <c:out value="${c.variedade}"/>
                  </c:when>
                  <c:otherwise>
                    <span class="muted">—</span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:choose>
                  <c:when test="${not empty camp}">
                    <c:out value="${camp.nome}"/>
                  </c:when>
                  <c:otherwise>
                    <span class="muted">Campanha #<c:out value="${c.campanhaId}"/></span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td class="num">
                <fmt:formatNumber value="${c.hectares}" type="number" minFractionDigits="1" maxFractionDigits="2"/> ha
              </td>
              <td><c:out value="${c.plantioFmt}"/></td>
              <td>
                <c:choose>
                  <c:when test="${not empty camp and camp.aberta}">
                    <span class="badge ok">Aberta</span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge off">Fechada</span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td class="nao-imprimir">
                <a class="btn" href="<c:url value='/admin/colheitas'><c:param name='cultivoId' value='${c.id}'/></c:url>">
                  Ver colheitas
                </a>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </c:otherwise>
</c:choose>
