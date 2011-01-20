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

package org.ow2.chameleon.shell.ipojo;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.apache.felix.service.command.CommandSession;
import org.fusesource.jansi.Ansi;

/**
 * Created by IntelliJ IDEA.
 * User: sauthieg
 * Date: 7 janv. 2010
 * Time: 11:30:28
 * To change this template use File | Settings | File Templates.
 */
@Component
@Command(name = "instances",
         scope = "arch",
         description = "List the component instances")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class InstancesAction implements Action {

    @Requires(optional = true)
    private Architecture[] architectures;

    @Option(name = "-v",
            aliases = "--verbose",
            description = "When activated, display DISPOSED instances as well.",
            required = false)
    private boolean verbose = false;

    public Object execute(final CommandSession session) throws Exception {

        Ansi buffer = Ansi.ansi();
        if (architectures.length != 0) {
            buffer.a(Ansi.Attribute.INTENSITY_BOLD);
            buffer.a("[ Status ] Instance name\n");
            buffer.a(Ansi.Attribute.INTENSITY_BOLD_OFF);
        }
        for (Architecture arch : architectures) {
            InstanceDescription instance = arch.getInstanceDescription();

            if (instance.getState() != ComponentInstance.DISPOSED) {
                printInstanceInfos(buffer, instance);
            } else {
                // Instance is disposed: only print it if verbose
                if (verbose) {
                    printInstanceInfos(buffer, instance);
                } // otherwise ignore it
            }
        }

        // Print instances statuses
        System.out.println(buffer.toString());
        return null;
    }

    private String getStateName(int state) {
        switch (state) {
            case ComponentInstance.VALID:
                return "   VALID";
            case ComponentInstance.INVALID:
                return " INVALID";
            case ComponentInstance.STOPPED:
                return " STOPPED";
            case ComponentInstance.DISPOSED:
                return "DISPOSED";
        }
        // Should not happen (no other known component instances states)
        return "";
    }

    private Ansi.Color getStateColor(int state) {
        switch (state) {
            case ComponentInstance.VALID:
                return Ansi.Color.GREEN;
            case ComponentInstance.INVALID:
                return Ansi.Color.RED;
            case ComponentInstance.STOPPED:
                return Ansi.Color.YELLOW;
            case ComponentInstance.DISPOSED:
                return Ansi.Color.BLUE;
        }
        // Should not happen (no other known component instances states)
        return Ansi.Color.DEFAULT;
    }

    private void printInstanceInfos(Ansi buffer,
                                    InstanceDescription instance) {

        int state = instance.getState();

        // Print status in the first column
        buffer.a("[");
        buffer.fg(getStateColor(state));
        buffer.a(getStateName(state));
        buffer.fg(Ansi.Color.DEFAULT);
        buffer.a("] ");

        // Then place the instance name
        buffer.a(instance.getName());
        buffer.a('\n');
    }
}