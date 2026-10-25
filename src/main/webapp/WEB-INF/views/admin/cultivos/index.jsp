<div class="pagina-topo">
  <div>
    <h1>Cultivos: <c:out value="${campanha.nome}"/></h1>
    <p class="muted">
      Período: <c:out value="${campanha.inicioFmt}"/> a <c:out value="${campanha.fimFmt}"/>
      ·
      <c:choose>
        <c:when test="${campanha.aberta}">
          <span class="badge ok">Aberta</span>
        </c:when>
        <c:otherwise>
          <span class="badge off">Fechada</span>
        </c:otherwise>
      </c:choose>
    </p>
  </div>
  <div>
    <a class="btn-sec" href="<c:url value='/admin/cultivos'/>">
      <svg class="icone" aria-hidden="true"><use href="#i-folha"/></svg> Outra campanha
    </a>
  </div>
</div>

<c:choose>
  <c:when test="${empty cultivos}">
    <p class="muted">Ainda não há cultivos registados nesta campanha.</p>
  </c:when>
  <c:otherwise>
    <div class="tabela-scroll">
      <table class="tabela">
        <thead>
          <tr>
            <th>Cultura</th>
            <th>Variedade</th>
            <th class="num">Área (ha)</th>
            <th>Data de plantio</th>
            <th class="nao-imprimir">Colheitas</th>
            <th class="nao-imprimir">Acções</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="cult" items="${cultivos}">
            <tr>
              <td><strong><c:out value="${cult.cultura}"/></strong></td>
              <td>
                <c:choose>
                  <c:when test="${not empty cult.variedade}">
                    <c:out value="${cult.variedade}"/>
                  </c:when>
                  <c:otherwise>
                    <span class="muted">—</span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td class="num">
                <fmt:formatNumber value="${cult.hectares}" type="number" minFractionDigits="1" maxFractionDigits="2"/> ha
              </td>
              <td><c:out value="${cult.plantioFmt}"/></td>
              <td class="nao-imprimir">
                <a class="btn-sec" href="<c:url value='/admin/colheitas'><c:param name='cultivoId' value='${cult.id}'/></c:url>">
                  Colheitas
                </a>
              </td>
              <td class="acoes nao-imprimir">
                <form method="post" action="<c:url value='/admin/cultivos/remover'/>" style="display:inline;">
                  <input type="hidden" name="_t" value="${tokenForm}">
                  <input type="hidden" name="id" value="${cult.id}">
                  <input type="hidden" name="campanhaId" value="${campanha.id}">
                  <button type="submit" class="btn-perigo btn-icone" title="Remover cultivo" aria-label="Remover"
                          data-confirm="Remover o cultivo «${cult.cultura}»? Esta acção não pode ser desfeita.">
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

<c:choose>
  <c:when test="${campanha.aberta}">
    <div class="cartao form-centrado" style="margin-top: 32px;">
      <h2>Novo cultivo</h2>
      <form class="form" method="post" action="<c:url value='/admin/cultivos'/>"
            data-validar novalidate>
        <input type="hidden" name="_t" value="${tokenForm}">
        <input type="hidden" name="campanhaId" value="${campanha.id}">

        <div class="form-grelha">
          <div class="campo">
            <label for="cultura">Cultura</label>
            <input type="text" id="cultura" name="cultura" value="<c:out value='${param.cultura}'/>"
                   placeholder="ex.: Milho, Feijão nhemba"
                   class="${erroCampo == 'cultura' ? 'invalido' : ''}" required>
          </div>

          <div class="campo">
            <label for="variedade">Variedade <span class="muted">(opcional)</span></label>
            <input type="text" id="variedade" name="variedade" value="<c:out value='${param.variedade}'/>"
                   placeholder="ex.: Matuba, PAN 53"
                   class="${erroCampo == 'variedade' ? 'invalido' : ''}">
          </div>

          <div class="campo">
            <label for="hectares">Área (hectares)</label>
            <input type="number" id="hectares" name="hectares" step="0.01" min="0.01"
                   value="<c:out value='${param.hectares}'/>"
                   placeholder="ex.: 10.5"
                   class="${erroCampo == 'hectares' ? 'invalido' : ''}" required>
          </div>

          <div class="campo">
            <label for="plantio">Data de plantio</label>
            <input type="date" id="plantio" name="plantio"
                   value="<c:out value='${param.plantio}'/>"
                   class="${erroCampo == 'plantio' ? 'invalido' : ''}" required>
          </div>

          <div class="form-acoes campo-cheio">
            <button class="btn" type="submit">
              <svg class="icone" aria-hidden="true"><use href="#i-mais"/></svg> Adicionar cultivo
            </button>
          </div>
        </div>
      </form>
    </div>
  </c:when>
  <c:otherwise>
    <div class="cartao form-centrado" style="margin-top: 32px;">
      <h2>Novo cultivo</h2>
      <div class="alerta erro" style="margin-bottom: 16px;">
        Esta campanha está <strong>fechada</strong>. Não é possível registar novos cultivos.
      </div>
      <form class="form" onsubmit="return false;">
        <div class="form-grelha">
          <div class="campo">
            <label for="cultura">Cultura</label>
            <input type="text" id="cultura" disabled placeholder="ex.: Milho">
          </div>

          <div class="campo">
            <label for="variedade">Variedade</label>
            <input type="text" id="variedade" disabled placeholder="ex.: Matuba">
          </div>

          <div class="campo">
            <label for="hectares">Área (hectares)</label>
            <input type="number" id="hectares" disabled placeholder="ex.: 10.5">
          </div>

          <div class="campo">
            <label for="plantio">Data de plantio</label>
            <input type="date" id="plantio" disabled>
          </div>

          <div class="form-acoes campo-cheio">
            <button class="btn" type="button" disabled title="Campanha fechada">
              <svg class="icone" aria-hidden="true"><use href="#i-mais"/></svg> Adicionar cultivo
            </button>
          </div>
        </div>
      </form>
    </div>
  </c:otherwise>
</c:choose>
