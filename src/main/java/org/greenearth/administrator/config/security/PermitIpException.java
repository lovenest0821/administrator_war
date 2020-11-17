package org.greenearth.administrator.config.security;

import org.springframework.security.core.AuthenticationException;

public class PermitIpException extends AuthenticationException {

    public PermitIpException(String msg) {
        super(msg);
    }
}
