package org.starfishrespect.myconsumption.server;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by thibaud on 14.05.15.
 */
public class UserWithSalt extends User {
    private Long salt;

    public UserWithSalt(String username, Long salt, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.salt = salt;
    }

    public Long getSalt() {
        return salt;
    }
}