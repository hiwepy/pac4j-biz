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
package org.pac4j.core.ext.profile;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.factory.ProfileFactory;

/**
 * Signature profile definition.
 */
public abstract class SignatureProfileDefinition<P extends SignatureProfile, T extends Signature> extends CommonProfileDefinition<P> {

    public SignatureProfileDefinition() {
        super();
    }

    public SignatureProfileDefinition(ProfileFactory<P> profileFactory) {
        super(profileFactory);
    }
    
    /**
     * Extract the user profile from the response (JSON, XML...) of the payload.
     *
     * @param body the response body
     * @return the returned profile
     */
    public abstract P extractUserProfile(String body, String signature);
    
    /**
     * Throws a {@link TechnicalException} to indicate that user profile extraction has failed.
     * 
     * @param body the request body that the user profile should be have been extracted from
     * @param missingNode the name of a JSON node that was found missing. may be omitted
     */
    protected void raiseProfileExtractionJsonError(String body, String missingNode) {
        logger.error("Unable to extract user profile as no JSON node '{}' was found in body: {}", missingNode, body);
        throw new TechnicalException("No JSON node '" + missingNode + "' to extract user profile from");
    }
    
    /**
     * Throws a {@link TechnicalException} to indicate that user profile extraction has failed.
     * 
     * @param body the request body that the user profile should have been extracted from
     */
    protected void raiseProfileExtractionJsonError(String body) {
        logger.error("Unable to extract user profile as no JSON node was found in body: {}", body);
        throw new TechnicalException("No JSON node to extract user profile from");
    }
    
    /**
     * Throws a {@link TechnicalException} to indicate that user profile extraction has failed.
     * 
     * @param body the request body that the user profile should have been extracted from
     */
    protected void raiseProfileExtractionError(String body) {
        logger.error("Unable to extract user profile from body: {}", body);
        throw new TechnicalException("Unable to extract user profile");
    }
    
}
