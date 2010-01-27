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

package org.ow2.chameleon.shell.gogo.handler;

import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.basic.DefaultActionPreparator;
import org.apache.felix.gogo.commands.converter.DefaultConverter;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.fusesource.jansi.Ansi;
import org.osgi.service.command.CommandSession;
import org.osgi.service.command.Converter;
import org.ow2.chameleon.shell.gogo.IUsagePrinter;

@Component
@Provides
public class GogoPreparator extends DefaultActionPreparator {

    /**
     * List of converters.
     */
    private List<Converter> converters;

    @Requires
    private IUsagePrinter printer;

    public GogoPreparator() {
        this.converters = new ArrayList<Converter>();
    }

    @Bind(id="converters", optional = true)
    public void addConverter(Converter converter) {
        this.converters.add(converter);
    }

    @Unbind(id="converters")
    public void removeConverter(Converter converter) {
        this.converters.remove(converter);
    }

    @Override
    protected Object convert(final Action action,
                             final CommandSession session,
                             final Object value,
                             final Type toType) throws Exception {
        // Use the DefaultConverter in first place
        DefaultConverter defaultConverter = new DefaultConverter(action.getClass().getClassLoader());
        Object result = defaultConverter.convert(value, toType);

        if (result != null) {
            return result;
        }

        // Use the bound converters (if the default converter did nothing)
        if (toType instanceof Class) {
            Class<?> type = (Class) toType;
            for (Converter converter : converters) {
                result = converter.convert(type, value);
                if (result != null) {
                    return result;
                }
            }
        }

        // Return null if nothing could convert the value
        return null;
    }

    @Override
    protected void printUsage(final Command command,
                              Set<Option> options,
                              final Set<Argument> arguments,
                              final PrintStream out) {
        // Delegate to the dedicated component
        if (command != null) {
            printer.printUsage(command, options, arguments, out);
        }
    }


}
