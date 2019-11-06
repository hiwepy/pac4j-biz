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

import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.ext.credentials.authenticator.TokenAuthenticator;
import org.pac4j.core.ext.credentials.extractor.TokenParameterExtractor;
import org.pac4j.core.ext.profile.Token;
import org.pac4j.core.ext.profile.TokenProfile;
import org.pac4j.core.ext.profile.creator.TokenProfileCreator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.client.direct.ParameterClient;

@SuppressWarnings("rawtypes")
public abstract class TokenClient<C extends TokenCredentials, P extends TokenProfile, T extends Token> extends ParameterClient {
	
	public TokenClient() {
	}

	public TokenClient(final String parameterName, final TokenAuthenticator<C, P, T> tokenAuthenticator) {
		super(parameterName, tokenAuthenticator);
	}

	public TokenClient(final String parameterName, final TokenAuthenticator<C, P, T> tokenAuthenticator,
			final ProfileCreator profileCreator) {
		super(parameterName, tokenAuthenticator, profileCreator);
	}
	
	@Override
	protected void clientInit() {
		super.clientInit();
		defaultProfileCreator(new TokenProfileCreator<TokenCredentials>());
		defaultCredentialsExtractor(new TokenParameterExtractor(this.getParameterName(), this.isSupportGetRequest(), this.isSupportPostRequest()));
		CommonHelper.assertNotNull("tokenAuthenticator", getAuthenticator());
	}
	
}
