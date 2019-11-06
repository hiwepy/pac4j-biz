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
package org.pac4j.core.ext.credentials.authenticator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.ext.profile.Token;
import org.pac4j.core.ext.profile.TokenProfile;
import org.pac4j.core.ext.profile.TokenProfileDefinition;
import org.pac4j.core.ext.profile.definition.TokenProfileDefinitionAware;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpUtils;
import org.pac4j.core.util.HttpUtils2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public abstract class TokenAuthenticator<C extends TokenCredentials, U extends TokenProfile, T extends Token>
	extends TokenProfileDefinitionAware<U, T>  implements Authenticator<C> {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected static final String AUTHORIZATION_PARAM = "token";
	private boolean supportGetRequest;
	private boolean supportPostRequest;
	/* Map containing user defined headers */
	private Map<String, String> customHeaders = new HashMap<>();
    /* Map containing user defined parameters */
    private Map<String, String> customParams = new HashMap<>();
		
    private String charset = Charset.defaultCharset().name();
    
    
	public TokenAuthenticator() {
	}
	
	public TokenAuthenticator(boolean supportGetRequest, boolean supportPostRequest) {
		this.supportGetRequest = supportGetRequest;
		this.supportPostRequest = supportPostRequest;
	}
	
	@Override
    protected void internalInit() {
    	ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        defaultObjectMapper(mapper);
        
    }
	
	@Override
    public void validate(final C credentials, final WebContext context) {
        
    	if (credentials == null) {
            throw new CredentialsException("No credential");
        }
        
        String token = credentials.getToken();
        if (CommonHelper.isBlank(token)) {
            throw new CredentialsException("Token cannot be blank");
        }
        
        final Optional<U> profile = retrieveUserProfileFromToken(context , credentials);
        
        logger.debug("profile: {}", profile.get());
        credentials.setUserProfile(profile.get());
        
    }
	
	/**
     * Get the access token from OAuth credentials.
     *
     * @param credentials credentials
     * @return the access token
     */
    protected abstract T getAccessToken(final C credentials);

    /**
     * Retrieve the user profile from the access token.
     *
     * @param context the web context
     * @param accessToken the access token
     * @return the user profile
     */
    protected Optional<U> retrieveUserProfileFromToken(final WebContext context, final C credentials) {
    	
    	final T accessToken = getAccessToken(credentials);
    	
    	logger.debug("accessToken: {}", accessToken.getRawResponse());
    	
    	TokenProfileDefinition<U, T> profileDefinition = getProfileDefinition();
		CommonHelper.assertNotNull("profileDefinition", profileDefinition);
    	
        final String profileUrl = profileDefinition.getProfileUrl(context, accessToken);

        final String body = callRestApi(context, accessToken, profileUrl);
        logger.debug("body: {}", body);
        if (body == null) {
            throw new HttpCommunicationException("No data found for accessToken: " + accessToken);
        }        
        final U profile = (U) getProfileDefinition().extractUserProfile(body);
        return Optional.of(profile);
    }
    
    /**
     * Return the body from the REST API, passing the token auth.
     * To be overridden using another HTTP client if necessary.
     *
    * @param context the web context
     * @param accessToken the access token
     * @param profileUrl  url of the data
     * @param verb        method used to request data
     * @return the response body
     */
    protected String callRestApi(final WebContext context, final T accessToken, final String profileUrl) {
    	
    	logger.debug("accessToken: {} / profileUrl: {}", accessToken.getRawResponse(), profileUrl);
        final long t0 = System.currentTimeMillis();
         
        HttpURLConnection connection = null;
        try {
        	
        	if (this.isSupportPostRequest()) {
        		connection = HttpUtils2.openPostConnection(profileUrl, getCustomHeaders(), getCustomParams(), getCharset());
			} else {
				connection = HttpUtils2.openGetConnection(profileUrl, getCustomHeaders(), getCustomParams());
			}
    		
            signRequest(context, accessToken, connection);
            
            int code = connection.getResponseCode();
            final long t1 = System.currentTimeMillis();
            logger.debug("Request took: " + (t1 - t0) + " ms for: " + profileUrl);
            
            if (code == 200) {
                logger.debug("Authentication success for token: {}", accessToken.getRawResponse());
                return HttpUtils.readBody(connection);
            } else if (code == 401 || code == 403) {
                logger.info("Authentication failure for token: {} -> {}", accessToken.getRawResponse(), HttpUtils.buildHttpErrorMessage(connection));
                return null;
            } else {
                logger.warn("Unexpected error for token: {} -> {}", accessToken.getRawResponse(), HttpUtils.buildHttpErrorMessage(connection));
                return null;
            }
        } catch (final IOException e) {
        	throw new HttpCommunicationException("Error getting body: " + e.getMessage());
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }

    /**
     * Sign the request.
     *
     * @param service the service
     * @param token the token
     * @param request the request
     */
    protected void signRequest(final WebContext context,  T token, HttpURLConnection connection) {}

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

	public Map<String, String> getCustomHeaders() {
		return customHeaders;
	}

	public void setCustomHeaders(Map<String, String> customHeaders) {
		this.customHeaders = customHeaders;
	}

	public Map<String, String> getCustomParams() {
		return customParams;
	}

	public void setCustomParams(Map<String, String> customParams) {
		this.customParams = customParams;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
    
}
