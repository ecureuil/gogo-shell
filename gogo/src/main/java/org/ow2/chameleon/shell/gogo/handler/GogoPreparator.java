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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.basic.DefaultActionPreparator;
import org.apache.felix.gogo.commands.converter.DefaultConverter;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.osgi.service.command.CommandSession;
import org.osgi.service.command.Converter;

@Component
@Provides
public class GogoPreparator extends DefaultActionPreparator {

    /**
     * List of converters.
     */
    private List<Converter> converters;

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
}
