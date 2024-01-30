package recipes.table.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${keycloak.table.public-key}")
    private String rsaPublicKey;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String token = extractToken(request);
        if (token != null) {
            try {
                Jws<Claims> claims = validateToken(token);
                if (claims != null) {
                    if ("tablerecipes".equals(claims.getPayload().get("azp"))) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(claims.getPayload().getSubject(), null, null);
                        usernamePasswordAuthenticationToken.setDetails(claims.getPayload());
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                log.warn("Expired token used, please try again... Reason: {} ", e.getMessage());
            }

        }

        filterChain.doFilter(request, response);
    }

    private Jws<Claims> validateToken(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Validate and parse the provided token...
        PublicKey publicKey = getPublicKey();
        return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token);
    }

    private PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(rsaPublicKey));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(keySpec);
    }

    private String extractToken(HttpServletRequest request) {
        // Extract the token from the request header
        return request.getHeader("Authorization");
    }
}
