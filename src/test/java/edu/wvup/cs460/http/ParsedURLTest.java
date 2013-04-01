package edu.wvup.cs460.http;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class ParsedURLTest {

    @Test
    public void testParsedURLNull(){
        String url = "";
        ParsedURL pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.unknown,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.unknown,  pURL.getObjectType());
    }

    @Test
    public void testEmptyURL(){
        String url = "/";
        ParsedURL pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.unknown,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.unknown,  pURL.getObjectType());
    }

    @Test
    public void testOneLevelURL(){
        String url = "/classlist";
        ParsedURL pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.unknown,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());

        url = "/classmeta";
        pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.unknown,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classmeta,  pURL.getObjectType());

        url = "/authentication";
        pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.unknown,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.authentication,  pURL.getObjectType());

        url = "/user";
        pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.unknown,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.user,  pURL.getObjectType());
    }
    @Test
    public void testTwoLevelURL(){
        //        search, update, invalidate, validate, unknown;

        String url = "/classlist/search";
        ParsedURL pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.search,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());

        url = "/classmeta/update";
        pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.update,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classmeta,  pURL.getObjectType());

        url = "/authentication/invalidate";
        pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.invalidate,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.authentication,  pURL.getObjectType());

        url = "/user/validate";
        pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.validate,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.user,  pURL.getObjectType());

        url = "/user/random-unknown";
        pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.unknown,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.user,  pURL.getObjectType());
    }

    @Test
    public void testOneLevelURLWithTrailingSlash(){

        String url = "/classlist";
        ParsedURL pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.unknown,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());

    }
    @Test
    public void testTwoLevelURLWithTrailingSlash(){

        String url = "/classlist/validate/";
        ParsedURL pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.validate,  pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());

    }

    @Test
    public void testThreeLevelURL(){

        String url = "/classlist/validate/trogdor";
        String[] expectedRemnants = new String[]{"trogdor"};
        ParsedURL pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.validate, pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());
        assertArrayEquals(expectedRemnants, pURL.getRemainingSegments());

    }

    @Test
    public void testSevenLevelURL(){

        String url = "/classlist/validate/trogdor/comes/in/the/night";
        String[] expectedRemnants = new String[]{"trogdor", "comes", "in", "the", "night"};
        ParsedURL pURL = new ParsedURL(url);
        assertNotNull(pURL);
        assertEquals(ParsedURL.ACTION_TYPE.validate, pURL.getActionType());
        assertEquals(ParsedURL.OBJECT_TYPE.classlist,  pURL.getObjectType());
        assertArrayEquals(expectedRemnants, pURL.getRemainingSegments());

    }
}
