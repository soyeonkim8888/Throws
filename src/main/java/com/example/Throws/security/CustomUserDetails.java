package com.example.Throws.security;

import com.example.Throws.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member){
        this.member = member;
    }

    @Override
    public String getUsername(){
        return member.getEmail();
    }

    @Override
    public String getPassword(){
        return member.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(member.getRole().name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; }
    @Override
    public boolean isAccountNonLocked() {
        return true; }
    @Override
    public boolean isCredentialsNonExpired() {
        return true; }
    @Override
    public boolean isEnabled() {
        return true; }

    public Member getMember(){
        return member;
    }

}
