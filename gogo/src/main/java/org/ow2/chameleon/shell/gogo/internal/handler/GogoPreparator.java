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

package org.ow2.chameleon.shell.gogo.internal.handler;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.basic.DefaultActionPreparator;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.service.command.CommandSession;
import org.ow2.chameleon.shell.gogo.IConverterManager;
import org.ow2.chameleon.shell.gogo.IUsagePrinter;

@Component
@Provides
public class GogoPreparator extends DefaultActionPreparator {

    /**
     * Converter manager.
     */
    @Requires
    private IConverterManager converterManager;

    @Requires
    private IUsagePrinter printer;

    @Override
    protected Object convert(final Action action,
                             final CommandSession session,
                             final Object value,
                             final Type toType) throws Exception {
        // Delegate to the converter manager
        return converterManager.convert((Class<?>) toType, value);
    }

    @Override
    protected void printUsage(CommandSession session,
                              Action action,
                              Map<Option,Field> optionsMap,
                              Map<Argument,Field> argsMap,
                              PrintStream out) {
        // Delegate to the dedicated component
        Command command = action.getClass().getAnnotation(Command.class);
        if (command != null) {
            printer.printUsage(command, optionsMap.keySet(), argsMap.keySet(), out);
        }

    }


}
