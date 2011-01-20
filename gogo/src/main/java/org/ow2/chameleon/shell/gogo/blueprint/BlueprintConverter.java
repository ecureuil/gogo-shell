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

package org.ow2.chameleon.shell.gogo.blueprint;

import org.apache.felix.service.command.Converter;
import org.osgi.service.blueprint.container.ReifiedType;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 5 oct. 2009
 * Time: 22:05:57
 * To change this template use File | Settings | File Templates.
 */
public class BlueprintConverter implements Converter {

    private org.osgi.service.blueprint.container.Converter converter;

    public BlueprintConverter(org.osgi.service.blueprint.container.Converter converter) {
        this.converter = converter;
    }

    /**
     * Convert an object to the desired type.
     * <p/>
     * Return null if the conversion can not be done. Otherwise return and
     * object that extends the desired type or implements it.
     *
     * @param desiredType The type that the returned object can be assigned to
     * @param in          The object that must be converted
     * @return An object that can be assigned to the desired type or null.
     * @throws Exception
     */
    public Object convert(Class<?> desiredType, Object in) throws Exception {
        ReifiedType type = new GenericType(desiredType); 
        if (converter.canConvert(in, type)) {
            return converter.convert(in, type);
        }
        return null;

    }

    /**
     * Convert an object to a CharSequence object in the requested format. The
     * format can be INSPECT, LINE, or PART. Other values must throw
     * IllegalArgumentException.
     *
     * @param target The object to be converted to a String
     * @param level  One of INSPECT, LINE, or PART.
     * @param escape Use this object to format sub ordinate objects.
     * @return A printed object of potentially multiple lines
     * @throws Exception
     */
    public CharSequence format(Object target, int level, Converter escape) throws Exception {
        // The Blueprint converting system does not provide formatting features
        return null;
    }
}
