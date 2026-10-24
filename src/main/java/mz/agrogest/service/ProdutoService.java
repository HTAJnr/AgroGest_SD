package mz.agrogest.service;

import java.time.LocalDate;
import java.util.*;

import mz.agrogest.model.*;
import mz.agrogest.repo.DataStore;
import mz.agrogest.service.FornecedorService;
import mz.agrogest.util.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

//Gerende dos produtos
public class ProdutoService {
	//Constant
	private static final ProdutoService INSTANCIA = new ProdutoService();
	
	//Constructor
	private ProdutoService() {
		
	}
	
	//Methods
	public Produto criar(String nome, Categoria categoria, int fornecedorID, BigDecimal preco, 
			double quantidade, String unidade, String descricao, String fotoUrl, LocalDate validade) {
		//Validacoes
		if(nome == null || nome.isBlank())
			throw new NegocioException("O nome do produto é obrigatório.");
		if(categoria == null)
			throw new NegocioException("Escolha uma categoria.");
		if(preco == null || preco.compareTo(BigDecimal.ZERO) <= 0)
			throw new NegocioException("O preço tem de ser  maior que zero");
		if(quantidade < 0)
			throw new NegocioException("A quantidade não pode ser negativa.");
		if(unidade == null || unidade.isBlank())
			throw new NegocioException("A unidade é obrigatória(ex:kg, saco, unidade");
		if(FornecedorService.getInstance().porId(fornecedorID) == null)
			throw new NegocioException("O fornecedor escolhido não existe.");
		
		//Criacao do do objeto
		Produto p = new Produto();
		p.setNome(nome.trim());
		p.setCategoria(categoria);
		p.setFornecedorId(fornecedorID);
		p.setPreco(preco.setScale(2, RoundingMode.HALF_UP));
		p.setQuantidade(quantidade);
		p.setUnidade(unidade.trim());
		p.setDescricao(descricao == null ? "" : descricao.trim());
		p.setFotoUrl(fotoUrl == null ? "" : fotoUrl.trim());
		p.setValidade(validade);
		
		//Guardar
		DataStore ds = DataStore.getInstance();
		synchronized(ds.getLock()) {
			p.setId(ds.novoIdProduto());
			ds.getProdutos().add(p);
		}
		
		return p;
		
	}
	
	//Leituras
	public List<Produto> listar() {
		DataStore ds = DataStore.getInstance();
		
		synchronized(ds.getLock()) {
			return new ArrayList<>(ds.getProdutos());
		}
	}
	
	public List<Produto> listarEmStock() {
		DataStore ds = DataStore.getInstance();
		synchronized(ds.getLock()) {
			List<Produto> resultado = new ArrayList<>();
			
			for(Produto p: ds.getProdutos()) {
				if(p.getQuantidade() > 0)
					resultado.add(p);
			}
			
			return resultado;
		}
	}
	
	public List<Produto> listarPorCategoria(Categoria categoria) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            List<Produto> resultado = new ArrayList<>();
            for (Produto p : ds.getProdutos()) {
                if (p.getCategoria() == categoria)
                    resultado.add(p);
            }
            return resultado;
        }
    }
	
	public Produto porId(int id) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            return procurar(ds, id);
        }
    }
	
	public boolean existeComFornecedor(int fornecedorId) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            for (Produto p : ds.getProdutos()) {
                if (p.getFornecedorId() == fornecedorId)
                    return true;
            }
            return false;
        }
    }
	
	public void reporStock(int id, double quantidade) {
        if (quantidade <= 0)
            throw new NegocioException("A quantidade a repor tem de ser maior que zero.");
        
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            Produto p = procurar(ds, id);
            if (p == null)
                throw new NegocioException("O produto não existe.");
            
            p.setQuantidade(p.getQuantidade() + quantidade);
        }
    }
	
	public void remover(int id) {
        DataStore ds = DataStore.getInstance();
        synchronized (ds.getLock()) {
            Produto p = procurar(ds, id);
            if (p == null) 
                throw new NegocioException("O produto não existe.");
            
            ds.getProdutos().remove(p);
        }
    }

	public static ProdutoService getInstance() {
		return INSTANCIA;
	}
	
	
	//Auxiliary methods
	private Produto procurar(DataStore ds, int id) {
        for (Produto p : ds.getProdutos()) {
            if (p.getId() == id)
                return p;
        }
        return null;
    }
}
