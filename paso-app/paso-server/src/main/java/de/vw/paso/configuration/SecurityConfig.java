package de.vw.paso.configuration;

import de.vw.paso.service.accesstoken.AccessTokenDecoderService;
import de.vw.paso.service.auth.AuthRestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http, AccessTokenDecoderService jwtDecoder) throws Exception {
      http
        .authorizeHttpRequests(authorizeHttpRequest -> authorizeHttpRequest
          .requestMatchers(AuthRestService.URL, "/actuator/**").permitAll()
          .anyRequest().authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)));

      return http.build();
    }

}
