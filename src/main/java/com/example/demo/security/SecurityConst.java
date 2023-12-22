package com.example.demo.security;

public class SecurityConst {
    public static final String ROLE_CLAIM = "role";
    public static final String USER_ID_CLAIM="userId";
    public static final String SESSION_ID_CLAIM="sessionId";


    public enum Role {
        ADMIN,
        USER,
        REFRESH_TOKEN,


        ;
    }

}
