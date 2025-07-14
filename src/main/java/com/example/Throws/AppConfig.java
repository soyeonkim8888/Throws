package com.example.Throws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//로그인 프로세스 Stage 0.사용자가 입력한 비밀번호와 DB에 저장된 해시 비밀번호를 비교하기 위해 등록해야 하는 passwordEncoder
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
