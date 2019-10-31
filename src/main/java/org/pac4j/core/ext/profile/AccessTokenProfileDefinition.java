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
package org.pac4j.core.ext.profile;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Access Token profile definition.
 */
public class AccessTokenProfileDefinition extends TokenProfileDefinition<TokenProfile, AccessToken> {
	
	protected static ObjectMapper mapper;

	static {
		if (mapper == null) {
	        mapper = new ObjectMapper();
	        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    }
	}
	
    public AccessTokenProfileDefinition() {
        super();
    }

    public AccessTokenProfileDefinition(final Function<Object[], TokenProfile> profileFactory) {
        super(profileFactory);
    }
    
    public Map<String, String> getCustomHeaders(){
    	return null;
    };
    
    /**
     * Retrieve the url of the profile of the authenticated user for the provider.
     *
     * @param accessToken only used when constructing dynamic urls from data in the token
     * @return the url of the user profile given by the provider
     */
    public String getProfileUrl(WebContext context, AccessToken accessToken) {
    	return null;
    }

    /**
     * Extract the user profile from the response (JSON, XML...) of the profile url.
     *
     * @param body the response body
     * @return the returned profile
     */
    public TokenProfile extractUserProfile(String body) {
    	final TokenProfile profileClass = this.newProfile();
        final TokenProfile profile;
        try {
            profile = mapper.readValue(body, profileClass.getClass());
        } catch (final IOException e) {
            throw new TechnicalException(e);
        }
        logger.debug("profile: {}", profile);
    	return null;
    }
    
}
