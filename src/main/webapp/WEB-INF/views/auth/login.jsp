<h1>Entrar</h1>

<form class="form" method="post" action="<c:url value='/login'/>">
  <input type="hidden" name="_t" value="${tokenForm}">
  <label for="email">Email</label>
  <input type="email" id="email" name="email" value="<c:out value='${param.email}'/>" autofocus>

  <label for="password">Palavra-passe</label>
  <input type="password" id="password" name="password">

  <button class="btn" type="submit">Entrar</button>
</form>

<p class="muted">Ainda n&atilde;o tem conta? <a href="<c:url value='/registo'/>">Registe-se</a></p>