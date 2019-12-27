/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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
package org.pac4j.core.ext.http.callback;

import java.util.HashMap;
import java.util.Map;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.callback.QueryParameterCallbackUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.util.CommonHelper;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">wandl</a>
 */

public class QueryParameterCallbackUrlExtResolver extends QueryParameterCallbackUrlResolver {

	/**
	 * If <code>true</code>, will always redirect to the value of {@code callbackUrl}
	 * (defaults to <code>false</code>).
	 */
	private boolean alwaysUseCallbackUrl = false;
	/** 
	 * The location of the client callback URL, i.e. https://localhost:8080/myapp/callback 
	 */
	private String callbackUrl;
	
    private Map<String, String> customParams = new HashMap<>();

    public QueryParameterCallbackUrlExtResolver() {
    }
    
    public QueryParameterCallbackUrlExtResolver(final boolean alwaysUseCallbackUrl, final  String callbackUrl, Map<String, String> customParams) {
    	this.alwaysUseCallbackUrl = alwaysUseCallbackUrl;
    	this.callbackUrl = callbackUrl;
    	this.customParams = customParams;
    }
 
    @Override
    public String compute(final UrlResolver urlResolver, final String url, final String clientName, final WebContext context) {
    	
        String newUrl = this.isAlwaysUseCallbackUrl() ? this.getCallbackUrl() : urlResolver.compute(url, context);
        
        if (newUrl != null && !newUrl.contains(this.getClientNameParameter() + '=')) {
            newUrl = CommonHelper.addParameter(newUrl, this.getClientNameParameter(), clientName);
        }
        for (final Map.Entry<String, String> entry : this.customParams.entrySet()) {
            newUrl = CommonHelper.addParameter(newUrl, entry.getKey(), entry.getValue());
        }
        return newUrl;
    }
    
	public String getCallbackUrl() {
		return callbackUrl;
	}
	
	public boolean isAlwaysUseCallbackUrl() {
		return alwaysUseCallbackUrl;
	}
	
}
