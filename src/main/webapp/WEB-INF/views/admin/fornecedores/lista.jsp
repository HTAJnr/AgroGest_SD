<div class="pagina-topo">
  <h1>Fornecedores</h1>
  <a class="btn" href="<c:url value='/admin/fornecedores/novo'/>">+ Novo fornecedor</a>
</div>

<c:choose>
  <c:when test="${empty fornecedores}">
    <p class="muted">Ainda não há fornecedores registados.</p>
  </c:when>
  <c:otherwise>
    <div class="tabela-scroll">
      <table class="tabela">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Tipo</th>
            <th>Contacto</th>
            <th>Província</th>
            <th class="nao-imprimir">Acções</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="f" items="${fornecedores}">
            <tr>
              <td><c:out value="${f.nome}"/></td>
              <td><span class="badge">${f.tipo}</span></td>
              <td><c:out value="${f.contacto}"/></td>
              <td><c:out value="${f.provincia}"/></td>
              <td class="acoes nao-imprimir">
                <form method="post" action="<c:url value='/admin/fornecedores/remover'/>"
                      onsubmit="return confirm('Remover este fornecedor?');">
                  <input type="hidden" name="_t" value="${tokenForm}">
                  <input type="hidden" name="id" value="${f.id}">
                  <button type="submit" class="btn-perigo btn-icone" title="Remover" aria-label="Remover">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                      <path d="M3 6h18"/>
                      <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
                      <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
                      <line x1="10" y1="11" x2="10" y2="17"/>
                      <line x1="14" y1="11" x2="14" y2="17"/>
                    </svg>
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
