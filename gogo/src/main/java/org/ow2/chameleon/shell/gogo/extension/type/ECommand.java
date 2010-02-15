package org.ow2.chameleon.shell.gogo.extension.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 5 févr. 2010
 * Time: 21:41:44
 * To change this template use File | Settings | File Templates.
 */
public class ECommand {

    /**
     * The action component name.
     */
    private String action;

    /**
     * The list of completors.
     */
    private List<ECompletor> completors;


    public ECommand(String action) {
        this.action = action;
        completors = new ArrayList<ECompletor>();
    }

    public void addCompletor(ECompletor completor) {
        completors.add(completor);
    }

    public String getAction() {
        return action;
    }

    public List<ECompletor> getCompletors() {
        return completors;
    }

    @Override
    public String toString() {
        return "ECommand{" +
                "action='" + action + '\'' +
                ", completors=" + completors +
                '}';
    }
}
