package org.ow2.chameleon.shell.builtin;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
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
@Command(name = "help",
         scope = "builtin",
         description = "Display help about commands")
public class HelpAction implements Action {

    public Object execute(final CommandSession session) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
