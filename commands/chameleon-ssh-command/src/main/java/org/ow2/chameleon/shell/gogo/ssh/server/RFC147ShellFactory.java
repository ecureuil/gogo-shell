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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.apache.sshd.server.ShellFactory;
import org.osgi.service.command.CommandProcessor;
import org.osgi.service.command.CommandSession;
import org.ow2.chameleon.shell.gogo.console.JLineConsole;

public class RFC147ShellFactory implements ShellFactory {

	private CommandProcessor provider;
    private JLineConsole console;

    public RFC147ShellFactory(CommandProcessor provider) {
		this.provider = provider;
	}

	public Shell createShell() {
		return new SimpleShell();
	}

	private class SimpleShell implements Shell {

		private InputStream in;
		private OutputStream out;
		private OutputStream err;
		private CommandSession session;
		private ExitCallback callback;

		public void destroy() {
			// TODO Auto-generated method stub

		}

		public void setErrorStream(OutputStream err) {
			this.err = err;
		}

		public void setExitCallback(ExitCallback callback) {
			this.callback = callback;
		}

		public void setInputStream(InputStream in) {
			this.in = in;
		}

		public void setOutputStream(OutputStream out) {
			this.out = out;
		}

		public void start(Environment env) throws IOException {

	        try {
                console = new JLineConsole(provider,
                                                        null,
                        in, new PrintStream(out), new PrintStream(err)) {

					@Override
					public void run() {
						super.run();
						// the user has quit from the shell
						// invoke a callback
						onExit();
					}

                };
                session = console.getSession();
                for (Map.Entry<String,String> e : env.getEnv().entrySet()) {
                    session.put(e.getKey(), e.getValue());
                }
                new Thread(console, "SSH Console").start();
            } catch (Exception e) {
                throw (IOException) new IOException("Unable to start shell").initCause(e);
            }

		}

		public void onExit() {
            console.close();
			// close the streams
			close(in, out, err);
			callback.onExit(0);
		}

	}

    private static void close(Closeable... closeables) {
        for (Closeable c : closeables) {
            try {
                c.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

}
