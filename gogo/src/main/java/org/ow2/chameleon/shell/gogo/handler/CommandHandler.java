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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import jline.Completor;
import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.basic.ActionPreparator;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.service.command.CommandProcessor;
import org.osgi.service.command.CommandSession;
import org.osgi.service.command.Function;
import org.ow2.chameleon.shell.gogo.ICompletableCommand;

/**
 * This Handler manages the link between the iPOJO component (implementing the
 * {@code Action} interface) and the shell runtime.
 */
@Handler(name = "command",
		 namespace = CommandHandler.NAMESPACE, architecture = true)
@Provides(specifications = Function.class)
public class CommandHandler extends PrimitiveHandler implements Function, ICompletableCommand {

	/**
     * The handler Namespace.
     */
    public static final String NAMESPACE = "org.ow2.chameleon.shell.gogo";

    /**
     * Defines supported command types.
     */
	public static enum Type {
        /**
         * A stateless command is a command that keep no state information.
         */
		STATELESS,

        /**
         * A stateful command is a command that keeps some state in the instances.
         */
        STATEFUL
	}

    /**
     * Name of the 'type' attribute.
     */
	public static String TYPE_ATTRIBUTE = "type";

    /**
     * Name of the 'command' element.
     */
	public static String COMMAND_ELEMENT = "command";

	/**
	 * Default type is stateless.
	 */
	private Type type = Type.STATELESS;

    /**
     * The command.
     */
	private GogoCommand command;

    @Requires
    private ActionPreparator preparator;

    private List<Completor> completors;

    @ServiceProperty(name = CommandProcessor.COMMAND_SCOPE)
    private String scope;

    @ServiceProperty(name = CommandProcessor.COMMAND_FUNCTION)
    private String function;


	@Override
	public void configure(Element element, Dictionary dictionary)
			throws ConfigurationException {

		// Explore the elements
		if (!element.containsElement(COMMAND_ELEMENT, NAMESPACE)) {
			throw new ConfigurationException("Missing 'command' element.");
		}

		// There is always at least 1 'command' element
		Element commandElement = element.getElements(COMMAND_ELEMENT, NAMESPACE)[0];

		// Get the type
		String typeAttr = commandElement.getAttribute(TYPE_ATTRIBUTE, NAMESPACE);
		if (typeAttr != null) {
			try {
				type = Type.valueOf(typeAttr.toUpperCase());
			} catch (IllegalArgumentException iae) {
				// Invalid value
				throw new ConfigurationException("Invalid value for 'type' attribute "
						                         + "('stateless' or 'stateful' only are permitted).");
			}
		} // by default, a command is stateless

		// OK, now we have the configuration
        
        completors = new ArrayList<Completor>();

	}

    @Bind(optional = true,
          aggregate = true)
    public void bindCompletor(Completor completor) {
        completors.add(completor);
    }

    @Unbind
    public void unbindCompletor(Completor completor) {
        completors.remove(completor);
    }

    /**
     * Simply wraps the inner command.
     */
    public Object execute(final CommandSession commandSession,
                          final List<Object> objects) throws Exception {
        if (command != null) {
            return command.execute(commandSession, objects);
        } else {
            System.err.println("No command registered !!");
            return null;
        }
    }

    public List<Completor> getCompletors() {
        return completors;
    }

	@Override
	public void start() {

        // Do some preliminary checking
        Class<?> actionClass = this.getInstanceManager().getClazz();
        // Ensure the component implements the Action gogo interface
        if (!Action.class.isAssignableFrom(actionClass)) {
            throw new IllegalArgumentException("The Component is not inheriting from the Action interface!");
        }
        // Ensure that the component is annotated with @Command
        Command cmd = actionClass.getAnnotation(Command.class);
        if (cmd == null) {
            throw new IllegalArgumentException("Action class is not annotated with @Command");
        }

        // Prepare service properties
        scope = cmd.scope();
        function = cmd.name();

        // Actually create the command given its declared type
        switch (type) {
        case STATEFUL:
            command = new StatefulGogoCommand(this.getInstanceManager(),
                                              preparator,
                                              completors);
            break;
        case STATELESS:
            command = new StatelessGogoCommand(this.getInstanceManager(),
                                               preparator,
                                               completors);
            break;
        }
	}

	@Override
	public void stop() {
        scope = null;
        function = null;
        command = null;
	}
}
