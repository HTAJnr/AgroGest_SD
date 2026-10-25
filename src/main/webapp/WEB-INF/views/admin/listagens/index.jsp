<div class="conteudo">
  <div class="pagina-topo">
    <h1>Listagens</h1>
  </div>

  <%-- Abas de navegação --%>
  <nav class="abas nao-imprimir" aria-label="Listagens">
    <a href="<c:url value='/admin/listagens/produtos'/>"
       aria-current="${vista == 'produtos' ? 'page' : 'false'}">Produtos</a>
    <a href="<c:url value='/admin/listagens/fornecedores'/>"
       aria-current="${vista == 'fornecedores' ? 'page' : 'false'}">Fornecedores</a>
    <a href="<c:url value='/admin/listagens/clientes'/>"
       aria-current="${vista == 'clientes' ? 'page' : 'false'}">Clientes</a>
    <a href="<c:url value='/admin/listagens/armazem'/>"
       aria-current="${vista == 'armazem' ? 'page' : 'false'}">Armazém</a>
    <a href="<c:url value='/admin/listagens/compras'/>"
       aria-current="${vista == 'compras' ? 'page' : 'false'}">Compras detalhadas</a>
  </nav>

  <c:url var="base" value="/admin/listagens/${vista}"/>
  <c:set var="inv" value="${dir == 'asc' ? 'desc' : 'asc'}"/>

  <%-- Helper: seta e aria-sort --%>
  <%-- Usa classes CSS .th-link --%>

  <c:if test="${empty lista}">
    <div class="estado-vazio cartao">
      <svg class="icone estado-vazio-icone" aria-hidden="true"><use href="#i-caixa"/></svg>
      <p>Sem registos nesta listagem.</p>
    </div>
  </c:if>

  <c:if test="${not empty lista}">
    <c:choose>

      <%-- PRODUTOS e ARMAZÉM --%>
      <c:when test="${vista == 'produtos' || vista == 'armazem'}">
        <div class="tabela-scroll">
          <table class="tabela">
            <thead>
              <tr>
                <th>Foto</th>
                <th aria-sort="${ordenar == 'nome' ? (dir == 'asc' ? 'ascending' : 'descending') : 'none'}">
                  <a class="th-link" href="${base}?ordenar=nome&amp;dir=${ordenar == 'nome' ? inv : 'asc'}">
                    Nome<c:if test="${ordenar == 'nome'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                  </a>
                </th>
                <th aria-sort="${ordenar == 'categoria' ? (dir == 'asc' ? 'ascending' : 'descending') : 'none'}">
                  <a class="th-link" href="${base}?ordenar=categoria&amp;dir=${ordenar == 'categoria' ? inv : 'asc'}">
                    Categoria<c:if test="${ordenar == 'categoria'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                  </a>
                </th>
                <th aria-sort="${ordenar == 'fornecedor' ? (dir == 'asc' ? 'ascending' : 'descending') : 'none'}">
                  Fornecedor
                </th>
                <th class="num" aria-sort="${ordenar == 'preco' ? (dir == 'asc' ? 'ascending' : 'descending') : 'none'}">
                  <a class="th-link" href="${base}?ordenar=preco&amp;dir=${ordenar == 'preco' ? inv : 'asc'}">
                    Preço<c:if test="${ordenar == 'preco'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                  </a>
                </th>
                <th class="num" aria-sort="${ordenar == 'stock' ? (dir == 'asc' ? 'ascending' : 'descending') : 'none'}">
                  <a class="th-link" href="${base}?ordenar=stock&amp;dir=${ordenar == 'stock' ? inv : 'asc'}">
                    Stock<c:if test="${ordenar == 'stock'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                  </a>
                </th>
                <th aria-sort="${ordenar == 'validade' ? (dir == 'asc' ? 'ascending' : 'descending') : 'none'}">
                  <a class="th-link" href="${base}?ordenar=validade&amp;dir=${ordenar == 'validade' ? inv : 'asc'}">
                    Validade<c:if test="${ordenar == 'validade'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                  </a>
                </th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="p" items="${lista}">
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
                      <c:when test="${p.quantidade <= 0}"><span class="badge off">Esgotado</span></c:when>
                      <c:otherwise><fmt:formatNumber value="${p.quantidade}" type="number" maxFractionDigits="2"/> <c:out value="${p.unidade}"/></c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${empty p.validadeFmt}"><span class="muted">—</span></c:when>
                      <c:when test="${p.expirado}"><span class="badge aviso">Expirado</span> ${p.validadeFmt}</c:when>
                      <c:otherwise>${p.validadeFmt}</c:otherwise>
                    </c:choose>
                  </td>
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
                <th aria-sort="${ordenar == 'nome' ? (dir == 'asc' ? 'ascending' : 'descending') : 'none'}">
                  <a class="th-link" href="${base}?ordenar=nome&amp;dir=${ordenar == 'nome' ? inv : 'asc'}">
                    Nome<c:if test="${ordenar == 'nome'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                  </a>
                </th>
                <th aria-sort="${ordenar == 'tipo' ? (dir == 'asc' ? 'ascending' : 'descending') : 'none'}">
                  <a class="th-link" href="${base}?ordenar=tipo&amp;dir=${ordenar == 'tipo' ? inv : 'asc'}">
                    Tipo<c:if test="${ordenar == 'tipo'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                  </a>
                </th>
                <th>Contacto</th>
                <th aria-sort="${ordenar == 'provincia' ? (dir == 'asc' ? 'ascending' : 'descending') : 'none'}">
                  <a class="th-link" href="${base}?ordenar=provincia&amp;dir=${ordenar == 'provincia' ? inv : 'asc'}">
                    Província<c:if test="${ordenar == 'provincia'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                  </a>
                </th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="f" items="${lista}">
                <tr>
                  <td><c:out value="${f.nome}"/></td>
                  <td><span class="badge"><c:out value="${f.tipo}"/></span></td>
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
                <th aria-sort="${ordenar == 'nome' ? (dir == 'asc' ? 'ascending' : 'descending') : 'none'}">
                  <a class="th-link" href="${base}?ordenar=nome&amp;dir=${ordenar == 'nome' ? inv : 'asc'}">
                    Nome<c:if test="${ordenar == 'nome'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                  </a>
                </th>
                <th aria-sort="${ordenar == 'email' ? (dir == 'asc' ? 'ascending' : 'descending') : 'none'}">
                  <a class="th-link" href="${base}?ordenar=email&amp;dir=${ordenar == 'email' ? inv : 'asc'}">
                    Email<c:if test="${ordenar == 'email'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                  </a>
                </th>
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
        <div class="compras-ordenar nao-imprimir">
          <span class="muted">Ordenar por:</span>
          <a class="th-link" href="${base}?ordenar=data&amp;dir=${ordenar == 'data' ? inv : 'desc'}">Data<c:if test="${ordenar == 'data'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if></a>
          <a class="th-link" href="${base}?ordenar=valor&amp;dir=${ordenar == 'valor' ? inv : 'desc'}">Valor<c:if test="${ordenar == 'valor'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if></a>
          <a class="th-link" href="${base}?ordenar=cliente&amp;dir=${ordenar == 'cliente' ? inv : 'asc'}">Cliente<c:if test="${ordenar == 'cliente'}"> <span class="seta-ord" aria-hidden="true">${dir == 'asc' ? '▲' : '▼'}</span></c:if></a>
        </div>

        <c:forEach var="v" items="${lista}">
          <div class="cartao compra-cartao">
            <div class="compra-cabecalho">
              <div>
                <h3 class="compra-num">
                  <svg class="icone" aria-hidden="true"><use href="#i-recibo"/></svg>
                  <a href="<c:url value='/factura'><c:param name='id' value='${v.id}'/></c:url>"><c:out value="${v.numeroFactura}"/></a>
                </h3>
                <p class="muted compra-data">${v.dataFmt}</p>
              </div>
              <div class="compra-total-bloco">
                <span class="muted">Total</span>
                <span class="num total compra-total-valor"><fmt:formatNumber value="${v.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</span>
              </div>
            </div>
            <div class="compra-cliente-info">
              <p><strong>Cliente:</strong> <c:out value="${v.clienteNome}"/> (<c:out value="${v.clienteEmail}"/>)</p>
              <p><strong>Telefone:</strong> <c:out value="${v.clienteTelefone}"/> &nbsp;·&nbsp; <strong>Endereço:</strong> <c:out value="${v.clienteEndereco}"/></p>
            </div>
            <div class="tabela-scroll">
              <table class="tabela">
                <thead>
                  <tr>
                    <th>Produto</th>
                    <th class="num">Quantidade</th>
                    <th>Unidade</th>
                    <th class="num">Unitário</th>
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
                <tfoot>
                  <tr>
                    <td colspan="4" class="num muted">Subtotal</td>
                    <td class="num"><fmt:formatNumber value="${v.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
                  </tr>
                  <tr>
                    <td colspan="4" class="num muted">IVA (16%)</td>
                    <td class="num"><fmt:formatNumber value="${v.iva}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
                  </tr>
                  <tr class="linha-total">
                    <td colspan="4" class="num"><strong>Total</strong></td>
                    <td class="num total"><fmt:formatNumber value="${v.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/> MT</td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>
        </c:forEach>
      </c:when>

    </c:choose>
  </c:if>

</div>
