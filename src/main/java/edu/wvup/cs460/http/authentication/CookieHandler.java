package edu.wvup.cs460.http.authentication;

import edu.wvup.cs460.util.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Tom Byrne kylar42@gmail.com
 * "Code early, Code often."
 * Class for dealing with all aspects of HTTP cookies.
 */
public class CookieHandler {
    private static Logger LOG = LoggerFactory.getLogger(CookieHandler.class);

    //========================================================================

    private static final String COOKIE_VERSION = "NTP1";
    private static final ConcurrentHashMap<String, String> COOKIE_CACHE = new ConcurrentHashMap<String, String>(4096);//hold 4K cookies. Can't imagine going bigger than that with expiration. famous last words.

    /**
     * This can only return one item: A real authentication context usable for this request. Will throw otherwise.
     *
     * @param cookieKey
     * @return The found and decoded authentication context.
     * @throws AuthenticationException if the cookieValue doesn't exist, or is expired.
     */
    public AuthenticationContext fetchCookie(String cookieKey) throws AuthenticationException {
        //Let's see if we have a cookieValue cached.
        final String encodedCookieInfo = COOKIE_CACHE.get(cookieKey);
        if (null == encodedCookieInfo) {
            throw new AuthenticationException(AuthenticationException.AuthExceptionType.NOT_FOUND);//no cookie
        }
        final AuthenticationContext authenticationContext = decodeCookieValue(encodedCookieInfo);
        if(authenticationContext.isExpired()){
            throw new AuthenticationException(AuthenticationException.AuthExceptionType.EXPIRED);
        }


        return authenticationContext;
    }

    /**
     * Find this cookie in the cache and remove it.
     * @param cookieKey
     * @return
     */
    public boolean dropCookieFromCache(String cookieKey){
        final String remove = COOKIE_CACHE.remove(cookieKey);
        return null != remove;//true if there was something there.
    }

    /**
     * Helper method to encode an authenticationcontext into a string value for a cookie.
     * @param context
     * @param goodForInMillis
     * @param ipAddr
     * @return
     */
    private static String encodeCookieValue(AuthenticationContext context, long goodForInMillis, String ipAddr) {
        StringBuilder sb = new StringBuilder();
        long expiresAt = System.currentTimeMillis() + goodForInMillis;
        String encodedCookieVersion = urlEncode(COOKIE_VERSION);
        String encodedUser = urlEncode(context.getUser());
        String encodedExpiration = urlEncode(Long.toString(expiresAt));
        String encodedAuthType = urlEncode(context.getAuthType().typeValue());
        String encodedIP = urlEncode(ipAddr);
        sb.append(encodedCookieVersion).append("&");
        sb.append(encodedUser).append("&");
        sb.append(encodedExpiration).append("&");
        sb.append(encodedAuthType).append("&");
        sb.append(encodedIP);
        //now base64 encode
        return new String(Base64.encodeBase64(sb.toString().getBytes()));
    }

    /**
     * Helper method to do URLEncoding
     * @param value
     * @return
     */
    private static String urlEncode(String value) {
        if (null == value || value.isEmpty()) {
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Unsupported Encoding Exception in CookieHandler", e);
            return "";
        }
    }

    /**
     * Helper method to do URLDecoding
     * @param value
     * @return
     */
    private static String urlDecode(String value) {
        if (null == value || value.isEmpty()) {
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Unsupported Encoding Exception in CookieHandler", e);
            return "";
        }
    }

    /**
     * Decode a cookie value retrieved from the cache into an AuthenticationContext.
     * @param encodedValue
     * @return
     * @throws AuthenticationException
     */
    private AuthenticationContext decodeCookieValue(String encodedValue) throws AuthenticationException {
        if (null == encodedValue) {
            //fark.
            LOG.error("Was given a null value to encode. This should not happen.");
            throw new AuthenticationException(AuthenticationException.AuthExceptionType.UNKNOWN);
        }
        //first base64 decode:
        String toParse = new String(Base64.decodeBase64(encodedValue));

        final String[] split = toParse.split("&");
        if (null == split || split.length < 1) {
            throw new AuthenticationException(AuthenticationException.AuthExceptionType.UNKNOWN);//something bad happened.
        }

        final String version = urlDecode(split[0]);
        //this way if we change what or how we encode cookies, we can easily branch here.
        if (COOKIE_VERSION.equals(version)){
            //check length.
            if(split.length < 4){
                throw new AuthenticationException(AuthenticationException.AuthExceptionType.UNKNOWN_COOKIE_VERSION);
            }
            String unencodedUser = urlDecode(split[1]);
            String unencodedExpiration = urlDecode(split[2]);
            String unencodedAuthType = urlDecode(split[3]);
            String unencodedIP = null;
            AuthenticationTypes authType = AuthenticationTypes.typeFromValue(unencodedAuthType);
            long expiration = 0;
            try {
                expiration = Long.parseLong(unencodedExpiration);
            } catch (NumberFormatException e) {
                LOG.warn("Wasn't able to parse expiration for cookie:{}, exp value:{}", encodedValue, unencodedExpiration);
            }
            //check for IP
            if(split.length > 4){
                unencodedIP = urlDecode(split[4]);
            }
            AuthenticationContext context = new AuthenticationContext(unencodedUser, null,  authType, "CookieRealm", unencodedIP, expiration);
            return context;

        }else{
            throw new AuthenticationException(AuthenticationException.AuthExceptionType.UNKNOWN_COOKIE_VERSION);
        }
    }

    /**
     * Create a new cookie and store it in the cache.
     * @param context
     * @param expires
     * @param ipAddr
     * @return
     */
    public String createAndStoreCookie(AuthenticationContext context, long expires, String ipAddr) {
        String encodedCookieValue = encodeCookieValue(context, expires, ipAddr);
        UUID uuid = UUID.randomUUID();
        COOKIE_CACHE.put(uuid.toString(), encodedCookieValue);
        return uuid.toString();
    }
}
