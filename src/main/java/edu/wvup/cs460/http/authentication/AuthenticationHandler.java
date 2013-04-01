package edu.wvup.cs460.http.authentication;

/**
 * User: Tom Byrne(kylar42@gmail.com)
 * "Code early, Code often."
 */
public class AuthenticationHandler {

    public AuthenticationHandler(){

    }


    public Principal authenticate(String header){
        AuthHeaderParser parser = new AuthHeaderParser();
        final AuthenticationContext context = parser.parse(header);
        if(context == AuthenticationContext.NO_AUTHENTICATION_FOUND){
            return Principal.UNAUTHORIZED;
        }else{
            return authenticate(context);
        }

    }

    private Principal authenticate(AuthenticationContext context){
        if(context.getAuthType() == AuthenticationTypes.BASIC &&
                context.getUser().equalsIgnoreCase("guest")){
            return Principal.READ_USER;
        }else if(context.getAuthType() == AuthenticationTypes.BASIC &&
                context.getUser().equalsIgnoreCase("admin")){
            return Principal.READ_WRITE_USER;
        }

        return Principal.UNAUTHORIZED;
    }

}
