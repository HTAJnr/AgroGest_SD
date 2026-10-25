<div class="pagina-topo">
  <h1>Campanhas</h1>
  <a class="btn" href="<c:url value='/admin/campanhas/novo'/>"><svg class="icone" aria-hidden="true"><use href="#i-mais"/></svg> Nova campanha</a>
</div>

<c:choose>
  <c:when test="${empty campanhas}">
    <p class="muted">Ainda não há campanhas registadas.</p>
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
            <th class="nao-imprimir">Cultivos</th>
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
                <a class="btn-sec" href="<c:url value='/admin/cultivos'><c:param name='campanhaId' value='${c.id}'/></c:url>">Cultivos</a>
              </td>
              <td class="acoes nao-imprimir">
                <c:choose>
                  <c:when test="${c.aberta}">
                    <form method="post" action="<c:url value='/admin/campanhas/fechar'/>" class="form-inline">
                      <input type="hidden" name="_t" value="${tokenForm}">
                      <input type="hidden" name="id" value="${c.id}">
                      <button type="submit" class="btn-sec" title="Fechar campanha">Fechar</button>
                    </form>
                  </c:when>
                  <c:otherwise>
                    <form method="post" action="<c:url value='/admin/campanhas/reabrir'/>" class="form-inline">
                      <input type="hidden" name="_t" value="${tokenForm}">
                      <input type="hidden" name="id" value="${c.id}">
                      <button type="submit" class="btn-sec" title="Reabrir campanha">Reabrir</button>
                    </form>
                  </c:otherwise>
                </c:choose>
                <form method="post" action="<c:url value='/admin/campanhas/remover'/>" class="form-inline">
                  <input type="hidden" name="_t" value="${tokenForm}">
                  <input type="hidden" name="id" value="${c.id}">
                  <button type="submit" class="btn-perigo btn-icone"
                          title="Remover" aria-label="Remover"
                          data-confirm="Remover a campanha «${c.nome}»? Esta acção não pode ser desfeita.">
                    <svg class="icone" aria-hidden="true"><use href="#i-lixo"/></svg>
                  </button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </c:otherwise>
</c:choose>
