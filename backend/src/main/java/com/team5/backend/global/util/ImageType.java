package com.team5.backend.global.util;

import lombok.Getter;

@Getter
public enum ImageType {

    PROFILE("profiles/"),
    PRODUCT("products/");

    private final String directory;

    ImageType(String directory) {
        this.directory = directory;
    }
}
