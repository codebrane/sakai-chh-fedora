/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl.util;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import uk.ac.uhi.ral.DigitalItemInfo;

import java.io.*;
import java.util.HashMap;

import fedora.fedoraSystemDef.foxml.DatastreamType;
import fedora.fedoraSystemDef.foxml.StateType;
import fedora.fedoraSystemDef.foxml.DatastreamVersionType;
import fedora.fedoraSystemDef.foxml.XmlContentType;
import info.fedora.definitions.x1.x0.types.Datastream;

public class Utils {
  /**
   * Dumps an XML document to disk.
   *
   * @param doc The document to dump to disk
   * @param filePath The full path and name of the file
   */
  public static void dumpXML(XmlObject doc, String filePath) {
    HashMap<String, String> namespaces = new HashMap<String, String>();
    namespaces.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
    namespaces.put("http://www.nsdl.org/ontologies/relationships#", "myns");
    XmlOptions xmlOptions = new XmlOptions();
    xmlOptions.setSavePrettyPrint();
    xmlOptions.setSavePrettyPrintIndent(2);
    //xmlOptions.setUseDefaultNamespace();
    //xmlOptions.setSaveAggressiveNamespaces();
    xmlOptions.setSaveSuggestedPrefixes(namespaces);
    //xmlOptions.setSaveNamespacesFirst();

    try {
      doc.save(new File(filePath), xmlOptions);
    }
    catch(Exception e) {
    }
  }

  /**
   * Returns an XMLBeans document as a String with full Fedora/RDF namespace support
   *
   * @param doc The document to parse to a String
   * @return String version of the document. This will contain prefixes for rdf and myns
   */
  public static String xmlToString(XmlObject doc) {
    HashMap<String, String> namespaces = new HashMap<String, String>();
    namespaces.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
    namespaces.put("http://www.nsdl.org/ontologies/relationships#", "myns");
    XmlOptions xmlOptions = new XmlOptions();
    xmlOptions.setSaveSuggestedPrefixes(namespaces);
    xmlOptions.setSaveAggressiveNamespaces();
    StringWriter out = new StringWriter();
    try {
      doc.save(out, xmlOptions);
      return out.toString();
    }
    catch(Exception e) {
      return null;
    }
  }

  public static byte[] getContentBytes(InputStream is) {
    int bytesRead = 0;
    byte[] buffer = new byte[1024];
    ByteArrayOutputStream bytes = new ByteArrayOutputStream(2048);
    try {
      while ((bytesRead = is.read(buffer)) != -1) {
        bytes.write(buffer, 0, bytesRead);
      }

      return bytes.toByteArray();
    }
    catch (IOException e) {
      return null;
    }
  }

  public static byte[] getDCBytes(DigitalItemInfo item, Datastream ds) {
    DatastreamType dcDatastream = DatastreamType.Factory.newInstance();
    dcDatastream.setID("DC");
    dcDatastream.setSTATE(StateType.A);
    dcDatastream.setCONTROLGROUP(DatastreamType.CONTROLGROUP.X);

    DatastreamVersionType dcDatastreamVersion = dcDatastream.addNewDatastreamVersion();
    dcDatastreamVersion.setID(ds.getID());
    dcDatastreamVersion.setMIMETYPE(ds.getMIMEType());
    dcDatastreamVersion.setLABEL(ds.getLabel());

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

    return xmlContent.toString().getBytes();
  }
}
