package com.seesunit.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

//설정파일임을 등록하기 위한 어노테이션
@Configuration
//OAuth 서버에 필요한 기본 설정(토큰을 발행하고, 발행된 토큰을 검증하는 역할)
@EnableAuthorizationServer
//인증 서버에 대한 설정
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;

    //클라이언트에 대한 설정(클라이언트 정보 관리, 제공할 scope나 권한)
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()//DB가 아닌 메모리에 저장하는 방식
                    .withClient("authorization") //Authorization Code Grant Type
                        .secret("secret")
                        .authorizedGrantTypes("authorization_code","refresh_token")
                        .scopes("read_profile")
                        .redirectUris("http://localhost:8080/callback")
                        .accessTokenValiditySeconds(60*10)
                        .refreshTokenValiditySeconds(60*60)
                    .and()
                    .withClient("implicit") //Implicit Grant Type 방식
                        .secret("secret")
                        .authorizedGrantTypes("implicit")
                        .redirectUris("http://localhost:8080/callback")
                        .scopes("read_profile")
                        .accessTokenValiditySeconds(60*10)
                    .and()
                    .withClient("resource") //Resource Owner Password Credentials Grant
                        .secret("secret")
                        .authorizedGrantTypes("password","refresh_token")
                        .scopes("read_profile")
                        .accessTokenValiditySeconds(60*10)
                        .refreshTokenValiditySeconds(60*60)
                    .and()
                    .withClient("client") //client라는 이름과 secret라는 비밀번호를 사용해 권한 서버를 사용할 클라이언트 설정
                        .secret("secret")// secret
                        .authorizedGrantTypes("client_credentials")
                        .scopes("read_profile")
                        .accessTokenValiditySeconds(60*10);

    }
    //토큰 인증, 토큰 엔드포인트 정의
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }
}
