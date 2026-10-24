package mz.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class LinhaCarrinho implements Serializable {
    private static final long serialVersionUID = 1L;

    private Produto produto;
    private double quantidade;
    private BigDecimal subtotal;

    public LinhaCarrinho() {
    }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public double getQuantidade() { return quantidade; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}