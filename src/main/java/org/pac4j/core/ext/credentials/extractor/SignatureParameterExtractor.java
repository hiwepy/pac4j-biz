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

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.ext.Pac4jExtConstants;
import org.pac4j.core.ext.credentials.SignatureCredentials;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureParameterExtractor implements CredentialsExtractor<SignatureCredentials> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String signatureParamName = Pac4jExtConstants.SIGNATURE_PARAM;
	
	private String paylodParamName = Pac4jExtConstants.PAYLOAD_PARAM;

    private boolean supportGetRequest = true;

    private boolean supportPostRequest;
    
    private String charset = StandardCharsets.UTF_8.name();
    
	public SignatureParameterExtractor(String paylodParamName, String signatureParamName) {
		this(signatureParamName, paylodParamName, false, true, StandardCharsets.UTF_8.name());
	}
	
	public SignatureParameterExtractor(String paylodParamName, String signatureParamName, String charset) {
		this(signatureParamName, paylodParamName, false, true, charset);
	}
	
	public SignatureParameterExtractor(String paylodParamName, String signatureParamName, boolean supportGetRequest,
			boolean supportPostRequest, String charset) {
		this.paylodParamName = paylodParamName;
		this.signatureParamName = signatureParamName;
        this.supportGetRequest = supportGetRequest;
        this.supportPostRequest = supportPostRequest;
        this.charset = charset;
	}
	
	@Override
    public Optional<SignatureCredentials> extract(WebContext context) {
		
		logger.debug("supportGetRequest: {}", this.supportGetRequest);
		logger.debug("supportPostRequest: {}", this.supportPostRequest);
		
        if (ContextHelper.isGet(context) && ! supportGetRequest) {
            throw new CredentialsException("GET requests not supported");
        } else if (ContextHelper.isPost(context) && !supportPostRequest) {
            throw new CredentialsException("POST requests not supported");
        }
        
        logger.debug("paylodParamName: {}", this.paylodParamName);
        logger.debug("signatureParamName: {}", this.signatureParamName);
        
        Optional<String> paylod = context.getRequestParameter(this.paylodParamName);
        Optional<String> signature = context.getRequestParameter(this.signatureParamName);
        if (!paylod.isPresent() || !signature.isPresent()) {
    		return Optional.empty();
        }
        
    	logger.debug("paylod : {}", paylod.get());
    	logger.debug("signature : {}", signature.get());
    	return Optional.of(new SignatureCredentials(paylod.get(), signature.get()));
    }
	
	@Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "signatureParamName", signatureParamName,
        		"paylodParamName", paylodParamName, "supportGetRequest", supportGetRequest, "supportPostRequest", supportPostRequest, "charset", charset);
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

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getSignatureParamName() {
		return signatureParamName;
	}

	public void setSignatureParamName(String signatureParamName) {
		this.signatureParamName = signatureParamName;
	}

	public String getPaylodParamName() {
		return paylodParamName;
	}

	public void setPaylodParamName(String paylodParamName) {
		this.paylodParamName = paylodParamName;
	}

}
