package mz.agrogest.service;

import java.time.LocalDate;
import java.util.*;

import mz.agrogest.model.*;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Serviço de produtos: criar, listar, repor stock, remover. */
public final class ProdutoService {
	private static final ProdutoService INSTANCIA = new ProdutoService();

	private ProdutoService() {
	}

	public Produto criar(String nome, Categoria categoria, int fornecedorID, BigDecimal preco,
			double quantidade, String unidade, String descricao, String fotoUrl, LocalDate validade) {
		// Validações com Validacao (D17)
		String n = Validacao.nomeCoisa("nome", nome);
		if (categoria == null) {
			throw new CampoException("categoria", "Escolha uma categoria.");
		}
		BigDecimal p = Validacao.precoPositivo("preco", preco);
		Validacao.quantidadeNaoNegativa("quantidade", quantidade);
		String un = Validacao.unidade("unidade", unidade);
		Validacao.validade("validade", validade);

		// Fornecedor existe?
		Fornecedor forn = FornecedorService.getInstance().porId(fornecedorID);
		if (forn == null) {
			throw new CampoException("fornecedorId", "O fornecedor escolhido não existe.");
		}

		// D13: compatibilidade categoria ↔ tipo de fornecedor
		Validacao.compatibilidadeCategoria(categoria, forn.getTipo(), forn.getNome());

		// Criação do objeto
		Produto prod = new Produto();
		prod.setNome(n);
		prod.setCategoria(categoria);
		prod.setFornecedorId(fornecedorID);
		prod.setPreco(p.setScale(2, RoundingMode.HALF_UP));
		prod.setQuantidade(quantidade);
		prod.setUnidade(un);
		prod.setDescricao(descricao == null ? "" : Validacao.normalizar(descricao));
		prod.setFotoUrl(fotoUrl == null ? "" : fotoUrl.trim());
		prod.setValidade(validade);

		// Guardar (com unicidade D14 dentro do lock)
		DataStore ds = DataStore.getInstance();
		synchronized (ds.getLock()) {
			// D14: unicidade nome + fornecedor
			for (Produto existente : ds.getProdutos()) {
				if (existente.getNome().equalsIgnoreCase(n)
						&& existente.getFornecedorId() == fornecedorID) {
					throw new CampoException("nome",
							"Já existe um produto «" + existente.getNome()
									+ "» deste fornecedor.");
				}
			}
			prod.setId(ds.novoIdProduto());
			ds.getProdutos().add(prod);
		}

		return prod;
	}

	// Leituras
	public List<Produto> listar() {
		DataStore ds = DataStore.getInstance();

		synchronized (ds.getLock()) {
			return new ArrayList<>(ds.getProdutos());
		}
	}

	public List<Produto> listarEmStock() {
		DataStore ds = DataStore.getInstance();
		synchronized (ds.getLock()) {
			List<Produto> resultado = new ArrayList<>();

			for (Produto p : ds.getProdutos()) {
				if (p.getQuantidade() > 0)
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
        Validacao.quantidadePositiva("quantidade", quantidade);

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

	// Auxiliary methods
	private Produto procurar(DataStore ds, int id) {
        for (Produto p : ds.getProdutos()) {
            if (p.getId() == id)
                return p;
        }
        return null;
    }
}
