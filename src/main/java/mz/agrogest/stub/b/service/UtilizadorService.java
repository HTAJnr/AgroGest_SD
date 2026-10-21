package mz.agrogest.stub.b.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import mz.agrogest.model.Perfil;
import mz.agrogest.model.Utilizador;
import mz.agrogest.util.NegocioException;

// STUB da Pessoa B (temporário). O real é da Pessoa A (com BCrypt).
// Aqui a password fica em texto simples SÓ para testar. Na integração vai o real.
// Contas de teste: admin@agro.mz / admin234  e  joao@teste.mz / 1234
public final class UtilizadorService {

    private static final UtilizadorService INSTANCIA = new UtilizadorService();
    private final List<Utilizador> utilizadores = new ArrayList<>();
    private final AtomicInteger seq = new AtomicInteger();

    private UtilizadorService() {
        criar("Administrador", "admin@agro.mz", "admin234", "", "", Perfil.ADMIN);
        criar("João Silva", "joao@teste.mz", "1234", "840000000", "Av. Eduardo Mondlane, Nampula", Perfil.CLIENTE);
    }

    private Utilizador criar(String nome, String email, String password, String telefone,
                             String endereco, Perfil perfil) {
        Utilizador u = new Utilizador();
        u.setId(seq.incrementAndGet());
        u.setNome(nome);
        u.setEmail(email);
        u.setPassHash(password);
        u.setTelefone(telefone);
        u.setEndereco(endereco);
        u.setPerfil(perfil);
        u.setAtivo(true);
        utilizadores.add(u);
        return u;
    }

    public static UtilizadorService getInstance() { return INSTANCIA; }

    public synchronized Utilizador registarCliente(String nome, String email, String password,
                                                   String telefone, String endereco) {
        if (nome == null || nome.isBlank()) throw new NegocioException("O nome é obrigatório.");
        if (email == null || email.isBlank()) throw new NegocioException("O email é obrigatório.");
        if (password == null || password.isEmpty()) throw new NegocioException("A palavra-passe é obrigatória.");
        String e = email.trim().toLowerCase();
        for (Utilizador u : utilizadores) {
            if (u.getEmail().equals(e)) throw new NegocioException("Já existe uma conta com esse email.");
        }
        return criar(nome.trim(), e, password, telefone, endereco, Perfil.CLIENTE);
    }

    public synchronized Utilizador autenticar(String email, String password) {
        if (email == null || password == null) return null;
        String e = email.trim().toLowerCase();
        for (Utilizador u : utilizadores) {
            if (u.getEmail().equals(e) && u.getPassHash().equals(password)) return u;
        }
        return null;
    }

    public synchronized Utilizador porId(int id) {
        for (Utilizador u : utilizadores) {
            if (u.getId() == id) return u;
        }
        return null;
    }
}
