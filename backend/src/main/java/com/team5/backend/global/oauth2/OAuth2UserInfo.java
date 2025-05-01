package com.team5.backend.global.oauth2;

public interface OAuth2UserInfo {

    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
    String getImageUrl();
}
