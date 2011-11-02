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

package org.ow2.chameleon.shell.gogo.console;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import jline.Terminal;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.Completer;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Converter;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.ow2.chameleon.shell.gogo.internal.handler.completer.ScopeCompleter;

/**
 * Created by IntelliJ IDEA.
 * User: sauthieg
 * Date: 6 janv. 2010
 * Time: 17:46:33
 * To change this template use File | Settings | File Templates.
 */
public class JLineConsole implements Runnable {

    /**
     * OSGi Shell session.
     */
    private CommandSession session;

    /**
     * JLine reader (enable ANSI support for shell).
     */
    private ConsoleReader reader;

    private boolean running = true;

    /**
     * Default prompt.
     * TODO Use Ansi coloring
     */
    private static final String DEFAULT_PROMPT = "${user.name}@${application.name}$ ";

    public JLineConsole(final CommandProcessor processor,
                        Completer completer,
                        final InputStream in,
                        final PrintStream out,
                        final PrintStream err) throws Exception {

        // Wrap the output and error streams to enable ANSI support
        PrintStream wrappedOut = wrap(out);
        PrintStream wrappedErr = wrap(err);

        // Create an OSGi Shell session using the given streams
        session = processor.createSession(in,
                                          wrappedOut,
                                          wrappedErr);

        // Get an os specific terminal
        Terminal terminal = TerminalFactory.get();

        // Create the JLine reader
        reader = new ConsoleReader(in,
                                   new PrintWriter(wrappedOut),
                                   null, // TODO key-bindings
                                   terminal);

        // Create a composite completer that use the given Completer and wrap it
        // in a Completer that tries to complement using the known scopes (from
        // SCOPE session variable)
        Completer composite = new AggregateCompleter(new Completer[] {
                completer, new ScopeCompleter(completer, session)
        });
        reader.addCompleter(composite);

        // TODO Setup History
    }

    private PrintStream wrap(PrintStream stream) {

        // Try top unwrap the given Stream to avoid infinite recursion
        PrintStream ps = unwrapStream(stream);

        // Enable ANSI support on the stream
        OutputStream result = AnsiConsole.wrapOutputStream(ps);

        // Return a PrintStream instance
        if (result instanceof PrintStream) {
            return (PrintStream) result;
        }
        return new PrintStream(result);
    }

    private PrintStream unwrapStream(PrintStream stream) {
        try {
            // Used reflection to avoid a dependency on gogo ThreadPrintStream class
            Method method = stream.getClass().getMethod("getRoot");
            return (PrintStream) method.invoke(stream);
        } catch (Throwable t) {
            // Could not unwrap the stream, return it unchanged
            return stream;
        }
    }

    public CommandSession getSession() {
        return session;
    }

    public void close() {
        running = false;
    }

    public void run() {
        while (running) {

            try {
                String line = reader.readLine(getPrompt());
                if (line == null) {
                    // Jump to the next iteration
                    continue;
                }

                // Execute the command line
                Object result = session.execute(line);


                // Format the result (if any)
                if (result != null) {
                    CharSequence value = session.format(result, Converter.INSPECT);
                    session.getConsole().println(value);
                }
            } catch (Throwable t) {
                // Something went wrong during execution

                // Store the exception and display a minimal error message
                session.put(Constants.EXCEPTION_VARIABLE, t);
                printError(t.getClass().getSimpleName(), t.getMessage());
            }

            // TODO Need to manage interruptions (^C, ...)

        }
    }

    private String getPrompt() {
        String prompt = (String) session.get(Constants.PROMPT_VARIABLE);
        if (prompt == null) {
            prompt = DEFAULT_PROMPT;
        }

        // TODO Need some caching
        // Interpolate prompt
        RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
        // Allow resolution of System properties
        interpolator.addValueSource( new PropertiesBasedValueSource(System.getProperties()));
        // then add values from the CommandSession
        interpolator.addValueSource(new SessionValueSource(session));

        // TODO Do we have some other sources of values that could be evaluated ?

        // Resolve the prompt
        try {
            return interpolator.interpolate(prompt);
        } catch (InterpolationException e) {
            printDebug("Cannot interpolate prompt '" + prompt + "'. Error: " + e.getMessage());
            return DEFAULT_PROMPT;
        }
    }

    private void printError(String type, String message) {
        String error = Ansi.ansi().fg(Ansi.Color.RED)
                           .a("[")
                           .a(type)
                           .a("]: ")
                           .reset().toString();
        session.getConsole().println(error + message);
    }

    private void printDebug(String message) {
        String error = Ansi.ansi().fg(Ansi.Color.GREEN)
                           .a("Debug: ")
                           .reset().toString();
        session.getConsole().println(error + message);
    }

}

