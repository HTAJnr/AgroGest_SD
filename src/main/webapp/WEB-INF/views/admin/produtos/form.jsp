  <div class="pagina-topo">
    <h1>Novo produto</h1>
    <a class="btn-sec" href="<c:url value='/admin/produtos'/>">← Voltar</a>
  </div>

<form class="form form-centrado" method="post" action="<c:url value='/admin/produtos/criar'/>"
      data-validar novalidate>
  <input type="hidden" name="_t" value="${tokenForm}">

  <div class="form-grelha">

    <div class="campo campo-cheio">
      <label for="nome">Nome</label>
      <input type="text" id="nome" name="nome"
             value="<c:out value='${param.nome}'/>"
             minlength="3" maxlength="80" required
             class="${erroCampo == 'nome' ? 'invalido' : ''}">
    </div>

    <div class="campo">
      <label for="categoria">Categoria</label>
      <select id="categoria" name="categoria"
              class="${erroCampo == 'categoria' ? 'invalido' : ''}" required>
        <option value="">-- Escolha --</option>
        <c:forEach var="c" items="${categorias}">
          <option value="${c}" ${param.categoria == c ? 'selected' : ''}>${c}</option>
        </c:forEach>
      </select>
    </div>

    <div class="campo">
      <label for="fornecedorId">Fornecedor</label>
      <select id="fornecedorId" name="fornecedorId"
              class="${erroCampo == 'fornecedorId' ? 'invalido' : ''}" required>
        <option value="">-- Escolha --</option>
        <c:forEach var="f" items="${fornecedores}">
          <option value="${f.id}"
                  data-tipo="${f.tipo}"
                  ${param.fornecedorId == f.id ? 'selected' : ''}><c:out value="${f.nome}"/></option>
        </c:forEach>
      </select>
    </div>

    <div class="campo">
      <label for="preco">Preço (MT)</label>
      <input type="text" id="preco" name="preco" inputmode="decimal"
             value="<c:out value='${param.preco}'/>"
             placeholder="ex.: 250,50"
             pattern="[0-9]+([.,][0-9]{1,2})?"
             required
             class="${erroCampo == 'preco' ? 'invalido' : ''}">
    </div>

    <div class="campo">
      <label for="quantidade">Quantidade inicial</label>
      <input type="text" id="quantidade" name="quantidade" inputmode="decimal"
             value="<c:out value='${param.quantidade}'/>"
             placeholder="ex.: 100"
             pattern="[0-9]+([.,][0-9]{1,2})?"
             required
             class="${erroCampo == 'quantidade' ? 'invalido' : ''}">
    </div>

    <div class="campo">
      <label>Unidade</label>
      <div class="chips ${erroCampo == 'unidade' ? 'invalido' : ''}">
        <c:forEach var="u" items="${unidades}">
          <label>
            <input type="radio" name="unidade" value="${u}"
                   ${param.unidade == u ? 'checked' : ''}
                   required>
            <span>${u}</span>
          </label>
        </c:forEach>
      </div>
    </div>

    <div class="campo campo-cheio">
      <label for="descricao">Descrição <span class="muted">(opcional, máx. 300 car.)</span></label>
      <textarea id="descricao" name="descricao" rows="3"
                maxlength="300"
                class="${erroCampo == 'descricao' ? 'invalido' : ''}"><c:out value="${param.descricao}"/></textarea>
    </div>

    <div class="campo campo-cheio">
      <label>Foto do produto <span class="muted">(opcional)</span></label>
      <%-- Galeria de miniaturas: radio button por foto local --%>
      <c:if test="${not empty fotosLocais}">
        <div class="galeria-fotos ${erroCampo == 'fotoUrl' ? 'invalido' : ''}">
          <label class="galeria-item ${(empty param.fotoUrl || param.fotoUrl == '') ? 'galeria-item-activa' : ''}">
            <input type="radio" name="fotoUrl" value=""
                   ${empty param.fotoUrl ? 'checked' : ''}>
            <img src="<c:url value='/assets/img/produto-placeholder.svg'/>"
                 alt="Sem foto" width="80" height="60" loading="lazy">
            <span>Sem foto</span>
          </label>
          <c:forEach var="foto" items="${fotosLocais}">
            <label class="galeria-item ${param.fotoUrl == foto ? 'galeria-item-activa' : ''}">
              <input type="radio" name="fotoUrl" value="${foto}"
                     ${param.fotoUrl == foto ? 'checked' : ''}>
              <img src="<c:url value='${foto}'/>" alt="${foto}" width="80" height="60" loading="lazy">
              <span>${fn:substringAfter(fn:substringBefore(foto, '.'), '/produtos/')}</span>
            </label>
          </c:forEach>
        </div>
        <p class="muted galeria-legenda">Ou introduza um URL directamente:</p>
      </c:if>
      <input type="text" id="fotoUrl" name="fotoUrl"
             value="<c:out value='${param.fotoUrl}'/>"
             placeholder="https://… ou /assets/img/produtos/milho.jpg"
             class="${erroCampo == 'fotoUrl' ? 'invalido' : ''}"
             ${not empty fotosLocais ? 'hidden' : ''}>
    </div>

    <div class="campo">
      <label for="validade">Validade <span class="muted">(opcional)</span></label>
      <input type="date" id="validade" name="validade"
             value="<c:out value='${param.validade}'/>"
             min="${hoje}"
             class="${erroCampo == 'validade' ? 'invalido' : ''}">
    </div>

    <div class="form-acoes campo-cheio">
      <a class="btn-sec" href="<c:url value='/admin/produtos'/>">Cancelar</a>
      <button class="btn" type="submit">
        <svg class="icone" aria-hidden="true"><use href="#i-mais"/></svg>
        Guardar
      </button>
    </div>

  </div>
</form>
