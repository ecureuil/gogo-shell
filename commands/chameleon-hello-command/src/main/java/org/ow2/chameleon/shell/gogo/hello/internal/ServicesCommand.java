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

package org.ow2.chameleon.shell.gogo.hello.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.service.command.CommandSession;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@Component
@Command(name="services2", scope="osgi", description="List services")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class ServicesCommand implements Action {

	@Argument
	private Bundle bundle;

	private BundleContext context;

	public ServicesCommand(BundleContext context) {
		this.context = context;
	}

	public Object execute(CommandSession session) throws Exception {
		if (bundle == null) {
			// Display all services
			return asList(context.getAllServiceReferences(null, null));
		} else {
			return asList(bundle.getRegisteredServices());
		}
	}

	private List<ServiceReference> asList(ServiceReference[] refs) {
		List<ServiceReference> references = new ArrayList<ServiceReference>();
		for (ServiceReference ref : refs) {
			references.add(ref);
		}
		return references;
	}
}
