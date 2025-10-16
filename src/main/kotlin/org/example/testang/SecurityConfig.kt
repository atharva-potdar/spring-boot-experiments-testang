package org.example.testang

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    // To override default security config, we create a security filter chain
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests{ authorize ->

                // If we're trying to access h2-console, ask for auth
                // Otherwise, no auth
                // This is a blacklist based system

                authorize
                    .requestMatchers("/h2-console/**").authenticated()
                    .requestMatchers("/tryAuthentication").authenticated()
                    .requestMatchers("/deleteUser").authenticated()
                    .anyRequest().permitAll()

            }
            .formLogin { } // enables the login page
            .logout { } // add a logout page at /logout
            .csrf { csrf -> csrf.disable() }
            .headers { headers->
                // X-Frame-Options disallows embedding a web page in a frame to prevent clickjacking
                // Unfortunately, h2-console still uses frames to render its console (no wonder looking at its console lol)
                // So we allow it to embed web pages in a frame ONLY IF it comes from the same site - which it does! Cooool.

                headers.frameOptions { frameOptions -> frameOptions.sameOrigin() }
            }

        return http.build()
    }

}