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

import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.ext.profile.AccessToken;
import org.pac4j.core.ext.profile.AccessTokenProfileDefinition;
import org.pac4j.core.ext.profile.TokenProfile;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class AccessTokenAuthenticator extends TokenAuthenticator<TokenCredentials, TokenProfile, AccessToken> {
	
	@Override
    protected void internalInit() {
        defaultProfileDefinition(new AccessTokenProfileDefinition(x -> new TokenProfile()));
        super.internalInit();
    }
	
	/**
     * Get the access token from OAuth credentials.
     *
     * @param credentials credentials
     * @return the access token
     */
    protected AccessToken getAccessToken(final TokenCredentials credentials) {
    	return new AccessToken(credentials.getToken());
    }    
    
}
