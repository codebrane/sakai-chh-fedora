/* CVS Header
   $
   $
*/

package fedora.webservices.client.api.m.test;

import fedora.fedoraSystemDef.foxml.XmlContentType;
import fedora.webservices.client.api.RepositoryTest;
import fedora.webservices.client.api.a.FedoraAPIAServiceStub;
import fedora.webservices.client.api.m.FedoraAPIMServiceStub;
import info.fedora.definitions.x1.x0.types.*;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.xmlbeans.XmlCursor;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;

public class ResourceOrCollection extends RepositoryTest {
  @Test
  public void resourceOrCollection() {
    // Build a new request document
    FindObjectsDocument doc = FindObjectsDocument.Factory.newInstance();

    FindObjectsDocument.FindObjects params = doc.addNewFindObjects();

    FieldSearchQuery query = params.addNewQuery();
    FieldSearchQuery.Conditions conditions = query.addNewConditions();
    Condition condition = conditions.addNewCondition();
    condition.setProperty("pid");
    condition.setOperator(ComparisonOperator.EQ);
//    condition.setValue("*");
    condition.setValue("demo:78f76bac116a00736c5-7ff0");

    // http://www.fedora.info/definitions/1/0/types/#complexType_ObjectFields_Link03247988
    ArrayOfString resultFields = params.addNewResultFields();
    resultFields.addItem("pid");
    resultFields.addItem("label");
    resultFields.addItem("ownerId");
    resultFields.addItem("title");
    resultFields.addItem("creator");
    resultFields.addItem("description");
    resultFields.addItem("type");
    resultFields.addItem("mDate");
    resultFields.addItem("publisher");
    resultFields.addItem("subject");

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
      // Register our custom SSL handler for this connection
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, authhttps);

      // Call the web service
      FindObjectsResponseDocument outDoc = stub.findObjects(doc);

      ObjectFields[] fields = outDoc.getFindObjectsResponse().getResult().getResultList().getObjectFieldsArray();

      ListDatastreamsDocument dsDoc = ListDatastreamsDocument.Factory.newInstance();
      ListDatastreamsDocument.ListDatastreams ds = dsDoc.addNewListDatastreams();
      ds.setPid(fields[0].getPid());
      ListDatastreamsResponseDocument dsOutDoc = stub.listDatastreams(dsDoc);
      DatastreamDef[] defs = dsOutDoc.getListDatastreamsResponse().getDatastreamDefArray();

      String collectionName = "Resource is not in a collection";

      for (DatastreamDef def : defs) {
        // RELS-EXT core datastream
        if (def.getID().equals("RELS-EXT")) {
          FedoraAPIMServiceStub mStub = new FedoraAPIMServiceStub(repositoryProperties.getString(PROPS_KEY_API_M_ENDPOINT));
          mStub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);
          mStub._getServiceClient().getOptions().setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, authhttps);
          GetDatastreamDocument dsInDoc = GetDatastreamDocument.Factory.newInstance();
          GetDatastreamDocument.GetDatastream dsIn = dsInDoc.addNewGetDatastream();
          dsIn.setPid(fields[0].getPid());
          dsIn.setDsID(def.getID());
          GetDatastreamResponseDocument dstreamOutDoc = mStub.getDatastream(dsInDoc);
          Datastream dstream = dstreamOutDoc.getGetDatastreamResponse().getDatastream();

          FedoraAPIAServiceStub aStub = new FedoraAPIAServiceStub(repositoryProperties.getString(PROPS_KEY_API_A_ENDPOINT));
          aStub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);
          aStub._getServiceClient().getOptions().setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, authhttps);
          GetDatastreamDisseminationDocument dsdDoc = GetDatastreamDisseminationDocument.Factory.newInstance();
          GetDatastreamDisseminationDocument.GetDatastreamDissemination dsd = dsdDoc.addNewGetDatastreamDissemination();
          dsd.setDsID(dstream.getID());
          dsd.setPid(fields[0].getPid());
          GetDatastreamDisseminationResponseDocument dsdOutDoc = aStub.getDatastreamDissemination(dsdDoc);
          MIMETypedStream dsStream = dsdOutDoc.getGetDatastreamDisseminationResponse().getDissemination();
          dsStream.getStream();
          XmlContentType xml = XmlContentType.Factory.parse(new ByteArrayInputStream(dsStream.getStream()));

          /*
          <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
            <rdf:Description rdf:about="info:fedora/demo:78f76bac116a00736c5-7ff0">
              <fed:isMemberOf rdf:resource="info:fedora/demo:testcollection" xmlns:fed="fedora"/>
              <myns:owner xmlns:myns="http://www.nsdl.org/ontologies/relationships#">admin</myns:owner>
            </rdf:Description>
          </rdf:RDF>
           */
          XmlCursor cursor = xml.newCursor();
          cursor.toFirstChild();
          String namespaceDecl = "declare namespace rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'; declare namespace fed='fedora'; ";
          cursor.selectPath(namespaceDecl + "$this//rdf:Description/fed:isMemberOf/@rdf:resource");
          while (cursor.toNextSelection()) {
            collectionName = "Resource is in collection " + cursor.getTextValue();
          }
          cursor.dispose();
        }
      }

      System.out.println(collectionName);
    }
    catch(Exception e) {
      fail(e.getMessage());
    }
  }
}
