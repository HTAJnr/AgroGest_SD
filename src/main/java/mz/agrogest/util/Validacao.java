package mz.agrogest.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import mz.agrogest.model.Categoria;
import mz.agrogest.model.TipoFornecedor;

/**
 * Métodos estáticos de validação (D17). Lançam {@link CampoException} com o
 * {@code name} do input e uma mensagem em português de Portugal.
 * <p>Texto normalizado: {@code trim()} e espaços internos colapsados.</p>
 */
public final class Validacao {

    private Validacao() {
    }

    // ---- constantes públicas (D16, D17) ----

    /** Unidades permitidas para produtos. */
    public static final List<String> UNIDADES =
            List.of("kg", "saco", "litro", "tonelada", "unidade");

    /** As 11 províncias de Moçambique. */
    public static final List<String> PROVINCIAS = List.of(
            "Maputo Cidade", "Maputo", "Gaza", "Inhambane",
            "Sofala", "Manica", "Tete", "Zambézia",
            "Nampula", "Cabo Delgado", "Niassa");

    // ---- padrões internos ----

    // Nome de pessoa: letras (com acentos), espaços, apóstrofo, hífen, ponto
    private static final Pattern NOME_PESSOA_PAT =
            Pattern.compile("^[\\p{L} '\\-.]+$", Pattern.UNICODE_CHARACTER_CLASS);

    // Nome de coisa: pelo menos uma letra
    private static final Pattern PELO_MENOS_LETRA =
            Pattern.compile("\\p{L}", Pattern.UNICODE_CHARACTER_CLASS);

    // Email simples
    private static final Pattern EMAIL_PAT =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$");

    // Telemóvel MZ: 8[2-7] seguido de 7 dígitos (9 dígitos no total)
    private static final Pattern TELEMOVEL_MZ = Pattern.compile("^8[2-7]\\d{7}$");

    // Fixo MZ: 2 seguido de 7 dígitos (8 dígitos no total)
    private static final Pattern FIXO_MZ = Pattern.compile("^2\\d{7}$");

    // Teto de preço
    private static final BigDecimal PRECO_TETO = new BigDecimal("10000000");

    // Teto de quantidade para reposição
    private static final double QTD_TETO = 1_000_000;

    // ---- normalização ----

    /** Trim + colapsar espaços internos. */
    public static String normalizar(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ");
    }

    // ---- validações de texto ----

    /**
     * Nome de pessoa: 3–80 caracteres, só letras (com acentos), espaços, {@code '}, {@code -}, {@code .}.
     * Não pode ser só dígitos.
     */
    public static String nomePessoa(String campo, String valor) {
        String v = normalizar(valor);
        if (v.isEmpty()) {
            throw new CampoException(campo, "O nome é obrigatório.");
        }
        if (v.length() < 3 || v.length() > 80) {
            throw new CampoException(campo, "O nome deve ter entre 3 e 80 caracteres.");
        }
        if (!NOME_PESSOA_PAT.matcher(v).matches()) {
            throw new CampoException(campo,
                    "O nome só pode conter letras, espaços, apóstrofo, hífen e ponto.");
        }
        return v;
    }

    /**
     * Nome de coisa (produto, fornecedor, campanha, cultura, variedade):
     * 3–80 caracteres, pelo menos uma letra.
     */
    public static String nomeCoisa(String campo, String valor) {
        String v = normalizar(valor);
        if (v.isEmpty()) {
            throw new CampoException(campo, "O nome é obrigatório.");
        }
        if (v.length() < 3 || v.length() > 80) {
            throw new CampoException(campo, "O nome deve ter entre 3 e 80 caracteres.");
        }
        if (!PELO_MENOS_LETRA.matcher(v).find()) {
            throw new CampoException(campo, "O nome tem de conter pelo menos uma letra.");
        }
        return v;
    }

    /** Email: formato válido, ≤ 120 caracteres. Devolvido em minúsculas. */
    public static String email(String campo, String valor) {
        String v = normalizar(valor).toLowerCase(Locale.ROOT);
        if (v.isEmpty()) {
            throw new CampoException(campo, "O email é obrigatório.");
        }
        if (v.length() > 120) {
            throw new CampoException(campo, "O email não pode ter mais de 120 caracteres.");
        }
        if (!EMAIL_PAT.matcher(v).matches()) {
            throw new CampoException(campo, "Introduza um email válido.");
        }
        return v;
    }

