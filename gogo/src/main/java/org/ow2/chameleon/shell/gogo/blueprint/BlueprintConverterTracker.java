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

import java.util.Hashtable;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.service.command.Converter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 5 oct. 2009
 * Time: 22:43:33
 * To change this template use File | Settings | File Templates.
 */
@Component
public class BlueprintConverterTracker {

    private Map<ServiceReference, ServiceRegistration> converters;
    private BundleContext context;

    public BlueprintConverterTracker(BundleContext context) {
        this.context = context;
        this.converters = new Hashtable<ServiceReference, ServiceRegistration>();
    }

    @Bind(id = "blueprint.converters",
          specification = "org.osgi.service.blueprint.container.Converter")
    public void addBlueprintConverter(ServiceReference reference) {
        // wrap the Blueprint Converter in a usual Converter service
        registerConverter(reference);
    }

    @Unbind(id = "blueprint.converters",
            specification = "org.osgi.service.blueprint.container.Converter")
    public void removeBlueprintConverter(ServiceReference reference) {
        // Unregister the service
        unregisterConverter(reference);
    }

    private void unregisterConverter(ServiceReference reference) {
        ServiceRegistration reg = converters.get(reference);
        reg.unregister();
        converters.remove(reference);
    }

    private void registerConverter(ServiceReference reference) {
        Object converter = context.getService(reference);
        Converter bpWrapper = new BlueprintConverter((org.osgi.service.blueprint.container.Converter) converter);
        ServiceRegistration reg = context.registerService(Converter.class.getName(),
                                                          bpWrapper,
                                                          null);
        this.converters.put(reference, reg);
    }


}
