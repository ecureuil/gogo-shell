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

import java.util.List;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.service.command.CommandSession;

@Component
@Command(name="hello", scope="test", description="A simple hello command")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class HelloCommand implements Action {

	@Argument(multiValued=true)
	private List<String> who;

	@Option(name="-l", aliases="--lang", required=false)
	private String lang = "en";

	public Object execute(CommandSession session) throws Exception {
		if ("en".equals(lang)) {
			return "Hello " + who;
		} else if ("fr".equals(lang)) {
			return "Bonjour " + who;
		} else {
			throw new Exception("Unknown language");
		}

	}
}
