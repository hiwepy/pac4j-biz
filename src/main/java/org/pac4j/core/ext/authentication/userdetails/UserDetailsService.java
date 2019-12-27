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
package org.pac4j.core.ext.authentication.userdetails;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.ext.authentication.generator.UserDetails;
import org.pac4j.core.ext.exception.UsernameNotFoundException;
import org.pac4j.core.profile.UserProfile;

/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public interface UserDetailsService<U extends UserProfile> {
	
	UserDetails loadUserDetails(final WebContext context, final UserProfile profile) throws UsernameNotFoundException;
	
}

