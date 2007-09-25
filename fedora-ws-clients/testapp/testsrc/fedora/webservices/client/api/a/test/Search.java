/* CVS Header
   $Id$
   $Log$
*/

package fedora.webservices.client.api.a.test;

import fedora.webservices.client.api.RepositoryTest;
import fedora.webservices.client.api.a.FedoraAPIAServiceStub;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import org.apache.axis2.transport.http.HTTPConstants;
import info.fedora.definitions.x1.x0.types.*;

import java.rmi.RemoteException;

public class Search extends RepositoryTest {
  @Test
  public void search() {
    // Build a new request document
    FindObjectsDocument doc = FindObjectsDocument.Factory.newInstance();

    FindObjectsDocument.FindObjects params = doc.addNewFindObjects();
    
    FieldSearchQuery query = FieldSearchQuery.Factory.newInstance();
    FieldSearchQuery.Conditions conditions = query.addNewConditions();
    Condition condition = conditions.addNewCondition();
    condition.setProperty("pid");
    condition.setValue("true");

    // http://www.fedora.info/definitions/1/0/types/#complexType_ObjectFields_Link03247988
    ArrayOfString resultFields = ArrayOfString.Factory.newInstance();
    resultFields.addItem("pid");
    resultFields.addItem("label");
    resultFields.addItem("ownerId");
    resultFields.addItem("title");
    resultFields.addItem("creator");
    resultFields.addItem("description");
    resultFields.addItem("type");
    
    params.setMaxResults(null); // no limit on results
    params.setQuery(query);
    params.setResultFields(resultFields);

    try {
      // Initiate the client connection to the API-A endpoint
      FedoraAPIAServiceStub stub = new FedoraAPIAServiceStub(repositoryProperties.getString(PROPS_KEY_API_A_ENDPOINT));

      // Add the auth creds to the client
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);

      // Call the web service
      FindObjectsResponseDocument outDoc = stub.findObjects(doc);
      assertNotNull(outDoc);
    }
    catch(RemoteException re) {
      fail(re.getMessage());
    }
  }
}
