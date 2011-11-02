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

package org.ow2.chameleon.shell.gogo.internal.handler;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.basic.ActionPreparator;
import org.apache.felix.ipojo.InstanceManager;

/**
 * A Stateless Command is a command whose Action does not keep state in the instance.
 * Just like EJB stateless beans, any instance of the action can be used to execute a request.
 * That means that if the user perform multiple invocation of the same command,
 * a new instance will be created for each execution.
 */
public class StatelessGogoCommand extends GogoCommand {

	public StatelessGogoCommand(InstanceManager manager,
                                ActionPreparator preparator) {
		super(manager, preparator);
	}

	@Override
	public Action createNewAction() {
        return (Action) manager.createPojoObject();
	}

    @Override
    public void releaseAction(Action action) throws Exception {
        manager.deletePojoObject(action);
    }
}
