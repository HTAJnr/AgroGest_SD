<h1>Criar conta</h1>

<form class="form" method="post" action="<c:url value='/registo'/>">
  <label for="nome">Nome</label>
  <input type="text" id="nome" name="nome" value="<c:out value='${param.nome}'/>" autofocus>

  <label for="email">Email</label>
  <input type="email" id="email" name="email" value="<c:out value='${param.email}'/>">

  <label for="password">Palavra-passe</label>
  <input type="password" id="password" name="password">

  <label for="confirmar">Confirmar palavra-passe</label>
  <input type="password" id="confirmar" name="confirmar">

  <label for="telefone">Telefone (opcional)</label>
  <input type="text" id="telefone" name="telefone" value="<c:out value='${param.telefone}'/>" placeholder="ex.: 840000000">

  <label for="endereco">Endere&ccedil;o (opcional)</label>
  <input type="text" id="endereco" name="endereco" value="<c:out value='${param.endereco}'/>">

  <button class="btn" type="submit">Criar conta</button>
</form>

<p class="muted">J&aacute; tem conta? <a href="<c:url value='/login'/>">Entrar</a></p>