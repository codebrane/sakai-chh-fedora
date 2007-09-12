/* SVN Header
   $Id: API_MTestSuite.java 86 2007-09-09 22:44:55Z sm00sm $
   $URL: https://source.uhi.ac.uk/svn/ctrep/trunk/testapp/testsrc/fedora/webservices/client/api/m/test/API_MTestSuite.java $
*/

package fedora.webservices.client.api.m.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Groups all the API-M tests together
 *
 * @author Alistair Young
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { DescribeUser.class })
public class API_MTestSuite {
}
