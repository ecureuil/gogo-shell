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

package org.ow2.chameleon.shell.gogo.extension.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.felix.gogo.commands.basic.ActionPreparator;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.api.composite.CompositeComponentType;
import org.apache.felix.ipojo.api.composite.ExportedService;
import org.apache.felix.ipojo.api.composite.ImportedService;
import org.apache.felix.ipojo.api.composite.Instance;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.framework.BundleContext;
import org.osgi.service.command.Function;
import org.ow2.chameleon.shell.gogo.extension.type.ECommand;
import org.ow2.chameleon.shell.gogo.extension.type.ECompletor;
import org.ow2.chameleon.shell.gogo.handler.CommandHandler;
import org.ow2.chameleon.shell.gogo.handler.CompletorServiceComparator;

import static org.ow2.chameleon.shell.gogo.extension.parser.MetadataUtils.getAttributeValue;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 30 janv. 2010
 * Time: 10:17:17
 * To change this template use File | Settings | File Templates.
 */
public class CommandParser {


	/**
     * The handler Namespace.
     */
    public static final String NAMESPACE = "org.ow2.chameleon.shell";

    public static final String ACTION = "action";
    public static final String SPECIFICATION = "specification";
    public static final String AGGREGATE = "aggregate";
    public static final String FILTER = "filter";
    public static final String OPTIONAL = "optional";
    public static final String COMPONENT = "component";
    public static final String PROPERTY = "property";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String VALUE = "value";
    public static final String ARRAY_TYPE = "array";
    public static final String VECTOR_TYPE = "vector";
    public static final String LIST_TYPE = "list";
    public static final String MAP_TYPE = "map";
    public static final String DICTIONARY_TYPE = "dictionary";
    public static final String COMPLETOR = "completor";
    public static final String EXPORT = "export";
    public static final String IMPORT = "import";

    public ECommand getCommandDescrition(Element metadata) throws ConfigurationException {

        ECommand type = new ECommand(getAttributeValue(metadata, ACTION));

        // Create completors instances
        Element[] completors = metadata.getElements(COMPLETOR, NAMESPACE);

        if (completors != null) {
            for (int i = 0; i < completors.length; i++) {
                ECompletor completor = createCompletor(completors[i]);
                // Add an attribute used to sort the completors automatically
                completor.addProperty(CompletorServiceComparator.POSITION,
                                      String.valueOf(i));
                type.addCompletor(completor);
            }
        }

        return type;
    }

    private ECompletor createCompletor(Element element) throws ConfigurationException {

        ECompletor instance = new ECompletor(getAttributeValue(element, COMPONENT));

        Element[] properties = element.getElements(PROPERTY);
        if (properties != null) {
            for (Element prop : properties) {
                String name = getAttributeValue(prop, NAME);
                String type = getAttributeValue(prop, TYPE, false);
                if (type != null) {
                    if (ARRAY_TYPE.equals(type)) {
                        instance.addProperty(name, getArrayProperty(prop));
                    } else if (VECTOR_TYPE.equals(type)) {
                        instance.addProperty(name, getVectorProperty(prop));
                    } else if (LIST_TYPE.equals(type)) {
                        instance.addProperty(name, getListProperty(prop));
                    } else if (MAP_TYPE.equals(type)) {
                        instance.addProperty(name, getMapProperty(prop));
                    } else if (DICTIONARY_TYPE.equals(type)) {
                        instance.addProperty(name, getDictionaryProperty(prop));
                    } else {
                        throw new ConfigurationException("Unknown type '" + type + "' on element '" + prop.getName() + "'.");
                    }
                } else {
                    // Handle inline array declaration {a,b,c}
                    NameValue nv = getSimpleProperty(prop);
                    String value = nv.value.trim();
                    if (value.startsWith("{") && value.endsWith("}")) {
                        // Remove first and last char
                        value = value.substring(1);
                        value = value.substring(0, value.lastIndexOf('}'));
                        // TODO trim attribute values ?
                        instance.addProperty(name, value.split(","));
                    } else {
                        // Normal String value
                        instance.addProperty(nv.name, nv.value);
                    }

                }
            }
        }

        return instance;
    }

    private String[] getArrayProperty(Element element) throws ConfigurationException {
        String[] strings = new String[0];
        Element[] properties = element.getElements(PROPERTY);
        if (properties != null) {
            strings = new String[properties.length];
            for (int i = 0; i < properties.length; i++) {
                strings[i] = getAttributeValue(properties[i], VALUE);
            }
        }
        return strings;
    }

    private Vector getVectorProperty(Element element) throws ConfigurationException {
        Vector vector = new Vector();
        Element[] properties = element.getElements(PROPERTY);
        if (properties != null) {
            for (int i = 0; i < properties.length; i++) {
                vector.add(getAttributeValue(properties[i], VALUE));
            }
        }
        return vector;
    }

    private List getListProperty(Element element) throws ConfigurationException {
        List list = new ArrayList();
        Element[] properties = element.getElements(PROPERTY);
        if (properties != null) {
            for (int i = 0; i < properties.length; i++) {
                list.add(getAttributeValue(properties[i], VALUE));
            }
        }
        return list;
    }

    private Map getMapProperty(Element element) throws ConfigurationException {
        Map map = new HashMap();
        Element[] properties = element.getElements(PROPERTY);
        if (properties != null) {
            for (Element property : properties) {
                NameValue nv = getSimpleProperty(property);
                map.put(nv.name, nv.value);
            }
        }
        return map;
    }

    private Dictionary getDictionaryProperty(Element element) throws ConfigurationException {
        Dictionary dictionary = new Properties();
        Element[] properties = element.getElements(PROPERTY);
        if (properties != null) {
            for (Element property : properties) {
                NameValue nv = getSimpleProperty(property);
                dictionary.put(nv.name, nv.value);
            }
        }
        return dictionary;
    }

    private NameValue getSimpleProperty(Element element) throws ConfigurationException {
        NameValue property = new NameValue();
        property.name = getAttributeValue(element, NAME);
        property.value = getAttributeValue(element, VALUE);
        return property;
    }


    private class NameValue {
        String name;
        String value;
    }
}