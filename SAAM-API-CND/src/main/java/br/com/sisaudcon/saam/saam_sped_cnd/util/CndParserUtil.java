package br.com.sisaudcon.saam.saam_sped_cnd.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CndParserUtil {

    public static String extrairTextoBase64(String base64Arquivo) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Arquivo);
            String texto;
            try (PDDocument document = PDDocument.load(new ByteArrayInputStream(decodedBytes))) {
                texto = new PDFTextStripper().getText(document);
            }
            return texto.replace('\u00A0', ' ')
                    .replaceAll("\\s+", " ");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair texto do PDF da CND", e);
        }
    }

    public static Map<String, String> extrairDadosCndBase64(String base64Arquivo) {
        Map<String, String> dados = new HashMap<>();
        String texto = extrairTextoBase64(base64Arquivo);

        int iSit = texto.indexOf("CERTIDÃO");
        int iNome = texto.indexOf("DE DÉBITOS");
        if (iSit >= 0 && iNome > iSit) {
            dados.put("situacao", texto.substring(iSit, iNome).trim());
        }

        dados.put("dataEmissao", extrairEntre(texto, "Emitida às ", " <"));

        dados.put("dataValidade", extrairEntre(texto, "Válida até ", "."));

        dados.put(
                "codigoControle",
                extrairEntre(texto, "Código de controle da certidão: ", " ")
        );

        return dados;
    }

    private static String extrairEntre(String texto, String inicio, String fim) {
        int i0 = texto.indexOf(inicio);
        if (i0 < 0) return null;
        int start = i0 + inicio.length();
        int i1 = texto.indexOf(fim, start);
        if (i1 < 0) {
            return texto.substring(start).trim();
        }
        return texto.substring(start, i1).trim();
    }
}