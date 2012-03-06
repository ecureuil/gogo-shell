package org.ow2.chameleon.shell.builtin.internal;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.service.command.CommandSession;
import org.ow2.chameleon.shell.gogo.ExitSessionException;

/**
 * @author Loris Bouzonnet
 */
@Component
@Command(name = "exit",
        scope = "shelbie",
        description = "Exit from the current session")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class ExitAction implements Action {

    public Object execute(CommandSession commandSession) throws Exception {
        throw new ExitSessionException();
    }

}
