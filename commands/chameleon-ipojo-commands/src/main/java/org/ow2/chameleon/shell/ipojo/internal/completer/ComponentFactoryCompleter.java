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

package org.ow2.chameleon.shell.ipojo.internal.completer;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;

/**
 * This Completer provides completion support for iPOJO factory names.
 */
@Component(propagation = true)
@Provides(specifications = Completer.class)
public class ComponentFactoryCompleter extends StringsCompleter {

    private SortedSet<String> names;

    public ComponentFactoryCompleter() {
        super("");
        names = new TreeSet<String>();
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> clist) {
        // Update candidates
        getStrings().clear();
        getStrings().addAll(names);
        return super.complete(buffer, cursor, clist);
    }

    @Bind(id = "factory",
          aggregate = true,
          optional = true)
    private void onFactoryArrival(Factory factory) {
        String name = factory.getName();
        // Do not accept duplicates
        if (!names.contains(name)) {
            names.add(name);
        }
    }

    @Unbind(id = "factory")
    private void onFactoryDeparture(Factory factory) {
        if (factory != null) {
            names.remove(factory.getName());
        }
    }
}