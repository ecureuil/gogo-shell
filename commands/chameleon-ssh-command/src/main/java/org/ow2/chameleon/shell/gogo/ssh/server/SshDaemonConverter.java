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

package org.ow2.chameleon.shell.gogo.ssh.server;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.service.command.Converter;

@Component
public class SshDaemonConverter implements Converter {

	public Object convert(Class<?> desiredType, Object in) throws Exception {
		// No convertion possible for now
		return null;
	}

	public CharSequence format(Object target, int level, Converter escape)
			throws Exception {
		if (target instanceof SshDaemonComponent) {
			switch (level) {
			case INSPECT:
				return "Inspecting " + target;
			case LINE:
				return "Lining " + target;
			default:
				// PART
				return "Parting " + target;
			}
		}
		return null;
	}

}
