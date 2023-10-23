package com.indra.userservice.controller;

import com.indra.userservice.domain.MemberDto;
import com.indra.userservice.domain.MemberRedis;
import com.indra.userservice.service.MemberService;
import com.indra.userservice.support.BaseApiController;
import com.indra.userservice.support.ValidationObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "token", produces = MediaTypes.HAL_JSON_VALUE)
public class TokenController extends BaseApiController {

    private final MemberService memberService;
    private final Environment env;

    @PostMapping("/refresh")
    public ResponseEntity createAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken,
                                            Locale locale) {

        String jwt = refreshToken.replace("Bearer", "");

        // access token 재 생성.
        String refreshScretKey = env.getProperty("jwt.refresh-token.secret");
        Long id = Long.parseLong(Jwts.parser().setSigningKey(refreshScretKey)
                .parseClaimsJws(jwt).getBody().get("id").toString());

        MemberDto userDetails = memberService.getUserDetailsById(id);

        MemberRedis memberRedis = MemberRedis.builder()
                .id(userDetails.getId())
                .accountId(userDetails.getAccountId())
                .companyId(userDetails.getCompanyId())
                .name(userDetails.getName())
                .role(userDetails.getRole().toString())
                .build();
        memberService.createMemberRedis(memberRedis);

        Claims claims = Jwts.claims().setSubject("access");
        claims.put("id",id);
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("jwt.access-token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("jwt.access-token.secret"))
                .compact();

        WebMvcLinkBuilder selfLink = linkTo(methodOn(TokenController.class).createAccessToken(null, locale));
        URI createUri = selfLink.toUri();

        Map<String,Object> accessMap = new HashMap<>();
        accessMap.put("access-token",accessToken);

        ResponseEntity res = makeDataMessageForCreated (
            createUri,
            accessMap,
            locale
        );

        return res;
    }


}
