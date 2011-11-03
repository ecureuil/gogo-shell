package org.ow2.chameleon.shell.gogo.internal.handler.completer;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 03/11/11
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
public class CharSequenceComparator implements Comparator<CharSequence> {
    public int compare(CharSequence one, CharSequence two) {
        return one.toString().compareTo(two.toString());
    }
}
