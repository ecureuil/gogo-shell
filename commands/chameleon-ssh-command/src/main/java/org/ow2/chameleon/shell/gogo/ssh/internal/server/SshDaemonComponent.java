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

package org.ow2.chameleon.shell.gogo.ssh.internal.server;

import java.io.IOException;

import jline.console.completer.Completer;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.jaas.JaasPasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.ow2.chameleon.shell.gogo.ssh.internal.command.Constants;

@Component
public class SshDaemonComponent {

	private SshServer server;

	@Requires
	private CommandProcessor provider;

    @Requires(filter = "(type=commands)")
    private Completer completer;

	@Property(mandatory = true,
              name = Constants.SSHD_PORT)
	private int port;


	@Validate
	public void start() throws IOException {
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(SshDaemonComponent.class.getClassLoader());

        try {
            server = SshServer.setUpDefaultServer();
            server.setPort(port);
            server.setShellFactory(new ShellFactoryImpl(provider, completer));
            server.setCommandFactory(new CommandFactoryImpl(provider));
            server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
            JaasPasswordAuthenticator pswdAuth = new JaasPasswordAuthenticator();
            pswdAuth.setDomain("shelbie");
            server.setPasswordAuthenticator(pswdAuth);
            server.start();
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
	}

	@Invalidate
	public void stop() throws Exception {
		server.stop();
		server = null;
	}
}