    /**
     * Telefone moçambicano (D17). Remove espaços, hífenes e parêntesis; aceita prefixo
     * {@code +258} ou {@code 00258} opcional. Telemóvel: {@code 8[2-7]XXXXXXX} (9 dígitos).
     * Fixo: {@code 2XXXXXXX} (8 dígitos). Devolve normalizado: {@code +258 XX XXX XXXX}.
     */
    public static String telefoneMz(String campo, String valor) {
        String v = normalizar(valor);
        if (v.isEmpty()) {
            throw new CampoException(campo, "O telefone é obrigatório.");
        }
        // Remover formatação
        String digitos = v.replaceAll("[\\s()\\-]", "");

        // Remover prefixo internacional
        if (digitos.startsWith("+258")) {
            digitos = digitos.substring(4);
        } else if (digitos.startsWith("00258")) {
            digitos = digitos.substring(5);
        }

        if (TELEMOVEL_MZ.matcher(digitos).matches()) {
            // +258 8X XXX XXXX
            return "+258 " + digitos.substring(0, 2) + " "
                    + digitos.substring(2, 5) + " " + digitos.substring(5);
        }
        if (FIXO_MZ.matcher(digitos).matches()) {
            // +258 2X XXX XXXX
            return "+258 " + digitos.substring(0, 2) + " "
                    + digitos.substring(2, 5) + " " + digitos.substring(5);
        }
        throw new CampoException(campo,
                "Introduza um número moçambicano válido, por exemplo 84 123 4567.");
    }

    /** Contacto de fornecedor: aceita telefone MZ ou email. */
    public static String contactoFornecedor(String campo, String valor) {
        String v = normalizar(valor);
        if (v.isEmpty()) {
            throw new CampoException(campo, "O contacto é obrigatório.");
        }
        // Tentar como telefone
        try {
            return telefoneMz(campo, valor);
        } catch (CampoException ignorada) {
            // não é telefone: tentar como email
        }
        // Tentar como email
        try {
            return email(campo, valor);
        } catch (CampoException ignorada) {
            // também não é email
        }
        throw new CampoException(campo,
                "Introduza um número moçambicano válido (ex.: 84 123 4567) ou um email.");
    }

    /** Endereço: 5–150 caracteres. */
    public static String endereco(String campo, String valor) {
        String v = normalizar(valor);
        if (v.isEmpty()) {
            throw new CampoException(campo, "O endereço é obrigatório.");
        }
        if (v.length() < 5 || v.length() > 150) {
            throw new CampoException(campo, "O endereço deve ter entre 5 e 150 caracteres.");
        }
        return v;
    }

    /** Palavra-passe: 4–72 caracteres (BCrypt limita a 72). */
    public static String password(String campo, String valor) {
        if (valor == null || valor.isEmpty()) {
            throw new CampoException(campo, "A palavra-passe é obrigatória.");
        }
        if (valor.length() < 4) {
            throw new CampoException(campo,
                    "A palavra-passe deve ter pelo menos 4 caracteres.");
        }
        if (valor.length() > 72) {
            throw new CampoException(campo,
                    "A palavra-passe não pode ter mais de 72 caracteres.");
        }
        return valor;
    }

    // ---- validações numéricas ----

