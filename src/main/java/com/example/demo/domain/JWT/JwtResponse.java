package com.example.demo.domain.JWT;

import com.example.demo.domain.common.GeneralResultModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class JwtResponse extends GeneralResultModel {
    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;

    public JwtResponse(String accessToken, String refreshToken, String errorCode, String errorMessage){
        super(errorCode,errorMessage);
        this.accessToken=accessToken;
        this.refreshToken=refreshToken;
    }
    public JwtResponse(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
