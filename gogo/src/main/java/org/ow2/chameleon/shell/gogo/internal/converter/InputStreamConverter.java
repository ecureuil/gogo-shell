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

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.service.command.Converter;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Loris Bouzonnet
 */
@Component
@Provides
public class InputStreamConverter implements Converter {

    @ServiceProperty(name = Converter.CONVERTER_CLASSES, value = "java.io.InputStream")
    private String supportedClasses;

    public Object convert(Class<?> desiredType, Object in) throws Exception {
        if (desiredType.equals(String.class) && in instanceof InputStream) {
            return read((InputStream) in);
        }
        return null;
    }

    public CharSequence format(Object target, int level, Converter converter) throws Exception {
        if (level == INSPECT && target instanceof InputStream) {
            return read((InputStream) target);
        }
        return null;
    }

    private CharSequence read(InputStream in) throws IOException {
        int c;
        StringBuffer sb = new StringBuffer();
        while ((c = in.read()) > 0) {
            if (c >= 32 && c <= 0x7F || c == '\n' || c == '\r') {
                sb.append((char) c);
            } else {
                String s = Integer.toHexString(c).toUpperCase();
                sb.append("\\");
                if (s.length() < 1) {
                    sb.append(0);
                }
                sb.append(s);
            }
        }
        return sb;
    }
}
