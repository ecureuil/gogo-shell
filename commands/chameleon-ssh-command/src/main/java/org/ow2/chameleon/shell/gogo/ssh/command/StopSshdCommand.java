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

package org.ow2.chameleon.shell.gogo.ssh.command;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.ow2.chameleon.shell.gogo.ssh.server.SshDaemonComponent;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.ipojo.annotations.Requires;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.command.CommandSession;

@Component
@Command(name = "stop-daemon",
         scope = "ssh",
         description = "Stop a SSHD daemon")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class StopSshdCommand implements Action {

    private static final String FILTER = "(" + ConfigurationAdmin.SERVICE_FACTORYPID + "="
                                         + SshDaemonComponent.class.getName() + ")";

	@Option(name = "-i",
            aliases = "--server-pid",
            required = false)
	private String serverPID;

	@Requires
	private ConfigurationAdmin configurationAdmin;

    public Object execute(CommandSession session) throws Exception {

        Configuration config = null;
		// Look the SSHDaemonComponent instances
		Configuration[] configurations = configurationAdmin.listConfigurations(FILTER);
        if (configurations == null || (configurations.length == 0)) {
            // That's a warning case: we try to stop but nothing has been started
        } else {
            if (serverPID == null) {
                if (configurations.length == 1) {
                    // No specific server was required to be stopped
                    // If there is only 1 server available, stop it
                    config = configurations[0];
                } else {
                    // Error, cannot choose the right server to stop
                    System.err.println("Server PID was not specified.");
                }
            } else {
                // We know the name of the server, find it in the configuration list
                for (Configuration c : configurations) {
                    if (c.getPid().equals(serverPID)) {
                        config = c;
                    }
                }
            }
        }

        if (config == null) {
            // Error
            System.err.println("Cannot find the Configuration object linked to the SSH Daemon.");
        } else {
            // Display some infos
            StringBuilder sb = new StringBuilder();
            sb.append("SSH Daemon stopped");
            System.out.println(sb.toString());

            // Delete the configuration
            config.delete();
        }

		return null;
	}
}
