/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral;

import java.util.PropertyResourceBundle;
import java.io.InputStream;

public interface DigitalRepository {
  /** Defines the Fedora API-A access web service endpoint */
  public static final String CONFIG_KEY_API_A_ENDPOINT = "api-a.endpoint";
  /** Defines the Fedora API-M management web service endpoint */
  public static final String CONFIG_KEY_API_M_ENDPOINT = "api-m.endpoint";
  /** The username for the connection */
  public static final String CONFIG_KEY_CONNECTION_USERNAME = "connection.username";
  /** The password for the connection */
  public static final String CONFIG_KEY_CONNECTION_PASSWORD = "connection.password";

  public PropertyResourceBundle getRepoConfig();
  public void init(PropertyResourceBundle config);
  public boolean createObject(DigitalItemInfo item);
  public void modifyObject();
  public void deleteObject();
  public void search();
  public InputStream getContentAsStream(String endpoint);
  public DigitalItemInfo generateItem();
  public DigitalItemInfo[] list();
  public DigitalItemInfo list(String id);
}
