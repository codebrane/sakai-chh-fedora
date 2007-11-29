/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl;

import fedora.fedoraSystemDef.foxml.*;
import fedora.webservices.client.api.a.FedoraAPIAServiceStub;
import fedora.webservices.client.api.m.FedoraAPIMServiceStub;
import info.fedora.definitions.x1.x0.types.*;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlCursor;
import org.guanxi.common.EntityConnection;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import uk.ac.uhi.ral.DigitalItemInfo;
import uk.ac.uhi.ral.DigitalRepository;
import uk.ac.uhi.ral.impl.util.Utils;

import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.PropertyResourceBundle;
import java.util.Vector;

public class FedoraDigitalRepositoryImpl implements DigitalRepository {
  /** The format and version of ingest messages */
  private static final String DIGITAL_OBJECT_FORMAT_FOXML = "foxml1.0";

  private static final Log log = LogFactory.getLog(FedoraDigitalRepositoryImpl.class);

  /** Connection information. This comes from the mountpoint XML file */
  private PropertyResourceBundle repoConfig = null;
  /** Global keystore settings. These come via the constructor */
  private String keystorePath = null;
  private String keystorePassword = null;
  /** Global truststore settings. These come via the constructor */
  private String truststorePath = null;
  private String truststorePassword = null;
  /** The Axis2 authentication mechanism for communicating with the remote Fedora */
  private HttpTransportProperties.Authenticator authenticator = null;
  /** Our custom protocol handler which supports SSL probing */
  private Protocol customProtocolHandler = null;
  private boolean debug = false;
  private String debugOutputPath = null;

  public FedoraDigitalRepositoryImpl(String keystorePath, String keystorePassword,
                                     String truststorePath, String truststorePassword,
                                     boolean debug, String debugOutputPath) {
    this.keystorePath = keystorePath;
    this.keystorePassword = keystorePassword;
    this.truststorePath = truststorePath;
    this.truststorePassword = truststorePassword;
    this.debug = debug;
    this.debugOutputPath = debugOutputPath;
  }

  public PropertyResourceBundle getRepoConfig() {
    return repoConfig;
  }

  public DigitalItemInfo generateItem() {
    return new FedoraItemInfo();
  }

  public void init(PropertyResourceBundle repoConfig) {
    this.repoConfig = repoConfig;

    // Prepare the authentication details for a web service request, i.e. fedora admin username and password
    authenticator = new HttpTransportProperties.Authenticator();
    authenticator.setUsername(repoConfig.getString(CONFIG_KEY_CONNECTION_USERNAME));
    authenticator.setPassword(repoConfig.getString(CONFIG_KEY_CONNECTION_PASSWORD));

    // This is essential as otherwise the auth creds are not sent with the initial request
    authenticator.setPreemptiveAuthentication(true);

    // We need a custom protocol handler to do SSL probing so we don't have to bother with importing certs
    customProtocolHandler = new Protocol("https",
                                         new FedoraProtocolSocketFactory(keystorePath, keystorePassword,
                                                                         truststorePath, truststorePassword),
                                         443);

    try {
      EntityConnection connection = new EntityConnection(repoConfig.getString(CONFIG_KEY_API_M_ENDPOINT),
                                                         "test-keystore-alias",
                                                         keystorePath, keystorePassword,
                                                         truststorePath, truststorePassword,
                                                         EntityConnection.PROBING_ON);
      X509Certificate fedoraX509 = connection.getServerCertificate();
      KeyStore fedoraTrustStore = KeyStore.getInstance("jks");
      fedoraTrustStore.load(new FileInputStream(truststorePath), truststorePassword.toCharArray());
      // ...under it's Subject DN as an alias...
      fedoraTrustStore.setCertificateEntry(fedoraX509.getSubjectDN().toString(), fedoraX509);
      // ...and rewrite the trust store
      fedoraTrustStore.store(new FileOutputStream(truststorePath), truststorePassword.toCharArray());
    }
    catch(Exception e) {
      System.out.println(e);
    }
  }

