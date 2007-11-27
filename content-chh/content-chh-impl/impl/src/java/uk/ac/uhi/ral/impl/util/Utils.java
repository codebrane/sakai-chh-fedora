/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl.util;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.util.HashMap;
import java.io.StringWriter;

public class Utils {
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
}
