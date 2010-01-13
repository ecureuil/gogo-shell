package org.ow2.chameleon.shell.gogo.console;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import jline.ConsoleReader;
import jline.Terminal;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.osgi.service.command.CommandProcessor;
import org.osgi.service.command.CommandSession;
import org.osgi.service.command.Converter;

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

    public JLineConsole(final CommandProcessor processor,
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
        Terminal terminal = Terminal.getTerminal();

        // Create the JLine reader
        reader = new ConsoleReader(in,
                                   new PrintWriter(wrappedOut),
                                   null, // TODO key-bindings
                                   terminal);

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

                printDebug("read line [" + line + "]");

                // Execute the command line
                Object result = session.execute(line);


                // Format the result (if any)
                if (result != null) {
                    printDebug("result object [" + result.getClass().getSimpleName() + "][" + result + "]");
                    CharSequence value = session.format(result, Converter.INSPECT);
                    printDebug("formatted result [" + value + "]");
                    session.getConsole().println(value);
                    printDebug("printing done.");
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
        return ">$ ";
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

