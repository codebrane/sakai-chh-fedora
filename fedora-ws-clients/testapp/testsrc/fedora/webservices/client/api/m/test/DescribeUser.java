/* SVN Header
   $Id: DescribeUser.java 86 2007-09-09 22:44:55Z sm00sm $
   $URL: https://source.uhi.ac.uk/svn/ctrep/trunk/testapp/testsrc/fedora/webservices/client/api/m/test/DescribeUser.java $
*/

package fedora.webservices.client.api.m.test;

import fedora.webservices.client.api.RepositoryTest;
import fedora.webservices.client.api.m.FedoraAPIMServiceStub;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.axis2.transport.http.HTTPConstants;
import info.fedora.definitions.x1.x0.types.DescribeUserDocument;
import info.fedora.definitions.x1.x0.types.DescribeUserResponseDocument;
import info.fedora.definitions.x1.x0.types.UserInfo;

import java.rmi.RemoteException;

/**
 * Test for the API-M describeUser web service method
 *
 * @author Alistair Young
 */
public class DescribeUser extends RepositoryTest {
  @Test
  public void describeUserTest() {
    DescribeUserDocument doc = DescribeUserDocument.Factory.newInstance();
    doc.addNewDescribeUser().setId(repositoryProperties.getString(PROPS_KEY_FEDORA_ADMIN_USERNAME));

    try {
      // Initiate the client connection to the API-A endpoint
      FedoraAPIMServiceStub stub = new FedoraAPIMServiceStub(repositoryProperties.getString(PROPS_KEY_API_M_ENDPOINT));

      // Add the auth creds to the client
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);

      // Call the web service
      DescribeUserResponseDocument outDoc = stub.describeUser(doc);
      assertNotNull(outDoc);

      DescribeUserResponseDocument.DescribeUserResponse out = outDoc.getDescribeUserResponse();
      assertNotNull(out);

      UserInfo userInfo = out.getUserInfo();
      assertNotNull(userInfo);

      assertEquals(userInfo.getId(), repositoryProperties.getString(PROPS_KEY_FEDORA_ADMIN_USERNAME));
      assertTrue(userInfo.getAdministrator());
    }
    catch(RemoteException re) {
      fail(re.getMessage());
    }
  }
}
