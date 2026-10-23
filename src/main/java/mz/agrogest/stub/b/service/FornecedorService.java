package mz.agrogest.stub.b.service;

import java.util.ArrayList;
import java.util.List;

import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.TipoFornecedor;

// STUB da Pessoa B (temporário). O real é da Pessoa C.
// Só listar() e porId(): é o que a Pessoa B usa.
public final class FornecedorService {

    private static final FornecedorService INSTANCIA = new FornecedorService();
    private final List<Fornecedor> fornecedores = new ArrayList<>();

    private FornecedorService() {
        fornecedores.add(novo(1, "Sementes do Sul, Lda", TipoFornecedor.SEMENTES, "840000001", "Maputo"));
        fornecedores.add(novo(2, "AgroMáquinas Nampula", TipoFornecedor.MAQUINARIA, "840000002", "Nampula"));
        fornecedores.add(novo(3, "FertiMoz", TipoFornecedor.FERTILIZANTES, "840000003", "Sofala"));
    }

    private Fornecedor novo(int id, String nome, TipoFornecedor tipo, String contacto, String provincia) {
        Fornecedor f = new Fornecedor();
        f.setId(id);
        f.setNome(nome);
        f.setTipo(tipo);
        f.setContacto(contacto);
        f.setProvincia(provincia);
        return f;
    }

    public static FornecedorService getInstance() { return INSTANCIA; }

    public List<Fornecedor> listar() { return new ArrayList<>(fornecedores); }

    public Fornecedor porId(int id) {
        for (Fornecedor f : fornecedores) {
            if (f.getId() == id) return f;
        }
        return null;
    }
}
