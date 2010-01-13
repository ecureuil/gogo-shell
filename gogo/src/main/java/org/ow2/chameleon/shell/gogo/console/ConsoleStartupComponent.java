package org.ow2.chameleon.shell.gogo.console;

import jline.ConsoleReader;
import jline.ConsoleReaderInputStream;
import jline.History;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.service.command.CommandProcessor;

/**
 * Created by IntelliJ IDEA.
 * User: sauthieg
 * Date: 6 janv. 2010
 * Time: 16:44:07
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ConsoleStartupComponent {

    @Requires
    private CommandProcessor processor;

    private JLineConsole console;

    @Validate
    public void startup() throws Exception {

        // Start the console
        console = new JLineConsole(processor,
                                   System.in,
                                   System.out,
                                   System.err);

        new Thread(console, "Chameleon Console Thread").start();

        // Store some global properties
        console.getSession().put("application.name", "chameleon");
    }

    @Invalidate
    public void shutdown() {
        console.close();
    }
}
