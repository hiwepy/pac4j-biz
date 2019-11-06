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
package org.pac4j.core.ext.utils;

import java.util.Map;
import java.util.Optional;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.HttpConstants.HTTP_METHOD;
import org.pac4j.core.context.WebContext;

/**
 * TODO
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
public class WebUtils {

	public static String buildURL(String baseURL, Map<String, String> paramsMap){
		if(paramsMap == null){
			return baseURL;
		}
		StringBuilder builder = new StringBuilder(baseURL);
		for (String key : paramsMap.keySet()) {
			builder.append(builder.indexOf("?") > 0 ? "&" : "?").append(key).append("=").append(String.valueOf(paramsMap.get(key)));
		}
		return builder.toString();
	}
	
	public static boolean isAjaxRequest(WebContext context) {
		Optional<String> header = context.getRequestHeader(HttpConstants.AJAX_HEADER_NAME);
		return header.isPresent() ? header.get().contains(HttpConstants.AJAX_HEADER_VALUE) : false;
	}

	public static boolean isContentTypeJson(WebContext context) {
		Optional<String> header = context.getRequestHeader(HttpConstants.CONTENT_TYPE_HEADER);
		return header.isPresent() ? header.get().contains(HttpConstants.APPLICATION_JSON) : false;
	}

	public static boolean isPostRequest(WebContext context) {
		return HTTP_METHOD.POST.name().equals(context.getRequestMethod());
	}

}
