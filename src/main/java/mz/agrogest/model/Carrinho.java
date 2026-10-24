package mz.agrogest.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


public class Carrinho implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Integer, Double> quantidades = new LinkedHashMap<>();

    public Carrinho() {
    }

    public synchronized void adicionar(int produtoId, double quantidade) {
        quantidades.merge(produtoId, quantidade, Double::sum);
    }
    
    public synchronized void atualizar(int produtoId, double quantidade) {
        if (!quantidades.containsKey(produtoId)) {
            return;
        }
        if (quantidade <= 0) {
            quantidades.remove(produtoId);
        } else {
            quantidades.put(produtoId, quantidade);
        }
    }

    public synchronized void remover(int produtoId) {
        quantidades.remove(produtoId);
    }

    public synchronized void esvaziar() {
        quantidades.clear();
    }

    public synchronized boolean isVazio() {
        return quantidades.isEmpty();
    }

    public synchronized int getTotalItens() {
        return quantidades.size();
    }

    public synchronized Map<Integer, Double> getQuantidades() {
        return new LinkedHashMap<>(quantidades);
    }
}