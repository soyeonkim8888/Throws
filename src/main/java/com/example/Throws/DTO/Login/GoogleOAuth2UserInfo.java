package com.example.Throws.DTO.Login;


import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo{
    public GoogleOAuth2UserInfo(Map<String,Object> attributes){
        super(attributes);
    }
    @Override
    public String getEmail(){
        return (String) attributes.get("email");
    }
}
