/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl.util;

import info.fedora.definitions.x1.x0.types.ObjectFields;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingHandler;
import org.sakaiproject.content.api.ContentHostingHandlerResolver;
import org.sakaiproject.content.chh.fedora.ContentCollectionFedora;
import org.sakaiproject.content.chh.fedora.ContentResourceFedora;
import uk.ac.uhi.ral.DigitalItemInfo;
import uk.ac.uhi.ral.DigitalRepository;
import uk.ac.uhi.ral.impl.FedoraItemInfo;
import uk.ac.uhi.ral.impl.FedoraPrivateItemInfo;

public class TypeMapper {
  public static DigitalItemInfo toDigitalItemInfo(ObjectFields fields) {
    DigitalItemInfo item = new FedoraItemInfo();
    FedoraPrivateItemInfo privateInfo = new FedoraPrivateItemInfo();

    item.setDescription(fields.getLabel());
    item.setDisplayName(fields.getTitleArray(0));
    item.setCreator(fields.getCreatorArray(0));
    item.setDescription(fields.getDescriptionArray(0));
    //item.setType(fields.getTypeArray(0));
    item.setType("TEST-TYPE");

    item.setIsCollection(false);
    item.setIsResource(true);

    privateInfo.setPid(fields.getPid());
    privateInfo.setOwnerId(fields.getOwnerId());

    item.setPrivateInfo(privateInfo);

    return item;
  }

  public static DigitalItemInfo[] toDigitalItemInfo(ObjectFields[] wsFields) {
    DigitalItemInfo[] items = new DigitalItemInfo[wsFields.length];

    int count = 0;
    for (ObjectFields fields : wsFields) {
      items[count] = toDigitalItemInfo(fields);
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
