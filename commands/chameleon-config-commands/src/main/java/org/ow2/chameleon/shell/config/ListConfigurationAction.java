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

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.ipojo.annotations.Requires;
import org.fusesource.jansi.Ansi;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.command.CommandSession;

@Component
@Command(name="list",
         scope="config",
         description="List configurations")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class ListConfigurationAction implements Action {

    @Argument(name = "filter",
              description = "LDAP filter used to filter the set of returned Configuration(s).")
    private String filter;

    @Option(name = "-t",
            aliases = "--type",
            description = "Reduce the list of Configuration(s) by choosing between factory"
                          + "and non factory Configurations. (accepted values: factory/conf)")
    private String type;

    @Requires
    private ConfigurationAdmin ca;

	public Object execute(CommandSession session) throws Exception {

        if (type != null) {
            // Only accept factory/conv as values
            if (!("factory".equals(type) || "conf".equals(type))) {
                throw new IllegalArgumentException("Only 'factory' or 'conf' are accepted for the --type option");
            }
        }

        List<Configuration> bag = new ArrayList<Configuration>();

        Configuration[] configurations = ca.listConfigurations(filter);
        if (configurations != null) {
            for (Configuration configuration : configurations) {
                // Place in our bag only the requested configuration type
                if (type == null) {
                    // accept all
                    bag.add(configuration);
                } else if ("factory".equalsIgnoreCase(type)
                        && (configuration.getFactoryPid() != null)) {
                    // Configuration represents a Factory Configuration
                    bag.add(configuration);
                } else if ("conf".equalsIgnoreCase(type)
                        && (configuration.getFactoryPid() == null)) {
                    // Configuration represents a normal Configuration
                    bag.add(configuration);
                } // in other cases, you forget that configuration
            }
        }

        // Print Configurations
        Ansi buffer = Ansi.ansi();
        if (bag.isEmpty()) {
            buffer.fg(Ansi.Color.RED);
            buffer.a("No Configurations available");
            if (filter != null) {
                buffer.a(", using the filter '" + filter + "'");
            }
            if (type != null) {
                buffer.a(", restricting to '" + type + "' type");
            }
            buffer.a(".\n");
            buffer.fg(Ansi.Color.DEFAULT);
        } else {
            buffer.a("Configurations:\n");

            for (Configuration config : bag) {
                buffer.a("  * ");
                buffer.a(Ansi.Attribute.INTENSITY_BOLD);
                buffer.a(config.getPid());
                buffer.a(Ansi.Attribute.INTENSITY_BOLD_OFF);
                if (config.getFactoryPid() != null) {
                    buffer.a(" [factory-pid:");
                    buffer.a(config.getFactoryPid());
                    buffer.a("]");
                }
                buffer.a("\n");
            }
        }

        System.out.println(buffer.toString());

        return null;
	}
}
