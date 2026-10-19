package mz.agrogest.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Cultivo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int campanhaId;
    private String cultura;
    private String variedade;
    private double hectares;
    private LocalDate plantio;

    public Cultivo() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCampanhaId() { return campanhaId; }
    public void setCampanhaId(int campanhaId) { this.campanhaId = campanhaId; }

    public String getCultura() { return cultura; }
    public void setCultura(String cultura) { this.cultura = cultura; }

    public String getVariedade() { return variedade; }
    public void setVariedade(String variedade) { this.variedade = variedade; }

    public double getHectares() { return hectares; }
    public void setHectares(double hectares) { this.hectares = hectares; }

    public LocalDate getPlantio() { return plantio; }
    public void setPlantio(LocalDate plantio) { this.plantio = plantio; }

    public String getPlantioFmt() {
        return plantio == null ? "" : plantio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
