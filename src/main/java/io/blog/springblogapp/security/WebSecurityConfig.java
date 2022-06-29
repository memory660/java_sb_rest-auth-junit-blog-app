package io.blog.springblogapp.security;

import io.blog.springblogapp.repository.UserRepository;
import io.blog.springblogapp.security.filters.AuthenticationFilter;
import io.blog.springblogapp.security.filters.AuthorizationFilter;
import io.blog.springblogapp.service.JwtService;
import io.blog.springblogapp.service.impl.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, SecurityConstants.LOGIN_URL).permitAll()
                    .antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll()
                    .antMatchers(HttpMethod.GET, SecurityConstants.VERIFICATION_EMAIL_URL).permitAll()
                    .antMatchers(HttpMethod.POST, SecurityConstants.RESET_PASSWORD_URL).permitAll()
                    .antMatchers(HttpMethod.POST, SecurityConstants.RESET_PASSWORD_UPDATE_URL).permitAll()
                    .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                    .antMatchers("/h2-console/**").permitAll()
                .mvcMatchers(SecurityConstants.LOGIN_URL).permitAll()
                    .antMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")

                .anyRequest()
                    .authenticated()
                .and()
                    .headers().frameOptions().disable()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .addFilter(getAuthenticationFilter())
                    .addFilter(new AuthorizationFilter(authenticationManager(), jwtService, userRepository));
    }

    public AuthenticationFilter getAuthenticationFilter() throws Exception {
        final AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), jwtService);
        authenticationFilter.setFilterProcessesUrl(SecurityConstants.LOGIN_URL);

        return authenticationFilter;
    }
}
