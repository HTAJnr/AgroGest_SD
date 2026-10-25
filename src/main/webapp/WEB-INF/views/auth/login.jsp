<div class="cartao auth-cartao">
  <h1 class="auth-titulo">Entrar</h1>

  <c:if test="${not empty erro}">
    <div class="auth-erro alerta erro" role="alert">
      <svg class="icone" aria-hidden="true"><use href="#i-alerta"/></svg>
      <span><c:out value="${erro}"/></span>
    </div>
  </c:if>

  <form class="form-auth" method="post" action="<c:url value='/login'/>">
    <input type="hidden" name="_t" value="${tokenForm}">

    <label for="email">Email</label>
    <input type="email" id="email" name="email"
           value="<c:out value='${not empty email ? email : param.email}'/>"
           autocomplete="email" required autofocus
           class="${not empty erro ? 'invalido' : ''}">

    <label for="password">Palavra-passe</label>
    <input type="password" id="password" name="password"
           autocomplete="current-password" required
           class="${not empty erro ? 'invalido' : ''}">

    <button class="btn auth-btn" type="submit">Entrar</button>
  </form>

  <p class="auth-link-rodape muted">Ainda não tem conta? <a href="<c:url value='/registo'/>">Registe-se</a></p>
</div>
