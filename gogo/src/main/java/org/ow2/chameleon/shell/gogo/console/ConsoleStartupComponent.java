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

import jline.console.completer.Completer;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Modified;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.service.command.CommandProcessor;
import org.ow2.chameleon.shell.gogo.IScopeRegistry;

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

    @Requires(filter = "(type=commands)")
    private Completer completer;

    private JLineConsole console;

    private String scopes;

    @Bind
    public void bindScopeRegistry(IScopeRegistry scopeRegistry) {
        scopes = scopeRegistry.getScopes();
        if (console != null) {
            console.getSession().put("SCOPE", scopes);
        }
    }

    @Modified
    public void modifiedScopeRegistry(IScopeRegistry scopeRegistry) {
        scopes = scopeRegistry.getScopes();
        if (console != null) {
            console.getSession().put("SCOPE", scopes);
        }
    }

    @Unbind
    public void unbindScopeRegistry(IScopeRegistry scopeRegistry) {
        if (console != null) {
            console.getSession().put("SCOPE", null);
        }
    }

    @Validate
    public void startup() throws Exception {

        // Start the console
        console = new JLineConsole(processor,
                                   completer,
                                   System.in,
                                   System.out,
                                   System.err);

        new Thread(console, "Chameleon Console Thread").start();

        // Store some global properties
        console.getSession().put("application.name", "chameleon");
        console.getSession().put("SCOPE", scopes);

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
