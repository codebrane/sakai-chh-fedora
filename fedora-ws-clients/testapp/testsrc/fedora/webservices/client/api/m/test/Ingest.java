/* CVS Header
   $Id$
   $Log$
*/

package fedora.webservices.client.api.m.test;

import fedora.webservices.client.api.RepositoryTest;
import fedora.webservices.client.api.m.FedoraAPIMServiceStub;
import fedora.fedoraSystemDef.foxml.*;
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
    ingest.setFormat(DIGITAL_OBJECT_FORMAT_FOXML);
    ingest.setLogMessage(TEST_DIGITAL_OBJECT_LOG_MESSAGE);

    DigitalObjectDocument objectDoc = DigitalObjectDocument.Factory.newInstance();
    DigitalObjectDocument.DigitalObject object = objectDoc.addNewDigitalObject();

    object.setPID(TEST_DIGITAL_OBJECT_PID);

    // /////////////////////////////////////////////////////////////////////////////////////////////////
    // Fedora object properties
    ObjectPropertiesType objectProperties = object.addNewObjectProperties();

    // <foxml:property NAME="http://www.w3.org/1999/02/22-rdf-syntax-ns#type" VALUE="FedoraObject"/>
    PropertyType typeProperty = objectProperties.addNewProperty();
    typeProperty.setNAME(PropertyType.NAME.HTTP_WWW_W_3_ORG_1999_02_22_RDF_SYNTAX_NS_TYPE);
    typeProperty.setVALUE("FedoraObject");

    // <foxml:property NAME="info:fedora/fedora-system:def/model#state" VALUE="A"/>
    PropertyType stateProperty = objectProperties.addNewProperty();
    stateProperty.setNAME(PropertyType.NAME.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_STATE);
    stateProperty.setVALUE(StateType.A.toString());

    // <foxml:property NAME="info:fedora/fedora-system:def/model#label" VALUE="FOXML Reference Example"/>
    PropertyType labelProperty = objectProperties.addNewProperty();
    labelProperty.setNAME(PropertyType.NAME.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_LABEL);
    labelProperty.setVALUE("FOXML Reference Example");

    // <foxml:property NAME="info:fedora/fedora-system:def/model#contentModel" VALUE="TEST_IMAGE"/>
    PropertyType contentModelProperty = objectProperties.addNewProperty();
    contentModelProperty.setNAME(PropertyType.NAME.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_CONTENT_MODEL);
    contentModelProperty.setVALUE("TEST_IMAGE");

    // <foxml:extproperty NAME="http://www.openarchives.org/OAI/1.1/oai-identifier.xsd" VALUE="oai:cornell.edu:demo:999"/>
    // For use with <foxml:datastreamVersion ID="DC.0" MIMETYPE="text/xml" LABEL="Default Dublin Core Record">
    // <foxml:xmlContent>/<oai_dc:dc>
    ExtpropertyType extProperty = objectProperties.addNewExtproperty();
    extProperty.setNAME("http://www.openarchives.org/OAI/1.1/oai-identifier.xsd");
    extProperty.setVALUE("oai:cornell.edu:demo:999");
    // /////////////////////////////////////////////////////////////////////////////////////////////////

    // /////////////////////////////////////////////////////////////////////////////////////////////////
    // Dublin Core record for the digital object
    DatastreamType datastream = object.addNewDatastream();
    datastream.setID("DC");
    datastream.setSTATE(StateType.A);
    datastream.setCONTROLGROUP(DatastreamType.CONTROLGROUP.X);

    DatastreamVersionType datastreamVersion = datastream.addNewDatastreamVersion();
    datastreamVersion.setID("DC.0");
    datastreamVersion.setMIMETYPE("text/xml");
    datastreamVersion.setLABEL("Default Dublin Core Record");

    XmlContentType xmlContent = datastreamVersion.addNewXmlContent();
    
    //xmlContent.getDomNode().appendChild()
    
    // /////////////////////////////////////////////////////////////////////////////////////////////////

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