    /** Preço positivo, ≤ 2 casas decimais, teto 10 000 000. */
    public static BigDecimal precoPositivo(String campo, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CampoException(campo, "O preço tem de ser maior que zero.");
        }
        if (valor.scale() > 2) {
            throw new CampoException(campo, "O preço não pode ter mais de 2 casas decimais.");
        }
        if (valor.compareTo(PRECO_TETO) > 0) {
            throw new CampoException(campo,
                    "O preço não pode ser superior a 10 000 000.");
        }
        return valor;
    }

    /** Quantidade ≥ 0 e ≤ 1 000 000. */
    public static double quantidadeNaoNegativa(String campo, double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0) {
            throw new CampoException(campo, "A quantidade não pode ser negativa.");
        }
        if (valor > QTD_TETO) {
            throw new CampoException(campo,
                    "A quantidade não pode ser superior a 1 000 000.");
        }
        return valor;
    }

    /** Quantidade de reposição: > 0 e ≤ 1 000 000. */
    public static double quantidadePositiva(String campo, double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor <= 0) {
            throw new CampoException(campo,
                    "A quantidade tem de ser maior que zero.");
        }
        if (valor > QTD_TETO) {
            throw new CampoException(campo,
                    "A quantidade não pode ser superior a 1 000 000.");
        }
        return valor;
    }

    /** Hectares > 0. */
    public static double hectares(String campo, double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor <= 0) {
            throw new CampoException(campo, "Os hectares têm de ser maiores que zero.");
        }
        return valor;
    }

    /** Kg > 0. */
    public static double kg(String campo, double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor <= 0) {
            throw new CampoException(campo, "Os quilogramas têm de ser maiores que zero.");
        }
        return valor;
    }

    /** Preço por kg > 0. */
    public static BigDecimal precoKg(String campo, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CampoException(campo,
                    "O preço por quilograma tem de ser maior que zero.");
        }
        return valor;
    }

    // ---- validações de domínio ----

    /** Validade não anterior a hoje (D15). Aceita null (validade opcional). */
    public static LocalDate validade(String campo, LocalDate valor) {
        if (valor != null && valor.isBefore(LocalDate.now())) {
            throw new CampoException(campo,
                    "A data de validade não pode ser anterior a hoje.");
        }
        return valor;
    }

    /** Unidade dentro das permitidas (D16). */
    public static String unidade(String campo, String valor) {
        String v = normalizar(valor).toLowerCase(Locale.ROOT);
        if (v.isEmpty()) {
            throw new CampoException(campo, "A unidade é obrigatória.");
        }
        if (!UNIDADES.contains(v)) {
            throw new CampoException(campo,
                    "Unidade inválida. Escolha uma de: " + String.join(", ", UNIDADES) + ".");
        }
        return v;
    }

    /** Província dentro das 11 de Moçambique. */
    public static String provincia(String campo, String valor) {
        String v = normalizar(valor);
        if (v.isEmpty()) {
            throw new CampoException(campo, "A província é obrigatória.");
        }
        if (!PROVINCIAS.contains(v)) {
            throw new CampoException(campo,
                    "Província inválida. Escolha uma da lista.");
        }
        return v;
    }

    // ---- compatibilidade categoria ↔ tipo de fornecedor (D13) ----

    /**
     * Devolve o {@link TipoFornecedor} compatível com a {@link Categoria}.
     * <p>D13: SEMENTE→SEMENTES, FERTILIZANTE→FERTILIZANTES,
     * MAQUINARIA→MAQUINARIA, FERRAMENTA→MAQUINARIA.</p>
     */
    public static TipoFornecedor tipoEsperado(Categoria categoria) {
        if (categoria == null) return null;
        switch (categoria) {
            case SEMENTE:      return TipoFornecedor.SEMENTES;
            case FERTILIZANTE: return TipoFornecedor.FERTILIZANTES;
            case MAQUINARIA:   return TipoFornecedor.MAQUINARIA;
            case FERRAMENTA:   return TipoFornecedor.MAQUINARIA;
            default:           return null;
        }
    }

    /**
     * Verifica se o fornecedor é compatível com a categoria do produto (D13).
     *
     * @throws CampoException se incompatível
     */
    public static void compatibilidadeCategoria(Categoria categoria,
                                                 TipoFornecedor tipoFornecedor,
                                                 String nomeFornecedor) {
        TipoFornecedor esperado = tipoEsperado(categoria);
        if (esperado != null && tipoFornecedor != esperado) {
            throw new CampoException("fornecedorId",
                    "O fornecedor «" + nomeFornecedor + "» é do tipo "
                            + tipoFornecedor + " e não fornece produtos da categoria "
                            + categoria + ".");
        }
    }
}
