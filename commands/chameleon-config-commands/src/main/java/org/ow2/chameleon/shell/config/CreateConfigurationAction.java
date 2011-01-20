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

import java.util.List;
import java.util.Properties;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.service.command.CommandSession;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

@Component
@Command(name="create",
         scope="config",
         description="Create Configurations")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class CreateConfigurationAction implements Action {

    @Option(name = "-f",
            aliases = "--factory",
            description = "Create a factory Configuration (given PID will become"
                          + "the factoryPid of that Configuration)")
    private boolean createFactoryConf = false;

    @Argument(name = "pid",
              required = true,
              index = 0,
              description = "Service (or factory) PID of the Configuration to create.")
    private String pid;

    @Argument(name = "properties",
              index = 1,
              multiValued = true,
              description = "Properties that will be placed in the newly created Configuration.")
    private List<String> properties;

    @Requires
    private ConfigurationAdmin ca;

	public Object execute(CommandSession session) throws Exception {

        Configuration config = null;

        if (createFactoryConf) {
            config = ca.createFactoryConfiguration(pid, null);
        } else {
            config = ca.getConfiguration(pid, null);
        }

        Properties p = getProperties();

        // Update the Configuration
        config.update(p);

        return null;
	}

    private Properties getProperties() {
        Properties p = new Properties();

        if (properties != null) {
            for (String property : properties) {
                String[] sections = property.split("=");
                if (sections.length == 2) {
                    p.setProperty(sections[0], sections[1]);
                } else {
                    // Warning there are some invalid properties here
                }
            }
        }

        return p;
    }
}