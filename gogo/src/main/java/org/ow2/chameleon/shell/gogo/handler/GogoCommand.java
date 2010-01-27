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

package org.ow2.chameleon.shell.gogo.handler;

import java.util.List;

import jline.Completor;
import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.felix.gogo.commands.basic.ActionPreparator;
import org.apache.felix.ipojo.InstanceManager;
import org.ow2.chameleon.shell.gogo.ICompletableCommand;


public abstract class GogoCommand extends AbstractCommand implements ICompletableCommand {

	protected InstanceManager manager;

    private ActionPreparator preparator;

    private List<Completor> completors;

	public GogoCommand(InstanceManager manager, ActionPreparator preparator, List<Completor> completors) {
		this.manager = manager;
        this.preparator = preparator;
        this.completors = completors;
	}

    public void release() {
		this.manager = null;
        this.preparator = null;
	}

	@Override
	protected ActionPreparator getPreparator() throws Exception {
		return preparator;
	}

    public List<Completor> getCompletors() {
        return completors;  
    }
}
