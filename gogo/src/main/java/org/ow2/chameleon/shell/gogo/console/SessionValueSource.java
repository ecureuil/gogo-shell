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

package org.ow2.chameleon.shell.gogo.console;

import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.osgi.service.command.CommandSession;

/**
 * Wrap an OSGi CommandSession as a ValueSource to permit resolution of expression
 * stored inside the session.
 */
public class SessionValueSource extends AbstractValueSource {
    
    private CommandSession session;

    public SessionValueSource(CommandSession session) {
        super(false);
        this.session = session;
    }

    /**
     * @return the value related to the expression, or null if not found.
     */
    public Object getValue(String expression) {
        return session.get(expression);
    }
}
