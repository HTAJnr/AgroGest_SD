<div class="conteudo">
  <div class="pagina-topo">
    <h1>Produtos</h1>
    <a class="btn" href="<c:url value='/admin/produtos/novo'/>">
      <svg class="icone" aria-hidden="true"><use href="#i-mais"/></svg>
      Novo produto
    </a>
  </div>

  <c:choose>
    <c:when test="${empty produtos}">
      <div class="estado-vazio cartao">
        <svg class="icone estado-vazio-icone" aria-hidden="true"><use href="#i-caixa"/></svg>
        <p>Ainda não há produtos. Comece por adicionar o primeiro.</p>
        <a class="btn" href="<c:url value='/admin/produtos/novo'/>">
          <svg class="icone" aria-hidden="true"><use href="#i-mais"/></svg>
          Novo produto
        </a>
      </div>
    </c:when>
    <c:otherwise>
      <div class="tabela-scroll">
        <table class="tabela">
          <thead>
            <tr>
              <th>Foto</th>
              <th>Nome</th>
              <th>Categoria</th>
              <th>Fornecedor</th>
              <th class="num">Preço</th>
              <th class="num">Stock</th>
              <th>Validade</th>
              <th class="acoes">Acções</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="p" items="${produtos}">
              <tr>
                <td>
                  <c:choose>
                    <c:when test="${not empty p.fotoUrl}">
                      <c:choose>
                        <c:when test="${fn:startsWith(p.fotoUrl, '/')}">
                          <img class="produto-mini" src="<c:url value='${p.fotoUrl}'/>"
                               alt="<c:out value='${p.nome}'/>" width="48" height="48" loading="lazy">
                        </c:when>
                        <c:otherwise>
                          <img class="produto-mini" src="<c:out value='${p.fotoUrl}'/>"
                               alt="<c:out value='${p.nome}'/>" width="48" height="48" loading="lazy">
                        </c:otherwise>
                      </c:choose>
                    </c:when>
                    <c:otherwise>
                      <img class="produto-mini" src="<c:url value='/assets/img/produto-placeholder.svg'/>"
                           alt="" width="48" height="48">
                    </c:otherwise>
                  </c:choose>
                </td>
                <td><c:out value="${p.nome}"/></td>
                <td><span class="badge"><c:out value="${p.categoria}"/></span></td>
                <td><c:out value="${nomesFornecedores[p.fornecedorId]}"/></td>
                <td class="num"><fmt:formatNumber value="${p.preco}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
                <td class="num">
                  <c:choose>
                    <c:when test="${p.quantidade <= 0}">
                      <span class="badge off">Esgotado</span>
                    </c:when>
                    <c:otherwise>
                      <fmt:formatNumber value="${p.quantidade}" type="number" maxFractionDigits="2"/> <c:out value="${p.unidade}"/>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td>
                  <c:choose>
                    <c:when test="${empty p.validadeFmt}"><span class="muted">—</span></c:when>
                    <c:when test="${p.expirado}"><span class="badge aviso">Expirado</span> ${p.validadeFmt}</c:when>
                    <c:otherwise>${p.validadeFmt}</c:otherwise>
                  </c:choose>
                </td>
                <td class="acoes">
                  <%-- Botão Repor abre dialog --%>
                  <button type="button" class="btn-sec btn-icone" title="Repor stock"
                          aria-label="Repor stock de ${p.nome}"
                          onclick="abrirDialogRepor(${p.id}, '<c:out value="${p.nome}" escapeXml="true"/>', '<c:out value="${p.unidade}"/>', ${p.quantidade})">
                    <svg class="icone" aria-hidden="true"><use href="#i-mais"/></svg>
                  </button>
                  <%-- Botão Remover com confirmação --%>
                  <form method="post" action="<c:url value='/admin/produtos/remover'/>" style="display:inline">
                    <input type="hidden" name="_t" value="${tokenForm}">
                    <input type="hidden" name="id" value="${p.id}">
                    <button type="submit" class="btn-perigo btn-icone"
                            title="Remover produto" aria-label="Remover ${p.nome}"
                            data-confirm="Remover o produto «${p.nome}»? Esta acção não pode ser desfeita.">
                      <svg class="icone" aria-hidden="true"><use href="#i-lixo"/></svg>
                    </button>
                  </form>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:otherwise>
  </c:choose>
</div>

<%-- Dialog de reposição de stock --%>
<dialog id="dlg-repor" aria-labelledby="dlg-repor-titulo">
  <form method="post" action="<c:url value='/admin/produtos/repor'/>" id="form-repor">
    <input type="hidden" name="_t" value="${tokenForm}">
    <input type="hidden" name="id" id="repor-id" value="">
    <h3 id="dlg-repor-titulo">Repor stock</h3>
    <p id="repor-descricao" class="muted"></p>
    <div class="campo">
      <label for="repor-quantidade">Quantidade a adicionar</label>
      <input type="text" id="repor-quantidade" name="quantidade"
             inputmode="decimal" placeholder="ex.: 50"
             pattern="[0-9]+([.,][0-9]{1,2})?"
             required autocomplete="off">
    </div>
    <div class="dlg-acoes">
      <button type="button" class="btn-sec" onclick="document.getElementById('dlg-repor').close()">Cancelar</button>
      <button type="submit" class="btn">
        <svg class="icone" aria-hidden="true"><use href="#i-check"/></svg>
        Repor
      </button>
    </div>
  </form>
</dialog>

<script>
function abrirDialogRepor(id, nome, unidade, stockActual) {
  document.getElementById('repor-id').value = id;
  document.getElementById('repor-descricao').textContent =
    nome + ' · Stock actual: ' + stockActual.toLocaleString('pt-PT', {maximumFractionDigits: 2}) + ' ' + unidade;
  document.getElementById('repor-quantidade').value = '';
  document.getElementById('dlg-repor').showModal();
}
</script>
