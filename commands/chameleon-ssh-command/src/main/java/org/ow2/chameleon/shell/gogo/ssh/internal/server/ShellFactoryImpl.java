package org.ow2.chameleon.shell.gogo.ssh.internal.server;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.ow2.chameleon.shell.gogo.console.JLineConsole;

import java.io.*;
import java.util.Map;

public class ShellFactoryImpl implements Factory<Command>
{
    private CommandProcessor commandProcessor;

    private JLineConsole console;

    public ShellFactoryImpl(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

    public Command create() {
        return new SimpleShell();
    }

    private class SimpleShell implements Command {

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
                console = new JLineConsole(commandProcessor,
                        null,
                        in,
                        new PrintStream(new LfToCrLfFilterOutputStream(out)),
                        new PrintStream(new LfToCrLfFilterOutputStream(err))) {

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

    public class LfToCrLfFilterOutputStream extends FilterOutputStream {

        private boolean lastWasCr;

        public LfToCrLfFilterOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(int b) throws IOException {
            if (!lastWasCr && b == '\n') {
                out.write('\r');
                out.write('\n');
            } else {
                out.write(b);
            }
            lastWasCr = b == '\r';
        }

    }
}
