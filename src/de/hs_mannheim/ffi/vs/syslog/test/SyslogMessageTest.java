package de.hs_mannheim.ffi.vs.syslog.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.hs_mannheim.ffi.vs.syslog.model.AsciiChars;
import de.hs_mannheim.ffi.vs.syslog.model.StructuredData;
import de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage;
import de.hs_mannheim.ffi.vs.syslog.model.StructuredData.Element;
import de.hs_mannheim.ffi.vs.syslog.model.StructuredData.Param;
import de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage.BinaryMessage;
import de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage.Facility;
import de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage.Severity;
import de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage.TextMessage;

class SyslogMessageTest {

    @Test
    void testToString() {
        var m1 = new SyslogMessage(//
                Facility.SECURITY1, //
                Severity.CRITICAL, //
                new AsciiChars.L255("mymachine.example.com"), //
                new AsciiChars.L048("su"), //
                new AsciiChars.L128(""), //
                new AsciiChars.L032("ID47"),//
                new StructuredData()//
                        .add(Element.newTimeQuality(true, true))
                        .add(new Element("exampleSDID@32473")
                                .add(new Param("iut", "3"))
                                .add(new Param("eventSource", "Application"))
                                .add(new Param("eventID", "1011")))
                        .add(new Element("examplePriority@32473")
                                .add(new Param("class", "high"))),
                new TextMessage("'su root' failed for lonvick on /dev/pts/8"));
        var s = m1.toString();
        assertEquals(s.substring(0, 6), "<34>1 ");
        assertEquals(s.substring(s.length() - 221, s.length()),
                " mymachine.example.com su - ID47 [timeQuality tzKnown=\"1\" isSynced=\"1\"][exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"][examplePriority@32473 class=\"high\"] ï»¿'su root' failed for lonvick on /dev/pts/8");
    }

    @Test
    void test2() {
        var m1 = new SyslogMessage(//
                Facility.SECURITY1, //
                Severity.CRITICAL, //
                new AsciiChars.L255("mymachine.example.com"), //
                new AsciiChars.L048("su"), //
                new AsciiChars.L128(""), //
                new AsciiChars.L032("ID47"),//
                null, //
                new TextMessage("'su root' failed for lonvick on /dev/pts/8"));
        var s = m1.toString();
        assertEquals(s.substring(0, 6), "<34>1 ");
        assertEquals(s.substring(s.length() - 78, s.length()),
                " mymachine.example.com su - ID47 ï»¿'su root' failed for lonvick on /dev/pts/8");

    }

    @Test
    void test3() {
        var m1 = new SyslogMessage(//
                Facility.SECURITY1, //
                Severity.CRITICAL, //
                new AsciiChars.L255("mymachine.example.com"), //
                new AsciiChars.L048("su"), //
                new AsciiChars.L128(""), //
                new AsciiChars.L032("ID47"),//
                new StructuredData()//
                        .add(Element.newTimeQuality(true, true))
                        .add(Element.newOrigin(
                                new String[] { "0.0.8.8", "8.8.8.8" }, null,
                                null, null))
                        .add(Element.newMeta(null, 32, "de")),
                new BinaryMessage(null));
        assertEquals(m1.data().toString(),
                "[timeQuality tzKnown=\"1\" isSynced=\"1\"][origin ip=\"0.0.8.8\" ip=\"8.8.8.8\"][meta sysUpTime=\"32\" language=\"de\"]");
        ;

    }
}
