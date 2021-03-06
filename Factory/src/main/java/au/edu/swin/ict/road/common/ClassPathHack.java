package au.edu.swin.ict.road.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * TODO
 */
public class ClassPathHack {
    private final Class[] parameters = new Class[]{URL.class};

    public void addFile(String s) throws IOException {
        File f = new File(s);
        addFile(f);
    }

    public void addFile(File f) throws IOException {
        addURL(f.toURI().toURL());
    }

    public void addURL(URL u) throws IOException {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{u});
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }

    }
}
