package hotelapp.config;

import hotelapp.security.JwtAuthenticationEntryPoint;
import hotelapp.security.JwtAuthenticationFilter;
import hotelapp.security.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@EnableWebSecurity
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class MultiHttpSecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        AppUserDetailsService customUserDetailsService;

        @Autowired
        private JwtAuthenticationEntryPoint unauthorizedHandler;

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter();
        }

        @Override
        public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
            authenticationManagerBuilder
                    .userDetailsService(customUserDetailsService)
                    .passwordEncoder(passwordEncoder());
        }

        @Bean(BeanIds.AUTHENTICATION_MANAGER)
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .cors()
                    .and()
                    .csrf()
                    .disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedHandler)
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .antMatcher("/api/**")
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/api/login")
                    .permitAll()
                    .anyRequest()
                    .authenticated();

            // Add our custom JWT security filter
            http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        }

        @Bean
        public EmbeddedServletContainerCustomizer containerCustomizer() {
            return (container -> {
                container.setContextPath("/");
                container.setPort(Integer.valueOf(System.getenv("PORT")));
            });
        }

        /*@Bean
        CorsConfigurationSource corsConfigurationSource() {
            final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
            return source;
        }*/
    }

    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .anyRequest().permitAll()
                    .and()
                    .formLogin().loginPage("/login")
                    .usernameParameter("email").passwordParameter("password")
                    .and()
                    .logout().logoutSuccessUrl("/login?logout")
                    .and()
                    .exceptionHandling().accessDeniedPage("/error/403")
                    .and()
                    .csrf();

            //http.csrf().disable();
            //CsrfTokenResponseHeaderBindingFilter csrfTokenFilter = new CsrfTokenResponseHeaderBindingFilter();
            //http.addFilterAfter(csrfTokenFilter, CsrfFilter.class);
        }
    }
}
