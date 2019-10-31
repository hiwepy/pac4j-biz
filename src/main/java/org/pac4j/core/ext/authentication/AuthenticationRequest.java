package org.pac4j.core.ext.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Rest 模式登录认证绑定的参数对象Model
 */
public class AuthenticationRequest {
	
    private String username;
    private String password;
    private String captcha;

    @JsonCreator
    public AuthenticationRequest(@JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("captcha") String captcha) {
        this.username = username;
        this.password = password;
        this.captcha = captcha;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
