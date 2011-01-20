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

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.ow2.chameleon.shell.gogo.ssh.command.Constants;

@Component
public class SshDaemonComponent {

	private SshServer server;

	@Requires
	private CommandProcessor provider;

	@Property(mandatory = true,
              name = Constants.SSHD_PORT)
	private int port;

	@Validate
	public void start() {
		server = SshServer.setUpDefaultServer();
		server.setPort(port);
		server.setShellFactory(new RFC147ShellFactory(provider));
		server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        server.setPasswordAuthenticator(new PasswordAuthenticator() {

            /**
             * Check the validity of a password.
             * This method should return null if the authentication fails.
             *
             * @param username the username
             * @param password the password
             * @return a non null identity object or <code>null</code if authentication fail
             */
            public Object authenticate(String username, String password, ServerSession session) {
                return username;
            }
        });
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
