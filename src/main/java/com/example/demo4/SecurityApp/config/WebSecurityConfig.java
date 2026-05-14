package com.example.demo4.SecurityApp.config;

import com.example.demo4.SecurityApp.entities.enums.Role;
import com.example.demo4.SecurityApp.filters.JwtAuthFilter;
import com.example.demo4.SecurityApp.handlers.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    private final String[] publicRoutes= {"/auth/**","/home.html"};

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicRoutes)
                        .permitAll() // permits all requests that match the endpoints mentioned here
//                        .requestMatchers("/posts/**").hasAnyRole(("ADMIN"))
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/posts/**").hasAnyRole(Role.ADMIN.name(), Role.CREATOR.name())
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oAuth2Config -> oAuth2Config
                        .failureUrl("/login?error=true")
                        .successHandler(oAuth2SuccessHandler));
//                .formLogin(Customizer.withDefaults()); // Default Login form behaviour - Login at /login, logout at /logout
//                .formLogin(formLoginConfig -> formLoginConfig.loginPage("/newlogin.html")); // Configures custom Login path

        return httpSecurity.build();
    }

    //In Memory Service - User for Testing Purposes
//    @Bean
//    UserDetailsService myInMemoryUserDetailsService() {
//        UserDetails normalUser = User.withUsername("monji").password(passwordEncoder().encode("monji123"))
//                .roles("USER").build();
//
//        UserDetails adminUser = User.withUsername("admin").password(passwordEncoder().encode("admin")).roles("ADMIN").build();
//
//        return new InMemoryUserDetailsManager(normalUser, adminUser);
//    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
