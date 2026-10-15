<%@ page isErrorPage="true" %>
<%-- DONO: Pessoa C. Página de erro para 403, 404 e 500 (ver web.xml). --%>
<c:set var="codigo" value="${pageContext.errorData.statusCode}"/>
<c:choose>
  <c:when test="${codigo == 403}">
    <h1>Acesso negado (403)</h1>
    <p>Não tens permissão para aceder a esta página.</p>
  </c:when>
  <c:when test="${codigo == 404}">
    <h1>Página não encontrada (404)</h1>
    <p>O endereço pedido não existe.</p>
  </c:when>
  <c:otherwise>
    <h1>Erro interno (<c:out value="${codigo}"/>)</h1>
    <p>Ocorreu um erro inesperado. Tenta novamente.</p>
  </c:otherwise>
</c:choose>
<p><a class="btn btn-sec" href="<c:url value='/'/>">Voltar ao início</a></p>
