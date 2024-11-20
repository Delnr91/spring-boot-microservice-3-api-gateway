package com.dani.spring_boot_microservice_3_api_gateway.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    //injecion objeto clase personalizada de un usuario
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    //metodo encriptar el password

    @Autowired
    private PasswordEncoder passwordEncoder;

    //auth de la validacion de los usuarios
    @Bean
    public AuthenticationManager authenticationManagert(AuthenticationConfiguration authenticationConfiguration) throws Exception{

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(){
        return new JwtAuthorizationFilter();
    }
    //metodo principal

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{

        AuthenticationManagerBuilder auth = http.getSharedObject(
                AuthenticationManagerBuilder.class);

        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);

       AuthenticationManager authenticationManager = auth.build();

       //componentes protegidos y de libre acceso
        http.csrf().disable().cors().disable()
                .authorizeHttpRequests().
                antMatchers("/api/authentication/sing-in", "/api/authentication/sing-up").permitAll()
                //end point authenticados
                .and()
                .authenticationManager(authenticationManager)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

}
