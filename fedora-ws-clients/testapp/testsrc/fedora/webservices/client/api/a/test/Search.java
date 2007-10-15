/* CVS Header
   $Id$
   $Log$
*/

package fedora.webservices.client.api.a.test;

import fedora.webservices.client.api.RepositoryTest;
import fedora.webservices.client.api.m.test.Ingest;
import fedora.webservices.client.api.m.test.Purge;
import fedora.webservices.client.api.m.test.SetOwner;
import fedora.webservices.client.api.a.FedoraAPIAServiceStub;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.axis2.transport.http.HTTPConstants;
import info.fedora.definitions.x1.x0.types.*;

import java.rmi.RemoteException;
import java.math.BigInteger;

/**
 * Test for the API-A findObjects web service method
 *
 * <a href="http://www.fedora.info/wiki/index.php/FindObjects">findObjects documentation</a>
 * @author Alistair Young
 */
public class Search extends RepositoryTest {
  @Test
  public void search() {
    // Add the test digital object to the repository - we're going to search for it
    Ingest ingestTest = new Ingest();
    ingestTest.ingest();

    SetOwner setOwnerTest = new SetOwner();
    setOwnerTest.modifyObjectFromTest();

    // Build a new request document
    FindObjectsDocument doc = FindObjectsDocument.Factory.newInstance();

    FindObjectsDocument.FindObjects params = doc.addNewFindObjects();

    FieldSearchQuery query = params.addNewQuery();
    FieldSearchQuery.Conditions conditions = query.addNewConditions();
    Condition condition = conditions.addNewCondition();
    condition.setProperty("pid");
    condition.setOperator(ComparisonOperator.EQ);
    condition.setValue(repositoryProperties.getString(PROPS_KEY_TEST_INGEST_FILE_PID));

    // http://www.fedora.info/definitions/1/0/types/#complexType_ObjectFields_Link03247988
    ArrayOfString resultFields = params.addNewResultFields();
    resultFields.addItem("pid");
    resultFields.addItem("label");
    resultFields.addItem("ownerId");
    resultFields.addItem("title");
    resultFields.addItem("creator");
    resultFields.addItem("description");
    resultFields.addItem("type");
    
    /* XMLBeans sets null as:
     * <maxResults xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>
     * but Fedora doesn't use XMLBeans at the server side and you get a NPE.
     */
    //params.setMaxResults(null);
    params.setMaxResults(new BigInteger("999999999")); // null = no limit on results

    try {
      // Initiate the client connection to the API-A endpoint
      FedoraAPIAServiceStub stub = new FedoraAPIAServiceStub(repositoryProperties.getString(PROPS_KEY_API_A_ENDPOINT));

      // Add the auth creds to the client
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);

      // Call the web service
      FindObjectsResponseDocument outDoc = stub.findObjects(doc);
      assertNotNull(outDoc);
      assertNotNull(outDoc.getFindObjectsResponse());
      assertNotNull(outDoc.getFindObjectsResponse().getResult());
      assertNotNull(outDoc.getFindObjectsResponse().getResult().getResultList());
      assertNotNull(outDoc.getFindObjectsResponse().getResult().getResultList().getObjectFieldsArray());

      ObjectFields[] fields = outDoc.getFindObjectsResponse().getResult().getResultList().getObjectFieldsArray();
      assertEquals(fields[0].getOwnerId(), repositoryProperties.getString(PROPS_KEY_TEST_RELSEXT_OWNER));
    }
    catch(RemoteException re) {
      cleanup();
      fail(re.getMessage());
    }

    cleanup();
  }

  private void cleanup() {
    Purge purgeTest = new Purge();
    purgeTest.purgeObject();
  }
}
