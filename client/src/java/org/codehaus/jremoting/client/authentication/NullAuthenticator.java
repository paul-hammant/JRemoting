/* ====================================================================
 * Copyright 2005-2006 JRemoting Committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.codehaus.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.codehaus.jremoting.client.authentication;

import org.codehaus.jremoting.client.Authenticator;
import org.codehaus.jremoting.authentication.Authentication;
import org.codehaus.jremoting.authentication.AuthenticationChallenge;

public class NullAuthenticator implements Authenticator {
    public Authentication authenticate(AuthenticationChallenge authChallenge) {
        return null;
    }
}
