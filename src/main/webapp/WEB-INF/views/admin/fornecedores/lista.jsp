<%-- Fornecedores: formulário de criar + tabela. Topo, rodapé e taglibs entram sozinhos. --%>
<h1>Fornecedores</h1>

<div class="cartao">
    <h2>Novo fornecedor</h2>
    <form class="form" method="post" action="<c:url value='/admin/fornecedores/criar'/>">
        <input type="hidden" name="_t" value="${tokenForm}">
        <label for="nome">Nome</label>
        <input type="text" id="nome" name="nome" value="<c:out value='${param.nome}'/>">

        <label for="tipo">Tipo</label>
        <select id="tipo" name="tipo">
            <option value="">-- escolher --</option>
            <c:forEach var="t" items="${tipos}">
                <option value="${t}" ${param.tipo == t.name() ? 'selected' : ''}>${t}</option>
            </c:forEach>
        </select>

        <label for="contacto">Contacto</label>
        <input type="text" id="contacto" name="contacto" value="<c:out value='${param.contacto}'/>">

        <label for="provincia">Província</label>
        <input type="text" id="provincia" name="provincia" value="<c:out value='${param.provincia}'/>">

        <button type="submit" class="btn">Criar fornecedor</button>
    </form>
</div>

<div class="cartao">
    <h2>Lista de fornecedores</h2>
    <c:choose>
        <c:when test="${empty fornecedores}">
            <p class="muted">Ainda não há fornecedores registados.</p>
        </c:when>
        <c:otherwise>
            <div class="tabela-scroll">
                <table class="tabela">
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Tipo</th>
                            <th>Contacto</th>
                            <th>Província</th>
                            <th class="nao-imprimir">Acções</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="f" items="${fornecedores}">
                            <tr>
                                <td><c:out value="${f.nome}"/></td>
                                <td><span class="badge">${f.tipo}</span></td>
                                <td><c:out value="${f.contacto}"/></td>
                                <td><c:out value="${f.provincia}"/></td>
                                <td class="acoes nao-imprimir">
                                    <form method="post" action="<c:url value='/admin/fornecedores/remover'/>"
                                          onsubmit="return confirm('Remover este fornecedor?');">
                                        <input type="hidden" name="_t" value="${tokenForm}">
                                        <input type="hidden" name="id" value="${f.id}">
                                        <button type="submit" class="btn btn-perigo">Remover</button>
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
