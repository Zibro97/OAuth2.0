package com.seesunit.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
//전반적인 서버에 대한 security 설정
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    @Bean
    //인증 토큰을 관리하는 클래스.
    //리소스에 대한 요청이 들어오면 AuthenticationManager는 Authenticate 메서드를 호출해 후속요청에 사용할 권한 부여 인스턴스를 가져옴
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //패스워드 인코딩 방식을 결정해주는 빈
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    //client 회원 정보 관리
    //클라이언트에서 사용될 사용자 정보를 등록
    @Bean
    public UserDetailsService userDetailsService() {
        PasswordEncoder encoder = passwordEncoder();
        String password = encoder.encode("pass");

        //메모리에 유저를 생성
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user").password(password).roles("USER").build());
        manager.createUser(User.withUsername("admin").password("{noop}pass").roles("USER", "ADMIN").build());
        return manager;
    }
    //
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .disable()
                .authorizeRequests() //시큐리티 처리에 있어 request 정보를 서블릿에 전달하기 위한 목적으로 사용하겠다
                    .anyRequest()
                    .authenticated() //모든 request는 인증이 된 상태여야 한다.
                    .and()
                .formLogin() //시큐리티에서 제공하는 로그인 form을 사용하겠다
                    .and()
                .httpBasic(); //특정 resource에 대한 접근을 요청 시 브라우저가 사용자에게 username과 password를 확인해 인가를 제한하는 방법
    }
}
