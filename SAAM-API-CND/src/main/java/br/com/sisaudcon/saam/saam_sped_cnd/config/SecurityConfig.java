package br.com.sisaudcon.saam.saam_sped_cnd.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:3000"));  // Corrigido para a origem do front
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .cors()
      .and()
      .csrf().disable()
      .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
    }

}
