package com.example.Throws.DTO.Login;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo{
    public KakaoOAuth2UserInfo(Map<String, Object> attributes){
        super (attributes);
    }

    public String getEmail(){
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        if(kakao_account == null) return  null;
        return (String) kakao_account.get("email");
    }
}
