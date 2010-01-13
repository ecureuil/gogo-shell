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

package org.ow2.chameleon.shell.gogo.ssh.server;

import java.io.IOException;

import org.ow2.chameleon.shell.gogo.ssh.command.Constants;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.osgi.service.command.CommandProcessor;

@Component
public class SshDaemonComponent {

	private SshServer server;

	@Requires
	private CommandProcessor provider;

	@Property(mandatory = true, name = Constants.SSHD_PORT)
	private int port;

	@Validate
	public void start() {
		server = SshServer.setUpDefaultServer();
		server.setPort(port);
		server.setShellFactory(new RFC147ShellFactory(provider));
		server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		try {
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Invalidate
	public void stop() {
		server.stop();
		server = null;
	}
}
