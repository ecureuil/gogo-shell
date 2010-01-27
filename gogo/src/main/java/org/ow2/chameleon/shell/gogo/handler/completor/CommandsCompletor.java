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

package org.ow2.chameleon.shell.gogo.handler.completor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.MultiCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.osgi.framework.ServiceReference;
import org.osgi.service.command.CommandProcessor;
import org.ow2.chameleon.shell.gogo.ICompletableCommand;

/**
 * Created by IntelliJ IDEA.
 * User: sauthieg
 * Date: 22 janv. 2010
 * Time: 13:13:42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Provides
public class CommandsCompletor implements Completor {


    private Map<ServiceReference, Completor> completors;

    public CommandsCompletor() {
        completors = new HashMap<ServiceReference, Completor>();
    }


    @Bind(aggregate = true,
          optional = true,
          filter = "(&(osgi.command.scope=*)(osgi.command.function=*))")
    public void bindFunctionServiceReference(Object service, ServiceReference reference) {

        System.out.println("Bound reference: " + reference);

        // For each new command
        // Provide a first Completor with registered function names
        String[] functionNames = getFunctionNames(reference);
        List<Completor> cl = new ArrayList<Completor>();
        cl.add(new SimpleCompletor(functionNames));

        // Then, each command may provides its own set of Completors
        if (service instanceof ICompletableCommand) {
            ICompletableCommand command = (ICompletableCommand) service;
            List<Completor> commandCompletors = command.getCompletors();
            if (commandCompletors != null) {
                for (Completor completor : commandCompletors) {
                    // Support case where the command explicitely set a null value in the list
                    if (completor == null) {
                        cl.add(new NullCompletor());
                    } else {
                        // Normal case, just add the given Completor
                        cl.add(completor);
                    }
                }
            }
        } else {
            cl.add(new NullCompletor());
        }
        // that we wrap in an ArgumentCompletor (one for each command)
        ArgumentCompletor argumentCompletor = new ArgumentCompletor(cl);

        // We finally store the ArgumentCompletor of the command in the map
        completors.put(reference, argumentCompletor);

    }

    @Unbind
    public void unbindFunctionServiceReference(ServiceReference reference) {
        completors.remove(reference);
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

        // Create a multi completor for all registered completors
        // and run them all
        Completor[] array = completors.values().toArray(new Completor[completors.size()]);
        int res = new MultiCompletor(array).complete(buffer, cursor, candidates);

        // Note to myself: It still seems to be a little bit magic, how can jline,
        // from a list of all completors of all commands, provides the right completion values ...
        // There is some magic left in the world ;)

        // Then sort the resulting candidate list
        Collections.sort(candidates);
        return res;
    }
}
