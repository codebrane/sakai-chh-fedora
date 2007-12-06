/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral;

import java.util.PropertyResourceBundle;
import java.io.InputStream;

public interface DigitalRepository {
  /** Defines the name of the repository for displaying in Sakai */
  public static final String CONFIG_KEY_DISPLAY_NAME = "display.name";
  /** Defines the Fedora API-A access web service endpoint */
  public static final String CONFIG_KEY_API_A_ENDPOINT = "api-a.endpoint";
  /** Defines the Fedora API-M management web service endpoint */
  public static final String CONFIG_KEY_API_M_ENDPOINT = "api-m.endpoint";
  /** Defines the Fedora API-M management web service endpoint */
  public static final String CONFIG_KEY_DISSEMINATION_ENDPOINT = "dissemination.endpoint";
  /** Defines the URL for uploading content to modify datastreams */
  public static final String CONFIG_KEY_UPLOAD_URL = "upload.url";
  /** The username for the connection */
  public static final String CONFIG_KEY_CONNECTION_USERNAME = "connection.username";
  /** The password for the connection */
  public static final String CONFIG_KEY_CONNECTION_PASSWORD = "connection.password";

  public static final boolean INCLUDE_RESOURCES_IN_COLLECTIONS = true;
  public static final boolean DO_NOT_INCLUDE_RESOURCES_IN_COLLECTIONS = false;

  public PropertyResourceBundle getRepoConfig();
  public void init(PropertyResourceBundle config);

  public boolean createObject(DigitalItemInfo item);
  public boolean modifyObject(DigitalItemInfo item, String dsID, byte[] dsContent, boolean inline);
  public boolean deleteObject(String pid);
  public boolean commitObject(DigitalItemInfo item);

//  public void search();
//  public DigitalItemInfo queryFedora(String pid);
//  public DigitalItemInfo[] queryFedora(String pid, boolean collectionsOnly, String collectionName);

  public InputStream getContentAsStream(String endpoint);
  public DigitalItemInfo generateItem();

  public DigitalItemInfo getResource(String pid);
  public DigitalItemInfo[] getResources(boolean includeResourcesInCollections);
  public DigitalItemInfo[] getCollections(String exludeThisCollection);
  public DigitalItemInfo[] getMembersInCollection(String collectionPid);
}
