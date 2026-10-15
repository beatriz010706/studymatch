package pt.studymatch.model.perfil;

/**
 * Uma linha da explicação de uma competência ("Porquê este valor?").
 * tipo = "NOTA" (usa fonte, nota e peso) ou "AUTO_DECLARACAO" (usa nivel e peso).
 * Os campos que não se aplicam ficam a null.
 */
public record ItemExplicacao(String tipo, String fonte, Double nota, Integer nivel, double peso) {
}
