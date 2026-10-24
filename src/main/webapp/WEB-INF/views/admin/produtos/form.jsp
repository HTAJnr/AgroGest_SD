<h1>Novo produto</h1>

<form class="form" method="post" action="<c:url value='/admin/produtos/criar'/>">
  <input type="hidden" name="_t" value="${tokenForm}">

  <label for="nome">Nome</label>
  <input type="text" id="nome" name="nome" value="<c:out value='${param.nome}'/>">

  <label for="categoria">Categoria</label>
  <select id="categoria" name="categoria">
    <option value="">-- Escolha --</option>
    <c:forEach var="c" items="${categorias}">
      <option value="${c}" ${param.categoria == c ? 'selected' : ''}>${c}</option>
    </c:forEach>
  </select>

  <label for="fornecedorId">Fornecedor</label>
  <select id="fornecedorId" name="fornecedorId">
    <option value="">-- Escolha --</option>
    <c:forEach var="f" items="${fornecedores}">
      <option value="${f.id}" ${param.fornecedorId == f.id ? 'selected' : ''}><c:out value="${f.nome}"/></option>
    </c:forEach>
  </select>

  <label for="preco">Preço (MT)</label>
  <input type="text" id="preco" name="preco" value="<c:out value='${param.preco}'/>" placeholder="ex.: 250,50">

  <label for="quantidade">Quantidade</label>
  <input type="text" id="quantidade" name="quantidade" value="<c:out value='${param.quantidade}'/>" placeholder="ex.: 3,5">

  <label for="unidade">Unidade</label>
  <input type="text" id="unidade" name="unidade" value="<c:out value='${param.unidade}'/>" placeholder="kg, saco, unidade">

  <label for="descricao">Descrição</label>
  <textarea id="descricao" name="descricao" rows="3"><c:out value="${param.descricao}"/></textarea>

  <label for="fotoUrl">URL da foto (opcional)</label>
  <input type="text" id="fotoUrl" name="fotoUrl" value="<c:out value='${param.fotoUrl}'/>">

  <label for="validade">Validade (opcional)</label>
  <input type="text" id="validade" name="validade" value="<c:out value='${param.validade}'/>" placeholder="dd/mm/aaaa, ex.: 31/03/2027" pattern="\d{2}/\d{2}/\d{4}" maxlength="10">

  <p>
    <button class="btn" type="submit">Guardar</button>
    <a class="btn-sec" href="<c:url value='/admin/produtos'/>">Cancelar</a>
  </p>
</form>
