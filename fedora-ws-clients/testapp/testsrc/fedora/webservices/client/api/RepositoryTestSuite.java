/* SVN Header
   $Id: RepositoryTestSuite.java 86 2007-09-09 22:44:55Z sm00sm $
   $URL: https://source.uhi.ac.uk/svn/ctrep/trunk/testapp/testsrc/fedora/webservices/client/api/RepositoryTestSuite.java $
*/

package fedora.webservices.client.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import fedora.webservices.client.api.a.test.API_ATestSuite;
import fedora.webservices.client.api.m.test.API_MTestSuite;

/**
 * Groups all the Fedora web services client tests together. Each test can be run individually
 * or the entire test suite can be run from here.
 *
 * @author Alistair Young
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { API_ATestSuite.class,
                       API_MTestSuite.class})
public class RepositoryTestSuite {
}
