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
import java.util.Enumeration;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.service.command.CommandSession;
import org.fusesource.jansi.Ansi;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

@Component
@Command(name="details",
         scope="config",
         description="Print the content of a Configurations")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class DetailsConfigurationAction implements Action {

    @Argument(name = "pid",
              required = true,
              description = "Service (or factory) PID of the Configuration to show.")
    private String pid;

    @Requires
    private ConfigurationAdmin ca;

	public Object execute(CommandSession session) throws Exception {

        Configuration config = ca.getConfiguration(pid, null);

        if (config != null) {

            Ansi buffer = Ansi.ansi();

            buffer.a(Ansi.Attribute.INTENSITY_BOLD);
            buffer.a(config.getPid());
            buffer.a(Ansi.Attribute.INTENSITY_BOLD_OFF);

            if (config.getFactoryPid() != null) {
                buffer.a(" [factory]\n");
            }

            Dictionary dict = config.getProperties();
            if (dict != null) {
                for (Enumeration e = dict.keys(); e.hasMoreElements();) {
                    String name = (String) e.nextElement();
                    String value = (String) dict.get(name);

                    buffer.a("  * ");
                    buffer.a(name + "=" + value);
                    buffer.a('\n');
                }
            }

            System.out.print(buffer.toString());

        }

        return null;
	}

}