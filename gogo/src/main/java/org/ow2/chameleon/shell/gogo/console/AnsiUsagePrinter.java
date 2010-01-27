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

package org.ow2.chameleon.shell.gogo.console;

import java.io.PrintStream;
import java.util.Set;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.fusesource.jansi.Ansi;
import org.ow2.chameleon.shell.gogo.IUsagePrinter;

/**
 * Print commands usage using an Ansi buffer.
 */
@Component
@Provides
public class AnsiUsagePrinter implements IUsagePrinter {

    /**
     * Prints in the given stream the usage information to be displayed to
     * the user for the given command.
     *
     * @param command   command annotation description
     * @param options   set of option annotation description
     * @param arguments set of argument annotation description
     * @param out       the PrintStream to be used to write the usage.
     */
    public void printUsage(final Command command,
                           final Set<Option> options,
                           final Set<Argument> arguments,
                           final PrintStream out) {

        Ansi buffer = Ansi.ansi();
        if (command.description() != null
            && command.description().length() > 0) {
            eol(buffer, 1);
            indent(buffer, 1);
            bold(buffer, command.name());
            buffer.a(" - ");
            buffer.a(command.description());
            eol(buffer, 2);
        }

        sectionTitle(buffer, "Syntax");
        indent(buffer, 1);
        bold(buffer, command.scope() + ":" + command.name());
        if (options.size() > 0) {
            buffer.a(" [");
            underline(buffer, "options");
            buffer.a("]...");
        }
        if (arguments.size() > 0) {
            buffer.a(" [");
            underline(buffer, "arguments");
            buffer.a("]...");
        }
        // Empty line
        eol(buffer, 2);

        if (arguments.size() > 0) {

            sectionTitle(buffer, "Arguments");
            for (Argument argument : arguments) {
                indent(buffer, 1);
                bold(buffer, argument.name());

                buffer.a(" : ");
                buffer.a(argument.description());
                eol(buffer, 1);
            }
            eol(buffer, 1);
        }
        if (options.size() > 0) {

            sectionTitle(buffer, "Options");
            for (Option option : options) {
                indent(buffer, 1);
                bold(buffer, option.name());
                if (option.aliases().length > 0) {
                    for (String alias : option.aliases()) {
                        buffer.a(", ");
                        bold(buffer, alias);
                    }
                }
                eol(buffer, 1);
                indent(buffer, 2);
                buffer.a(option.description());
            }
            eol(buffer, 1);
        }

        // Flush the ansi buffer int the stream
        out.println(buffer.toString());

    }

    private void indent(Ansi buffer, int level) {
        for (int i = 0; i < level; i++) {
            buffer.a("  ");
        }
    }

    private void eol(Ansi buffer, int level) {
        for (int i = 0; i < level; i++) {
            buffer.a("\n");
        }
    }

    private void sectionTitle(Ansi buffer, String message) {
        buffer.fg(Ansi.Color.BLUE);
        bold(buffer, " **** " + message + " ****");
        buffer.reset();
        buffer.a("\n\n");
    }

    private void bold(Ansi buffer, String message) {
        buffer.a(Ansi.Attribute.INTENSITY_BOLD);
        buffer.a(message);
        buffer.a(Ansi.Attribute.INTENSITY_BOLD_OFF);
    }

    private void underline(Ansi buffer, String message) {
        buffer.a(Ansi.Attribute.UNDERLINE);
        buffer.a(message);
        buffer.a(Ansi.Attribute.UNDERLINE_OFF);
    }
}
