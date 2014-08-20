
package haschlabs.pluginapplication;

import java.io.File;
import java.io.IOException;
import java.util.List;
import haschlabs.pluginapplication.api.plugin.SpecificPlugin;

/**
 *
 * @author Hauke Schulz <hauke27@googlemail.com>
 */
public class PluginApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        List<SpecificPlugin> plugins = null;

        File pluginFolder = new File("./plugins");

        if (!pluginFolder.exists()) {
            try {
                pluginFolder.mkdir();
            } catch (SecurityException e) {
                System.err.println(e.getMessage());
                return;
            }
        }

        ImplPluginLoader loader = new ImplPluginLoader(pluginFolder);

        try {
            plugins = loader.loadPlugins();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        ImplPluginManager manager = new ImplPluginManager();

        for (SpecificPlugin p : plugins) {
            p.setPluginManager(manager);
            p.start();

            System.out.println("--- Plugin ---");
            System.out.println("Name: " + p.getName());
            System.out.println("Description: " + p.getDescription());
            System.out.println("Version: " + p.getVersion());
            System.out.println("App-Version: " + p.getApplicationVersion());

            for (String author : p.getAuthors()) {
                System.out.println("Author: " + author);
            }

            System.out.println("Specific Method: " + p.specificMethod());
            System.out.println();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace(System.out);
        }

        for (SpecificPlugin p : plugins) {
            p.stop();
        }
    }
}
