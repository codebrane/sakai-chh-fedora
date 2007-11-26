/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl;

import fedora.webservices.client.api.a.FedoraAPIAServiceStub;
import info.fedora.definitions.x1.x0.types.*;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.httpclient.protocol.Protocol;
import org.guanxi.common.EntityConnection;
import uk.ac.uhi.ral.DigitalItemInfo;
import uk.ac.uhi.ral.DigitalRepository;
import uk.ac.uhi.ral.impl.util.TypeMapper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.PropertyResourceBundle;

public class FedoraDigitalRepositoryImpl implements DigitalRepository {
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

  public FedoraDigitalRepositoryImpl(String keystorePath, String keystorePassword,
                                     String truststorePath, String truststorePassword) {
    this.keystorePath = keystorePath;
    this.keystorePassword = keystorePassword;
    this.truststorePath = truststorePath;
    this.truststorePassword = truststorePassword;
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

  public void createObject() {
  }

  public void modifyObject() {}
  public void deleteObject() {}
  public void search() {}

  public DigitalItemInfo[] list() {
    return queryFedora(null);
  }

  public DigitalItemInfo list(String id) {
    return queryFedora(id)[0];
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

      return TypeMapper.toDigitalItemInfo(fields);
    }
    catch(RemoteException re) {
      return null;
    }
  }
}
