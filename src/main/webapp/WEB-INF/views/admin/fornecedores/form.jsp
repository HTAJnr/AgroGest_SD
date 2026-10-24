<h1>Novo fornecedor</h1>

<form class="form form-centrado" method="post" action="<c:url value='/admin/fornecedores'/>">
  <input type="hidden" name="_t" value="${tokenForm}">

  <div class="form-grelha">
    <div class="campo">
      <label for="nome">Nome</label>
      <input type="text" id="nome" name="nome" value="<c:out value='${param.nome}'/>"
             class="${erroCampo == 'nome' ? 'invalido' : ''}" required>
    </div>

    <div class="campo">
      <label for="tipo">Tipo</label>
      <select id="tipo" name="tipo" class="${erroCampo == 'tipo' ? 'invalido' : ''}" required>
        <option value="">-- Escolha o tipo --</option>
        <c:forEach var="t" items="${tipos}">
          <option value="${t}" ${param.tipo == t || param.tipo == t.name() ? 'selected' : ''}>${t}</option>
        </c:forEach>
      </select>
    </div>

    <div class="campo">
      <label for="contacto">Contacto</label>
      <input type="text" id="contacto" name="contacto" inputmode="tel"
             value="<c:out value='${param.contacto}'/>"
             placeholder="ex.: 84 123 4567 ou email@exemplo.mz"
             class="${erroCampo == 'contacto' ? 'invalido' : ''}" required>
    </div>

    <div class="campo">
      <label for="provincia">Província</label>
      <select id="provincia" name="provincia" class="${erroCampo == 'provincia' ? 'invalido' : ''}" required>
        <option value="">-- Escolha a província --</option>
        <c:forEach var="p" items="${provincias}">
          <option value="${p}" ${param.provincia == p ? 'selected' : ''}><c:out value="${p}"/></option>
        </c:forEach>
      </select>
    </div>

    <div class="form-acoes campo-cheio">
      <a class="btn-sec" href="<c:url value='/admin/fornecedores'/>">Cancelar</a>
      <button class="btn" type="submit">Guardar</button>
    </div>
  </div>
</form>
