package org.ow2.chameleon.shell.config.completor;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 28 janv. 2010
 * Time: 21:05:49
 * To change this template use File | Settings | File Templates.
 */
@Component(propagation = true)
@Provides(specifications = {Completer.class, ConfigurationListener.class})
public class ConfigurationCompletor extends StringsCompleter implements ConfigurationListener {

    private SortedSet<String> configurations;

    /**
     * Create a new SimpleCompletor with a single possible completion
     * values.
     */
    public ConfigurationCompletor() {
        super("");
        configurations = new TreeSet<String>();
    }

    @Override
    public int complete(String buffer, int cursor, List clist) {
        // Update candidates list
        getStrings().clear();
        getStrings().addAll(configurations);
        return super.complete(buffer, cursor, clist);
    }

    /**
     * Receives notification of a Configuration that has changed.
     *
     * @param event The <code>ConfigurationEvent</code>.
     */
    public void configurationEvent(ConfigurationEvent event) {
        switch (event.getType()) {
            case ConfigurationEvent.CM_UPDATED:
                configurations.add(event.getPid());
                break;
            case ConfigurationEvent.CM_DELETED:
                configurations.remove(event.getPid());
                break;
        }
    }
}
