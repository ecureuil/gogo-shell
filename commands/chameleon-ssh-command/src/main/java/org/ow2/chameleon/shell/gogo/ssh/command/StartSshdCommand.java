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

import java.util.Properties;

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
@Command(name="start-daemon", scope="ssh", description="Start a SSHD daemon")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class StartSshdCommand implements Action {

	@Option(name="-p", aliases="--port", required=false)
	private int port = 10022;

	@Option(name="-i", aliases="--server-id", required=false)
	private String serverId = Constants.DEFAULT_SSH_SERVER_ID;

	@Requires
	private ConfigurationAdmin configurationAdmin;

	public Object execute(CommandSession session) throws Exception {

		// Create the Configuration that will trigger the SSHD instance creation
		Configuration config = configurationAdmin.createFactoryConfiguration(SshDaemonComponent.class.getName(), null);
		Properties properties = new Properties();
		properties.setProperty(org.osgi.framework.Constants.SERVICE_PID, serverId);
		properties.setProperty(Constants.SSHD_PORT, String.valueOf(port));

		// Display some infos
		StringBuilder sb = new StringBuilder();
		sb.append("SSH Daemon ");
		if (!Constants.DEFAULT_SSH_SERVER_ID.equals(serverId)) {
			sb.append("(");
			sb.append(serverId);
			sb.append(") ");
		}
		sb.append(" started on port ");
		sb.append(port);
		sb.append(" ...");
		System.out.println(sb.toString());

		// Update the configuration
		config.update(properties);

		return null;
	}
}
