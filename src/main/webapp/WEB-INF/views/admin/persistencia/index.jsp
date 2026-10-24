<div class="conteudo">
  <h1>Persistência</h1>

  <div class="cartao">
    <p><strong>Ficheiro:</strong> <c:out value="${ficheiro}"/></p>

    <c:choose>
      <c:when test="${existe}">
        <p>Existe (<c:out value="${tamanhoKb}"/> KB). Última gravação: <c:out value="${modificado}"/>.</p>
      </c:when>
      <c:otherwise>
        <p class="muted">Ainda não existe ficheiro de dados.</p>
      </c:otherwise>
    </c:choose>

    <div class="acoes">
      <form method="post" action="<c:url value='/admin/persistencia/gravar'/>">
        <input type="hidden" name="_t" value="${tokenForm}">
        <button type="submit" class="btn">Gravar agora</button>
      </form>

      <form method="post" action="<c:url value='/admin/persistencia/eliminar'/>"
            onsubmit="return confirm('Eliminar o ficheiro de dados? Os dados em memória não são apagados.');">
        <input type="hidden" name="_t" value="${tokenForm}">
        <button type="submit" class="btn btn-perigo">Eliminar ficheiro</button>
      </form>
    </div>

    <p class="muted">
      "Eliminar" apaga só o ficheiro. Os dados em memória mantêm-se e voltam a ser
      gravados quando o servidor parar.
    </p>
  </div>
</div>
