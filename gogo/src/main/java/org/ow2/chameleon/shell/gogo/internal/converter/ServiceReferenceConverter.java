package org.ow2.chameleon.shell.gogo.internal.converter;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.service.command.Converter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Formatter;

/**
 * @author Loris Bouzonnet
 */
@Component
@Provides
public class ServiceReferenceConverter implements Converter {

    @ServiceProperty(name = Converter.CONVERTER_CLASSES, value = "org.osgi.framework.ServiceReference")
    private String supportedClasses;
    
    private BundleContext context;

    public ServiceReferenceConverter(BundleContext context) {
        this.context = context;
    }

    public Object convert(Class<?> desiredType, Object in) throws Exception {
        if (desiredType == ServiceReference.class) {
            String s = in.toString();
            if (s.startsWith("(") && s.endsWith(")")) {
                ServiceReference refs[] = context.getServiceReferences(null, String.format(
                        "(|(service.id=%s)(service.pid=%s))", in, in));
                if (refs != null && refs.length > 0) {
                    return refs[0];
                }
            }

            ServiceReference refs[] = context.getServiceReferences(null, String.format(
                    "(|(service.id=%s)(service.pid=%s))", in, in));
            if (refs != null && refs.length > 0) {
                return refs[0];
            }
            return null;
        }
        return null;
    }

    public CharSequence format(Object target, int level, Converter converter) throws Exception {
        if (level == LINE && target instanceof ServiceReference) {
            return print((ServiceReference) target);
        }
        if (level == PART && target instanceof ServiceReference) {
            return getShortNames((String[]) ((ServiceReference) target).getProperty("objectclass"));
        }
        return null;
    }


    private CharSequence print(ServiceReference ref) {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);

        String spid = "";
        Object pid = ref.getProperty("service.pid");
        if (pid != null) {
            spid = pid.toString();
        }

        f.format("%06d %3s %-40s %s", ref.getProperty("service.id"),
                ref.getBundle().getBundleId(),
                getShortNames((String[]) ref.getProperty("objectclass")), spid);
        return sb;
    }

    protected CharSequence getShortNames(String[] list) {
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (String s : list) {
            sb.append(del + getShortName(s));
            del = " | ";
        }
        return sb;
    }

    protected CharSequence getShortName(String name) {
        int n = name.lastIndexOf('.');
        if (n < 0) {
            n = 0;
        } else {
            n++;
        }
        return name.subSequence(n, name.length());
    }
}
