package com.example.demo4.SecurityApp.utils;

import com.example.demo4.SecurityApp.entities.enums.Permissions;
import com.example.demo4.SecurityApp.entities.enums.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static com.example.demo4.SecurityApp.entities.enums.Permissions.*;
import static com.example.demo4.SecurityApp.entities.enums.Role.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionMapping {

    private final static Map<Role, Set<Permissions>> map = Map.of(
            USER, Set.of(USER_VIEW, POST_VIEW),
            CREATOR, Set.of(USER_VIEW, POST_VIEW, USER_CREATE, USER_UPDATE, POST_CREATE, POST_UPDATE),
            ADMIN, Set.of(USER_VIEW, POST_VIEW, USER_CREATE, USER_DELETE, POST_CREATE, POST_DELETE)
            );

    public static Set<SimpleGrantedAuthority> getAuthoritiesOfRole(Role role) {
        return map.get(role).stream().map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet());
    }
}
