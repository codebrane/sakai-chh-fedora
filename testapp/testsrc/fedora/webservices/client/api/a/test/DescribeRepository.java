/* CVS Header
   $Id$
   $Log$
*/

package fedora.webservices.client.api.a.test;

import org.junit.Test;
import static org.junit.Assert.*;

import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.transport.http.HTTPConstants;
import info.fedora.definitions.x1.x0.types.DescribeRepositoryDocument;
import info.fedora.definitions.x1.x0.types.DescribeRepositoryResponseDocument;
import info.fedora.definitions.x1.x0.types.RepositoryInfo;
import fedora.webservices.client.api.a.FedoraAPIAServiceStub;
import fedora.webservices.client.api.RepositoryTest;

import java.rmi.RemoteException;

/**
 * Test for the API-A getRepositoryInfo web service method
 *
 * @author Alistair Young
 */
public class DescribeRepository extends RepositoryTest {
  @Test
  public void describeRepositoryTest() {
    // Build a new request document
    DescribeRepositoryDocument doc = DescribeRepositoryDocument.Factory.newInstance();
    doc.addNewDescribeRepository();

    try {
      // Initiate the client connection to the API-A endpoint
      FedoraAPIAServiceStub stub = new FedoraAPIAServiceStub(repositoryProperties.getString(PROPS_KEY_API_A_ENDPOINT));

      // Add the auth creds to the client
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);

      // Call the web service
      DescribeRepositoryResponseDocument outDoc = stub.describeRepository(doc);
      assertNotNull(outDoc);

      // Parse the response
      DescribeRepositoryResponseDocument.DescribeRepositoryResponse out = outDoc.getDescribeRepositoryResponse();
      assertNotNull(out);

      // Extract the returned info
      RepositoryInfo info = out.getRepositoryInfo();
      assertNotNull(info);

      // Make sure it worked
      assertEquals(info.getRepositoryVersion(), repositoryProperties.getString(PROPS_KEY_FEDORA_VERSION));
    }
    catch(RemoteException re) {
      fail(re.getMessage());
    }
  }
}
