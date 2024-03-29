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
package org.pac4j.core.ext;

import java.util.List;

import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

public class Pac4jInternalAuthorizer extends ProfileAuthorizer {

	@Override
	protected boolean isProfileAuthorized(WebContext context, SessionStore sessionStore, UserProfile profile) {
		return false;
	}

	@Override
	public boolean isAuthorized(WebContext context, SessionStore sessionStore, List<UserProfile> profiles) {
		return isAnyAuthorized(context, sessionStore, profiles);
	}

}
