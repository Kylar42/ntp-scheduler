package edu.wvup.cs460.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Tom Byrne
 * "If I am unable to see, it is because
 * I am being stood upon by giants."
 */
public class StringUtilsTest {


    @Test
    public void testToHexString() {
        byte[] array = new byte[]{0x01, 0x02, 0x03, 0x0A, 0x0F, 0x21, 0x7F, 0x70,
                (byte) -254, (byte) 255, (byte) 0, (byte) -1, 0x33, 0x44, 0x55, 0x66};

        String hexString = StringUtils.toHexString(array);
        assertEquals("123af217f702ff0ff33445566", hexString);
    }

    @Test
    public void parseIntegersFromStringTest() {
        String base = "A1B2C3ZZZ456AIINE\n";
        int expectedValue = 123456;
        int parsedValue = StringUtils.parseIntegersFromString(base);
        assertEquals(expectedValue, parsedValue);

        base = null;
        parsedValue = StringUtils.parseIntegersFromString(base);
        assertEquals(Integer.MIN_VALUE, parsedValue);

        base = "";

        parsedValue = StringUtils.parseIntegersFromString(base);
        assertEquals(Integer.MIN_VALUE, parsedValue);

        base = Long.toString(Long.MAX_VALUE);
        parsedValue = StringUtils.parseIntegersFromString(base);
        assertEquals(Integer.MIN_VALUE, parsedValue);


    }

    @Test
    public void splitStringWithoutEmptyTest() {
        String toSplit = "/abc/def//ghi/zzzzzzzz//";
        String[] expectedOutcome = new String[]{"abc", "def", "ghi", "zzzzzzzz"};

        String[] splitStrings = StringUtils.splitStringWithoutEmpty(toSplit, "/");

        assertEquals(splitStrings, expectedOutcome);

        //test NPE.
        NullPointerException npe = null;

        try {
            StringUtils.splitStringWithoutEmpty("foo", null);
        } catch (NullPointerException e) {
            npe = e;
        }

        assertNotNull(npe);

        npe = null;

        try {
            StringUtils.splitStringWithoutEmpty(null, "foo");
        } catch (NullPointerException e) {
            npe = e;
        }

        assertNotNull(npe);

    }

    @Test
    public void readStringTest() throws IOException {
        Charset utf8 = Charset.forName("utf8");
        String toPass = new String("Trogdor comes in the night".getBytes(), utf8);

        InputStream is = new ByteArrayInputStream(toPass.getBytes());

        String result = StringUtils.readString(is, utf8);

        assertEquals(toPass, result);

    }
}
