package mz.agrogest.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Campanha implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private LocalDate inicio;
    private LocalDate fim;
    private boolean aberta;

    public Campanha() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }

    public LocalDate getFim() { return fim; }
    public void setFim(LocalDate fim) { this.fim = fim; }

    public boolean isAberta() { return aberta; }
    public void setAberta(boolean aberta) { this.aberta = aberta; }

    public String getInicioFmt() {
         if(inicio == null) 
         {
            return "";
         }
        return inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getFimFmt() {
        if(fim == null) {
            return "";
        }
        retutn fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
