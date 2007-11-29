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
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.tool.cover.SessionManager;
import uk.ac.uhi.ral.DigitalItemInfo;
import uk.ac.uhi.ral.DigitalRepository;
import uk.ac.uhi.ral.impl.FedoraPrivateItemInfo;

import java.rmi.server.UID;

public class TypeMapper {
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

  public static DigitalItemInfo updateDigitalItemInfo(DigitalItemInfo item, ContentResourceEdit edit) {
    item.setTitle((String)edit.getProperties().get(ResourceProperties.PROP_DISPLAY_NAME));
    item.setCreator((String)edit.getProperties().get(ResourceProperties.PROP_CREATOR));
    item.setSubject((String)edit.getProperties().get(ResourceProperties.PROP_DESCRIPTION));
    item.setDescription((String)edit.getProperties().get(ResourceProperties.PROP_DESCRIPTION));
    item.setPublisher((String)edit.getProperties().get(ResourceProperties.PROP_CREATOR));
    item.setIdentifier(edit.getId());
    item.setMimeType(edit.getContentType());
    try {
      if (edit.streamContent() != null) {
        item.setBinaryContent(Utils.getContentBytes(edit.streamContent()));
      }
    }
    catch(ServerOverloadException soe) {
    }
    item.setDisplayName((String)edit.getProperties().get(ResourceProperties.PROP_DISPLAY_NAME));
    item.setModifiedDate((String)edit.getProperties().get(ResourceProperties.PROP_MODIFIED_DATE));
    item.setOriginalFilename((String)edit.getProperties().get(ResourceProperties.PROP_ORIGINAL_FILENAME));
    item.setType("TEST-TYPE");

    if (item.getTitle() == null) item.setTitle("TITLE_NOT_SET");
    if (item.getCreator() == null) item.setCreator("CREATOR_NOT_SET");
    if (item.getSubject() == null) item.setSubject("SUBJECT_NOT_SET");
    if (item.getDescription() == null) item.setDescription("DESCRIPTION_NOT_SET");
    if (item.getPublisher() == null) item.setPublisher("PUBLISHER_NOT_SET");
    if (item.getIdentifier() == null) item.setIdentifier("IDENTIFIER_NOT_SET");

    if (item.getPrivateInfo() == null) {
      item.setPrivateInfo(new FedoraPrivateItemInfo());
      item.setIsResource(true);
      item.setIsCollection(false);
      ((FedoraPrivateItemInfo)(item.getPrivateInfo())).setPid("demo:" + new UID().toString().replaceAll(":", ""));
      ((FedoraPrivateItemInfo)(item.getPrivateInfo())).setOwnerId(SessionManager.getCurrentSession().getUserEid());
    }

    return item;
  }
}
