package org.ow2.chameleon.shell.builtin.internal;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.service.command.CommandSession;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Loris Bouzonnet
 */
@Component
@Command(name = "shutdown",
        scope = "shelbie",
        description = "Shutdown the OSGi platform")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class ShutdownAction implements Action {

    private BundleContext bundleContext;

    public ShutdownAction(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public Object execute(CommandSession commandSession) throws Exception {
        Bundle bundle = bundleContext.getBundle(0);
        bundle.stop();
        return null;
    }

}
