<div class="pagina-topo">
  <h1>Fornecedores</h1>
  <a class="btn" href="<c:url value='/admin/fornecedores/novo'/>"><svg class="icone" aria-hidden="true"><use href="#i-mais"/></svg> Novo fornecedor</a>
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
