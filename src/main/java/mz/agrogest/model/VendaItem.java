package mz.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;

/** Linha de uma venda. Guarda cópia dos dados do produto no momento da compra. */
public class VendaItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int produtoId;
    private String produtoNome;
    private String unidade;
    private double quantidade;
    private BigDecimal precoUnit;
    private BigDecimal subtotal;

    public VendaItem() {
    }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnit() { return precoUnit; }
    public void setPrecoUnit(BigDecimal precoUnit) { this.precoUnit = precoUnit; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
