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

package org.ow2.chameleon.shell.gogo.extension.type;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 5 f�vr. 2010
 * Time: 21:42:59
 * To change this template use File | Settings | File Templates.
 */
public class ECompleter {

    /**
     * Completer component name.
     */
    private String component;

    /**
     * Configuration.
     */
    private Dictionary<String, Object> properties;

    public ECompleter(String component) {
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
        return "ECompleter{" +
                "component='" + component + '\'' +
                ", properties=" + properties +
                '}';
    }
}
