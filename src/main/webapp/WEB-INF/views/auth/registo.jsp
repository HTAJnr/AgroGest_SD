<div class="cartao auth-cartao auth-cartao-registo">
  <h1 class="auth-titulo">Criar conta</h1>

  <c:if test="${not empty erro}">
    <div class="auth-erro alerta erro" role="alert">
      <svg class="icone" aria-hidden="true"><use href="#i-alerta"/></svg>
      <span><c:out value="${erro}"/></span>
    </div>
  </c:if>

  <form class="form-auth" method="post" action="<c:url value='/registo'/>">
    <input type="hidden" name="_t" value="${tokenForm}">

    <label for="nome">Nome</label>
    <input type="text" id="nome" name="nome" value="<c:out value='${param.nome}'/>"
           minlength="3" maxlength="80" autocomplete="name" required
           class="${erroCampo == 'nome' ? 'invalido' : ''}"
           ${erroCampo == 'nome' ? 'aria-invalid="true"' : ''} autofocus>

    <label for="email">Email</label>
    <input type="email" id="email" name="email" value="<c:out value='${param.email}'/>"
           maxlength="120" autocomplete="email" required
           class="${erroCampo == 'email' ? 'invalido' : ''}"
           ${erroCampo == 'email' ? 'aria-invalid="true"' : ''}>

    <label for="password">Palavra-passe</label>
    <input type="password" id="password" name="password"
           minlength="4" maxlength="72" autocomplete="new-password" required
           class="${erroCampo == 'password' ? 'invalido' : ''}"
           ${erroCampo == 'password' ? 'aria-invalid="true"' : ''}>

    <label for="confirmar">Confirmar palavra-passe</label>
    <input type="password" id="confirmar" name="confirmar"
           minlength="4" maxlength="72" autocomplete="new-password" required
           class="${erroCampo == 'confirmar' ? 'invalido' : ''}"
           ${erroCampo == 'confirmar' ? 'aria-invalid="true"' : ''}>

    <label for="telefone">Telefone</label>
    <input type="tel" id="telefone" name="telefone" value="<c:out value='${param.telefone}'/>"
           inputmode="tel" placeholder="ex.: 84 123 4567" autocomplete="tel" required
           class="${erroCampo == 'telefone' ? 'invalido' : ''}"
           ${erroCampo == 'telefone' ? 'aria-invalid="true"' : ''}>

    <label for="endereco">Endereço</label>
    <input type="text" id="endereco" name="endereco" value="<c:out value='${param.endereco}'/>"
           minlength="5" maxlength="150" autocomplete="street-address" required
           class="${erroCampo == 'endereco' ? 'invalido' : ''}"
           ${erroCampo == 'endereco' ? 'aria-invalid="true"' : ''}>

    <button class="btn auth-btn" type="submit">Criar conta</button>
  </form>

  <p class="auth-link-rodape muted">Já tem conta? <a href="<c:url value='/login'/>">Entre</a></p>
</div>
