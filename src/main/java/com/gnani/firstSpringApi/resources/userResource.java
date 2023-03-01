package com.gnani.firstSpringApi.resources;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.standard.Severity;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gnani.firstSpringApi.Constants;
import com.gnani.firstSpringApi.domain.User;
import com.gnani.firstSpringApi.services.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class userResource {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dSource;

    @Autowired
    UserService userService;

    /**
     * This method handles the post method to register the user
     * 
     * @param userMap
     * @return name
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody Map<String, Object> userMap) {

        String firstName = (String) userMap.get("firstName");
        String lastName = (String) userMap.get("lastName");
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");

        User user = userService.registerUser(firstName, lastName, email, password);

        return new ResponseEntity<>(GenerateJwt(user), HttpStatus.CREATED);
    }

    /**
     * @param userMap
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, Object> userMap) {
        String email = (String) userMap.get("email");
        if (email != null)
            email = email.toLowerCase();
        String password = (String) userMap.get("password");
        User user = userService.validateUser(email, password);

        return new ResponseEntity<>(GenerateJwt(user), HttpStatus.OK);
    }

    @GetMapping("/test")
    public String test(ServletRequest servletRequest) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (authorizationHeader != null) {
            String[] authorizatonHdrArr = authorizationHeader.split("Bearer ");
            if (authorizationHeader.length() > 1 && authorizatonHdrArr[1] != null) {
                String token = authorizatonHdrArr[1];
                Claims claims = (Claims) Jwts.parser().setSigningKey(Constants.API_SECRET_KEY)
                        .parse(token).getBody();
                return claims.get("email").toString();

            } else {
                return "Error in the Authorization Header";
            }
        } else {
            return "Authorization Header cannot be null";
        }
    }

    /**
     * Creates JWT token for the user.
     * 
     * @param user
     * @return
     */
    public Map<String, String> GenerateJwt(User user) {
        long timeStamp = System.currentTimeMillis();

        JwtBuilder jsontoken = Jwts.builder().signWith(SignatureAlgorithm.HS256, Constants.API_SECRET_KEY)
                .setIssuedAt(new Date(timeStamp))
                .setExpiration(new Date(timeStamp + Constants.TOKEN_VALIDITY))
                .claim("userId", user.getUserId())
                .claim("email", user.getEmail())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName());

        String token = jsontoken.compact();

        Map<String, String> jwtToken = new HashMap<>();
        jwtToken.put("token", token);
        return jwtToken;
    }

}
