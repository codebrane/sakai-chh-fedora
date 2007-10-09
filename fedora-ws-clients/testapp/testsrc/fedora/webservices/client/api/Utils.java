/* CVS Header
   $Id$
   $Log$
*/

package fedora.webservices.client.api;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.io.File;
import java.io.RandomAccessFile;

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
    //HashMap namespaces = new HashMap();
    //namespaces.put(Shibboleth.NS_SAML_10_PROTOCOL, Shibboleth.NS_PREFIX_SAML_10_PROTOCOL);
    //namespaces.put(Shibboleth.NS_SAML_10_ASSERTION, Shibboleth.NS_PREFIX_SAML_10_ASSERTION);
    //namespaces.put(Guanxi.NS_SP_NAME_IDENTIFIER, "gxsp");
    XmlOptions xmlOptions = new XmlOptions();
    xmlOptions.setSavePrettyPrint();
    xmlOptions.setSavePrettyPrintIndent(2);
    //xmlOptions.setUseDefaultNamespace();
    //xmlOptions.setSaveAggressiveNamespaces();
    //xmlOptions.setSaveSuggestedPrefixes(namespaces);
    //xmlOptions.setSaveNamespacesFirst();

    try {
      doc.save(new File(filePath), xmlOptions);
    }
    catch(Exception e) {
    }
  }
}
