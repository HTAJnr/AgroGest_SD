package mz.agrogest.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class TokenFormFilterTest {

    @Test
    @DisplayName("TokenStore limita a 50 tokens e descarta o mais antigo (FIFO)")
    void testFifoLimit() {
        TokenFormFilter.TokenStore store = new TokenFormFilter.TokenStore();
        List<String> gerados = new ArrayList<>();

        for (int i = 0; i < 55; i++) {
            gerados.add(store.gerar());
        }

        assertEquals(50, store.total());

        // Os primeiros 5 devem ter sido descartados
        for (int i = 0; i < 5; i++) {
            assertFalse(store.contem(gerados.get(i)), "Token " + i + " deveria ter sido descartado");
            assertFalse(store.consumir(gerados.get(i)));
        }

        // Os últimos 50 devem estar presentes
        for (int i = 5; i < 55; i++) {
            assertTrue(store.contem(gerados.get(i)), "Token " + i + " deveria estar presente");
        }
    }

    @Test
    @DisplayName("Consumir token remove-o atomicamente (segunda tentativa falha)")
    void testConsumirToken() {
        TokenFormFilter.TokenStore store = new TokenFormFilter.TokenStore();
        String t = store.gerar();

        assertTrue(store.consumir(t));
        assertFalse(store.consumir(t));
        assertFalse(store.consumir(null));
        assertFalse(store.consumir(""));
        assertFalse(store.consumir("token-inexistente"));
    }

    @Test
    @DisplayName("Concorrência: múltiplos threads a submeter o mesmo token (só um passa)")
    void testConcorrenciaMesmoToken() throws Exception {
        TokenFormFilter.TokenStore store = new TokenFormFilter.TokenStore();
        String token = store.gerar();

        int numThreads = 20;
        ExecutorService exec = Executors.newFixedThreadPool(numThreads);
        CyclicBarrier barrier = new CyclicBarrier(numThreads);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            futures.add(exec.submit(() -> {
                barrier.await();
                return store.consumir(token);
            }));
        }

        int sucessos = 0;
        int falhas = 0;
        for (Future<Boolean> f : futures) {
            if (f.get()) {
                sucessos++;
            } else {
                falhas++;
            }
        }

        exec.shutdown();
        assertEquals(1, sucessos, "Apenas um pedido pode consumir o token");
        assertEquals(numThreads - 1, falhas, "Todos os outros pedidos concorrentes devem falhar");
    }

    @Test
    @DisplayName("Concorrência: múltiplos threads com tokens distintos de abas diferentes")
    void testConcorrenciaTokensDistintos() throws Exception {
        TokenFormFilter.TokenStore store = new TokenFormFilter.TokenStore();
        int numThreads = 15;
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            tokens.add(store.gerar());
        }

        ExecutorService exec = Executors.newFixedThreadPool(numThreads);
        CyclicBarrier barrier = new CyclicBarrier(numThreads);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final String t = tokens.get(i);
            futures.add(exec.submit(() -> {
                barrier.await();
                return store.consumir(t);
            }));
        }

        int sucessos = 0;
        for (Future<Boolean> f : futures) {
            if (f.get()) {
                sucessos++;
            }
        }

        exec.shutdown();
        assertEquals(numThreads, sucessos, "Cada aba/token distinto deve ser consumido com sucesso");
        assertEquals(0, store.total());
    }

    // ==========================================
    // Testes de integração do TokenFormFilter
    // ==========================================

    @Test
    @DisplayName("GET gera UUID, guarda na sessão e coloca tokenForm no request")
    void testGetGeraToken() throws ServletException, IOException {
        TokenFormFilter filter = new TokenFormFilter();
        SimuladorSessao sessao = new SimuladorSessao();
        SimuladorRequest req = new SimuladorRequest("GET", "/catalogo", sessao);
        SimuladorResponse resp = new SimuladorResponse();
        AtomicBoolean chainExecutada = new AtomicBoolean(false);

        filter.doFilter(req.proxy(), resp.proxy(), (q, r) -> chainExecutada.set(true));

        assertTrue(chainExecutada.get());
        String token = (String) req.attributes.get("tokenForm");
        assertNotNull(token);
        assertFalse(token.isBlank());

        TokenFormFilter.TokenStore store = (TokenFormFilter.TokenStore) sessao.attributes.get(TokenFormFilter.ATTR_SESSAO);
        assertNotNull(store);
        assertTrue(store.contem(token));
    }

    @Test
    @DisplayName("POST sem token é recusado, não chama chain e redireciona com flashErro")
    void testPostSemTokenRecusado() throws ServletException, IOException {
        TokenFormFilter filter = new TokenFormFilter();
        SimuladorSessao sessao = new SimuladorSessao();
        SimuladorRequest req = new SimuladorRequest("POST", "/compra", sessao);
        req.headers.put("Referer", "http://localhost:8080/agrogest/compra");
        SimuladorResponse resp = new SimuladorResponse();
        AtomicBoolean chainExecutada = new AtomicBoolean(false);

        filter.doFilter(req.proxy(), resp.proxy(), (q, r) -> chainExecutada.set(true));

        assertFalse(chainExecutada.get(), "Servlet não deve ser executado");
        assertEquals(TokenFormFilter.MENSAGEM_ERRO, sessao.attributes.get("flashErro"));
        assertEquals("http://localhost:8080/agrogest/compra", resp.redirectUrl);
    }

    @Test
    @DisplayName("POST com token válido é aceite, consumido e gera novo token para forward")
    void testPostComTokenValido() throws ServletException, IOException {
        TokenFormFilter filter = new TokenFormFilter();
        SimuladorSessao sessao = new SimuladorSessao();

        // Simula GET anterior
        SimuladorRequest getReq = new SimuladorRequest("GET", "/compra", sessao);
        SimuladorResponse getResp = new SimuladorResponse();
        filter.doFilter(getReq.proxy(), getResp.proxy(), (q, r) -> {});
        String tokenGerado = (String) getReq.attributes.get("tokenForm");

        // Submete POST com o token
        SimuladorRequest postReq = new SimuladorRequest("POST", "/compra", sessao);
        postReq.parameters.put("_t", tokenGerado);
        SimuladorResponse postResp = new SimuladorResponse();
        AtomicBoolean chainExecutada = new AtomicBoolean(false);

        filter.doFilter(postReq.proxy(), postResp.proxy(), (q, r) -> chainExecutada.set(true));

        assertTrue(chainExecutada.get(), "Servlet deve ser executado");
        // O token original foi consumido
        TokenFormFilter.TokenStore store = (TokenFormFilter.TokenStore) sessao.attributes.get(TokenFormFilter.ATTR_SESSAO);
        assertFalse(store.contem(tokenGerado));

        // Um novo token foi colocado no request para o caso de forward
        String novoToken = (String) postReq.attributes.get("tokenForm");
        assertNotNull(novoToken);
        assertTrue(store.contem(novoToken));
    }

    @Test
    @DisplayName("Duplo envio: segundo POST com o mesmo token é recusado")
    void testDuploEnvioRecusado() throws ServletException, IOException {
        TokenFormFilter filter = new TokenFormFilter();
        SimuladorSessao sessao = new SimuladorSessao();

        // GET inicial
        SimuladorRequest getReq = new SimuladorRequest("GET", "/compra", sessao);
        SimuladorResponse getResp = new SimuladorResponse();
        filter.doFilter(getReq.proxy(), getResp.proxy(), (q, r) -> {});
        String token = (String) getReq.attributes.get("tokenForm");

        // 1.º POST com token -> Sucesso
        SimuladorRequest post1 = new SimuladorRequest("POST", "/compra", sessao);
        post1.parameters.put("_t", token);
        SimuladorResponse resp1 = new SimuladorResponse();
        AtomicBoolean chain1 = new AtomicBoolean(false);
        filter.doFilter(post1.proxy(), resp1.proxy(), (q, r) -> chain1.set(true));
        assertTrue(chain1.get(), "Primeiro envio deve passar");

        // 2.º POST com o mesmo token (duplo clique) -> Recusado
        SimuladorRequest post2 = new SimuladorRequest("POST", "/compra", sessao);
        post2.parameters.put("_t", token);
        post2.headers.put("Referer", "http://localhost:8080/agrogest/compra");
        SimuladorResponse resp2 = new SimuladorResponse();
        AtomicBoolean chain2 = new AtomicBoolean(false);
        filter.doFilter(post2.proxy(), resp2.proxy(), (q, r) -> chain2.set(true));

        assertFalse(chain2.get(), "Segundo envio com mesmo token não pode executar o servlet");
        assertEquals(TokenFormFilter.MENSAGEM_ERRO, sessao.attributes.get("flashErro"));
        assertEquals("http://localhost:8080/agrogest/compra", resp2.redirectUrl);
    }

    @Test
    @DisplayName("/logout passa direto sem exigir token")
    void testLogoutExcecao() throws ServletException, IOException {
        TokenFormFilter filter = new TokenFormFilter();
        SimuladorSessao sessao = new SimuladorSessao();
        SimuladorRequest req = new SimuladorRequest("POST", "/logout", sessao);
        SimuladorResponse resp = new SimuladorResponse();
        AtomicBoolean chainExecutada = new AtomicBoolean(false);

        filter.doFilter(req.proxy(), resp.proxy(), (q, r) -> chainExecutada.set(true));

        assertTrue(chainExecutada.get(), "/logout deve passar direto");
    }

    @Test
    @DisplayName("Recursos estáticos passam sem gerar sessão ou token")
    void testRecursoEstatico() throws ServletException, IOException {
        TokenFormFilter filter = new TokenFormFilter();
        SimuladorRequest req = new SimuladorRequest("GET", "/css/estilo.css", null);
        SimuladorResponse resp = new SimuladorResponse();
        AtomicBoolean chainExecutada = new AtomicBoolean(false);

        filter.doFilter(req.proxy(), resp.proxy(), (q, r) -> chainExecutada.set(true));

        assertTrue(chainExecutada.get());
        assertEquals(0, req.attributes.size());
    }

    // ==========================================
    // Classes de simulação para testes
    // ==========================================

    static class SimuladorSessao {
        final Map<String, Object> attributes = Collections.synchronizedMap(new HashMap<>());

        HttpSession proxy() {
            return (HttpSession) Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class<?>[]{HttpSession.class},
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if ("getAttribute".equals(name)) {
                            return attributes.get(args[0]);
                        }
                        if ("setAttribute".equals(name)) {
                            attributes.put((String) args[0], args[1]);
                            return null;
                        }
                        if ("removeAttribute".equals(name)) {
                            return attributes.remove(args[0]);
                        }
                        if ("getId".equals(name)) {
                            return "SESSION-TEST-123";
                        }
                        return null;
                    });
        }
    }

    static class SimuladorRequest {
        final String method;
        final String path;
        SimuladorSessao sessao;
        final Map<String, Object> attributes = new HashMap<>();
        final Map<String, String> parameters = new HashMap<>();
        final Map<String, String> headers = new HashMap<>();
        String characterEncoding;

        SimuladorRequest(String method, String path, SimuladorSessao sessao) {
            this.method = method;
            this.path = path;
            this.sessao = sessao;
        }

        HttpServletRequest proxy() {
            return (HttpServletRequest) Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class<?>[]{HttpServletRequest.class},
                    (proxy, method1, args) -> {
                        String name = method1.getName();
                        if ("getMethod".equals(name)) return method;
                        if ("getServletPath".equals(name)) return path;
                        if ("getPathInfo".equals(name)) return null;
                        if ("getContextPath".equals(name)) return "/agrogest";
                        if ("getServerName".equals(name)) return "localhost";
                        if ("getCharacterEncoding".equals(name)) return characterEncoding;
                        if ("setCharacterEncoding".equals(name)) {
                            characterEncoding = (String) args[0];
                            return null;
                        }
                        if ("getParameter".equals(name)) return parameters.get(args[0]);
                        if ("getHeader".equals(name)) return headers.get(args[0]);
                        if ("getAttribute".equals(name)) return attributes.get(args[0]);
                        if ("setAttribute".equals(name)) {
                            attributes.put((String) args[0], args[1]);
                            return null;
                        }
                        if ("getSession".equals(name)) {
                            boolean create = args == null || args.length == 0 || (Boolean) args[0];
                            if (sessao == null && create) {
                                sessao = new SimuladorSessao();
                            }
                            return sessao == null ? null : sessao.proxy();
                        }
                        return null;
                    });
        }
    }

    static class SimuladorResponse {
        String redirectUrl;

        HttpServletResponse proxy() {
            return (HttpServletResponse) Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class<?>[]{HttpServletResponse.class},
                    (proxy, method, args) -> {
                        if ("sendRedirect".equals(method.getName())) {
                            redirectUrl = (String) args[0];
                            return null;
                        }
                        return null;
                    });
        }
    }
}
