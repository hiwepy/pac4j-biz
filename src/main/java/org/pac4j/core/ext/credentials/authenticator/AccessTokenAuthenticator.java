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

import java.net.HttpURLConnection;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.ext.profile.AccessToken;
import org.pac4j.core.ext.profile.AccessTokenProfileDefinition;
import org.pac4j.core.ext.profile.TokenProfile;
import org.pac4j.core.ext.utils.ContentType;
import org.pac4j.core.ext.utils.HttpHeaders;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpUtils2;

/**
 * TODO
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class AccessTokenAuthenticator extends TokenAuthenticator<TokenCredentials, TokenProfile, AccessToken> {
	
	private String profileUrl;
	// 连接超时 单位毫秒
	private int connectTimeout = 10000;
	// 读取超时 单位毫秒
	private int readTimeout = 3000;

	private String sessionID = "";
		
	public AccessTokenAuthenticator() {
	}
	
	public AccessTokenAuthenticator(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	
	@Override
    protected void internalInit() {
		CommonHelper.assertNotNull("profileUrl", profileUrl);
        defaultProfileDefinition(new AccessTokenProfileDefinition(profileUrl, x -> new TokenProfile()));
        super.internalInit();
    }
	
	@Override
	protected void signRequest(WebContext context, AccessToken token, HttpURLConnection connection) {
		
		// Post 请求不能使用缓存
		connection.setUseCaches(this.isSupportPostRequest());  
		
	    // This method takes effects to
        // every instances of this class.
        // URLConnection.setFollowRedirects是static函数，作用于所有的URLConnection对象。
        // connection.setFollowRedirects(true);
      
        // This methods only
        // takes effacts to this
        // instance.
        // URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
		connection.setInstanceFollowRedirects(false);
		
		//设置Session ID 解决多次请求不同会话问题
		connection.setRequestProperty("Cookie", sessionID == null ? HttpUtils2.getSessionID(connection) : sessionID);
	    
	    // 设置通用的请求属性 (模拟浏览器请求头) 
		connection.setRequestProperty(HttpHeaders.ACCEPT, "*/*");  
		connection.setRequestProperty(HttpHeaders.CONNECTION, "Keep-Alive");  
		connection.setRequestProperty(HttpHeaders.USER_AGENT, "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
		
		/* 
		 * 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode进行编码
		 */	
		connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8");
		
		/*
		
		HttpURLConnection是基于HTTP协议的，其底层通过socket通信实现。如果不设置超时（timeout），在网络异常的情况下，可能会导致程序僵死而不继续往下执行。可以通过以下两个语句来设置相应的超时：
		System.setProperty("sun.net.client.defaultConnectTimeout", 超时毫秒数字符串);
		System.setProperty("sun.net.client.defaultReadTimeout", 超时毫秒数字符串);
		
		其中： sun.net.client.defaultConnectTimeout：连接主机的超时时间（单位：毫秒）
		sun.net.client.defaultReadTimeout：从主机读取数据的超时时间（单位：毫秒）
		
		例如：
		System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
		System.setProperty("sun.net.client.defaultReadTimeout", "30000");
		
		JDK 1.5以前的版本，只能通过设置这两个系统属性来控制网络超时。在1.5中，还可以使用HttpURLConnection的父类URLConnection的以下两个方法：
		setConnectTimeout：设置连接主机超时（单位：毫秒）
		setReadTimeout：设置从主机读取数据超时（单位：毫秒） 

		*/
		//System.setProperty("sun.net.client.defaultConnectTimeout", connectTimeout+"");
		//System.setProperty("sun.net.client.defaultReadTimeout", readTimeout+""); 
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);
		
		super.signRequest(context, token, connection);
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

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getProfileUrl() {
		return profileUrl;
	}    
    
}
