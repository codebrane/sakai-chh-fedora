/* CVS Header
   $Id$
   $Log$
*/

package org.sakaiproject.content.chh.fedora.resource;

import org.junit.Test;
import static org.junit.Assert.*;

import org.sakaiproject.content.chh.fedora.RepositoryTest;

public class Resource extends RepositoryTest {
  @Test
  public void getSingleResource() throws Exception {
    assertNotNull(siteService);
    assertNotNull(userDirService);
  }
}
