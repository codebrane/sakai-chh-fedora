/* CVS Header
   $Id$
   $Log$
*/

package fedora.webservices.client.api.m.test;

import fedora.webservices.client.api.RepositoryTest;
import fedora.webservices.client.api.m.FedoraAPIMServiceStub;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.apache.axis2.transport.http.HTTPConstants;
import info.fedora.definitions.x1.x0.types.PurgeObjectDocument;
import info.fedora.definitions.x1.x0.types.PurgeObjectResponseDocument;

import java.rmi.RemoteException;

/**
 * Test for the API-M purgeObject web service method
 *
 * <a href="http://www.fedora.info/wiki/index.php/PurgeObject">purgeObject documentation</a>
 * @author Alistair Young
 */
public class Purge extends RepositoryTest {
  @Test
  public void purgeObject() {
    PurgeObjectDocument doc = PurgeObjectDocument.Factory.newInstance();
    PurgeObjectDocument.PurgeObject purge = doc.addNewPurgeObject();

    purge.setPid(repositoryProperties.getString(PROPS_KEY_TEST_INGEST_FILE_PID));
    purge.setLogMessage(repositoryProperties.getString(PROPS_KEY_PURGE_LOG_MESSAGE));
    // java.lang.AssertionError: fedora.server.errors.GeneralException: Forced object removal is not yet supported.
    purge.setForce(false);

    try {
      // Initiate the client connection to the API-A endpoint
      FedoraAPIMServiceStub stub = new FedoraAPIMServiceStub(repositoryProperties.getString(PROPS_KEY_API_M_ENDPOINT));

      // Add the auth creds to the client
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);
      // Register our custom SSL handler for this connection
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, authhttps);

      // Call the web service
      PurgeObjectResponseDocument outDoc = stub.purgeObject(doc);
      assertNotNull(outDoc);
      assertNotNull(outDoc.getPurgeObjectResponse());
      // 2007-10-15T08:59:46.827Z
      assertNotNull(outDoc.getPurgeObjectResponse().getPurgedDate());
    }
    catch(RemoteException re) {
      fail(re.getMessage());
    }
  }
}
