/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl.util;

import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingHandler;
import org.sakaiproject.content.api.ContentHostingHandlerResolver;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.chh.fedora.ContentCollectionFedora;
import org.sakaiproject.content.chh.fedora.ContentResourceFedora;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.entity.api.ResourceProperties;
import uk.ac.uhi.ral.DigitalItemInfo;
import uk.ac.uhi.ral.DigitalRepository;
import uk.ac.uhi.ral.impl.FedoraItemInfo;
import uk.ac.uhi.ral.impl.FedoraPrivateItemInfo;

public class TypeMapper {
  public static DigitalItemInfo toDigitalItemInfo(ContentResourceEdit edit) {
    DigitalItemInfo item = new FedoraItemInfo();
    FedoraPrivateItemInfo privateInfo = new FedoraPrivateItemInfo();

    try {
      item.setTitle(edit.getId());
      item.setCreator((String)edit.getProperties().get(ResourceProperties.PROP_CREATOR));
      item.setSubject((String)edit.getProperties().get(ResourceProperties.PROP_DESCRIPTION));
      item.setDescription((String)edit.getProperties().get(ResourceProperties.PROP_DESCRIPTION));
      item.setPublisher((String)edit.getProperties().get(ResourceProperties.PROP_CREATOR));
      item.setIdentifier(edit.getId());
      item.setMimeType(edit.getContentType());
      item.setBinaryContent(edit.getContent());
      item.setDisplayName((String)edit.getProperties().get(ResourceProperties.PROP_DISPLAY_NAME));
      item.setModifiedDate((String)edit.getProperties().get(ResourceProperties.PROP_MODIFIED_DATE));
      item.setOriginalFilename((String)edit.getProperties().get(ResourceProperties.PROP_ORIGINAL_FILENAME));
      item.setType("TEST-TYPE");

      item.setIsCollection(false);
      item.setIsResource(true);
    }
    catch(ServerOverloadException soe) {
    }

    privateInfo.setPid("");
    privateInfo.setOwnerId(SessionManager.getCurrentSession().getUserEid());

    item.setPrivateInfo(privateInfo);

    return item;
  }

  public static DigitalItemInfo[] toDigitalItemInfo(ContentResourceEdit[] edits) {
    DigitalItemInfo[] items = new DigitalItemInfo[edits.length];

    int count = 0;
    for (ContentResourceEdit edit : edits) {
      items[count] = toDigitalItemInfo(edit);
      count++;
    }

    return items;
  }

  public static ContentEntity toContentEntity(DigitalItemInfo item,
                                              ContentEntity realParent, String relativePath,
                                              ContentHostingHandler chh,
                                              ContentHostingHandlerResolver chhResolver,
                                              DigitalRepository repo) {
    if (item.isResource()) {
      ContentResourceFedora resource = new ContentResourceFedora(realParent, relativePath, chh, chhResolver, repo, item);
      resource.wrap();
      return resource;
    }
    else {
      ContentCollectionFedora collection = new ContentCollectionFedora(realParent, relativePath, chh, chhResolver, repo, item);
      collection.wrap();
      return collection;
    }
  }

  public static ContentEntity[] toContentEntity(DigitalItemInfo[] items,
                                                ContentEntity realParent, String relativePath,
                                                ContentHostingHandler chh,
                                                ContentHostingHandlerResolver chhResolver,
                                                DigitalRepository repo) {
    ContentEntity[] entities = new ContentEntity[items.length];

    int count = 0;
    for (DigitalItemInfo item : items) {
      entities[count] = toContentEntity(item, realParent, relativePath, chh, chhResolver, repo);
      count++;
    }

    return entities;
  }
}
