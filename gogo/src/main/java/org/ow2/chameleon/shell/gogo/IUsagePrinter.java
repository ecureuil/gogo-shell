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

package org.ow2.chameleon.shell.gogo;

import java.io.PrintStream;
import java.util.Set;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * Prints a command usage.
 */
public interface IUsagePrinter {

    /**
     * Prints in the given stream the usage information to be displayed to
     * the user for the given command.
     * @param command command annotation description
     * @param options set of option annotation description
     * @param arguments set of argument annotation description
     * @param out the PrintStream to be used to write the usage.
     */
    void printUsage(Command command,
                    Set<Option> options,
                    Set<Argument> arguments,
                    PrintStream out);
}
