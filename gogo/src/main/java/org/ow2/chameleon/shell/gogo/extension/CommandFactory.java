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

package org.ow2.chameleon.shell.gogo.extension;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.FactoryStateListener;
import org.apache.felix.ipojo.HandlerManager;
import org.apache.felix.ipojo.IPojoContext;
import org.apache.felix.ipojo.IPojoFactory;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.util.Logger;
import org.apache.felix.ipojo.util.Tracker;
import org.apache.felix.ipojo.util.TrackerCustomizer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.ow2.chameleon.shell.gogo.extension.parser.CommandParser;
import org.ow2.chameleon.shell.gogo.extension.type.ECommand;
import org.ow2.chameleon.shell.gogo.extension.type.ECompletor;

/**
 * <pre>
 * command @action
 *  completer @component {multiple}
 * </pre>
 */
public class CommandFactory extends IPojoFactory implements TrackerCustomizer {

    /**
     * The Command metadata parser.
     */
    private static CommandParser PARSER = new CommandParser();

    private ECommand command;

    /**
     * Factory name (named after the Action specified component name).
     */
    private String name;
    private Tracker tracker;

    private List<Factory> factories;
    private List<ComponentInstance> instances;
    private FactoryStateListener componentManager;

    /**
     * Creates an iPOJO Factory.
     * At the end of this method, the required set of handler is computed.
     * But the result is computed by a sub-class.
     *
     * @param context  the bundle context of the bundle containing the factory.
     * @param metadata the description of the component type.
     * @throws org.apache.felix.ipojo.ConfigurationException
     *          if the element describing the factory is malformed.
     */
    public CommandFactory(BundleContext context, Element metadata) throws ConfigurationException {
        super(context, metadata);
        instances = new ArrayList<ComponentInstance>();
        factories = new ArrayList<Factory>();
        componentManager = new ComponentManager();

        // Create the Composite type for the command
        command = PARSER.getCommandDescrition(m_componentMetadata);
    }

    /**
     * Computes the factory name.
     * Each sub-type must override this method.
     *
     * @return the factory name.
     */
    @Override
    public String getFactoryName() {
        if (name == null) {
            String action = m_componentMetadata.getAttribute("action");
            name = action + "CommandFactory";
        }
        return name;
    }

    @Override
    public Element getDescription() {
        return m_componentMetadata;
    }

    /**
     * Computes the required handler list.
     * Each sub-type must override this method.
     *
     * @return the required handler list
     */
    @Override
    public List getRequiredHandlerList() {
        return new ArrayList();
    }

    /**
     * Creates an instance.
     * This method is called with the monitor lock.
     *
     * @param config   the instance configuration
     * @param context  the iPOJO context to use
     * @param handlers the handler array to use
     * @return the new component instance.
     * @throws org.apache.felix.ipojo.ConfigurationException
     *          if the instance creation failed during the configuration process.
     */
    @Override
    public ComponentInstance createInstance(Dictionary config, IPojoContext context, HandlerManager[] handlers) throws ConfigurationException {
        return null;
    }

    /**
     * Gets the factory class name.
     *
     * @return the factory class name.
     * @see org.apache.felix.ipojo.Factory#getClassName()
     */
    @Override
    public String getClassName() {
        return "";
    }

    /**
     * Gets the version of the component type.
     *
     * @return the component type version or <code>null</code> if
     *         not specified.
     */
    public String getVersion() {
        return null;
    }

    /**
     * Stopping method.
     * This method is call when the factory is stopping.
     * This method is called when holding the lock on the factory.
     */
    @Override
    public void stopping() {
        stopInstances();
        tracker.close();
    }

    /**
     * Starting method.
     * This method is called when the factory is starting.
     * This method is called when holding the lock on the factory.
     */
    @Override
    public void starting() {
        try {
            String filter = "(&(" + Constants.OBJECTCLASS + "=" + Factory.class.getName() + ")"
            + "(factory.state=1)"
            + ")";
            tracker = new Tracker(m_context, m_context.createFilter(filter), this);
            tracker.open();
        } catch (InvalidSyntaxException e) {
            m_logger.log(Logger.ERROR, "A factory filter is not valid: " + e.getMessage());
            stop();
            return;
        }
    }

