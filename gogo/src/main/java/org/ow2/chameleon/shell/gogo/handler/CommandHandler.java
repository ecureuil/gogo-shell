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

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.basic.ActionPreparator;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.command.Function;

@Handler(name = "command",
		 namespace = CommandHandler.NAMESPACE)
public class CommandHandler extends PrimitiveHandler {

	/**
     * The handler Namespace.
     */
    public static final String NAMESPACE = "org.ow2.chameleon.shell.gogo";

	public static enum Type {
		STATELESS, STATEFUL
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
	 * Default type is stateful.
	 */
	private Type type = Type.STATEFUL;

	private ServiceRegistration registration;

	private GogoCommand command;

    @Requires
    private ActionPreparator preparator;

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
		} // by default, a command a stateless

		// OK, now we have the configuration
	}

	public void registerCommandService() {
		// register the service

		BundleContext context = this.getFactory().getBundleContext();
		Class<?> actionClass = this.getInstanceManager().getClazz();
        Command cmd = actionClass.getAnnotation(Command.class);
        if (cmd == null) {
            throw new IllegalArgumentException("Action class is not annotated with @Command");
        }
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put("osgi.command.scope", cmd.scope());
        props.put("osgi.command.function", cmd.name());

        command = null;
        switch (type) {
        case STATEFUL:
        	command = new StatefulGogoCommand(this.getInstanceManager(), preparator);
        	break;
        case STATELESS:
        	command = new StatelessGogoCommand(this.getInstanceManager(), preparator);
        	break;
        }

        registration = context.registerService(Function.class.getName(), command, props);
	}

	public void unregisterCommandService() {
		// un-register the service
		registration.unregister();
		command.release();
	}

	@Override
	public void stateChanged(int state) {
		switch(state) {
		case ComponentInstance.VALID:
			registerCommandService();
			break;
		case ComponentInstance.INVALID:
			unregisterCommandService();
			break;
		}
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}



}
