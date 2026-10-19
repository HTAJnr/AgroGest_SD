package mz.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Colheita implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int cultivoId;
    private LocalDate data;
    private double kg;
    private BigDecimal precoKg;

    public Colheita() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCultivoId() { return cultivoId; }
    public void setCultivoId(int cultivoId) { this.cultivoId = cultivoId; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public double getKg() { return kg; }
    public void setKg(double kg) { this.kg = kg; }

    public BigDecimal getPrecoKg() { return precoKg; }
    public void setPrecoKg(BigDecimal precoKg) { this.precoKg = precoKg; }

    /** kg × preço por kg, 2 casas, HALF_UP (D3). Ex.: 3200 × 22 = 70400,00 */
    public BigDecimal getValorTotal() {
        if (precoKg == null) {
            return BigDecimal.ZERO.setScale(2);
        }
        return precoKg.multiply(BigDecimal.valueOf(kg)).setScale(2, RoundingMode.HALF_UP);
    }

    public String getDataFmt() {
        if (data == null) {
            return ""; 
        }
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
