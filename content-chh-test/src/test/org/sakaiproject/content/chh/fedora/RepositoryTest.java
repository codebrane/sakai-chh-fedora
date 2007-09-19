/* CVS Header
   $Id$
   $Log$
*/

package org.sakaiproject.content.chh.fedora;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.component.impl.SpringCompMgr;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.apache.catalina.loader.WebappClassLoader;

import static org.sakaiproject.content.chh.fedora.PropertyConstants.*;

import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

/**
 * Base class for Sakai/Fedora unit tests. This class initialises and loads the Sakai Component Manager
 * and takes care of other time consuming startup tasks.
 *
 * @author Alistair Young alistairskye@googlemail.com
 */
public abstract class RepositoryTest {
  protected static ResourceBundle repositoryProperties = null;
  protected static ComponentManager componentManager;
  protected static SiteService siteService = null;
  protected static UserDirectoryService userDirService = null;

  @BeforeClass
  public static void init() throws Exception {
    repositoryProperties = ResourceBundle.getBundle("org.sakaiproject.content.chh.fedora.repository");

    if(componentManager == null) {
      String tomcatHome = repositoryProperties.getString(PROPS_KEY_SAKAI_TOMCAT_HOME);
      String sakaiHome = tomcatHome + File.separatorChar + "sakai" + File.separatorChar;
      String componentsDir = tomcatHome + File.separatorChar + "components" + File.separatorChar;

      // Set the system properties and CLASSPATH needed by the sakai component manager
      System.setProperty("sakai.home", sakaiHome);
      System.setProperty(ComponentManager.SAKAI_COMPONENTS_ROOT_SYS_PROP, componentsDir);

      URL[] sakaiUrls = getJarUrls(new String[] {tomcatHome + File.separatorChar + "common/endorsed" + File.separatorChar,
                                                 tomcatHome + File.separatorChar + "common/lib" + File.separatorChar,
                                                 tomcatHome + File.separatorChar + "shared/lib" + File.separatorChar });
      URLClassLoader appClassLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
      Method addMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      addMethod.setAccessible(true);
      for(int i=0; i<sakaiUrls.length; i++) {
        addMethod.invoke(appClassLoader, sakaiUrls[i]);
      }

      // Get a tomcat classloader
      WebappClassLoader wcloader = new WebappClassLoader(Thread.currentThread().getContextClassLoader());
      wcloader.start();

      // Initialize spring component manager
      Class clazz = wcloader.loadClass(SpringCompMgr.class.getName());
      Constructor constructor = clazz.getConstructor(ComponentManager.class);
      componentManager = (ComponentManager)constructor.newInstance(new Object[] {null});
      Method initMethod = clazz.getMethod("init");
      initMethod.invoke(componentManager);

      siteService = (SiteService)getService(SiteService.class.getName());
      userDirService = (UserDirectoryService)getService(UserDirectoryService.class.getName());
    }
  }

  @AfterClass
  public static void destroy() {
    if(componentManager != null) {
      componentManager.close();
    }
  }

  protected static Object getService(String beanId) {
    return org.sakaiproject.component.cover.ComponentManager.get(beanId);
  }

  private static URL[] getJarUrls(String dirPath) throws Exception {
    File dir = new File(dirPath);
    File[] jars = dir.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        if(pathname.getName().startsWith("xml-apis")) {
          return false;
        }
        return true;
      }
    });
    URL[] urls = new URL[jars.length];
    for(int i = 0; i < jars.length; i++) {
      urls[i] = jars[i].toURL();
    }
    return urls;
  }

  private static URL[] getJarUrls(String[] dirPaths) throws Exception {
    List jarList = new ArrayList<List>();

    // Add all of the tomcat jars
    for(int i=0; i<dirPaths.length; i++) {
      jarList.addAll(Arrays.asList(getJarUrls(dirPaths[i])));
    }

    URL[] urlArray = new URL[jarList.size()];
    jarList.toArray(urlArray);
    return urlArray;
  }
}
