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

import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.fusesource.jansi.Ansi;
import org.osgi.service.command.CommandSession;

/**
 * List the iPOJO component factories.
 */
@Component
@Command(name = "factories",
         scope = "arch",
         description = "List the iPOJO component factories.")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class FactoriesAction implements Action {

    @Requires(optional = true)
    private Factory[] factories;

    @Option(name = "-v",
            aliases = "--verbose",
            description = "When activated, display additional factory informations.",
            required = false)
    private boolean verbose = false;

    public Object execute(final CommandSession session) throws Exception {

        Ansi buffer = Ansi.ansi();
        for (Factory factory : factories) {
            printFactory(buffer, factory);
        }

        // Print factories statuses
        System.out.println(buffer.toString());
        return null;
    }

    private void printFactory(Ansi buffer, Factory factory) {

        String status = "INVALID";
        Ansi.Color color = Ansi.Color.RED;

        // Check Factory state
        if (factory.getState() == Factory.VALID) {
            status = "  VALID";
            color = Ansi.Color.GREEN;
        }

        // Print status in the first column
        buffer.a("[");
        buffer.fg(color);
        buffer.a(status);
        buffer.fg(Ansi.Color.DEFAULT);
        buffer.a("] ");

        // Then place the factory name
        buffer.a(factory.getName());

        // Display version
        if (verbose && (factory.getVersion() != null)) {
            buffer.a(" (v:" + factory.getVersion() + ")");
        }

        // Display bundle hosting the factory
        buffer.a(" (from bundle: " + factory.getBundleContext().getBundle().getBundleId() + ")");
        buffer.a('\n');

        // Display missing handlers
        if (verbose && (factory.getState() == Factory.INVALID)) {
            List<String> handlers = factory.getMissingHandlers();
            buffer.a("  [ ");
            for (String handler : handlers) {
                buffer.fg(Ansi.Color.RED);
                buffer.a(handler);
                buffer.fg(Ansi.Color.DEFAULT);
                buffer.a(" ");

            }
            buffer.a("]\n");
        }

    }
}