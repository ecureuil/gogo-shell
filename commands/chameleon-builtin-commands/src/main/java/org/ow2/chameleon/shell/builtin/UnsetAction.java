package org.ow2.chameleon.shell.builtin;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.service.command.CommandSession;

/**
 * Created by IntelliJ IDEA.
 * User: sauthieg
 * Date: 7 janv. 2010
 * Time: 11:30:28
 * To change this template use File | Settings | File Templates.
 */
@Component
@Command(name = "unset",
         scope = "builtin",
         description = "Unset the value from a variable (remove it)")
public class UnsetAction implements Action {

    @Option(name = "-v",
            aliases = "--verbose",
            description = "Verbose mode")
    private boolean verbose = false;

    @Argument(required = true,
              description = "The variable to remove")
    private String variableName;

    public Object execute(final CommandSession session) throws Exception {
        session.put(variableName, null);
        return null;
    }
}