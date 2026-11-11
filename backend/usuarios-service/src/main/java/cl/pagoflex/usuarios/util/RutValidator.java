package cl.pagoflex.usuarios.util;

import java.util.Locale;

public final class RutValidator {

    private RutValidator() {
    }

    public static String normalizarYValidar(String rut) {

        if (rut == null || rut.isBlank()) {
            throw new IllegalArgumentException(
                "El RUT es obligatorio"
            );
        }

        String limpio = rut.trim()
                .toUpperCase(Locale.ROOT)
                .replace(".", "")
                .replace("-", "")
                .replace(" ", "");

        if (!limpio.matches("\\d{1,8}[0-9K]")) {
            throw new IllegalArgumentException(
                "El formato del RUT no es válido"
            );
        }

        String cuerpo = limpio.substring(
                0, limpio.length() - 1
        );

        char digitoIngresado =
                limpio.charAt(limpio.length() - 1);

        int suma = 0;
        int multiplicador = 2;

        for (int i = cuerpo.length() - 1; i >= 0; i--) {

            int numero = Character.getNumericValue(
                    cuerpo.charAt(i)
            );

            suma += numero * multiplicador;

            multiplicador++;

            if (multiplicador > 7) {
                multiplicador = 2;
            }
        }

        int resultado = 11 - (suma % 11);

        char digitoEsperado;

        if (resultado == 11) {
            digitoEsperado = '0';
        } else if (resultado == 10) {
            digitoEsperado = 'K';
        } else {
            digitoEsperado = (char) ('0' + resultado);
        }

        if (digitoIngresado != digitoEsperado) {
            throw new IllegalArgumentException(
                "El dígito verificador del RUT no es válido"
            );
        }

        return cuerpo + "-" + digitoIngresado;
    }
}
