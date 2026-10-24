package com.br.lucasthest8ic.easypark.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;


class PlacaUtilTest {

    @Test
    @DisplayName("Given a valid plate then convert to Uppercase")
    void normalizePlaca_ConvertToUppercase() {

        var receivedPlaca = "abc1234";

        var result = PlacaUtil.normalizePlaca(receivedPlaca);

        assertThat(result).isEqualTo("ABC1234");
    }

    @Test
    @DisplayName("Given a plate with hyphen then remove the hyphen")
    void normalizePlaca_RemoveHyphen() {

        var receivedPlaca = "abc-1234";

        var result = PlacaUtil.normalizePlaca(receivedPlaca);

        assertThat(result).isEqualTo("ABC1234");
    }

    @Test
    @DisplayName("Given a plate with spaces then remove all spaces")
    void normalizePlaca_RemoveSpaces() {

        var receivedPlaca = " ABC 1234 ";

        var result = PlacaUtil.normalizePlaca(receivedPlaca);

        assertThat(result).isEqualTo("ABC1234");
    }


    @Test
    @DisplayName("Given a plate with a tab then remove any whitespace")
    void normalizePlaca_RemoveTab() {

        var receivedPlaca = "ABC\t1234";

        var result = PlacaUtil.normalizePlaca(receivedPlaca);

        assertThat(result).isEqualTo("ABC1234");
    }

    @Test
    @DisplayName("Given a Mercosul plate then convert to Uppercase")
    void normalizePlaca_MercosulPlate() {

        var receivedPlaca = "abc1d23";

        var result = PlacaUtil.normalizePlaca(receivedPlaca);

        assertThat(result).isEqualTo("ABC1D23");
    }

    @Test
    @DisplayName("Given a blank plate then return empty text")
    void normalizePlaca_BlankPlate() {

        var receivedPlaca = "   ";

        var result = PlacaUtil.normalizePlaca(receivedPlaca);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Given a null plate then return null")
    void normalizePlaca_NullPlate() {

        var result = PlacaUtil.normalizePlaca(null);

        assertThat(result).isNull();
    }

    @ParameterizedTest(name = "\"{0}\" deve ser aceita")
    @DisplayName("Given a valid plate format then the regex matches")
    @ValueSource(strings = {
            "ABC1234",   // padrão antigo
            "abc1234",   // minúsculas
            "ABC-1234",  // padrão antigo com hífen
            "ABC1D23",   // Mercosul
            "abc1d23"    // Mercosul em minúsculas
    })
    void regexPlaca_ValidFormats(String placa) {

        assertThat(placa).matches(PlacaUtil.REGEX_PLACA);
    }

    @ParameterizedTest(name = "\"{0}\" deve ser recusada")
    @DisplayName("Given an invalid plate format then the regex does not match")
    @ValueSource(strings = {
            "AB1234",     // letras de menos
            "ABCD123",    // letras de mais
            "ABC123",     // números de menos
            "ABC12345",   // números de mais
            "A1C1234",    // número no meio das letras
            "1234ABC",    // ordem invertida
            "ABC-D123",   // letra no lugar errado
            "ABC 1234",   // espaço no meio
            " ABC1234",   // espaço no início
            "ABC1234 ",   // espaço no fim
            ""            // vazio
    })
    void regexPlaca_InvalidFormats(String placa) {

        assertThat(placa).doesNotMatch(PlacaUtil.REGEX_PLACA);
    }
}