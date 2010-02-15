package org.ow2.chameleon.shell.gogo.extension.type;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 5 févr. 2010
 * Time: 21:42:59
 * To change this template use File | Settings | File Templates.
 */
public class ECompletor {

    /**
     * Completor component name.
     */
    private String component;

    /**
     * Configuration.
     */
    private Dictionary<String, Object> properties;

    public ECompletor(String component) {
        this.component = component;
    }

    public void addProperty(String name, Object value) {
        if (properties == null) {
            properties = new Hashtable<String, Object>();
        }
        properties.put(name, value);
    }

    public String getComponent() {
        return component;
    }

    public Dictionary<String, Object> getConfiguration() {
        return properties;
    }

    @Override
    public String toString() {
        return "ECompletor{" +
                "component='" + component + '\'' +
                ", properties=" + properties +
                '}';
    }
}
