/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral;

import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingHandlerResolver;
import org.sakaiproject.content.api.ContentHostingHandler;

import java.util.PropertyResourceBundle;

public interface DigitalRepository {
  /** Defines the Fedora API-A access web service endpoint */
  public static final String CONFIG_KEY_API_A_ENDPOINT = "api-a.endpoint";
  /** Defines the Fedora API-M management web service endpoint */
  public static final String CONFIG_KEY_API_M_ENDPOINT = "api-m.endpoint";
  /** The username for the connection */
  public static final String CONFIG_KEY_CONNECTION_USERNAME = "connection.username";
  /** The password for the connection */
  public static final String CONFIG_KEY_CONNECTION_PASSWORD = "connection.password";

  public void init(PropertyResourceBundle config, ContentHostingHandlerResolver contentHostingHandlerResolver, ContentHostingHandler chh);
  public void createObject();
  public void modifyObject();
  public void deleteObject();
  public void search();
  public ContentEntity list();
}
