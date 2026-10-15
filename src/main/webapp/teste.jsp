<%-- TESTE DE AMBIENTE. Se esta página abrir bem, o Tomcat, o JSTL, o UTF-8 e o topo/rodapé funcionam.
     A (integração) apaga este ficheiro no fim. --%>
<h1>AgroGest — ambiente OK</h1>
<ul>
  <li>Contexto: <code><c:out value="${pageContext.request.contextPath}"/></code></li>
  <li>Acentos (devem aparecer certos): João, açúcar, coração, ção, é, à</li>
  <li>Número formatado (em pt_PT deve aparecer 1 234,50, com espaço como separador de milhares e vírgula decimal): <fmt:formatNumber value="1234.5" type="number" minFractionDigits="2" maxFractionDigits="2"/></li>
  <li>Servidor: <c:out value="${pageContext.servletContext.serverInfo}"/></li>
</ul>
