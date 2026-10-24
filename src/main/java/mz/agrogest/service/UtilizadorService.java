package mz.agrogest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mz.agrogest.model.Perfil;
import mz.agrogest.model.Utilizador;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.CampoException;
import mz.agrogest.util.NegocioException;
import mz.agrogest.util.SenhaUtil;
import mz.agrogest.util.Validacao;

/**
 * Contas de utilizador: registo de clientes, autenticação e conta de admin.
 * Todas as leituras e escritas das listas vivas acontecem dentro do lock comum do DataStore.
 */
public final class UtilizadorService {

    public static final String ADMIN_EMAIL = "admin@agro.mz";   // D11
    private static final String ADMIN_PASSWORD = "admin234";    // D11 (só o hash é guardado)

    private static final UtilizadorService INSTANCE = new UtilizadorService();

    private final DataStore store = DataStore.getInstance();

    private UtilizadorService() {
    }

    public static UtilizadorService getInstance() {
        return INSTANCE;
    }

    /** Regista um cliente. Lança NegocioException se os dados forem inválidos ou o email já existir. */
    public Utilizador registarCliente(String nome, String email, String password,
                                      String telefone, String endereco) {
        String n = Validacao.nomePessoa("nome", nome);
        String e = Validacao.email("email", email);
        Validacao.password("password", password);
        String t = Validacao.telefoneMz("telefone", telefone);
        String d = Validacao.endereco("endereco", endereco);

        // BCrypt é lento de propósito (~100 ms). Não mexe em estado partilhado,
        // por isso fica FORA do lock para não bloquear vendas e outros registos.
        String hash = SenhaUtil.hash(password);

        synchronized (store.getLock()) {
            // verificar e inserir têm de ser uma só operação atómica, senão dois
            // pedidos simultâneos com o mesmo email passam ambos na verificação.
            if (procurarPorEmail(e) != null) {
                throw new CampoException("email", "Já existe uma conta com o email " + e + ".");
            }
            Utilizador u = criar(n, e, hash, Perfil.CLIENTE, t, d);
            store.getUtilizadores().add(u);
            return u;
        }
    }

    /** Devolve o utilizador se as credenciais forem válidas; null caso contrário. */
    public Utilizador autenticar(String email, String password) {
        String e = normalizar(email);
        if (e.isEmpty() || password == null) {
            return null;
        }
        Utilizador u;
        synchronized (store.getLock()) {
            u = procurarPorEmail(e);
        }
        if (u == null) {
            return null;
        }
        // verificação fora do lock (lenta e só lê o hash, que não muda depois de criado)
        return SenhaUtil.verificar(password, u.getPassHash()) ? u : null;
    }

    public Utilizador porId(int id) {
        synchronized (store.getLock()) {
            for (Utilizador u : store.getUtilizadores()) {
                if (u.getId() == id) {
                    return u;
                }
            }
            return null;
        }
    }

    /** Cópia da lista de clientes (perfil CLIENTE), por ordem de registo. */
    public List<Utilizador> listarClientes() {
        List<Utilizador> clientes = new ArrayList<>();
        synchronized (store.getLock()) {
            for (Utilizador u : store.getUtilizadores()) {
                if (u.getPerfil() == Perfil.CLIENTE) {
                    clientes.add(u);
                }
            }
        }
        return clientes;
    }

    /** Cria o admin se não existir. Idempotente: depois de restaurar o .ser, não faz nada. */
    public void garantirAdmin() {
        synchronized (store.getLock()) {
            if (procurarPorEmail(ADMIN_EMAIL) != null) {
                return;
            }
            Utilizador admin = criar("Administrador", ADMIN_EMAIL, SenhaUtil.hash(ADMIN_PASSWORD),
                    Perfil.ADMIN, "", "");
            store.getUtilizadores().add(admin);
        }
    }

    // ---- auxiliares (chamar sempre com o lock já tomado quando tocam nas listas) ----

    private Utilizador procurarPorEmail(String emailNormalizado) {
        for (Utilizador u : store.getUtilizadores()) {
            if (emailNormalizado.equals(normalizar(u.getEmail()))) {
                return u;
            }
        }
        return null;
    }

    private Utilizador criar(String nome, String email, String hash, Perfil perfil,
                             String telefone, String endereco) {
        Utilizador u = new Utilizador();
        u.setId(store.novoIdUtilizador());
        u.setNome(nome);
        u.setEmail(email);
        u.setPassHash(hash);
        u.setPerfil(perfil);
        u.setAtivo(true);   // D1
        u.setTelefone(telefone);
        u.setEndereco(endereco);
        return u;
    }

    private static String normalizar(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
