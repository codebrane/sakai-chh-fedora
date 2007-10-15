/* SVN Header
   $Id: API_ATestSuite.java 86 2007-09-09 22:44:55Z sm00sm $
   $URL: https://source.uhi.ac.uk/svn/ctrep/trunk/testapp/testsrc/fedora/webservices/client/api/a/test/API_ATestSuite.java $
*/

package fedora.webservices.client.api.a.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Groups all the API-A tests together
 *
 * @author Alistair Young
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { DescribeRepository.class,
                       Search.class })
public class API_ATestSuite {
}
