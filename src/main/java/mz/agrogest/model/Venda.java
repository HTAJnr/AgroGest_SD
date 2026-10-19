package mz.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Venda com cópia dos dados do cliente: a factura não muda se o cliente mudar depois. */
public class Venda implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String numeroFactura;
    private LocalDateTime data;
    private int clienteId;
    private String clienteNome;
    private String clienteEmail;
    private String clienteTelefone;
    private String clienteEndereco;
    private List<VendaItem> itens = new ArrayList<>();
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;

    public Venda() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }

    public String getClienteTelefone() { return clienteTelefone; }
    public void setClienteTelefone(String clienteTelefone) { this.clienteTelefone = clienteTelefone; }

    public String getClienteEndereco() { return clienteEndereco; }
    public void setClienteEndereco(String clienteEndereco) { this.clienteEndereco = clienteEndereco; }

    public List<VendaItem> getItens() { return itens; }
    public void setItens(List<VendaItem> itens) { this.itens = itens; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    /** dd/MM/yyyy HH:mm, ou "" se não houver data. */
    public String getDataFmt() {
        if (data == null) {
            return "";
         } 
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