  public boolean createObject(DigitalItemInfo item) {
    // Build a new request document
    IngestDocument doc = IngestDocument.Factory.newInstance();

    IngestDocument.Ingest ingest = doc.addNewIngest();
    // java.lang.AssertionError: fedora.server.errors.ObjectValidityException: [DOValidatorImpl]: failed Schematron rules validation. null
    // if this is wrong
    ingest.setFormat(DIGITAL_OBJECT_FORMAT_FOXML);
    //ingest.setLogMessage(repositoryProperties.getString(PROPS_KEY_INGEST_LOG_MESSAGE));

    DigitalObjectDocument objectDoc = DigitalObjectDocument.Factory.newInstance();
    DigitalObjectDocument.DigitalObject object = objectDoc.addNewDigitalObject();

    object.setPID(((FedoraPrivateItemInfo)(item.getPrivateInfo())).getPid());

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
    labelProperty.setVALUE(((FedoraPrivateItemInfo)(item.getPrivateInfo())).getPid());

    // <foxml:property NAME="info:fedora/fedora-system:def/model#contentModel" VALUE="TEST_IMAGE"/>
    PropertyType contentModelProperty = objectProperties.addNewProperty();
    contentModelProperty.setNAME(PropertyType.NAME.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_CONTENT_MODEL);
    contentModelProperty.setVALUE("text/plain");

    // <foxml:extproperty NAME="http://www.openarchives.org/OAI/1.1/oai-identifier.xsd" VALUE="oai:cornell.edu:demo:999"/>
    // For use with <foxml:datastreamVersion ID="DC.0" MIMETYPE="text/xml" LABEL="Default Dublin Core Record">
    // <foxml:xmlContent>/<oai_dc:dc>
    ExtpropertyType extProperty = objectProperties.addNewExtproperty();
    extProperty.setNAME("http://www.openarchives.org/OAI/1.1/oai-identifier.xsd");
    extProperty.setVALUE("oai:cornell.edu:" + ((FedoraPrivateItemInfo)(item.getPrivateInfo())).getPid());
    // /////////////////////////////////////////////////////////////////////////////////////////////////

    // /////////////////////////////////////////////////////////////////////////////////////////////////
    // Dublin Core record for the digital object
    DatastreamType dcDatastream = object.addNewDatastream();
    dcDatastream.setID("DC");
    dcDatastream.setSTATE(StateType.A);
    dcDatastream.setCONTROLGROUP(DatastreamType.CONTROLGROUP.X);

    DatastreamVersionType dcDatastreamVersion = dcDatastream.addNewDatastreamVersion();
    dcDatastreamVersion.setID("DC.0");
    dcDatastreamVersion.setMIMETYPE("text/xml");
    dcDatastreamVersion.setLABEL("Default Dublin Core Record");

    XmlContentType xmlContent = dcDatastreamVersion.addNewXmlContent();

    Element dc = xmlContent.getDomNode().getOwnerDocument().createElementNS("oai_dc", "dc");

    // http://www.fedora.info/bugzilla/show_bug.cgi?id=153
    dc.setAttribute("xmlns:oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
    dc.setAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");

    Element dcTitle = xmlContent.getDomNode().getOwnerDocument().createElementNS("dc", "title");
    Text textNode = xmlContent.getDomNode().getOwnerDocument().createTextNode(item.getTitle());
    dcTitle.appendChild(textNode);
    dc.appendChild(dcTitle);

    Element dcCreator = xmlContent.getDomNode().getOwnerDocument().createElementNS("dc", "creator");
    textNode = xmlContent.getDomNode().getOwnerDocument().createTextNode(item.getCreator());
    dcCreator.appendChild(textNode);
    dc.appendChild(dcCreator);

    Element dcSubject = xmlContent.getDomNode().getOwnerDocument().createElementNS("dc", "subject");
    textNode = xmlContent.getDomNode().getOwnerDocument().createTextNode(item.getSubject());
    dcSubject.appendChild(textNode);
    dc.appendChild(dcSubject);

    Element dcDescription = xmlContent.getDomNode().getOwnerDocument().createElementNS("dc", "description");
    textNode = xmlContent.getDomNode().getOwnerDocument().createTextNode(item.getDescription());
    dcDescription.appendChild(textNode);
    dc.appendChild(dcDescription);

    Element dcPublisher = xmlContent.getDomNode().getOwnerDocument().createElementNS("dc", "publisher");
    textNode = xmlContent.getDomNode().getOwnerDocument().createTextNode(item.getPublisher());
    dcPublisher.appendChild(textNode);
    dc.appendChild(dcPublisher);

    Element dcIdentifier = xmlContent.getDomNode().getOwnerDocument().createElementNS("dc", "identifier");
    textNode = xmlContent.getDomNode().getOwnerDocument().createTextNode(item.getTitle());
    dcIdentifier.appendChild(textNode);
    dc.appendChild(dcIdentifier);

    xmlContent.getDomNode().appendChild(dc);
    // /////////////////////////////////////////////////////////////////////////////////////////////////

    // /////////////////////////////////////////////////////////////////////////////////////////////////
    // RELS-EXT (RDF) relationships for the object
    DatastreamType rdfDatastream = object.addNewDatastream();
    rdfDatastream.setID("RELS-EXT");
    rdfDatastream.setCONTROLGROUP(DatastreamType.CONTROLGROUP.X);

    DatastreamVersionType rdfDatastreamVersion = rdfDatastream.addNewDatastreamVersion();
    rdfDatastreamVersion.setID("RELS-EXT.0");
    rdfDatastreamVersion.setMIMETYPE("text/xml");
    rdfDatastreamVersion.setLABEL("Fedora Object-to-Object Relationship Metadata");

    XmlContentType rdfXmlContent = rdfDatastreamVersion.addNewXmlContent();

    Element rdfRoot = rdfXmlContent.getDomNode().getOwnerDocument().createElementNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF");

    Element rdfDescription = rdfXmlContent.getDomNode().getOwnerDocument().createElementNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description");
    rdfDescription.setAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about", "info:fedora/" + ((FedoraPrivateItemInfo)(item.getPrivateInfo())).getPid());
    rdfRoot.appendChild(rdfDescription);

    /*
    Element fedIsMemberOfCollection = rdfXmlContent.getDomNode().getOwnerDocument().createElementNS("fedora", "isMemberOfCollection");
    fedIsMemberOfCollection.setAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource", "info:fedora/test:collection1");
    rdfDescription.appendChild(fedIsMemberOfCollection);
    */

    Element owner = rdfXmlContent.getDomNode().getOwnerDocument().createElementNS("http://www.nsdl.org/ontologies/relationships#", "owner");
    Text ownerTextNode = rdfXmlContent.getDomNode().getOwnerDocument().createTextNode(item.getCreator());
    owner.appendChild(ownerTextNode);
    rdfDescription.appendChild(owner);

    rdfXmlContent.getDomNode().appendChild(rdfRoot);
    // /////////////////////////////////////////////////////////////////////////////////////////////////

    // /////////////////////////////////////////////////////////////////////////////////////////////////
    // Datastream for the object
    DatastreamType objDatastream = object.addNewDatastream();
    objDatastream.setID("PDF");
    objDatastream.setSTATE(StateType.A);
    objDatastream.setCONTROLGROUP(DatastreamType.CONTROLGROUP.M);

    DatastreamVersionType objDatastreamVersion = objDatastream.addNewDatastreamVersion();
    objDatastreamVersion.setID("TEST");
    objDatastreamVersion.setMIMETYPE(item.getMimeType());
    objDatastreamVersion.setLABEL(item.getTitle());

    objDatastreamVersion.setBinaryContent(item.getBinaryContent());
    // /////////////////////////////////////////////////////////////////////////////////////////////////

    //ingest.setObjectXML(objectDoc.toString().getBytes());

    XmlCursor cursor = objectDoc.newCursor();
    if (cursor.toFirstChild())
      cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"),
                                        "info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-0.xsd");

    ingest.setObjectXML(Utils.xmlToString(objectDoc).getBytes());

    if (debug) {
      Utils.dumpXML(objectDoc, debugOutputPath + System.getProperty("file.separator") + "ingest.xml");
    }

    try {
      // Initiate the client connection to the API-A endpoint
      FedoraAPIMServiceStub stub = new FedoraAPIMServiceStub(repoConfig.getString(CONFIG_KEY_API_M_ENDPOINT));

      // Add the auth creds to the client
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);
      // Register our custom SSL handler for this connection
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, customProtocolHandler);

