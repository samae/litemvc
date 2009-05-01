/*
 * Copyright 2009 Pavel Jbanov
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

package com.litemvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestHelper {

	private RequestHelper() {}
	
	private static ThreadLocal<HttpServletRequest> threadLocalRequest = new ThreadLocal<HttpServletRequest>();
	private static ThreadLocal<HttpServletResponse> threadLocalResponse = new ThreadLocal<HttpServletResponse>();
	
	static void setHttpServletRequest(HttpServletRequest req) {
		threadLocalRequest.set(req);
	}
	
	public static HttpServletRequest getHttpServletRequest() {
		HttpServletRequest req = threadLocalRequest.get();
		if (req == null) {
			throw new RuntimeException("request is not initialized");
		}
		
		return req;
	}
	
	static void setHttpServletResponse(HttpServletResponse resp) {
		threadLocalResponse.set(resp);
	}
	
	public static HttpServletResponse getHttpServletResponse() {
		HttpServletResponse resp = threadLocalResponse.get();
		if (resp == null) {
			throw new RuntimeException("response is not initialized");
		}
		
		return resp;
	}
	
	static void clean() {
		threadLocalRequest.set(null);
		threadLocalResponse.set(null);
	}
}
