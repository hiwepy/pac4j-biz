/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.pac4j.core.ext.client;

import java.util.Optional;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.http.RedirectionActionHelper;
import org.pac4j.core.ext.credentials.authenticator.TokenAuthenticator;
import org.pac4j.core.ext.credentials.extractor.TokenParameterExtractor;
import org.pac4j.core.ext.profile.Token;
import org.pac4j.core.ext.profile.TokenProfile;
import org.pac4j.core.ext.profile.creator.TokenProfileCreator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;

public abstract class TokenClient<P extends TokenProfile, T extends Token> extends DirectClient<TokenCredentials> {

	private String parameterName = "";
	
	private boolean supportGetRequest = false;
	
	private boolean supportPostRequest = true;
	/** 
	 * The location of the client login URL, i.e. https://localhost:8080/myapp/login 
	 */
	private String loginUrl;
	
	public TokenClient() {
	}

	public TokenClient(final String parameterName, final TokenAuthenticator<P, T> tokenAuthenticator) {
		this.parameterName = parameterName;
        defaultAuthenticator(tokenAuthenticator);
	}

    public TokenClient(final String parameterName,
                           final TokenAuthenticator<P, T> tokenAuthenticator,
                           final ProfileCreator<TokenCredentials> profileCreator) {
        this.parameterName = parameterName;
        defaultAuthenticator(tokenAuthenticator);
        defaultProfileCreator(profileCreator);
    }
	
	@Override
	protected void clientInit() {
		defaultProfileCreator(new TokenProfileCreator());
		defaultCredentialsExtractor(new TokenParameterExtractor(this.getParameterName(), this.isSupportGetRequest(), this.isSupportPostRequest()));
		
		// ensures components have been properly initialized
        CommonHelper.assertNotNull("credentialsExtractor", getCredentialsExtractor());
        CommonHelper.assertNotNull("authenticator", getAuthenticator());
        CommonHelper.assertNotNull("profileCreator", getProfileCreator());
	}

	@Override
	protected Optional<TokenCredentials> retrieveCredentials(WebContext context) {
		init();
        try {
            final Optional<TokenCredentials> credentials = super.retrieveCredentials(context);
            if (!credentials.isPresent()) {
                // redirect to the login page
                logger.debug("redirectionUrl: {}", getLoginUrl());
                throw RedirectionActionHelper.buildRedirectUrlAction(context, getLoginUrl());
            }

            return credentials;
        } catch (CredentialsException e) {
            logger.error("Failed to retrieve or validate Token credentials", e);
            return Optional.empty();
        }
	}
	
	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public boolean isSupportGetRequest() {
		return supportGetRequest;
	}

	public void setSupportGetRequest(boolean supportGetRequest) {
		this.supportGetRequest = supportGetRequest;
	}

	public boolean isSupportPostRequest() {
		return supportPostRequest;
	}

	public void setSupportPostRequest(boolean supportPostRequest) {
		this.supportPostRequest = supportPostRequest;
	}
	
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	
	public String getLoginUrl() {
		return loginUrl;
	}
}
