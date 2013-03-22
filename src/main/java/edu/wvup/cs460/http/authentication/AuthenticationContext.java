package edu.wvup.cs460.http.authentication;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * "Code early, Code often."
 */
public class AuthenticationContext {

    public static final AuthenticationContext NO_AUTHENTICATION_FOUND = new AuthenticationContext("", "", AuthenticationTypes.UNAUTHENTICATED, "");

    private String _user;
    private String _password;
    private String _realm;
    private AuthenticationTypes _type;


    public AuthenticationContext(String user, String password, AuthenticationTypes type, String realm) {
        _user = user;
        _password = password;
        _type = type;
        _realm = realm;
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

}
