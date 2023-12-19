//package com.example.demo.domain.user;
//
//import com.example.demo.infrastructure.repositories.user.UserEntity;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.List;
//
//
//public class UserInfoDetails implements UserDetails {
//    private String login;
//    private String password;
//    //private List<GrantedAuthority> authorities;
//
//    public UserInfoDetails(UserEntity entity) {
//        login = entity.getLogin();
//        password = entity.getPassword();
//        authorities = List.of(new SimpleGrantedAuthority(entity.getRole()));
//    }
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return login;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
