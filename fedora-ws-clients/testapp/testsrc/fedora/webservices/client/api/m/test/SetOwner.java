/* CVS Header
   $Id$
   $Log$
*/

package fedora.webservices.client.api.m.test;

import fedora.webservices.client.api.RepositoryTest;
import fedora.webservices.client.api.m.FedoraAPIMServiceStub;
import info.fedora.definitions.x1.x0.types.ModifyObjectDocument;
import info.fedora.definitions.x1.x0.types.ModifyObjectResponseDocument;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.apache.axis2.transport.http.HTTPConstants;

import java.rmi.RemoteException;

/**
 * Test for the API-M modifyObject web service method
 *
 * <a href="http://www.fedora.info/wiki/index.php/ModifyObject">modifyObject documentation</a>
 * @author Alistair Young
 */
public class SetOwner extends RepositoryTest {
  private boolean standalone = true;

  /**
   * Other unit tests can call this method to bypass setup and cleanup of a new object
   */
  public void modifyObjectFromTest() {
    standalone = false;
    modifyObject();
  }
  
  @Test
  public void modifyObject() {
    if (standalone) {
      Ingest ingestTest = new Ingest();
      ingestTest.ingest();
    }

    ModifyObjectDocument doc = ModifyObjectDocument.Factory.newInstance();
    ModifyObjectDocument.ModifyObject modifyObject = doc.addNewModifyObject();

    modifyObject.setPid(repositoryProperties.getString(PROPS_KEY_TEST_INGEST_FILE_PID));
    modifyObject.setOwnerId(repositoryProperties.getString(PROPS_KEY_TEST_RELSEXT_OWNER));
    modifyObject.setLogMessage("Updating owner");

    try {
      // Initiate the client connection to the API-A endpoint
      FedoraAPIMServiceStub stub = new FedoraAPIMServiceStub(repositoryProperties.getString(PROPS_KEY_API_M_ENDPOINT));

      // Add the auth creds to the client
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);

      // Call the web service
      ModifyObjectResponseDocument outDoc = stub.modifyObject(doc);
      assertNotNull(outDoc);
    }
    catch(RemoteException re) {
      fail(re.getMessage());
    }

    if (standalone) {
      Purge purgeTest = new Purge();
      purgeTest.purgeObject();
    }
  }
}
