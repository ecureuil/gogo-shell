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

package org.ow2.chameleon.shell.gogo.internal.handler.completer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jline.console.completer.*;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.whiteboard.Wbp;
import org.apache.felix.service.command.CommandProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ow2.chameleon.shell.gogo.ICompletable;
import org.ow2.chameleon.shell.gogo.IScopeRegistry;

/**
 * Created by IntelliJ IDEA.
 * User: sauthieg
 * Date: 22 janv. 2010
 * Time: 13:13:42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Provides
@Wbp(filter = "(&(osgi.command.scope=*)(osgi.command.function=*))",
     onArrival = "onArrival",
     onDeparture = "onDeparture")
public class CommandsCompleter implements Completer, IScopeRegistry {


    private Map<ServiceReference, Completer> references;
    private Map<String, Integer> scopes;
    private BundleContext context;

    @ServiceProperty
    private String type = "commands";

    public CommandsCompleter(BundleContext context) {
        references = new HashMap<ServiceReference, Completer>();
        scopes = new HashMap<String, Integer>();
        this.context = context;
    }


    public void onArrival(ServiceReference reference) {

        // For each new command
        // Provide a first Completer with registered function names
        String[] functionNames = getFunctionNames(reference);
        List<Completer> cl = new ArrayList<Completer>();
        cl.add(new StringsCompleter(functionNames));

        // Then, each command may provides its own set of Completers
        try {
            Object service = context.getService(reference);

            if (service instanceof ICompletable) {
                ICompletable completable = (ICompletable) service;
                List<Completer> completers = completable.getCompleters();
                if (completers != null) {
                    for (Completer completer : completers) {
                        // Support case where the completable explicitely set a null value in the list
                        if (completer == null) {
                            cl.add(new NullCompleter());
                        } else {
                            // Normal case, just add the given Completer
                            cl.add(completer);
                        }
                    }
                }
            } else {
                cl.add(new NullCompleter());
            }
            // then we wrap in an ArgumentCompleter (one for each command)
            ArgumentCompleter argumentCompleter = new ArgumentCompleter(cl);

            // We finally store the ArgumentCompleter of the command in the map
            references.put(reference, argumentCompleter);
        } finally {
            context.ungetService(reference);
        }

        // Lookup the scope of the command
        String scope = (String) reference.getProperty(CommandProcessor.COMMAND_SCOPE);
        Integer numberOfCommands = scopes.get(scope);
        if (numberOfCommands == null) {
            numberOfCommands = 0;
        }
        scopes.put(scope, ++numberOfCommands);
    }

    public void onDeparture(ServiceReference reference) {
        references.remove(reference);

        // Lookup the scope of the command
        String scope = (String) reference.getProperty(CommandProcessor.COMMAND_SCOPE);

        Integer numberOfCommands = scopes.get(scope);
        if (numberOfCommands != null) {
            scopes.put(scope, --numberOfCommands);
        }
    }

    private String[] getFunctionNames(ServiceReference reference) {

        List<String> names = new ArrayList<String>();

        // Get the functions and scope from the reference
        Object functionValue = reference.getProperty(CommandProcessor.COMMAND_FUNCTION);
        String scope = (String) reference.getProperty(CommandProcessor.COMMAND_SCOPE);

        // Function may be available as a simple String or as an String array
        if (functionValue.getClass().isArray()
                && String.class.equals(functionValue.getClass().getComponentType())) {
            String[] values = (String[]) functionValue;
            for (String name : values) {
                names.add(scope + ":" + name);
            }
        } else {
            // Should be a String
            names.add(scope + ":" + functionValue.toString());
        }

        return names.toArray(new String[names.size()]);

    }

    /**
     * Populates <i>candidates</i> with a list of possible
     * completions for the <i>buffer</i>. The <i>candidates</i>
     * list will not be sorted before being displayed to the
     * user: thus, the complete method should sort the
     * {@link java.util.List} before returning.
     *
     * @param buffer     the buffer
     * @param candidates the {@link java.util.List} of candidates to populate
     * @return the index of the <i>buffer</i> for which
     *         the completion will be relative
     */
    public int complete(String buffer, int cursor, List candidates) {

        // Create a multi completer for all registered completers
        // and run them all
        Completer[] array = references.values().toArray(new Completer[references.size()]);
        int res = new AggregateCompleter(array).complete(buffer, cursor, candidates);

        // Note to myself: It still seems to be a little bit magic, how can jline,
        // from a list of all completers of all commands, provides the right completion values ...
        // There is some magic left in the world ;)

        // Then sort the resulting candidate list
        Collections.sort(candidates);
        return res;
    }

    /**
     * Return a list of currently available scopes.
     * The returned value is verified only at the time of the invocation.
     *
     * @return a list of currently available command scopes
     */
    public Set<String> getScopes() {
        return scopes.keySet();
    }
}
