package com.epic.securityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private RestAuthenticationEntryPoint authenticationEntryPoint;

	@Bean
	@SuppressWarnings({ "removal", "deprecation" })
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf()
			.disable()
			.authorizeRequests()
			.requestMatchers("/public/**").permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.httpBasic()
			.authenticationEntryPoint(authenticationEntryPoint);

		return http.build();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
		auth.inMemoryAuthentication()
			.passwordEncoder(PasswordEncoderConfig.passwordEncoder())
			.withUser("epic_novelvox")
			.password(PasswordEncoderConfig.passwordEncoder().encode("NovelVox123"))
			.roles("USER");
	}

}
