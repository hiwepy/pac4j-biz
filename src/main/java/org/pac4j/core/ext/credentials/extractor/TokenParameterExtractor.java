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
package org.pac4j.core.ext.credentials.extractor;

import java.util.Optional;

import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenParameterExtractor extends ParameterExtractor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final String parameterName;

    private boolean supportGetRequest;

    private boolean supportPostRequest;
    
	public TokenParameterExtractor(String parameterName) {
		this(parameterName, false, true);
	}
	
	public TokenParameterExtractor(String parameterName, boolean supportGetRequest, boolean supportPostRequest) {
		super(parameterName, supportGetRequest, supportPostRequest);
		this.parameterName = parameterName;
        this.supportGetRequest = supportGetRequest;
        this.supportPostRequest = supportPostRequest;
	}
	
	@Override
    public Optional<TokenCredentials> extract(WebContext context) {
		
		logger.debug("supportGetRequest: {}", this.supportGetRequest);
		logger.debug("supportPostRequest: {}", this.supportPostRequest);
		
        if (ContextHelper.isGet(context) && ! supportGetRequest) {
            throw new CredentialsException("GET requests not supported");
        } else if (ContextHelper.isPost(context) && !supportPostRequest) {
            throw new CredentialsException("POST requests not supported");
        }

        logger.debug("parameterName: {}", this.parameterName);
       
        
        final Optional<String> value = context.getRequestParameter(this.parameterName);
        
        logger.debug("token : {}", value.get());
        
        if (!value.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new TokenCredentials(value.get()));
    }
	
	@Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "parameterName", parameterName,
                "supportGetRequest", supportGetRequest, "supportPostRequest", supportPostRequest);
    }

}
