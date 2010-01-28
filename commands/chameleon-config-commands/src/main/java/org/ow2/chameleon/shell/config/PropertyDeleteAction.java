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

package org.ow2.chameleon.shell.config;

import java.util.Dictionary;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.ipojo.annotations.Requires;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.command.CommandSession;

@Component
@Command(name="del",
         scope="config",
         description="Delate a given set of property from a given Configuration.")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class PropertyDeleteAction implements Action {

    @Argument(name = "pid",
              required = true,
              index = 0,
              description = "Service PID of the Configuration to update.")
    private String pid;

    @Argument(name = "properties",
              index = 1,
              multiValued = true,
              description = "Properties that will be removed from the Configuration.")
    private List<String> properties;

    @Requires
    private ConfigurationAdmin ca;

	public Object execute(CommandSession session) throws Exception {

        Configuration config = ca.getConfiguration(pid, null);

        if ((config != null) && (config.getProperties() != null)) {
            Dictionary p = getProperties(config);

            // Update the Configuration
            if (p != null) {
                config.update(p);
            }

        }

        return null;
	}

    private Dictionary getProperties(Configuration config) {
        Dictionary p = null;

        // Merge new properties inside the old set of properties
        if (properties != null) {
            p = config.getProperties();
            for (String property : properties) {
                p.remove(property);
            }
        }

        return p;
    }
}