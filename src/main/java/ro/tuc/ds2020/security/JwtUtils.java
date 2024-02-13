    package ro.tuc.ds2020.security;

    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.SignatureAlgorithm;
    import io.jsonwebtoken.*;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.stereotype.Component;

    import java.util.Date;
    import java.util.List;
    import java.util.stream.Collectors;

    @Component
    public class JwtUtils {
        @Value("${app.jwtSecret}")
        private String jwtSecret;

        @Value("${app.jwtExpirationMs}")
        private int jwtExpirationMs;

        public String generateJwtToken(Authentication authentication) {

            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userPrincipal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return Jwts.builder()
                    .setSubject((userPrincipal.getUsername()))
                    .claim("roles", roles)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();
        }

        public String getUserNameFromJwtToken(String token) {
            return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
        }
        public boolean validateJwtToken(String authToken) {
            try {
                Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
                return true;
            } catch (SignatureException e) {
                System.out.println("Invalid JWT signature: " +  e.getMessage());
            } catch (MalformedJwtException e) {
                System.out.println("Invalid JWT token: " + e.getMessage());
            } catch (ExpiredJwtException e) {
                System.out.println("JWT token is expired: " + e.getMessage());
            } catch (UnsupportedJwtException e) {
                System.out.println("JWT token is unsupported: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("JWT claims string is empty: " +  e.getMessage());
            }
            return false;
        }


    }