<div class="pagina-topo">
  <h1>Cultivos — Selecionar campanha</h1>
</div>

<p class="muted">Os cultivos pertencem a uma campanha agrícola. Selecione uma campanha para gerir os seus cultivos.</p>

<c:choose>
  <c:when test="${empty campanhas}">
    <p class="muted">Ainda não existem campanhas registadas. <a href="<c:url value='/admin/campanhas/novo'/>">Criar uma campanha</a> primeiro.</p>
  </c:when>
  <c:otherwise>
    <div class="tabela-scroll">
      <table class="tabela">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Início</th>
            <th>Fim</th>
            <th>Estado</th>
            <th class="nao-imprimir">Acções</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="c" items="${campanhas}">
            <tr>
              <td><c:out value="${c.nome}"/></td>
              <td><c:out value="${c.inicioFmt}"/></td>
              <td><c:out value="${c.fimFmt}"/></td>
              <td>
                <c:choose>
                  <c:when test="${c.aberta}">
                    <span class="badge ok">Aberta</span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge off">Fechada</span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td class="nao-imprimir">
                <a class="btn" href="<c:url value='/admin/cultivos'><c:param name='campanhaId' value='${c.id}'/></c:url>">
                  Ver cultivos
                </a>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </c:otherwise>
</c:choose>
