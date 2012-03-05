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
import org.apache.felix.ipojo.whiteboard.Wbp;
import org.apache.felix.service.command.Converter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.container.ReifiedType;
import org.ow2.chameleon.shell.gogo.IConverterManager;
import org.ow2.chameleon.shell.gogo.TypeBasedConverter;
import org.ow2.chameleon.shell.gogo.internal.blueprint.GenericType;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Loris Bouzonnet
 */
@Component
@Provides
@Wbp(filter = "(" + Constants.OBJECTCLASS + "=org.apache.felix.service.command.Converter)",
        onArrival = "addConverter", onDeparture = "removeConverter", onModification = "updateConverter")
public class ConverterManagerImpl implements IConverterManager {

    /**
     * Supported classes by converter.
     */
    private final Map<Converter, Set<Class<?>>> supportedClassesByConverter =
            new IdentityHashMap<Converter, Set<Class<?>>>();

    /**
     * Converter by service reference, ordered by descending service rank.
     */
    private final TreeMap<ServiceReference, Converter> converterByRef =
            new TreeMap<ServiceReference, Converter>(new Comparator<ServiceReference>() {
                // We want a descending order
                public int compare(ServiceReference sr1, ServiceReference sr2) {
                    return - sr1.compareTo(sr2);
                }
            });

    private BundleContext bundleContext;

    public ConverterManagerImpl(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void addConverter(final ServiceReference ref) {
        Converter converter = (Converter) bundleContext.getService(ref) ;
        supportedClassesByConverter.put(converter, findSupportedClasses(ref));
        converterByRef.put(ref, converter);
    }

    public void removeConverter(final ServiceReference ref) {
        Converter converter = converterByRef.remove(ref);
        if (converter != null) {
            supportedClassesByConverter.remove(converter);
            bundleContext.ungetService(ref);
        }
    }

    public void updateConverter(final ServiceReference ref) {
        supportedClassesByConverter.put(converterByRef.get(ref), findSupportedClasses(ref));
    }

    private Set<Class<?>> findSupportedClasses(final ServiceReference ref) {
        Object classes = ref.getProperty(Converter.CONVERTER_CLASSES);
        Set<Class<?>> supportedClasses = null;
        if (classes instanceof String) {
            supportedClasses  = new HashSet<Class<?>>();
            try {
                supportedClasses.add((Class<?>) ref.getBundle().loadClass((String) classes));
            } catch (ClassNotFoundException e) {

            }
        } else if (classes instanceof String[]) {
            supportedClasses  = new HashSet<Class<?>>();
            for (String c : (String[]) classes) {
                try {
                    supportedClasses.add((Class<?>) ref.getBundle().loadClass(c));
                } catch (ClassNotFoundException e) {

                }
            }
        }
        return supportedClasses;
    }

    public Object convert(ReifiedType desiredType, Object in) throws Exception {

        if (desiredType.getRawClass().isAssignableFrom(in.getClass())) {
            return in;
        }

        for (Converter converter : converterByRef.values()) {
            Set<Class<?>> supportedClasses = supportedClassesByConverter.get(converter);
            if (supportedClasses == null || isSupportedClass(in, supportedClasses)) {
                Object result;
                if (converter instanceof TypeBasedConverter) {
                    result = ((TypeBasedConverter) converter).convert(desiredType, in);
                } else {
                    result = converter.convert(desiredType.getRawClass(), in);
                }
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public Object convert(final Type desiredType,final Object in) throws Exception {

        GenericType genericType = new GenericType(desiredType);
        if (genericType.getRawClass().isAssignableFrom(in.getClass())) {
            return in;
        }

        for (Converter converter : converterByRef.values()) {
            Set<Class<?>> supportedClasses = supportedClassesByConverter.get(converter);
            if (supportedClasses == null || isSupportedClass(in, supportedClasses)) {
                Object result = converter.convert(genericType.getRawClass(), in);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private boolean isSupportedClass(final Object in, Set<Class<?>> supportedClasses) {
        for (Class<?> c : supportedClasses) {
            if (c.isAssignableFrom(in.getClass())) {
                return true;
            }
        }
        return false;
    }
}
