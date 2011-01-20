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

package org.ow2.chameleon.shell.gogo.handler.completor;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import jline.Completor;
import org.apache.felix.service.command.CommandSession;

/**
 * This completor is used to prefix the command name with the available scope.
 * This is useful when the user do not type the command scope.
 */
public class ScopeCompletor implements Completor {

    /**
     * Delegating Completor.
     */
    private Completor delegate;

    /**
     * The session containing the 'SCOPE' variable.
     */
    private CommandSession session;

    public ScopeCompletor(Completor delegate, CommandSession session) {
        this.delegate = delegate;
        this.session = session;
    }

    /**
     * Populates <i>candidates</i> with a list of possible
     * completions for the <i>buffer</i>. The <i>candidates</i>
     * list will not be sorted before being displayed to the
     * user: thus, the complete method should sort the
     * {@link java.util.List} before returning.
     *
     * @param buffer     the buffer
     * @param candidates the {@link java.util.List} of candidates to populate
     * @return the index of the <i>buffer</i> for which
     *         the completion will be relative
     */
    public int complete(String buffer, int cursor, List candidates) {

        // Use a SortedMap keyed by the return result of the delegate Completor so that
        // all values are grouped and maximum value can be easily retrieved
        SortedMap<Integer, List<List>> completions = new TreeMap<Integer, List<List>>();

        String scopeValue = (String) session.get("SCOPE");
        if (scopeValue != null) {

            // Split the SCOPE variable (delimited with ':')
            String[] scopes = scopeValue.split(":");

            // Run completer for each scope, saving its completion results
            for (String scope : scopes) {

                List subCandidates = new ArrayList(candidates);
                int value = delegate.complete((scope + ":" + buffer),
                                              (scope + ":").length() + cursor,
                                              subCandidates);

                List<List> completionResult = completions.get(value);
                if (completionResult == null) {
                    // Init the list if required
                    completionResult = new ArrayList<List>();
                    completions.put(value, completionResult);
                }
                // Store the sub-candidates list
                completionResult.add(subCandidates);

            }

            // When all scopes have been used, select the List of candidates
            // that has returned the max value (possibly multiple List)
            int max = completions.lastKey();
            List<List> listOfCandidates = completions.get(max);
            for (List selectedCandidates : listOfCandidates) {
                // Append theses results to the selection list
                candidates.addAll(selectedCandidates);
            }

            return max;
        }

        return -1;

    }
}
