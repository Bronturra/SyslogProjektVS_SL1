package de.hs_mannheim.ffi.vs.syslog.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.hs_mannheim.ffi.vs.syslog.model.AsciiChars;

class AsciiCharsTest {

    @Test
    void nullValue() {
        var ac = new AsciiChars.L004(null);
        assertEquals(ac.toString(), "-");
    }

    @Test
    void emptyValue() {
        var ac = new AsciiChars.L004("");
        assertEquals(ac.toString(), "-");
    }

    @Test
    void longValue() {
        var ac = new AsciiChars.L004("1234");
        assertEquals(ac.toString(), "1234");
    }

    @Test
    void longerValue() {
        var thrown = assertThrows(IllegalArgumentException.class, () -> {
            new AsciiChars.L004("12345");
        });
        assertEquals("Stringlänge = 5 > 4", thrown.getMessage());
    }

    @Test
    void space() {
        var thrown = assertThrows(IllegalArgumentException.class, () -> {
            new AsciiChars.L004("1 1");
        });
        assertEquals("Stringinhalt nicht printable US-ASCII ohne Space",
                thrown.getMessage());
    }

    @Test
    void special() {
        var thrown = assertThrows(IllegalArgumentException.class, () -> {
            new AsciiChars.L004("ä");
        });
        assertEquals("Stringinhalt nicht printable US-ASCII ohne Space",
                thrown.getMessage());
    }
}
