package com.StayHub.payload;

import lombok.Data;

@Data
public class JwtTokenDto {

    private String type;
    private String token;
}
