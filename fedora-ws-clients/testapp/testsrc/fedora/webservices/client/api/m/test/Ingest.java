/* CVS Header
   $Id$
   $Log$
*/

package fedora.webservices.client.api.m.test;

import fedora.webservices.client.api.RepositoryTest;
import fedora.webservices.client.api.m.FedoraAPIMServiceStub;
import fedora.fedoraSystemDef.foxml.DigitalObjectDocument;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.apache.axis2.transport.http.HTTPConstants;
import info.fedora.definitions.x1.x0.types.IngestDocument;
import info.fedora.definitions.x1.x0.types.IngestResponseDocument;

import java.rmi.RemoteException;

/**
 * Test for the API-M ingest web service method
 *
 * @see http://www.fedora.info/download/2.0/userdocs/digitalobjects/ingestExport.html
 * @author Alistair Young
 */
public class Ingest extends RepositoryTest {
  @Test
  public void ingest() {
    // Build a new request document
    IngestDocument doc = IngestDocument.Factory.newInstance();

    IngestDocument.Ingest ingest = doc.addNewIngest();
    ingest.setFormat("");
    ingest.setLogMessage("");

    DigitalObjectDocument objectDoc = DigitalObjectDocument.Factory.newInstance();
    DigitalObjectDocument.DigitalObject object = objectDoc.addNewDigitalObject();

    object.setPID(TEST_DIGITAL_OBJECT_PID);

    ingest.setObjectXML(objectDoc.toString().getBytes());

    try {
      // Initiate the client connection to the API-A endpoint
      FedoraAPIMServiceStub stub = new FedoraAPIMServiceStub(repositoryProperties.getString(PROPS_KEY_API_M_ENDPOINT));

      // Add the auth creds to the client
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);

      // Call the web service
      IngestResponseDocument outDoc = stub.ingest(doc);
      assertNotNull(outDoc);
    }
    catch(RemoteException re) {
      fail(re.getMessage());
    }
  }
}
