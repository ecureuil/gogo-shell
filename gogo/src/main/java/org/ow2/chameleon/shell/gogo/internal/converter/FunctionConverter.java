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
import org.apache.felix.service.command.Function;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @author Loris Bouzonnet
 */
@Component
@Provides
public class FunctionConverter implements Converter {

    @ServiceProperty(name = Converter.CONVERTER_CLASSES, value = "org.apache.felix.service.command.Function")
    private String supportedClasses;

    public Object convert(Class<?> desiredType, final Object in) throws Exception {
        if (in instanceof Function && desiredType.isInterface()
                && desiredType.getDeclaredMethods().length == 1) {
            return Proxy.newProxyInstance(desiredType.getClassLoader(),
                    new Class[]{desiredType}, new InvocationHandler() {
                Function command = ((Function) in);

                public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {
                    return command.execute(null, Arrays.asList(args));
                }
            });
        }
        return null;
    }

    public CharSequence format(Object o, int i, Converter converter) throws Exception {
        return null;
    }
}
