package edu.wvup.cs460.util;

import edu.wvup.cs460.http.MimeType;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * User: Tom Byrne
 * "If I am unable to see, it is because
 * I am being stood upon by giants."
 */
public class MimeUtilsTest {
    @Test
    public void contentTypeForFileTest(){
        File toPass = new File("foobar.jpg");
        MimeType mimeType = MimeUtils.contentTypeForFile(toPass);

        assertEquals(MimeType.JPEG, mimeType);

        toPass = new File("foobar.jpeg");
        mimeType = MimeUtils.contentTypeForFile(toPass);
        assertEquals(MimeType.JPEG, mimeType);

        toPass = new File("foobar.html");
        mimeType = MimeUtils.contentTypeForFile(toPass);
        assertEquals(MimeType.TEXT_HTML, mimeType);

        toPass = new File("foobar.css");
        mimeType = MimeUtils.contentTypeForFile(toPass);
        assertEquals(MimeType.TEXT_CSS, mimeType);

        toPass = new File("foobar.js");
        mimeType = MimeUtils.contentTypeForFile(toPass);
        assertEquals(MimeType.APP_JAVASCRIPT, mimeType);

        toPass = new File("foobar.json");
        mimeType = MimeUtils.contentTypeForFile(toPass);
        assertEquals(MimeType.APP_JSON, mimeType);

        toPass = new File("foobar.ico");
        mimeType = MimeUtils.contentTypeForFile(toPass);
        assertEquals(MimeType.XICON, mimeType);

        toPass = new File("foobar.xml");
        mimeType = MimeUtils.contentTypeForFile(toPass);
        assertEquals(MimeType.APP_XML, mimeType);

        toPass = new File("foobar.plist");
        mimeType = MimeUtils.contentTypeForFile(toPass);
        assertEquals(MimeType.APP_PLIST, mimeType);

        toPass = new File("foobar.txt");
        mimeType = MimeUtils.contentTypeForFile(toPass);
        assertEquals(MimeType.TEXT_PLAIN, mimeType);

        toPass = new File("");
        mimeType = MimeUtils.contentTypeForFile(toPass);
        assertEquals(MimeType.UNKNOWN, mimeType);


    }
}
