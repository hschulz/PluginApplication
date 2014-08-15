
package haschlabs.pluginapplication;

import haschlabs.pluginmanager.PluginLoader;
import haschlabs.pluginapplication.api.plugin.SpecificPlugin;
import haschlabs.pluginapplication.ImplPluginManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 *
 * @author Hauke Schulz <hauke27@googlemail.com>
 */
public class ImplPluginLoader extends PluginLoader {

    private List<Class<? extends SpecificPlugin>> plugClasses = null;

    private List<SpecificPlugin> plugs = null;

    private ImplPluginManager manager = null;

    public ImplPluginLoader(File pluginFolder) {
        super(pluginFolder);

        this.plugClasses = new ArrayList<>();
        this.plugs = new ArrayList<>();
    }

    public ImplPluginLoader(File pluginFolder, ImplPluginManager manager) {
        this(pluginFolder);

        this.manager = manager;
    }

    @Override
    public List<SpecificPlugin> loadPlugins() throws IOException {

        this.fileArrayToURLArray();

        this.extractClassesFromJARs();

        this.createPluginObjects();

        return (List<SpecificPlugin>) this.plugs;
    }

    @Override
    protected void extractClassesFromJAR(File jar) throws IOException {

        JarInputStream jaris = new JarInputStream(new FileInputStream(jar));

        JarEntry ent = null;

        while ((ent = jaris.getNextJarEntry()) != null) {

            if (ent.getName().toLowerCase().endsWith(".class")) {
                try {
                    Class<?> cls = cl.loadClass(
                        ent.getName()
                            .substring(0, ent.getName().length() - 6)
                                .replace('/', '.')
                    );

                    if (this.isPluginClass(cls)) {
                        @SuppressWarnings("unchecked")
                        Class<? extends SpecificPlugin> spcls = (Class<? extends SpecificPlugin>) cls;
                        this.plugClasses.add(spcls);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Can't load Class " + ent.getName());
                    e.printStackTrace(System.err);
                }
            }
        }

        jaris.close();
    }

    @Override
    protected void createPluginObjects() {

        this.plugs = new ArrayList<>(this.plugClasses.size());

        for (Class<? extends SpecificPlugin> plug : this.plugClasses) {
            try {
                SpecificPlugin pluginInstance = plug.newInstance();

                if (this.manager != null) {
                    pluginInstance.setPluginManager(this.manager);
                }

                this.plugs.add(pluginInstance);
            } catch (InstantiationException e) {
                System.err.println("Can't instantiate plugin: " + plug.getName());
            } catch (IllegalAccessException e) {
                System.err.println("IllegalAccess for plugin: " + plug.getName());
            }
        }
    }
}
