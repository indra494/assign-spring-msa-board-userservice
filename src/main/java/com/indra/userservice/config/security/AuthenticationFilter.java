package com.indra.userservice.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indra.userservice.constant.CommonConstants;
import com.indra.userservice.domain.LoginDto;
import com.indra.userservice.domain.MemberDto;
import com.indra.userservice.domain.MemberRedis;
import com.indra.userservice.service.MemberService;
import com.indra.userservice.support.ResponseMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private MemberService memberService;
    private Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager, MemberService memberService, Environment env) {
        super(authenticationManager);
        this.memberService = memberService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            LoginDto creds = new ObjectMapper().readValue(request.getInputStream(), LoginDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getAccountId(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String userName = ((User)authResult.getPrincipal()).getUsername();
        MemberDto userDetails = memberService.getUserDetailsByAccountId(userName);

        MemberRedis memberRedis = MemberRedis.builder()
                .id(userDetails.getId())
                .accountId(userDetails.getAccountId())
                .companyId(userDetails.getCompanyId())
                .name(userDetails.getName())
                .role(userDetails.getRole().toString())
                .build();

        Claims claims = Jwts.claims().setSubject("access");
        claims.put("id",userDetails.getId());

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("jwt.access-token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("jwt.access-token.secret"))
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject("refresh")
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("jwt.refresh-token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("jwt.refresh-token.secret"))
                .compact();

        memberService.createMemberRedis(memberRedis);

        response.addHeader("access-token", accessToken);
        response.addHeader("refresh-token", refreshToken);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String,Object> tokenMap = new HashMap<String,Object>();
        tokenMap.put("access-token",accessToken);
        tokenMap.put("refresh-token",refreshToken);

        ResponseMessage responseMessage = new ResponseMessage("");
        responseMessage.setData(tokenMap);
        new ObjectMapper().writeValue(response.getOutputStream(), responseMessage);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ResponseMessage responseMessage = new ResponseMessage("");
        responseMessage.setStatus(CommonConstants.FAILURE);
        new ObjectMapper().writeValue(response.getOutputStream(), responseMessage);
    }
}
