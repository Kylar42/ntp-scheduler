package edu.wvup.cs460.http.authentication;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 * Contextual object for holding authentication information.
 */
public class AuthenticationContext {

    public static final AuthenticationContext NO_AUTHENTICATION_FOUND = new AuthenticationContext("", "", AuthenticationTypes.UNAUTHENTICATED, "");

    private final String                _user;
    private final String                _password;
    private final String                _realm;
    private final AuthenticationTypes   _type;

    //these are non-final on purpose.
    private String                      _originatingIP;
    private long                        _expirationTime;


    public AuthenticationContext(String user, String password, AuthenticationTypes type, String realm) {
        _user = user;
        _password = password;
        _type = type;
        _realm = realm;
    }

    public AuthenticationContext(String user, String password, AuthenticationTypes type, String realm, String originatingIP, long expirationTime) {
        _user = user;
        _password = password;
        _type = type;
        _realm = realm;
        _originatingIP = originatingIP;
        _expirationTime = expirationTime;
    }

    public String getUser() {
        return _user;
    }

    public String getPassword() {
        return _password;
    }

    public String getRealm() {
        return _realm;
    }

    public AuthenticationTypes getAuthType() {
        return _type;
    }

    public String getOriginatingIP(){
        return _originatingIP;
    }

    public long getExpirationTime(){
        return _expirationTime;
    }

    public boolean isExpired(){
        return System.currentTimeMillis() > _expirationTime;
    }

    public void setOriginatingIP(String newIP){
        _originatingIP = newIP;
    }

    public void setExpirationTime(long expTime){
        _expirationTime = expTime;
    }

}
