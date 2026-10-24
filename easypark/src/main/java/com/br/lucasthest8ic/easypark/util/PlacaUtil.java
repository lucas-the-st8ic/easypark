package com.br.lucasthest8ic.easypark.util;

public final class PlacaUtil {

    public static final String REGEX_PLACA = "^[a-zA-Z]{3}-?([0-9]{4}|[0-9][a-zA-Z][0-9]{2})$";

    private PlacaUtil() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada.");
    }


    public static String normalizePlaca(String placa) {
        if(placa == null) {
            return null;
        }

        return placa.toUpperCase()
                .replace("-", "")
                .replaceAll("\\s+", "");
    }
}

