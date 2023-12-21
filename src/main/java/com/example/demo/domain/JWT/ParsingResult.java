package com.example.demo.domain.JWT;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Data
@AllArgsConstructor
public class ParsingResult {
    private ParsingResultType parsingResultType;
    private UsernamePasswordAuthenticationToken authentication;

    public enum ParsingResultType {
        ERROR,
        UNAUTHORIZED,
        OK,


        ;
    }
}
