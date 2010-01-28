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

package org.ow2.chameleon.shell.gogo.console;

import java.util.Set;

import jline.Completor;
import jline.ConsoleReader;
import jline.ConsoleReaderInputStream;
import jline.History;
import jline.MultiCompletor;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.service.command.CommandProcessor;
import org.ow2.chameleon.shell.gogo.IScopeRegistry;
import org.ow2.chameleon.shell.gogo.handler.completor.ScopeCompletor;

/**
 * Created by IntelliJ IDEA.
 * User: sauthieg
 * Date: 6 janv. 2010
 * Time: 16:44:07
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ConsoleStartupComponent {

    @Requires
    private CommandProcessor processor;

    @Requires
    private Completor completor;

    @Requires
    private IScopeRegistry scopeRegistry;

    private JLineConsole console;

    @Validate
    public void startup() throws Exception {

        // Start the console
        console = new JLineConsole(processor,
                                   completor,
                                   System.in,
                                   System.out,
                                   System.err);

        new Thread(console, "Chameleon Console Thread").start();

        // Store some global properties
        console.getSession().put("application.name", "chameleon");

        StringBuilder scopeValue = new StringBuilder();
        if (scopeRegistry != null) {
            Set<String> scopes = scopeRegistry.getScopes();
            if (scopes != null) {
                for (String scope : scopes) {
                    scopeValue.append(scope);
                    scopeValue.append(":");
                }
            }
        }
        scopeValue.append("*");
        console.getSession().put("SCOPE", scopeValue.toString());

        // TODO, handle property substitution using Beans
        OperatingSystem os = new OperatingSystem();
        os.setName(System.getProperty("os.name"));
        os.setArch(System.getProperty("os.arch"));
        os.setVersion(System.getProperty("os.version"));
        console.getSession().put("os", os);
    }

    @Invalidate
    public void shutdown() {
        console.close();
    }
}
