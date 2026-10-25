<h1>Nova campanha</h1>

<form class="form form-centrado" method="post" action="<c:url value='/admin/campanhas'/>"
      data-validar novalidate>
  <input type="hidden" name="_t" value="${tokenForm}">

  <div class="form-grelha">
    <div class="campo campo-cheio">
      <label for="nome">Nome da campanha</label>
      <input type="text" id="nome" name="nome" value="<c:out value='${param.nome}'/>"
             placeholder="ex.: Campanha Agrícola 2026/2027"
             minlength="3" maxlength="80" required
             class="${erroCampo == 'nome' ? 'invalido' : ''}">
    </div>

    <div class="campo">
      <label for="inicio">Data de início</label>
      <input type="date" id="inicio" name="inicio" value="<c:out value='${param.inicio}'/>"
             required
             class="${erroCampo == 'inicio' ? 'invalido' : ''}">
    </div>

    <div class="campo">
      <label for="fim">Data de fim</label>
      <input type="date" id="fim" name="fim" value="<c:out value='${param.fim}'/>"
             required
             class="${erroCampo == 'fim' ? 'invalido' : ''}">
    </div>

    <div class="form-acoes campo-cheio">
      <a class="btn-sec" href="<c:url value='/admin/campanhas'/>">Cancelar</a>
      <button class="btn" type="submit">
        <svg class="icone" aria-hidden="true"><use href="#i-mais"/></svg>
        Guardar
      </button>
    </div>
  </div>
</form>
