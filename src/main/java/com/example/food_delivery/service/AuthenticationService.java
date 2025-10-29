package com.example.food_delivery.service;

import com.example.food_delivery.domain.entity.InvalidatedToken;
import com.example.food_delivery.domain.entity.Users;
import com.example.food_delivery.dto.request.IntrospectRequest;
import com.example.food_delivery.dto.request.LoginRequest;
import com.example.food_delivery.dto.request.LogoutRequest;
import com.example.food_delivery.dto.request.RefreshRequest;
import com.example.food_delivery.dto.response.AuthenticationResponse;
import com.example.food_delivery.dto.response.IntrospectResponse;
import com.example.food_delivery.exception.AppException;
import com.example.food_delivery.exception.ErrorCode;
import com.example.food_delivery.reponsitory.InvalidatedTokenRepository;
import com.example.food_delivery.reponsitory.UserReponsitory;
import com.example.food_delivery.service.imp.AuthenticationServiceImp;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements AuthenticationServiceImp {
    UserReponsitory userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.privatekey:PA7ummSP4r0RY9AlAn1LfgNoNBjZsejBHoiQAF79HGE=}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration:3600}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration:86400}")
    protected Long REFRESHABLE_DURATION;

    @Override
    public AuthenticationResponse checkLogin(LoginRequest loginRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByUserName(loginRequest.getUsername());
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        boolean issSuccess = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());

        if (!issSuccess) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var token = generateToken(user);
        return AuthenticationResponse.builder().authenticated(true).token(token).build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().expiryTime(expiryTime).id(jit).build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var jit = signedJWT.getJWTClaimsSet().getJWTID();

        invalidatedTokenRepository.save(
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build());

        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUserName(username);
        if (user == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);
        return AuthenticationResponse.builder().authenticated(true).token(token).build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(Duration.ofSeconds(REFRESHABLE_DURATION))
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private String generateToken(Users users) {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(users.getUserName())
                .issuer("")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(Duration.ofSeconds(REFRESHABLE_DURATION)).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buiScope(users))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buiScope(Users user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (user.getRoles() != null) {
            var role = user.getRoles();
            stringJoiner.add("ROLE_" + role.getRoleName());
            if (!CollectionUtils.isEmpty(role.getPermissions())) {
                role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            }
        }
        return stringJoiner.toString();
    }
}
