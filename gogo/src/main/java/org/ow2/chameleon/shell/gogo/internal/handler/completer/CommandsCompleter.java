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

import java.util.*;

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

import static java.util.Collections.*;

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
     onArrival = "bindCommand",
     onDeparture = "unbindCommand")
public class CommandsCompleter implements Completer, IScopeRegistry {

    private List<ServiceReference> references;
    private Map<String, Integer> scopes;
    private BundleContext context;

    @ServiceProperty(value = "commands")
    private String type;

    public CommandsCompleter(BundleContext context) {
        references = new ArrayList<ServiceReference>();
        scopes = new HashMap<String, Integer>();
        this.context = context;
    }


    public void bindCommand(ServiceReference reference) {

        references.add(reference);

        // Lookup the scope of the command
        String scope = (String) reference.getProperty(CommandProcessor.COMMAND_SCOPE);
        Integer numberOfCommands = scopes.get(scope);
        if (numberOfCommands == null) {
            numberOfCommands = 0;
        }
        scopes.put(scope, ++numberOfCommands);

    }

    public void unbindCommand(ServiceReference reference) {
        references.remove(reference);

        // Lookup the scope of the command
        String scope = (String) reference.getProperty(CommandProcessor.COMMAND_SCOPE);

        Integer numberOfCommands = scopes.get(scope);
        if (numberOfCommands != null) {
            numberOfCommands--;
            if (numberOfCommands == 0) {
                scopes.remove(scope);
            } else {
                scopes.put(scope, numberOfCommands);
            }
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
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {

        // Create an aggregate completer for all registered completer
        // and run them all
        int res = new AggregateCompleter(getCompleters()).complete(buffer, cursor, candidates);

        // Note to myself: It still seems to be a little bit magic, how can jline,
        // from a list of all completer of all commands, provides the right completion values ...
        // There is some magic left in the world ;)

        // Then sort the resulting candidate list
        Collections.sort(candidates, new CharSequenceComparator());
        return res;
    }

    private Collection<Completer> getCompleters() {

        Collection<Completer> completers = new ArrayList<Completer>();

        for (ServiceReference reference : references) {
            // For each command
            // Provide a first Completer with registered function names
            String[] functionNames = getFunctionNames(reference);
            List<Completer> cl = new ArrayList<Completer>();
            cl.add(new StringsCompleter(functionNames));

            // Then, each command may provides its own set of Completers
            try {
                Object service = context.getService(reference);

                if (service instanceof ICompletable) {
                    ICompletable completable = (ICompletable) service;
                    List<Completer> commandCompleters = completable.getCompleters();
                    if (commandCompleters != null) {
                        for (Completer completer : commandCompleters) {
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
                completers.add(new ArgumentCompleter(cl));

            } finally {
                context.ungetService(reference);
            }
        }

        return completers;
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
