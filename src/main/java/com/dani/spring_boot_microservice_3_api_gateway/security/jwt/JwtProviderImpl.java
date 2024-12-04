package com.dani.spring_boot_microservice_3_api_gateway.security.jwt;

import com.dani.spring_boot_microservice_3_api_gateway.security.UserPrincipal;
import com.dani.spring_boot_microservice_3_api_gateway.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtProviderImpl implements JwtProvider{


    @Value("${app.jwt.secret}")
    private String JWT_SECRET;

    @Value("${app.jwt.expiration-in-ms}")
    private Long JWT_EXPIRATION_IN_MS;

    //para crear token basado en la data del usario
    @Override
    public String generateToken(UserPrincipal auth){

        String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        //para desencriptar el token por una key

        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(auth.getUsername())
                .claim("roles" , authorities)
                .claim("userId", auth.getId())
                .setExpiration(new Date(System.currentTimeMillis()+ JWT_EXPIRATION_IN_MS))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

    }
    //metodo publico para obtener la autentificacion

    @Override
    public Authentication getAuthentication(HttpServletRequest request){

        Claims claims = extractClaims(request);
        if(claims ==null) {
            return null;
        }

        String username = claims.getSubject();
        Long userId = claims.get("userId", Long.class);

        Set<GrantedAuthority> authorities = Arrays.stream(claims.get("roles").toString().split(","))
                .map(SecurityUtils::convertToAuthority)
                .collect(Collectors.toSet());

        UserDetails userDetails = UserPrincipal.builder()
                .username(username)
                .authorities(authorities)
                .id(userId)
                .build();

        if(username == null){
            return null;
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null , authorities);

    }

    //Metodo para saber si el token es valido
    @Override
    public boolean istTokenValid(HttpServletRequest request){

        Claims claims = extractClaims(request);
        if (claims == null){
            return false;
        }
        if(claims.getExpiration().before(new Date())){

            return false;
        }

        return true;
    }

    //metodo para extraer todo los Claims(propiedades o valores dentro del Token)

    private Claims extractClaims(HttpServletRequest request){

        String token = SecurityUtils.extractAuthTOkenFromRequest(request);
        if(token == null){
            return null;
        }

        //Creacion llave de acceso
        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        return  Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();


    }
}
