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
