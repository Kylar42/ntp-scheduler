package edu.wvup.cs460.http.authentication;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class CookieHandlerTest {


    @Test
    public void testCookieEncoding() throws Exception{//we don't actually want to throw an exception.
        CookieHandler handler = new CookieHandler();
        AuthenticationContext testContext = context();
        final String storedCookie = handler.createAndStoreCookie(testContext, 1000, "");
        //Should be able to fetch cookie right away.
        AuthenticationContext fetchedContext = handler.fetchCookie(storedCookie);//if this throws, we will fail
    }

    @Test
    public void testExpiredCookie() throws Exception{//we don't actually want to throw an exception.
        CookieHandler handler = new CookieHandler();
        AuthenticationContext testContext = context();
        final String storedCookie = handler.createAndStoreCookie(testContext, 500, "");
        Thread.currentThread().sleep(1000);
        AuthenticationException ae = null;
        try {
            AuthenticationContext fetchedContext = handler.fetchCookie(storedCookie);//if this throws, we will succeed.
        } catch (AuthenticationException e) {
            ae = e;
        }

        assertNotNull(ae);
        assert(AuthenticationException.AuthExceptionType.EXPIRED == ae.getType());

    }


    private AuthenticationContext context(){
        AuthenticationContext toReturn = new AuthenticationContext("testUser", "testPassword", AuthenticationTypes.BASIC, "TESTREALM");
        return toReturn;
    }
}
