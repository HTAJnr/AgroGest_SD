<div class="conteudo">
  <div class="pagina-topo">
    <h1>Persistência</h1>
  </div>

  <div class="cartao form-centrado">
    <div class="campo">
      <p><strong>Ficheiro de dados:</strong> <code><c:out value="${ficheiro}"/></code></p>

      <c:choose>
        <c:when test="${existe}">
          <p class="badge ok" style="display:inline-flex">
            <svg class="icone" aria-hidden="true"><use href="#i-check"/></svg>
            Ficheiro existe
          </p>
          <p class="muted">Tamanho: <c:out value="${tamanhoKb}"/> KB &nbsp;·&nbsp; Última gravação: <c:out value="${modificado}"/>.</p>
        </c:when>
        <c:otherwise>
          <p class="badge aviso" style="display:inline-flex">
            <svg class="icone" aria-hidden="true"><use href="#i-alerta"/></svg>
            Ainda não existe ficheiro de dados
          </p>
          <p class="muted">Os dados estão apenas em memória. Grave para os persistir.</p>
        </c:otherwise>
      </c:choose>
    </div>

    <hr style="border:none;border-top:1px solid var(--border);margin:16px 0">

    <div class="campo">
      <h3>Gravar dados</h3>
      <p class="muted">Grava o estado actual da memória no ficheiro de dados. Faz-se automaticamente quando o servidor para.</p>
      <form method="post" action="<c:url value='/admin/persistencia/gravar'/>">
        <input type="hidden" name="_t" value="${tokenForm}">
        <button type="submit" class="btn">
          <svg class="icone" aria-hidden="true"><use href="#i-cofre-upload"/></svg>
          Gravar agora
        </button>
      </form>
    </div>

    <hr style="border:none;border-top:1px solid var(--border);margin:16px 0">

    <div class="campo">
      <h3>Eliminar ficheiro</h3>
      <p class="muted">
        Apaga apenas o ficheiro em disco. Os dados em memória <strong>não são perdidos</strong>
        e continuam disponíveis até parar o servidor. Na próxima paragem serão gravados de novo.
      </p>
      <form method="post" action="<c:url value='/admin/persistencia/eliminar'/>">
        <input type="hidden" name="_t" value="${tokenForm}">
        <button type="submit" class="btn-perigo"
                data-confirm="Eliminar o ficheiro de dados? Os dados em memória não são apagados e voltam a ser gravados quando o servidor parar."
                ${not existe ? 'disabled aria-disabled="true"' : ''}>
          <svg class="icone" aria-hidden="true"><use href="#i-lixo"/></svg>
          Eliminar ficheiro
        </button>
      </form>
    </div>
  </div>
</div>
