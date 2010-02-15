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

import java.io.PrintStream;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.fusesource.jansi.Ansi;
import org.osgi.service.command.CommandSession;
import org.ow2.chameleon.shell.ipojo.util.AnsiPrintToolkit;

/**
 * Created by IntelliJ IDEA.
 * User: sauthieg
 * Date: 7 janv. 2010
 * Time: 11:30:28
 * To change this template use File | Settings | File Templates.
 */
@Component
@Command(name = "instance",
         scope = "arch",
         description = "Display information about one given Component instance")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class InstanceAction implements Action {

    @Requires(optional = true)
    private Architecture[] architectures;

    @Option(name = "-v",
            aliases = "--verbose",
            description = "When activated, display additional information about teh given instance.",
            required = false)
    private boolean verbose = false;

    @Argument(name = "instance-name",
              required = true,
              description = "A given Component Instance name")
    private String name;

    public Object execute(final CommandSession session) throws Exception {

        AnsiPrintToolkit toolkit = new AnsiPrintToolkit();
        boolean found = false;
        for (Architecture arch : architectures) {
            InstanceDescription instance = arch.getInstanceDescription();
            if (name.equals(instance.getName())) {
                printInstanceDetails(toolkit, instance);
                found = true;
            }
        }

        PrintStream stream = System.out;
        if (!found) {
            // Use error stream
            stream = System.err;
            Ansi buffer = toolkit.getBuffer();

            // Creates an error message
            buffer.a(" [");
            toolkit.red("ERROR");
            buffer.a("] ");

            buffer.a("Instance '");
            toolkit.italic(name);
            buffer.a("' was not found.\n");
        }

        // Flush buffer's content
        stream.println(toolkit.getBuffer().toString());

        return null;
    }

    private String getStateName(int state) {
        switch (state) {
            case ComponentInstance.VALID:
                return "VALID";
            case ComponentInstance.INVALID:
                return "INVALID";
            case ComponentInstance.STOPPED:
                return "STOPPED";
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

    private void printInstanceDetails(AnsiPrintToolkit toolkit,
                                      InstanceDescription instance) {

        Ansi buffer = toolkit.getBuffer();
        int state = instance.getState();

        toolkit.eol();
        toolkit.bold(instance.getName());

        // Print status in the first column
        buffer.a(" [");
        buffer.fg(getStateColor(state));
        buffer.a(getStateName(state));
        buffer.fg(Ansi.Color.DEFAULT);
        buffer.a("]");

        toolkit.eol(2);

        toolkit.printElement(0, instance.getDescription());
    }
}