    public boolean addingService(ServiceReference reference) {
        // Only interested in action or completor components
        String name = (String) reference.getProperty("factory.name");

        // First case of error
        if (name == null) {
            return false;
        }

        return isRequiredFactory(name);
    }

    private boolean isRequiredFactory(String name) {
        if (command.getAction().equals(name)) {
            return true;
        }

        for (ECompletor completor : command.getCompletors()) {
            if (completor.getComponent().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public void addedService(ServiceReference reference) {
        // Got one of the required valid factories
        Factory factory = (Factory) this.getBundleContext().getService(reference);
        if (isRequiredFactory(factory.getName())) {
            // Add a state listener
            factory.addFactoryStateListener(componentManager);
            factories.add(factory);
            updateState();
        }
        
    }

    public void modifiedService(ServiceReference reference, Object service) {
        // Handle
    }

    public void removedService(ServiceReference reference, Object service) {
        //To change body of implemented methods use File | Settings | File Templates.
        // TODO to be implemented

        Factory factory = (Factory) this.getBundleContext().getService(reference);
        if (isRequiredFactory(factory.getName())) {
            // One required disappeared
            stopInstances();
            factories.remove(factory);

        }

    }

    private class ComponentManager implements FactoryStateListener {
        public void stateChanged(Factory factory, int newState) {
            updateState();
        }
    }

    private void updateState() {

        boolean allValids = true;

        // Check that we have all the required factories
        // and that they are all valid
        List<String> requiredFactories = getRequiredFactories();
        for (Factory factory : factories) {
            if (Factory.INVALID == factory.getState()) {
                allValids = false;
            }
            requiredFactories.remove(factory.getName());
        }

        if (allValids && requiredFactories.isEmpty()) {
            startInstances();
        } else {
            stopInstances();
        }
    }

    private List<String> getRequiredFactories() {

        List<String> names = new ArrayList<String>();
        names.add(command.getAction());
        for (ECompletor completor : command.getCompletors()) {
            names.add(completor.getComponent());
        }
        return names;
    }

    private void stopInstances() {
        for (ComponentInstance instance : instances) {
            if (instance.isStarted()) {
                instance.stop();
                instance.dispose();
            }
        }

        instances.clear();
    }

    private void startInstances() {
        Factory actionFactory = getActionFactory();
        ComponentInstance actionInstance = null;
        try {
            actionInstance = actionFactory.createComponentInstance(null);
        } catch (Exception e) {
            // TODO Fixme
            e.printStackTrace();
        }
        String id = actionInstance.getInstanceName();

        List<Factory> completorFactories = getCompletorFactories();
        for (Factory factory : completorFactories) {
            Dictionary dict = getCompletorConfiguration(factory.getName());
            dict.put("command.id", id);

            ComponentInstance completor = null;
            try {
                completor = factory.createComponentInstance(dict);
            } catch (Exception e) {
                // TODO Fixme
                e.printStackTrace();
            }
            instances.add(completor);
        }

        instances.add(actionInstance);

        for (ComponentInstance instance : instances) {
            instance.start();
        }
    }

    private Dictionary getCompletorConfiguration(String name) {

        for (ECompletor completor : command.getCompletors()) {
            if (completor.getComponent().equals(name)) {
                return completor.getConfiguration();
            }
        }
        throw new IllegalStateException("Should not happen");
    }

    private List<Factory> getCompletorFactories() {

        List<Factory> completors = new ArrayList<Factory>();
        for (Factory factory : factories) {
            // We have action or completors, nothing else
            if (!factory.getName().equals(command.getAction())) {
                completors.add(factory);
            }
        }
        return completors;
    }

    private Factory getActionFactory() {
        for (Factory factory : factories) {
            if (factory.getName().equals(command.getAction())) {
                return factory;
            }
        }

        throw new IllegalStateException("Should not happen");
    }
}
