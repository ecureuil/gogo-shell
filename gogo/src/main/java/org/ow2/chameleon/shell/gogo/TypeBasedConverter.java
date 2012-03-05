/**
 * Copyright 2012 OW2 Chameleon
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

package org.ow2.chameleon.shell.gogo;

import org.apache.felix.service.command.Converter;
import org.osgi.service.blueprint.container.ReifiedType;

import java.lang.reflect.Type;

/**
 * @author Loris Bouzonnet
 */
public interface TypeBasedConverter extends Converter {

    Object convert(ReifiedType desiredType, Object in) throws Exception;
}
