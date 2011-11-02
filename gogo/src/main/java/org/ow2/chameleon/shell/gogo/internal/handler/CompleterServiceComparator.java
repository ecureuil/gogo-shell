package org.ow2.chameleon.shell.gogo.internal.handler;

import java.util.Comparator;

import org.osgi.framework.ServiceReference;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 30 janv. 2010
 * Time: 12:43:49
 * To change this template use File | Settings | File Templates.
 */
public class CompleterServiceComparator implements Comparator<ServiceReference> {

    public static final String POSITION = "completer.position";

    public int compare(ServiceReference ref1, ServiceReference ref2) {

        String pos1 = (String) ref1.getProperty(POSITION);
        String pos2 = (String) ref2.getProperty(POSITION);

        if ((pos1 != null) && (pos2 != null)) {
            // Completers are comparable
            return pos1.compareTo(pos2);
        }
        return 0;
    }
}
