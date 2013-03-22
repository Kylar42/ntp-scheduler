package edu.wvup.cs460.http.authentication;

import com.Ostermiller.util.Base64;
import edu.wvup.cs460.util.StringUtils;

/**
 * User: Tom Byrne(tom.byrne@apple.com)
 * "Code early, Code often."
 */
public class AuthHeaderParser {



    public AuthenticationContext parse(String header){
        //should start with "basic"
        if(null == header){
            return AuthenticationContext.NO_AUTHENTICATION_FOUND;
        }

        if(header.startsWith("Basic ")){
            String rest = header.substring(6);
            if(rest.isEmpty()){
                return AuthenticationContext.NO_AUTHENTICATION_FOUND;
            }
            final byte[] decode = Base64.decode(rest.getBytes());
            String decoded = new String(decode);
            String[] split = StringUtils.splitStringWithoutEmpty(decoded, ":");
            System.out.println(header);
            String user = "";
            String password = "";
            if(null != split){
                if(split.length > 0){
                    user = split[0];
                }
                if(split.length > 1){
                    password = split[1];
                }
            }
            return new AuthenticationContext(user, password, AuthenticationTypes.BASIC, "NTP");
        }


        return AuthenticationContext.NO_AUTHENTICATION_FOUND;

    }
}
