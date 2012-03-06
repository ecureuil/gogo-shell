/**
 * Copyright 2012 OW2 Chameleon
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

package org.ow2.chameleon.shell.gogo.internal.converter;

import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.service.command.Converter;

import java.io.ByteArrayInputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author Loris Bouzonnet
 */
public class StringConverter implements Converter {

    @ServiceProperty(name = Converter.CONVERTER_CLASSES, value = "java.lang.String")
    private String supportedClasses;

    public Object convert(Class<?> toType, Object value) throws Exception {
        if (Class.class.equals(toType)) {

            // Try to load
            try {
                return Class.forName(value.toString());
            } catch (ClassNotFoundException e) {
                return null;
            }
        } else if (Locale.class.equals(toType)) {

            // Locale parsing
            String[] tokens = value.toString().split("_");
            if (tokens.length == 1) {
                return new Locale(tokens[0]);
            } else if (tokens.length == 2) {
                return new Locale(tokens[0], tokens[1]);
            } else if (tokens.length == 3) {
                return new Locale(tokens[0], tokens[1], tokens[2]);
            } else {
                throw new Exception("Invalid locale string:" + value);
            }
        } else if (Pattern.class.equals(toType)) {
            return Pattern.compile(value.toString());
        } else if (Properties.class.equals(toType)) {
            Properties props = new Properties();
            ByteArrayInputStream in = new ByteArrayInputStream(value.toString().getBytes("UTF8"));
            props.load(in);
            return props;
        } else if (Boolean.class.equals(toType)) {
            if ("yes".equalsIgnoreCase(value.toString()) || "true".equalsIgnoreCase(value.toString()) || "on".equalsIgnoreCase(value.toString())) {
                return Boolean.TRUE;
            } else if ("no".equalsIgnoreCase(value.toString()) || "false".equalsIgnoreCase(value.toString()) || "off".equalsIgnoreCase(value.toString())) {
                return Boolean.FALSE;
            } else {
                throw new RuntimeException("Invalid boolean value: " + value);
            }
        } else if (Integer.class.equals(toType)) {
            return Integer.valueOf(value.toString());
        } else if (Short.class.equals(toType)) {
            return Short.valueOf(value.toString());
        } else if (Long.class.equals(toType)) {
            return Long.valueOf(value.toString());
        } else if (Float.class.equals(toType)) {
            return Float.valueOf(value.toString());
        } else if (Double.class.equals(toType)) {
            return Double.valueOf(value.toString());
        } else if (Character.class.equals(toType)) {
            if (value.toString().length() == 6 && value.toString().startsWith("\\u")) {
                int code = Integer.parseInt(value.toString().substring(2), 16);
                return (char) code;
            } else if (value.toString().length() == 1) {
                return value.toString().charAt(0);
            } else {
                throw new Exception("Invalid value for character type: " + value);
            }
        } else if (Byte.class.equals(toType)) {
            return Byte.valueOf(value.toString());
        } else if (Enum.class.isAssignableFrom(toType)) {
            return Enum.valueOf((Class<Enum>) toType, value.toString());
        }
        return null;
    }

    public CharSequence format(Object o, int i, Converter converter) throws Exception {
        return null;
    }

}
