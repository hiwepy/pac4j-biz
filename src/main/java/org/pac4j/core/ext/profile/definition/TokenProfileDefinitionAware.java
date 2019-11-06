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
package org.pac4j.core.ext.profile.definition;

import java.util.Arrays;
import java.util.List;

import org.pac4j.core.ext.profile.Token;
import org.pac4j.core.ext.profile.TokenProfile;
import org.pac4j.core.ext.profile.TokenProfileDefinition;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

/**
 * For classes that can set the profile definition.
 */
public abstract class TokenProfileDefinitionAware<P extends TokenProfile, T extends Token> extends InitializableObject {

	private List<TokenProfileDefinition<P, T>> profileDefinitions;
	
	public List<TokenProfileDefinition<P, T>> getProfileDefinitions() {
		return profileDefinitions;
	}

	public void setProfileDefinitions(final List<TokenProfileDefinition<P, T>> profileDefinitions) {
		CommonHelper.assertNotNull("profileDefinitions", profileDefinitions);
		this.profileDefinitions = profileDefinitions;
	}

	protected void defaultProfileDefinition(final TokenProfileDefinition<P, T> profileDefinition) {
		CommonHelper.assertNotNull("profileDefinition", profileDefinition);
		if (this.profileDefinitions == null) {
			this.profileDefinitions = Arrays.asList(profileDefinition);
		}
	}
	
}
