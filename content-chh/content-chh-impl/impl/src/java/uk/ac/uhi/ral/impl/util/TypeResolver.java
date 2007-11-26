/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl.util;

import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingHandler;
import org.sakaiproject.content.api.ContentHostingHandlerResolver;
import uk.ac.uhi.ral.DigitalItemInfo;
import uk.ac.uhi.ral.DigitalRepository;

public class TypeResolver {
  /**
   * 
   * @param realParent
   * @param relativePath If this is "" then we're at the root of the mount, so should return a collection
   * @param chh
   * @param repo
   * @return
   */
  public static ContentEntity resolveEntity(ContentEntity realParent, String relativePath,
                                            ContentHostingHandler chh,
                                            ContentHostingHandlerResolver chhResolver,
                                            DigitalRepository repo) {
    while (relativePath.length() > 0 && relativePath.charAt(0) == '/')
      relativePath = relativePath.substring(1);
    while (relativePath.length() > 0
        && relativePath.charAt(relativePath.length() - 1) == '/')
      relativePath = relativePath.substring(0, relativePath.length() - 1);

    DigitalItemInfo item = null;

    // Root of the mount, so it's a collection
    if (relativePath.equals("")) {
      item = repo.generateItem();
      relativePath = "/" + relativePath;
      item.setIsCollection(true);
      item.setIsResource(false);
      item.setDescription("TEST");
      item.setCreator("TEST");
      item.setDisplayName("TEST");
      item.setModifiedDate("TEST");
      item.setDisplayName("TEST");
    }
    else {
      item = repo.list(relativePath);
      item.setIsResource(true);
    }

    return TypeMapper.toContentEntity(item, realParent, relativePath, chh, chhResolver, repo);
  }
}
