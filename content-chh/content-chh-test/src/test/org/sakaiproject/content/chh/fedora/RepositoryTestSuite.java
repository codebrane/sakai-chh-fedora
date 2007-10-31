/* CVS Header
   $Id$
   $Log$
*/

package org.sakaiproject.content.chh.fedora;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.sakaiproject.content.chh.fedora.resource.Resource;

/**
 * Groups together all the Sakai/Fedora unit tests
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { Resource.class })
public class RepositoryTestSuite {
}
