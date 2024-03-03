package com.bob.bankapispringapp.configuration.enums;

import lombok.Getter;

@Getter
public enum AuthUrlMapping {
    CLIENT(ROLE.ROLE_CLIENT.name(), new String[] {
            "/client/**",
            "/branch/all",
            "/branch/{id}",
            "/account/**"
    }),

    ADMIN(ROLE.ROLE_ADMIN.name(), new String[] {
            "/account/**",
            "/branch/**",
            "/client/**",
            "/auth/**"
    }),
    PERMIT_ALL(null, new String[] {
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/auth/**",
            "/client/register",
            "/client/{id}",
    }),

    ANY_AUTHENTICATED(null, new String[] {
            "/branch/all",
            "/branch/{id}"
    });


    private final String role;
    private final String[] urls;

    AuthUrlMapping(String role, String[] urls) {
        this.role = role;
        this.urls = urls;
    }

}
