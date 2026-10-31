package utu;

import org.junit.Test;
import utu.util.PasswordUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PasswordUtilTest {

    @Test
    public void hashGeneraFormatoBCrypt() {
        String hash = PasswordUtil.hash("admin123");
        assertTrue(hash.startsWith("$2a$"));
        assertEquals(60, hash.length());
    }

    @Test
    public void verificarContrasenaCorrecta() {
        String hash = PasswordUtil.hash("secreto123");
        assertTrue(PasswordUtil.verificar("secreto123", hash));
    }

    @Test
    public void verificarContrasenaIncorrecta() {
        String hash = PasswordUtil.hash("secreto123");
        assertFalse(PasswordUtil.verificar("otraCosa", hash));
    }

    @Test
    public void hashesDistintosParaMismoPassword() {
        String hash1 = PasswordUtil.hash("mismoPassword");
        String hash2 = PasswordUtil.hash("mismoPassword");
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void hashGeneradoConContrasenaVaciaEsValido() {
        String hash = PasswordUtil.hash("");
        assertTrue(PasswordUtil.verificar("", hash));
        assertFalse(PasswordUtil.verificar(" ", hash));
    }

    @Test
    public void verificarConCaracteresEspecialesYUnicode() {
        String contrasena = "p4ssw0rd ¡ñÑáéíóú! @#$%&/*-+ 😀";
        String hash = PasswordUtil.hash(contrasena);
        assertTrue(PasswordUtil.verificar(contrasena, hash));
        assertFalse(PasswordUtil.verificar("p4ssw0rd ¡ñÑáéíóú! @#$%&/*-+", hash));
    }

    @Test
    public void verificarConContrasenaLarga() {
        String contrasena = "aB9$".repeat(15); // 60 caracteres
        String hash = PasswordUtil.hash(contrasena);
        assertTrue(PasswordUtil.verificar(contrasena, hash));
        assertFalse(PasswordUtil.verificar(contrasena.substring(1), hash));
    }

    @Test
    public void espaciosEnLosExtremosSonSignificativos() {
        String hash = PasswordUtil.hash("  clave con espacios  ");
        assertTrue(PasswordUtil.verificar("  clave con espacios  ", hash));
        assertFalse(PasswordUtil.verificar("clave con espacios", hash));
    }

    @Test
    public void verificarConHashInvalidoLanzaExcepcion() {
        // LoginDAO captura esta excepción y trata el login como inválido
        try {
            PasswordUtil.verificar("cualquierCosa", "esto-no-es-un-hash-bcrypt");
            fail("Debía lanzar IllegalArgumentException con un hash corrupto");
        } catch (IllegalArgumentException expected) {
            // comportamiento esperado
        }
    }

    @Test
    public void hashConTamanioBCryptEstándar() {
        // 60 caracteres: formato $2a$ + 2 dígitos de costo + $ + 22 de salt + 31 de hash
        String hash = PasswordUtil.hash("x");
        assertEquals("$2a$", hash.substring(0, 4));
        assertEquals(60, hash.length());
    }
}
