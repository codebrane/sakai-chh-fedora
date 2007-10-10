/* CVS Header
   $Id$
   $Log$
*/

package fedora.webservices.client.api;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * Utility class to support unit tests
 */
public class Utils {
  /**
   * Reads a file into an array of bytes
   *
   * @param filePath Full path and name of the file to read
   * @return an array of bytes
   */
  public static byte[] readFile(String filePath) {
    try {
      File f = new File(filePath);
      byte[] bytes = new byte[(int)f.length()]; // assume < 2gb
      RandomAccessFile raf = new RandomAccessFile(f, "r");
      raf.readFully(bytes);
      raf.close();
      return new String(bytes).getBytes();
    }
    catch(Exception e) {
      return null;
    }
  }

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
}
