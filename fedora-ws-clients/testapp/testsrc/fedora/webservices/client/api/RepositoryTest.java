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
  /** The format and version of ingest messages */
  protected static final String DIGITAL_OBJECT_FORMAT_FOXML = "foxml1.0";
  
  /** Definitions for keys in repository.properties */
  protected static final String PROPS_KEY_TRUSTSTORE_LOCATION = "truststore.location";
  protected static final String PROPS_KEY_TRUSTSTORE_PASSWORD = "truststore.password";
  protected static final String PROPS_KEY_API_A_ENDPOINT = "fedora.api.a.ws.endpoint";
  protected static final String PROPS_KEY_API_M_ENDPOINT = "fedora.api.m.ws.endpoint";
  protected static final String PROPS_KEY_FEDORA_ADMIN_USERNAME = "fedora.admin.username";
  protected static final String PROPS_KEY_FEDORA_ADMIN_PASSWORD = "fedora.admin.password";
  protected static final String PROPS_KEY_FEDORA_VERSION = "fedora.version";
  
  protected static final String PROPS_KEY_TEST_INGEST_FILE = "test.file";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_PID = "test.file.pid";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_MIME_TYPE = "test.file.mime.type";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_LABEL = "test.file.label";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_CONTENT_MODEL = "test.file.content.model";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_DATASTREAM_ID = "test.file.datastream.id";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_DATASTREAM_VERSION_ID = "test.file.datastream.version.id";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_TITLE = "test.file.title";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_CREATOR = "test.file.creator";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_SUBJECT = "test.file.subject";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_DESCRIPTION = "test.file.description";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_PUBLISHER = "test.file.publisher";
  protected static final String PROPS_KEY_TEST_INGEST_FILE_IDENTIFIER = "test.file.identifier";

  protected static final String PROPS_KEY_TEST_RELSEXT_MIME_TYPE = "relsext.mime.type";
  protected static final String PROPS_KEY_TEST_RELSEXT_VERSION_ID = "relsext.version.id";
  protected static final String PROPS_KEY_TEST_RELSEXT_LABEL = "relsext.label";
  protected static final String PROPS_KEY_TEST_RELSEXT_OWNER = "relsext.owner";

  protected static final String PROPS_KEY_DUMP_INGEST_XML = "ingest.dump";
  protected static final String PROPS_KEY_DUMP_INGEST_XML_FILE = "ingest.dump.file";
  protected static final String PROPS_KEY_INGEST_LOG_MESSAGE = "ingest.log.message";
  protected static final String PROPS_KEY_PURGE_LOG_MESSAGE = "purge.log.message";

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
