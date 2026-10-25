<div class="pagina-topo">
  <div>
    <h1>Colheitas: <c:out value="${cultivo.cultura}"/><c:if test="${not empty cultivo.variedade}"> (<c:out value="${cultivo.variedade}"/>)</c:if></h1>
    <p class="muted">
      Campanha: <strong><c:out value="${campanha != null ? campanha.nome : '—'}"/></strong>
      ·
      <c:choose>
        <c:when test="${campanha != null and campanha.aberta}">
          <span class="badge ok">Aberta</span>
        </c:when>
        <c:otherwise>
          <span class="badge off">Fechada</span>
        </c:otherwise>
      </c:choose>
      ·
      Área: <fmt:formatNumber value="${cultivo.hectares}" type="number" minFractionDigits="1" maxFractionDigits="2"/> ha
      ·
      Plantio: <c:out value="${cultivo.plantioFmt}"/>
    </p>
  </div>
  <div>
    <a class="btn-sec" href="<c:url value='/admin/colheitas'/>">
      <svg class="icone" aria-hidden="true"><use href="#i-folha"/></svg> Outro cultivo
    </a>
  </div>
</div>

<c:choose>
  <c:when test="${empty colheitas}">
    <p class="muted">Ainda não há colheitas registadas neste cultivo.</p>
  </c:when>
  <c:otherwise>
    <div class="tabela-scroll">
      <table class="tabela">
        <thead>
          <tr>
            <th>Data da colheita</th>
            <th class="num">Quantidade</th>
            <th class="num">Preço por kg</th>
            <th class="num">Valor total</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="c" items="${colheitas}">
            <tr>
              <td><c:out value="${c.dataFmt}"/></td>
              <td class="num">
                <fmt:formatNumber value="${c.kg}" type="number" minFractionDigits="1" maxFractionDigits="2"/> kg
              </td>
              <td class="num">
                <fmt:formatNumber value="${c.precoKg}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT
              </td>
              <td class="num">
                <strong><fmt:formatNumber value="${c.valorTotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</strong>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </c:otherwise>
</c:choose>

<c:choose>
  <c:when test="${campanha != null and campanha.aberta}">
    <div class="cartao form-centrado" style="margin-top: 32px;">
      <h2>Registar colheita</h2>
      <form class="form" method="post" action="<c:url value='/admin/colheitas'/>"
            data-validar novalidate>
        <input type="hidden" name="_t" value="${tokenForm}">
        <input type="hidden" name="cultivoId" value="${cultivo.id}">

        <div class="form-grelha">
          <div class="campo">
            <label for="data">Data da colheita</label>
            <input type="date" id="data" name="data"
                   value="<c:out value='${param.data}'/>"
                   class="${erroCampo == 'data' ? 'invalido' : ''}" required>
          </div>

          <div class="campo">
            <label for="kg">Quantidade (kg)</label>
            <input type="number" id="kg" name="kg" step="0.01" min="0.01"
                   value="<c:out value='${param.kg}'/>"
                   placeholder="ex.: 3200"
                   class="${erroCampo == 'kg' ? 'invalido' : ''}" required>
          </div>

          <div class="campo">
            <label for="precoKg">Preço por kg (MT)</label>
            <input type="number" id="precoKg" name="precoKg" step="0.01" min="0.01"
                   value="<c:out value='${param.precoKg}'/>"
                   placeholder="ex.: 22.00"
                   class="${erroCampo == 'precoKg' ? 'invalido' : ''}" required>
          </div>

          <div class="form-acoes campo-cheio">
            <button class="btn" type="submit">
              <svg class="icone" aria-hidden="true"><use href="#i-mais"/></svg> Registar colheita
            </button>
          </div>
        </div>
      </form>
    </div>
  </c:when>
  <c:otherwise>
    <div class="cartao form-centrado" style="margin-top: 32px;">
      <h2>Registar colheita</h2>
      <div class="alerta erro" style="margin-bottom: 16px;">
        A campanha deste cultivo está <strong>fechada</strong>. Não é possível registar novas colheitas.
      </div>
      <form class="form" onsubmit="return false;">
        <div class="form-grelha">
          <div class="campo">
            <label for="data">Data da colheita</label>
            <input type="date" id="data" disabled>
          </div>

          <div class="campo">
            <label for="kg">Quantidade (kg)</label>
            <input type="number" id="kg" disabled placeholder="ex.: 3200">
          </div>

          <div class="campo">
            <label for="precoKg">Preço por kg (MT)</label>
            <input type="number" id="precoKg" disabled placeholder="ex.: 22.00">
          </div>

          <div class="form-acoes campo-cheio">
            <button class="btn" type="button" disabled title="Campanha fechada">
              <svg class="icone" aria-hidden="true"><use href="#i-mais"/></svg> Registar colheita
            </button>
          </div>
        </div>
      </form>
    </div>
  </c:otherwise>
</c:choose>
