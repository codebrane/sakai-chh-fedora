/* SVN Header
   $Id: RepositoryTest.java 86 2007-09-09 22:44:55Z sm00sm $
   $URL: https://source.uhi.ac.uk/svn/ctrep/trunk/testapp/testsrc/fedora/webservices/client/api/RepositoryTest.java $
*/

package fedora.webservices.client.api;

import org.junit.BeforeClass;
import org.apache.axis2.transport.http.HttpTransportProperties;

import java.util.ResourceBundle;

/**
 * This is the base class that all Fedora web services test classes must extend. It provides
 * common functionality
 */
public abstract class RepositoryTest {
  protected static final String PROPS_KEY_TRUSTSTORE_LOCATION = "truststore.location";
  protected static final String PROPS_KEY_TRUSTSTORE_PASSWORD = "truststore.password";
  protected static final String PROPS_KEY_API_A_ENDPOINT = "fedora.api.a.ws.endpoint";
  protected static final String PROPS_KEY_API_M_ENDPOINT = "fedora.api.m.ws.endpoint";
  protected static final String PROPS_KEY_FEDORA_ADMIN_USERNAME = "fedora.admin.username";
  protected static final String PROPS_KEY_FEDORA_ADMIN_PASSWORD = "fedora.admin.password";
  protected static final String PROPS_KEY_FEDORA_VERSION = "fedora.version";
  
  protected static ResourceBundle repositoryProperties = null;
  protected static HttpTransportProperties.Authenticator authenticator = null;

  @BeforeClass
  public static void init() {
    repositoryProperties = ResourceBundle.getBundle("fedora.webservices.client.api.repository");

    // Set up the truststore. We need this to verify the Fedora server's certificate as we are connecting via HTTPS
    System.setProperty("javax.net.ssl.trustStore", repositoryProperties.getString(PROPS_KEY_TRUSTSTORE_LOCATION));
    System.setProperty("javax.net.ssl.trustStorePassword", repositoryProperties.getString(PROPS_KEY_TRUSTSTORE_PASSWORD));

    // Prepare the authentication details for a web service request, i.e. fedora admin username and password
    authenticator = new HttpTransportProperties.Authenticator();
    authenticator.setUsername(repositoryProperties.getString(PROPS_KEY_FEDORA_ADMIN_USERNAME));
    authenticator.setPassword(repositoryProperties.getString(PROPS_KEY_FEDORA_ADMIN_PASSWORD));

    // This is essential as otherwise the auth creds are not sent with the initial request
    authenticator.setPreemptiveAuthentication(true);
  }
}
