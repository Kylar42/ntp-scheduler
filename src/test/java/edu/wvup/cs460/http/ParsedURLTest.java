package edu.wvup.cs460.http;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class ParsedURLTest {

    @Test
    public void testParsedURLNull(){
        String url = "";
        ParsedURL pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.nonexistant,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.nonexistant,  pURL.getObjectType());
    }

    @Test
    public void testEmptyURL(){
        String url = "/";
        ParsedURL pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.nonexistant,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.nonexistant,  pURL.getObjectType());
        assertTrue(pURL.isRoot());
    }

    @Test
    public void testOneLevelURL(){
        String url = "/classlist";
        ParsedURL pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.nonexistant,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());

        url = "/classmeta";
        pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.nonexistant,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classmeta,  pURL.getObjectType());

        url = "/authentication";
        pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.nonexistant,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.authentication,  pURL.getObjectType());

        url = "/user";
        pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.nonexistant,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.user,  pURL.getObjectType());
    }
    @Test
    public void testTwoLevelURL(){
        //        search, update, invalidate, validate, unknown;

        String url = "/classlist/search";
        ParsedURL pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.search,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());

        url = "/classmeta/update";
        pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.update,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classmeta,  pURL.getObjectType());

        url = "/authentication/invalidate";
        pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.invalidate,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.authentication,  pURL.getObjectType());

        url = "/user/validate";
        pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.validate,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.user,  pURL.getObjectType());

        url = "/user/random-unknown";
        pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.unknown,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.user,  pURL.getObjectType());
    }

    @Test
    public void testOneLevelURLWithTrailingSlash(){

        String url = "/classlist";
        ParsedURL pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.nonexistant,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());

    }
    @Test
    public void testTwoLevelURLWithTrailingSlash(){

        String url = "/classlist/validate/";
        ParsedURL pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.validate,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());

    }

    @Test
    public void testThreeLevelURL(){

        String url = "/classlist/validate/trogdor";
        String[] expectedRemnants = new String[]{"trogdor"};
        ParsedURL pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.validate, pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());
        assertArrayEquals(expectedRemnants, pURL.getRemainingSegments());

    }

    @Test
    public void testSevenLevelURL(){

        String url = "/classlist/validate/trogdor/comes/in/the/night";
        String[] expectedRemnants = new String[]{"trogdor", "comes", "in", "the", "night"};
        ParsedURL pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.validate, pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());
        assertArrayEquals(expectedRemnants, pURL.getRemainingSegments());

    }

    @Test
    public void testStringNonexistantURLReturnsUnknown(){
        String url = "/nonexistant/nonexistant/foo";
        ParsedURL pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.unknown, pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.unknown,  pURL.getObjectType());
    }
    @Test
    public void testUnauthorizedSegment(){
        String url = "/unauthorized/login.html";
        ParsedURL pURL = ParsedURL.createParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.unknown, pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.unauthorized,  pURL.getObjectType());
        assertEquals("login.html", pURL.elementAt(1));
    }

    @Test
    public void testUnauthorizedWithRelativeSegment(){
        String url = "/unauthorized/../index.html";
        ParsedURL pURL = ParsedURL.createParsedURL(url);   //should return ROOT_URL since it's got a relative segment.
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.nonexistant, pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.nonexistant,  pURL.getObjectType());

        assertTrue(pURL.isRoot());

        url="/..unauthorized/index.html";
        pURL = ParsedURL.createParsedURL(url);   //should return ROOT_URL since it's got a relative segment.
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.nonexistant, pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.nonexistant,  pURL.getObjectType());

        assertTrue(pURL.isRoot());

    }

}
