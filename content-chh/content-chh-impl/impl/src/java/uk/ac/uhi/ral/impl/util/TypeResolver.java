/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl.util;

import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingHandler;
import org.sakaiproject.content.api.ContentHostingHandlerResolver;
import org.sakaiproject.content.chh.fedora.ContentCollectionFedora;
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
                                            DigitalRepository repo,
                                            DigitalItemInfo item) {
    while (relativePath.length() > 0 && relativePath.charAt(0) == '/')
      relativePath = relativePath.substring(1);
    while (relativePath.length() > 0
        && relativePath.charAt(relativePath.length() - 1) == '/')
      relativePath = relativePath.substring(0, relativePath.length() - 1);

    // Root of the mount, so it's a collection
    if (relativePath.equals("")) {
      relativePath = "/";
      ContentCollectionFedora collection = new ContentCollectionFedora(realParent, relativePath, chh, chhResolver, repo, item);
      collection.wrap();
      return collection;
    }

    // relativePath will be the pid to get the data for
    relativePath = "/" + relativePath;
    return TypeMapper.toContentEntity(item, realParent, relativePath, chh, chhResolver, repo);
  }
}
