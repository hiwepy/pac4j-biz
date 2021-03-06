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
package org.pac4j.core.ext.profile.definition;

import org.pac4j.core.ext.profile.Signature;
import org.pac4j.core.ext.profile.SignatureProfile;
import org.pac4j.core.ext.profile.SignatureProfileDefinition;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

/**
 * For classes that can set the profile definition.
 */
public abstract class SignatureProfileDefinitionAware<P extends SignatureProfile, T extends Signature> extends InitializableObject {

	private SignatureProfileDefinition<P, T> profileDefinition;
	
	public SignatureProfileDefinition<P, T> getProfileDefinition() {
		return profileDefinition;
	}

	public void setProfileDefinition(final SignatureProfileDefinition<P, T> profileDefinition) {
		CommonHelper.assertNotNull("profileDefinition", profileDefinition);
		this.profileDefinition = profileDefinition;
	}

	protected void defaultProfileDefinition(final SignatureProfileDefinition<P, T> profileDefinition) {
		CommonHelper.assertNotNull("profileDefinition", profileDefinition);
		if (this.profileDefinition == null) {
			this.profileDefinition = profileDefinition;
		}
	}
	
}
