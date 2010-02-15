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
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.ipojo.annotations.Requires;
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
@Command(name = "factory",
         scope = "arch",
         description = "Display information about one given Component Factory")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class FactoryAction implements Action {

    @Requires(optional = true)
    private Factory[] factories;

    @Option(name = "-v",
            aliases = "--verbose",
            description = "When activated, display additional Factory informations.",
            required = false)
    private boolean verbose = false;

    @Argument(name = "factory-name",
              required = true,
              description = "A given Component Factory name")
    private String name;

    public Object execute(final CommandSession session) throws Exception {

        Ansi buffer = Ansi.ansi();
        boolean found = false;
        for (Factory factory : factories) {

            if (name.equals(factory.getName())) {
                printFactoryDetails(buffer, factory);
                found = true;
            }
        }

        PrintStream stream = System.out;
        if (!found) {
            // Use error stream
            stream = System.err;

            // Creates an error message
            buffer.a(" [");
            buffer.a(Ansi.Color.RED);
            buffer.a("ERROR");
            buffer.a(Ansi.Color.DEFAULT);
            buffer.a("] ");

            buffer.a("Factory '");
            buffer.a(Ansi.Attribute.ITALIC);
            buffer.a(name);
            buffer.a(Ansi.Attribute.ITALIC_OFF);
            buffer.a("' was not found.\n");
        }

        // Flush buffer's content
        stream.println(buffer.toString());

        return null;
    }

    private void printFactoryDetails(Ansi buffer, Factory factory) {

        String status = "INVALID";
        Ansi.Color color = Ansi.Color.RED;

        // Check Factory state
        if (factory.getState() == Factory.VALID) {
            status = "VALID";
            color = Ansi.Color.GREEN;
        }

        // Print factory name first
        buffer.a(factory.getName());
        buffer.a(" ");

        // Then its status
        buffer.a("[");
        buffer.fg(color);
        buffer.a(status);
        buffer.fg(Ansi.Color.DEFAULT);
        buffer.a("]\n");

        // Finally display the factory description

        AnsiPrintToolkit toolkit = new AnsiPrintToolkit(buffer);
        toolkit.printElement(0, factory.getDescription());

    }



}