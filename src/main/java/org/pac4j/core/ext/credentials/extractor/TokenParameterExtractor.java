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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class TokenParameterExtractor extends ParameterExtractor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final String parameterName;

    private boolean supportGetRequest = true;

    private boolean supportPostRequest;
    
    private String charset = StandardCharsets.UTF_8.name();
    
	public TokenParameterExtractor(String parameterName) {
		this(parameterName, false, true);
	}
	
	public TokenParameterExtractor(String parameterName, boolean supportGetRequest, boolean supportPostRequest) {
		super(parameterName, supportGetRequest, supportPostRequest);
		this.parameterName = parameterName;
        this.supportGetRequest = supportGetRequest;
        this.supportPostRequest = supportPostRequest;
	}
	
	public TokenParameterExtractor(String parameterName, boolean supportGetRequest, boolean supportPostRequest, String charset) {
		super(parameterName, supportGetRequest, supportPostRequest);
		this.parameterName = parameterName;
        this.supportGetRequest = supportGetRequest;
        this.supportPostRequest = supportPostRequest;
        this.charset = charset;
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

        logger.debug("ParameterName: {}", this.parameterName);
        
        Optional<String> value = context.getRequestParameter(this.parameterName);
        if (!value.isPresent()) {
        	value = context.getRequestHeader(this.parameterName);
        	if (!value.isPresent()) {
        		return Optional.empty();
        	}
        }
        
        logger.debug("RequestContent: {}",  context.getRequestContent());
        logger.debug("RequestParameters: {}",  JSONObject.toJSONString(context.getRequestParameters()));
        
        try {
        	String tokenString =  URLEncoder.encode(value.get(), getCharset());
        	logger.debug("encoded token : {}", tokenString);
        	return Optional.of(new TokenCredentials(tokenString));
		} catch (UnsupportedEncodingException e) {
			logger.debug("token : {}", value.get());
			return Optional.of(new TokenCredentials(value.get()));
		}
    }
	
	@Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "parameterName", parameterName,
                "supportGetRequest", supportGetRequest, "supportPostRequest", supportPostRequest, "charset", charset);
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

	public String getParameterName() {
		return parameterName;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
