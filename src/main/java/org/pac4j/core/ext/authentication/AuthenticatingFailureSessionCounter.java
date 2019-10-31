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
package org.pac4j.core.ext.authentication;

import org.pac4j.core.context.WebContext;
@SuppressWarnings("unchecked")
public class AuthenticatingFailureSessionCounter implements AuthenticatingFailureCounter {
	
	@Override
	public int get(WebContext context, String retryTimesKeyAttribute) {
		Object count = context.getSessionStore().get(context, retryTimesKeyAttribute);
		if (null != count) {
			return Integer.parseInt(String.valueOf(count));
		}
		return 0;
	}

	@Override
	public void increment(WebContext context, String retryTimesKeyAttribute) {
		Object count = context.getSessionStore().get(context, retryTimesKeyAttribute);
		if (null == count) {
			context.getSessionStore().set(context, retryTimesKeyAttribute, 1);
		} else {
			context.getSessionStore().set(context, retryTimesKeyAttribute,
					Long.parseLong(String.valueOf(count)) + 1);
		}
	}

}
