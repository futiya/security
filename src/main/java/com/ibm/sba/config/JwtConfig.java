package com.ibm.sba.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Candise Li (jieqli@cn.ibm.com)
 * @create 2019-11-01 22:57
 */
@Getter
public class JwtConfig {
    @Value("${security.jwt.uri}")
    private String Uri;

    @Value("${security.jwt.header}")
    private String header;

    @Value("${security.jwt.prefix}")
    private String prefix;

    @Value("${security.jwt.expiration}")
    private int expiration;

    @Value("${security.jwt.secret}")
    private String secret;

}
