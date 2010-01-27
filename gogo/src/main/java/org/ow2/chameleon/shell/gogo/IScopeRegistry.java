/**
 * Copyright 2010 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.chameleon.shell.gogo;

import java.util.Set;

/**
 * This interface provides a simple way to init the 'SCOPE' variable with the scopes
 * of currently registered commands.
 * There is no synchronization between the content of the SCOPE variable and the Set
 * of returned scope names: the set is only used at init time of the session.
 */
public interface IScopeRegistry {

    /**
     * Return a list of currently available scopes.
     * The returned value is verified only at the time of the invocation.
     * @return a list of currently available command scopes
     */
    Set<String> getScopes();
}
