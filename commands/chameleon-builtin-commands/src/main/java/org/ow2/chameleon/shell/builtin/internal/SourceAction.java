package org.ow2.chameleon.shell.builtin.internal;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.service.command.CommandSession;

/**
 * Created by IntelliJ IDEA.
 * User: sauthieg
 * Date: 7 janv. 2010
 * Time: 11:30:28
 * To change this template use File | Settings | File Templates.
 */
@Component
@Command(name = "source",
         scope = "builtin",
         description = "Execute a file containing a script.")  
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.chameleon.shell.gogo'/>")
public class SourceAction implements Action {

    @Argument(name = "source-file",
              required = true,
              description = "The source file to be executed.")
    private File file;

    public Object execute(final CommandSession session) throws Exception {

        // This action could be smarter.
        // For example, if a line ends with a '\'
        // the current algorithm will probably treat the line as 2 line ...

        file = file.getAbsoluteFile();

        if (!file.isFile()) {
            throw new Exception("Not a file '" + file + "'");
        }

        BufferedReader reader = null;
        try {
            // Open the file
            FileInputStream stream = new FileInputStream(file);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(stream);
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            //Read File Line By Line
            while ((line = reader.readLine()) != null)   {
                line = line.trim();
                // Do not execute commented line
                if (!line.startsWith("#")) {
                    session.execute(line);
                    // TODO What to do with the resulting Object ?
                }
            }
        } catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}