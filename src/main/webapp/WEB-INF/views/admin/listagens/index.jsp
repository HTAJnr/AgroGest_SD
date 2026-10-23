<div class="conteudo">
  <h1>Listagens</h1>

  <div class="acoes nao-imprimir">
    <a class="${vista == 'produtos' ? 'btn' : 'btn-sec'}" href="<c:url value='/admin/listagens/produtos'/>">Produtos</a>
    <a class="${vista == 'fornecedores' ? 'btn' : 'btn-sec'}" href="<c:url value='/admin/listagens/fornecedores'/>">Fornecedores</a>
    <a class="${vista == 'clientes' ? 'btn' : 'btn-sec'}" href="<c:url value='/admin/listagens/clientes'/>">Clientes</a>
    <a class="${vista == 'armazem' ? 'btn' : 'btn-sec'}" href="<c:url value='/admin/listagens/armazem'/>">Armazém</a>
    <a class="${vista == 'compras' ? 'btn' : 'btn-sec'}" href="<c:url value='/admin/listagens/compras'/>">Compras detalhadas</a>
  </div>

  <c:url var="base" value="/admin/listagens/${vista}"/>
  <c:set var="inv" value="${dir == 'asc' ? 'desc' : 'asc'}"/>
  <c:set var="seta" value="${dir == 'asc' ? ' ▲' : ' ▼'}"/>

  <h2><c:out value="${titulo}"/></h2>

  <c:if test="${empty lista}">
    <p class="muted">Sem registos.</p>
  </c:if>

  <c:if test="${not empty lista}">
    <c:choose>

      <%-- PRODUTOS e ARMAZÉM --%>
      <c:when test="${vista == 'produtos' || vista == 'armazem'}">
        <div class="tabela-scroll">
          <table class="tabela">
            <thead>
              <tr>
                <th><a href="${base}?ordenar=nome&amp;dir=${ordenar == 'nome' ? inv : 'asc'}">Nome<c:if test="${ordenar == 'nome'}">${seta}</c:if></a></th>
                <th><a href="${base}?ordenar=categoria&amp;dir=${ordenar == 'categoria' ? inv : 'asc'}">Categoria<c:if test="${ordenar == 'categoria'}">${seta}</c:if></a></th>
                <th>Fornecedor</th>
                <th class="num"><a href="${base}?ordenar=preco&amp;dir=${ordenar == 'preco' ? inv : 'asc'}">Preço<c:if test="${ordenar == 'preco'}">${seta}</c:if></a></th>
                <th class="num"><a href="${base}?ordenar=stock&amp;dir=${ordenar == 'stock' ? inv : 'asc'}">Stock<c:if test="${ordenar == 'stock'}">${seta}</c:if></a></th>
                <th><a href="${base}?ordenar=validade&amp;dir=${ordenar == 'validade' ? inv : 'asc'}">Validade<c:if test="${ordenar == 'validade'}">${seta}</c:if></a></th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="p" items="${lista}">
                <tr>
                  <td><c:out value="${p.nome}"/></td>
                  <td><c:out value="${p.categoria}"/></td>
                  <td><c:out value="${nomesFornecedores[p.fornecedorId]}"/></td>
                  <td class="num"><fmt:formatNumber value="${p.preco}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
                  <td class="num"><fmt:formatNumber value="${p.quantidade}" type="number" maxFractionDigits="2"/> <c:out value="${p.unidade}"/></td>
                  <td>${p.validadeFmt}</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:when>

      <%-- FORNECEDORES --%>
      <c:when test="${vista == 'fornecedores'}">
        <div class="tabela-scroll">
          <table class="tabela">
            <thead>
              <tr>
                <th><a href="${base}?ordenar=nome&amp;dir=${ordenar == 'nome' ? inv : 'asc'}">Nome<c:if test="${ordenar == 'nome'}">${seta}</c:if></a></th>
                <th><a href="${base}?ordenar=tipo&amp;dir=${ordenar == 'tipo' ? inv : 'asc'}">Tipo<c:if test="${ordenar == 'tipo'}">${seta}</c:if></a></th>
                <th>Contacto</th>
                <th><a href="${base}?ordenar=provincia&amp;dir=${ordenar == 'provincia' ? inv : 'asc'}">Província<c:if test="${ordenar == 'provincia'}">${seta}</c:if></a></th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="f" items="${lista}">
                <tr>
                  <td><c:out value="${f.nome}"/></td>
                  <td><c:out value="${f.tipo}"/></td>
                  <td><c:out value="${f.contacto}"/></td>
                  <td><c:out value="${f.provincia}"/></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:when>

      <%-- CLIENTES --%>
      <c:when test="${vista == 'clientes'}">
        <div class="tabela-scroll">
          <table class="tabela">
            <thead>
              <tr>
                <th><a href="${base}?ordenar=nome&amp;dir=${ordenar == 'nome' ? inv : 'asc'}">Nome<c:if test="${ordenar == 'nome'}">${seta}</c:if></a></th>
                <th><a href="${base}?ordenar=email&amp;dir=${ordenar == 'email' ? inv : 'asc'}">Email<c:if test="${ordenar == 'email'}">${seta}</c:if></a></th>
                <th>Telefone</th>
                <th>Endereço</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="c" items="${lista}">
                <tr>
                  <td><c:out value="${c.nome}"/></td>
                  <td><c:out value="${c.email}"/></td>
                  <td><c:out value="${c.telefone}"/></td>
                  <td><c:out value="${c.endereco}"/></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:when>

      <%-- COMPRAS DETALHADAS --%>
      <c:when test="${vista == 'compras'}">
        <p class="nao-imprimir">Ordenar por:
          <a href="${base}?ordenar=data&amp;dir=${ordenar == 'data' ? inv : 'desc'}">Data<c:if test="${ordenar == 'data'}">${seta}</c:if></a> |
          <a href="${base}?ordenar=valor&amp;dir=${ordenar == 'valor' ? inv : 'desc'}">Valor<c:if test="${ordenar == 'valor'}">${seta}</c:if></a> |
          <a href="${base}?ordenar=cliente&amp;dir=${ordenar == 'cliente' ? inv : 'asc'}">Cliente<c:if test="${ordenar == 'cliente'}">${seta}</c:if></a>
        </p>

        <c:forEach var="v" items="${lista}">
          <div class="cartao" style="margin-bottom:16px">
            <h3>
              Factura
              <a href="<c:url value='/factura'><c:param name='id' value='${v.id}'/></c:url>"><c:out value="${v.numeroFactura}"/></a>
              <span class="muted"> · ${v.dataFmt}</span>
            </h3>
            <p>
              <strong>Cliente:</strong> <c:out value="${v.clienteNome}"/> (<c:out value="${v.clienteEmail}"/>)<br>
              <strong>Endereço:</strong> <c:out value="${v.clienteEndereco}"/><br>
              <strong>Telefone:</strong> <c:out value="${v.clienteTelefone}"/>
            </p>
            <div class="tabela-scroll">
              <table class="tabela">
                <thead>
                  <tr>
                    <th>Produto</th>
                    <th class="num">Quantidade</th>
                    <th>Unidade</th>
                    <th class="num">Valor unitário</th>
                    <th class="num">Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="i" items="${v.itens}">
                    <tr>
                      <td><c:out value="${i.produtoNome}"/></td>
                      <td class="num"><fmt:formatNumber value="${i.quantidade}" type="number" maxFractionDigits="2"/></td>
                      <td><c:out value="${i.unidade}"/></td>
                      <td class="num"><fmt:formatNumber value="${i.precoUnit}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
                      <td class="num"><fmt:formatNumber value="${i.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            <p class="num">
              Subtotal: <fmt:formatNumber value="${v.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT<br>
              IVA (16%): <fmt:formatNumber value="${v.iva}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT<br>
              <span class="total">Total: <fmt:formatNumber value="${v.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</span>
            </p>
          </div>
        </c:forEach>
      </c:when>

    </c:choose>
  </c:if>
</div>
