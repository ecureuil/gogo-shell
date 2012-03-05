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
import org.apache.felix.service.command.Converter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;

/**
 * @author Loris Bouzonnet
 */
@Component
@Provides
public class BundleConverter implements Converter {

    private BundleContext context;

    public BundleConverter(BundleContext context) {
        this.context = context;
    }

    public Object convert(Class<?> desiredType, Object in) throws Exception {
        if (desiredType == Bundle.class) {
            String s = in.toString();
            try {
                long id = Long.parseLong(s);
                return context.getBundle(id);
            } catch (NumberFormatException nfe) {
                // Ignore
            }

            Bundle bundles[] = context.getBundles();
            for (Bundle b : bundles) {
                if (b.getLocation().equals(s)) {
                    return b;
                }

                if (b.getSymbolicName().equals(s)) {
                    return b;
                }
            }
        }
        return null;
    }

    public CharSequence format(Object target, int level, Converter converter) throws Exception {
        if (level == LINE && target instanceof Bundle) {
            return print((Bundle) target);
        }
        if (level == PART && target instanceof Bundle) {
            return ((Bundle) target).getSymbolicName();
        }
        return null;
    }

    private CharSequence print(Bundle bundle) {
        // [ ID ] [STATE      ] [ SL ] symname
        StartLevel sl = null;
        ServiceReference ref = context.getServiceReference(StartLevel.class.getName());
        if (ref != null) {
            sl = (StartLevel) context.getService(ref);
        }

        if (sl == null) {
            return String.format("%5d|%-11s|%s (%s)", bundle.getBundleId(),
                    getState(bundle), bundle.getSymbolicName(), bundle.getVersion());
        }

        int level = sl.getBundleStartLevel(bundle);
        context.ungetService(ref);

        return String.format("%5d|%-11s|%5d|%s (%s)", bundle.getBundleId(),
                getState(bundle), level, bundle.getSymbolicName(), bundle.getVersion());
    }

    private String getState(Bundle bundle) {
        switch (bundle.getState()) {
            case Bundle.ACTIVE:
                return "Active";

            case Bundle.INSTALLED:
                return "Installed";

            case Bundle.RESOLVED:
                return "Resolved";

            case Bundle.STARTING:
                return "Starting";

            case Bundle.STOPPING:
                return "Stopping";

            case Bundle.UNINSTALLED:
                return "Uninstalled ";
        }
        return null;
    }
}