      // Call the web service
      IngestResponseDocument outDoc = stub.ingest(doc);

      if (!outDoc.getIngestResponse().getObjectPID().equals(((FedoraPrivateItemInfo)(item.getPrivateInfo())).getPid())) {
        return false;
      }

      return true;
    }
    catch(RemoteException re) {
      log.error(re);
      return false;
    }
  }

  public DigitalItemInfo[] list() {
    return queryFedora(null);
  }

  public DigitalItemInfo list(String id) {
    DigitalItemInfo[] items = queryFedora(id);
    // If we have an id (pid) at it doesn't exist, then it's a new one being uploaded
    if (items.length == 0) return null;
    else return items[0];
  }

  public InputStream getContentAsStream(String endpoint) {
    try {
      EntityConnection connection = new EntityConnection(endpoint,
                                                         "test-keystore-alias",
                                                         keystorePath, keystorePassword,
                                                         truststorePath, truststorePassword,
                                                         EntityConnection.PROBING_ON);
      X509Certificate fedoraX509 = connection.getServerCertificate();
      KeyStore fedoraTrustStore = KeyStore.getInstance("jks");
      fedoraTrustStore.load(new FileInputStream(truststorePath), truststorePassword.toCharArray());
      // ...under it's Subject DN as an alias...
      fedoraTrustStore.setCertificateEntry(fedoraX509.getSubjectDN().toString(), fedoraX509);
      // ...and rewrite the trust store
      fedoraTrustStore.store(new FileOutputStream(truststorePath), truststorePassword.toCharArray());

      connection.setAuthentication(repoConfig.getString(CONFIG_KEY_CONNECTION_USERNAME),
                                   repoConfig.getString(CONFIG_KEY_CONNECTION_PASSWORD));
      
      return connection.getInputStream();
    }
    catch(Exception e) {
      return null;
    }
  }

  private DigitalItemInfo[] queryFedora(String pid) {
    // Build a new request document
    FindObjectsDocument doc = FindObjectsDocument.Factory.newInstance();

    FindObjectsDocument.FindObjects params = doc.addNewFindObjects();

    FieldSearchQuery query = params.addNewQuery();
    FieldSearchQuery.Conditions conditions = query.addNewConditions();
    Condition condition = conditions.addNewCondition();
    condition.setProperty("pid");
    if (pid == null) {
      condition.setOperator(ComparisonOperator.HAS);
      condition.setValue("*");
    }
    else {
      condition.setOperator(ComparisonOperator.EQ);
      condition.setValue(pid);
    }

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
      FedoraAPIAServiceStub stub = new FedoraAPIAServiceStub(repoConfig.getString(CONFIG_KEY_API_A_ENDPOINT));

      // Add the auth creds to the client
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);
      // Register our custom SSL handler for this connection
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, customProtocolHandler);

      // Call the web service
      FindObjectsResponseDocument outDoc = stub.findObjects(doc);

      ObjectFields[] fields = outDoc.getFindObjectsResponse().getResult().getResultList().getObjectFieldsArray();

      Vector<DigitalItemInfo> items = new Vector<DigitalItemInfo>();

      // Loop through all the objects that were found
      for (ObjectFields field : fields) {
        FedoraItemInfo item = new FedoraItemInfo();

        item.setCreator(field.getCreatorArray(0));
        item.setDescription(field.getDescriptionArray(0));
        item.setDisplayName(field.getTitleArray(0));
        item.setIdentifier(field.getPid());
        item.setModifiedDate(field.getMDate());
        item.setOriginalFilename(field.getPid());
        item.setPublisher(field.getPublisherArray(0));
        item.setSubject(field.getSubjectArray(0));
        item.setTitle(field.getTitleArray(0));
        //item.setType(field.getTypeArray(0));
        item.setIsCollection(false);
        item.setIsResource(true);

        FedoraPrivateItemInfo privateInfo = new FedoraPrivateItemInfo();
        privateInfo.setPid(field.getPid());
        privateInfo.setOwnerId(field.getOwnerId());
        item.setPrivateInfo(privateInfo);

        ListDatastreamsDocument dsDoc = ListDatastreamsDocument.Factory.newInstance();
        ListDatastreamsDocument.ListDatastreams ds = dsDoc.addNewListDatastreams();
        ds.setPid(field.getPid());
        ListDatastreamsResponseDocument dsOutDoc = stub.listDatastreams(dsDoc);
        DatastreamDef[] defs = dsOutDoc.getListDatastreamsResponse().getDatastreamDefArray();

        int count = 0;
        for (DatastreamDef def : defs) {
          GetDatastreamDisseminationDocument dissDoc = GetDatastreamDisseminationDocument.Factory.newInstance();
          GetDatastreamDisseminationDocument.GetDatastreamDissemination diss = dissDoc.addNewGetDatastreamDissemination();
          diss.setDsID(def.getID());
          diss.setPid(field.getPid());
          GetDatastreamDisseminationResponseDocument dissOutDoc = stub.getDatastreamDissemination(dissDoc);
          MIMETypedStream stream = dissOutDoc.getGetDatastreamDisseminationResponse().getDissemination();
          
          // The first one seems to be the default content, such as PDF etc, with the rest being the DC and RELS-EXT
          if (count == 0) {
            item.setMimeType(stream.getMIMEType());
            item.setBinaryContent(stream.getStream());
            // https://sgarbh.smo.uhi.ac.uk:8101/fedora/get/demo:002/PDF
            item.setURL(repoConfig.getString(CONFIG_KEY_DISSEMINATION_ENDPOINT) + "/" +
                        field.getPid() + "/" + def.getID());

            count++;
          }

          // Dublin core datastream
          if (def.getID().equals("DC")) {
            ((FedoraPrivateItemInfo)(item.getPrivateInfo())).setDCDatastreamID(def.getID());
          }

          // RELS-EXT core datastream
          if (def.getID().equals("RELS-EXT")) {
            ((FedoraPrivateItemInfo)(item.getPrivateInfo())).setRelsExtDatastreamID(def.getID());
          }
        }

        items.add(item);
      }

      DigitalItemInfo[] digitalItems = new DigitalItemInfo[items.size()];
      return (DigitalItemInfo[])items.toArray(digitalItems);
    }
    catch(RemoteException re) {
      return null;
    }
  }

  public boolean deleteObject(String pid) {
    PurgeObjectDocument doc = PurgeObjectDocument.Factory.newInstance();
    PurgeObjectDocument.PurgeObject purge = doc.addNewPurgeObject();

    purge.setPid(pid);
    //purge.setLogMessage(repositoryProperties.getString(PROPS_KEY_PURGE_LOG_MESSAGE));
    // fedora.server.errors.GeneralException: Forced object removal is not yet supported.
    purge.setForce(false);

    try {
      // Initiate the client connection to the API-A endpoint
      FedoraAPIMServiceStub stub = new FedoraAPIMServiceStub(repoConfig.getString(CONFIG_KEY_API_M_ENDPOINT));

      // Add the auth creds to the client
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);
      // Register our custom SSL handler for this connection
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, customProtocolHandler);

      // Call the web service
      PurgeObjectResponseDocument outDoc = stub.purgeObject(doc);

      // 2007-10-15T08:59:46.827Z
      if (outDoc.getPurgeObjectResponse().getPurgedDate() != null)
        return true;
      else
        return false;
    }
    catch(RemoteException re) {
      log.error(re);
      return false;
    }
  }

  public boolean modifyObject(DigitalItemInfo item) {
    try {
      // Initiate the client connection to the API-A endpoint
      FedoraAPIMServiceStub stub = new FedoraAPIMServiceStub(repoConfig.getString(CONFIG_KEY_API_M_ENDPOINT));

      // Add the auth creds to the client
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);
      // Register our custom SSL handler for this connection
      stub._getServiceClient().getOptions().setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER, customProtocolHandler);

      GetDatastreamDocument dsInDoc = GetDatastreamDocument.Factory.newInstance();
      GetDatastreamDocument.GetDatastream dsIn = dsInDoc.addNewGetDatastream();
      dsIn.setPid(((FedoraPrivateItemInfo)(item.getPrivateInfo())).getPid());
      dsIn.setDsID(((FedoraPrivateItemInfo)(item.getPrivateInfo())).getDCDatastreamID());
      GetDatastreamResponseDocument dsOutDoc = stub.getDatastream(dsInDoc);
      Datastream ds = dsOutDoc.getGetDatastreamResponse().getDatastream();

      ModifyDatastreamByValueDocument inDoc = ModifyDatastreamByValueDocument.Factory.newInstance();
      ModifyDatastreamByValueDocument.ModifyDatastreamByValue in = inDoc.addNewModifyDatastreamByValue();

//      in.setChecksum("");
//      in.setChecksumType("");
      in.setDsContent(Utils.getDCBytes(item, ds));
      in.setDsID(((FedoraPrivateItemInfo)(item.getPrivateInfo())).getDCDatastreamID());
      in.setDsLabel(ds.getLabel());
      in.setForce(true);
      in.setLogMessage("Update from Sakai");
      in.setMIMEType(ds.getMIMEType());
      in.setPid(((FedoraPrivateItemInfo)(item.getPrivateInfo())).getPid());

      // Call the web service
      ModifyDatastreamByValueResponseDocument outDoc = stub.modifyDatastreamByValue(inDoc);

      // 2007-10-15T08:59:46.827Z
      if (outDoc.getModifyDatastreamByValueResponse().getModifiedDate() != null)
        return true;
      else
        return false;
    }
    catch(RemoteException re) {
      log.error(re);
      return false;
    }
  }

  public void search() {}
}
