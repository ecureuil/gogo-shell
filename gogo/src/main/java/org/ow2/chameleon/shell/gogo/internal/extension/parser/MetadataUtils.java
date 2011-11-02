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

package org.ow2.chameleon.shell.gogo.internal.extension.parser;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.metadata.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 30 janv. 2010
 * Time: 11:19:41
 * To change this template use File | Settings | File Templates.
 */
public class MetadataUtils {

    public static String getAttributeValue(final Element element,
                                     final String name) throws ConfigurationException {
        return getAttributeValue(element, name, true);
    }


    public static String getAttributeValue(final Element element,
                                     final String name,
                                     final boolean mandatory) throws ConfigurationException {
        String value = element.getAttribute(name);
        if ((value == null) && mandatory) {
            throw new ConfigurationException("Missing mandatory attribute '" + name + "' on element '" + element.getName() + "'.");
        }
        return value;
    }

}
