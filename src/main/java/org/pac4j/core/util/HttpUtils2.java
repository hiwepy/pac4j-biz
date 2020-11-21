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
package org.pac4j.core.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.ext.utils.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils2 {
	
	protected static final Logger logger = LoggerFactory.getLogger(HttpUtils2.class);
	
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
	
	public static String getSessionID(HttpURLConnection httpConn){
		// Get Session ID
		String key = "";
		String sessionId = "";
		if (httpConn != null) {
			for (int i = 1; (key = httpConn.getHeaderFieldKey(i)) != null; i++) {
				if (key.equalsIgnoreCase("set-cookie")) {
					sessionId = httpConn.getHeaderField(key);
					sessionId = sessionId.substring(0, sessionId.indexOf(";"));
				}
			}
		}
		return sessionId;
	}
	
	public static HttpURLConnection openGetConnection(final URL url) throws IOException {
        return HttpUtils.openConnection(url, HttpConstants.HTTP_METHOD.GET.name(), null);
    }
	
	public static HttpURLConnection openGetConnection(String url, final Map<String, String> params) throws IOException {
		url = buildURL(url, params);
		logger.debug("url : {}", url);
        return HttpUtils.openConnection(new URL(url), HttpConstants.HTTP_METHOD.GET.name(), null);
    }

    public static HttpURLConnection openGetConnection(String url, final Map<String, String> headers, final Map<String,String> params) throws IOException {
    	url = buildURL(url, params);
    	logger.debug("url : {}", url);
        return HttpUtils.openConnection(new URL(url), HttpConstants.HTTP_METHOD.GET.name(), headers);
    }
    
    public static HttpURLConnection openPostConnection(String url, final Map<String, String> headers , final Map<String,String> params) throws IOException{
    	logger.debug("url : {}", url);
		// 此处的urlConnection对象实际上是根据URL的 请求协议(此处是http)生成的URLConnection类 的子类HttpURLConnection,故此处最好将其转化 为HttpURLConnection类型的对象,以便用到HttpURLConnection更多的API.如下: 
		HttpURLConnection httpConn = HttpUtils.openPostConnection(new URL(url), headers); 
		/* 
	        Post请求的OutputStream实际上不是网络流，而是写入内存，在 getInputStream中才真正把写道流里面的内容作为正文与根据之前的配置生成的http request头合并成真正的http request，并在此时才真正向服务器发送。
	        HttpURLConnection.setChunkedStreamingMode函 数可以改变这个模式，设置了ChunkedStreamingMode后，不再等待OutputStream关闭后生成完整的http request一次过发送，而是先发送http request头，
	                       正文内容则是网路流的方式实时传送到服务器。实际上是不告诉服务器http正文的长度，这种模式适用于向服务器传送较大的或者是不容易 获取长度的数据，如文件上传。
	                       与readContentFromPost()最大的不同，设置了块大小为5字节 
	    */
	    httpConn.setChunkedStreamingMode(5);  
	    /* 
	     * 注意，下面的getOutputStream函数工作方式于在readContentFromPost()里面的不同 ；在readContentFromPost()里面该函数仍在准备http request，没有向服务器发送任何数据 
	     * 而在这里由于设置了ChunkedStreamingMode，getOutputStream函数会根据connect之前的配置  生成http request头，先发送到服务器。 
	     */  
	    	
    	//组织参数内容
		StringBuilder paramsBuilder = new StringBuilder();
		boolean isfirst = true;
		for (String paramName : params.keySet()) {
			// 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
			if(isfirst) {
				paramsBuilder.append(HttpUtils.encodeQueryParam(paramName, String.valueOf(params.get(paramName))));					
				isfirst = false;
			}else {
				paramsBuilder.append("&").append(HttpUtils.encodeQueryParam(paramName, String.valueOf(params.get(paramName))));	
			}
		}
		
		// 获得上传信息的字节大小以及长度  
        //byte[] postdata = buffer.toString().getBytes();  
        httpConn.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(paramsBuilder.length()));
        // 连接，从urlPost.openConnection()至此的配置必须要在connect之前完成，要注意的是connection.getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，  所以在开发中不调用上述的connect()也可以)。 
        httpConn.connect();
    	//建立输入流，向指向的URL传入参数
	    DataOutputStream output = new DataOutputStream(httpConn.getOutputStream());
	    // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
	    output.writeBytes(params.toString());
	    //output.write(postdata , 0 , postdata.length );
	    // flush输出流的缓冲  
	    output.flush();
	    // 到此时服务器已经收到了完整的http request了，而在readContentFromPost()函数里，要等到下一句服务器才能收到http请求。
	    output.close(); 
		
		return httpConn;
	}
	 
    
}
