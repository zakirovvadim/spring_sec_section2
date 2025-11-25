package ru.vadim.springsecsection2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import ru.vadim.springsecsection2.exceptionhandling.CustomBasicAuthenticationEntryPoint;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("!prod")
public class ProjectSecurityConfig {
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests((requests) -> requests.anyRequest().denyAll()); запретит всех
//        http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll()); разрешит всем
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                .requestMatchers("/notices", "/contact", "/error", "/register").permitAll());
        http.formLogin(withDefaults());
//        http.redirectToHttps(https -> https.requestMatchers(AnyRequestMatcher.INSTANCE)); // толкьо HTTP, по новым правилам вообще ничего не указываем
        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())); // использование кастомного сообщения об ошибке 401 только для httpBasic формы, также есть глобальный метод
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withUsername("user").password("{noop}12345").authorities("read").build();
//        UserDetails admin = User.withUsername("admin").password("$2a$12$NUSBnw/flPI5Hogk752fY.sSRFayWRiCIvgN90K3LLN7Oywl5q90K").authorities("admin").build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }

//    @Bean
//    public UserDetailsService userDetailsService(DataSource ds) {
//      return new JdbcUserDetailsManager(ds);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
