/**
 * 
 */
package org.sakaiproject.content.chh.fedora;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.*;
import org.sakaiproject.entity.api.Edit;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.tool.cover.SessionManager;
import uk.ac.uhi.ral.DigitalItemInfo;
import uk.ac.uhi.ral.DigitalRepository;
import uk.ac.uhi.ral.DigitalRepositoryFactory;
import uk.ac.uhi.ral.impl.FedoraItemInfo;
import uk.ac.uhi.ral.impl.util.TypeResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PropertyResourceBundle;

/**
 *
 *
 * @author Alistair Young alistairskye@googlemail.com
 */
public class ContentHostingHandlerImplFedora implements ContentHostingHandler {
  private static final String LOG_MARKER = "[CTREP:ContentHostingHandlerImplFedora] ";
  
  /** Our logger */
  private static final Log log = LogFactory.getLog(ContentHostingHandlerImplFedora.class);

  /** The Sakai content hosting resolver */
  private ContentHostingHandlerResolver contentHostingHandlerResolver = null;

  /** The repository factory implementation to use */
  private DigitalRepositoryFactory repoFactory = null;

  /**
   * Retrieves the DigitalRepositoryFactory we are using
   * @return DigitalRepositoryFactory we are using
   */
  public DigitalRepositoryFactory getRepository() {
    return repoFactory;
  }

  /**
   * Sets the DigitalRepositoryFactory to use. Injected by Spring from components.xml
   * @param repoFactory DigitalRepositoryFactory to use
   */
  public void setRepoFactory(DigitalRepositoryFactory repoFactory) {
    this.repoFactory = repoFactory;
  }

  /**
   * Sets the Sakai ContentHostingHandlerResolver to use. Injected by Spring from components.xml
   * @param chhr ContentHostingHandlerResolver to use
   */
  public void setContentHostingHandlerResolver(ContentHostingHandlerResolver chhr) {
    contentHostingHandlerResolver = chhr;
  }

  /**
   * Retrieves the Sakai ContentHostingHandlerResolver we are using
   * @return ContentHostingHandlerResolver we are using
   */
  public ContentHostingHandlerResolver getContentHostingHandlerResolver() {
    return contentHostingHandlerResolver;
  }

  /**
	 * Cancel an edit to a collection, if this needs to be done in the impl.
	 * 
	 * @param edit
	 */
	public void cancel(ContentCollectionEdit edit) {
	}

	/**
	 * cancel an edit to a resource ( if this needs to be done )
	 * 
	 * @param edit
	 */
	public void cancel(ContentResourceEdit edit) {
	}

	/**
	 * commit a collection
	 * 
	 * @param edit
	 */
	public void commit(ContentCollectionEdit edit) {
	}

	/**
	 * commit a resource
	 * 
	 * @param edit
	 */
	public void commit(ContentResourceEdit edit) {
	}

	/**
	 * commit a deleted resource
	 * 
	 * @param edit
	 * @param uuid
	 */
	public void commitDeleted(ContentResourceEdit edit, String uuid) {
	}

	/**
	 * get a list of collections contained within the supplied collection
	 * 
	 * @param collection
	 * @return
	 */
	public List getCollections(ContentCollection collection) {
    log.info(LOG_MARKER + "getCollections : getId = " + collection.getId());
    
    ContentEntity cc = collection.getVirtualContentEntity();

    if (!(cc instanceof ContentCollectionFedora)) {
      return null;
    }

    ContentCollectionFedora fedoraCollection = (ContentCollectionFedora)cc;
    List l = fedoraCollection.getMembers();
    //ArrayList<Edit> collections = new ArrayList<Edit>(l.size());
    ArrayList<Edit> collections = new ArrayList<Edit>(0);

    /*
    for (Iterator i = l.listIterator(); i.hasNext();) {
      String id = (String)i.next();

      ContentEntityFedora ceds = resolveDSpace(ccds.realParent, ccds.endpoint,
      ccds.basehandle, id.substring(ccds.realParent.getId().length() + 1),
      this, ccds.searchable);
      
      if (ceds instanceof ContentCollectionDSpace) collections.add(ceds.wrap());
    }
    */

    return collections;
	}

	/**
	 * get a ContentCollectionEdit for the ID, creating it if necessary, this should not persist until commit is invoked
	 * 
	 * @param id
	 * @return
	 */
	public ContentCollectionEdit getContentCollectionEdit(String id) {
    log.info(LOG_MARKER + "getContentCollectionEdit : id = " + id);
    return null;
	}

	/**
	 * get a content resource edit for the supplied ID, creating it if necesary. This sould not persist until commit is invoked
	 * 
	 * @param id
	 * @return
	 */
	public ContentResourceEdit getContentResourceEdit(String id) {
    log.info(LOG_MARKER + "getContentResourceEdit : id = " + id);
    return null;
	}

	/**
	 * get a list of string ids of all resources below this point
	 * 
	 * @param ce
	 * @return
	 */
	public List getFlatResources(ContentEntity ce) {
    log.info(LOG_MARKER + "getFlatResources : id = " + ce.getId());
    return null;
	}

	/**
	 * get the resource body
	 * 
	 * @param resource
	 * @return
	 * @throws ServerOverloadException
	 */
	public byte[] getResourceBody(ContentResource resource) throws ServerOverloadException {
    log.info(LOG_MARKER + "getResourceBody : id = " + resource.getId());
    return null;
	}

	/**
	 * get a list of resource ids as strings within the collection
	 * 
	 * @param collection
	 * @return
	 */
	public List getResources(ContentCollection collection) {
    log.info(LOG_MARKER + "getResources : id = " + collection.getId());

    ContentEntity cc = collection.getVirtualContentEntity();

    if (!(cc instanceof ContentCollectionFedora)) {
      return null;
    }

    ContentCollectionFedora fedoraCollection = (ContentCollectionFedora)cc;
    return fedoraCollection.getMemberResources();
	}

	/**
	 * Convert the passed-in ContentEntity into a virtual Content Entity. The implementation should check that the passed in entity is managed by this content handler before performing the translation. Additionally it must register the content handler
	 * with the newly proxied ContentEntity so that subsequent invocations are routed back to the correct ContentHostingHandler implementation
	 * 
	 * @param edit
   * @param finalId /public/Fedora/fedora-mountpoint.txt/
	 * @return
	 */
	public ContentEntity getVirtualContentEntity(ContentEntity edit, String finalId) {
    log.info(LOG_MARKER + "getVirtualContentEntity : id = " + edit.getId() + " finalId = " + finalId);

    try {
      // Get the resource content
      PropertyResourceBundle config = new PropertyResourceBundle(new ByteArrayInputStream(((ContentResource)edit).getContent()));

      SessionManager.getCurrentSession().getUserEid();

      DigitalRepository repo = repoFactory.create();

      repo.init(config);

      //ThreadLocalManager.set("FEDORA" + edit.getId(), repo);

      DigitalItemInfo item = new FedoraItemInfo();
      item.setDescription("TEST");
      item.setCreator("TEST");
      item.setDisplayName("TEST");
      item.setModifiedDate("TEST");
      item.setDisplayName("TEST");

      ContentEntityFedora entity = (ContentEntityFedora)TypeResolver.resolveEntity(edit, finalId.substring(edit.getId().length()), this,
                                                                                   contentHostingHandlerResolver, repo, item);
      return (ContentEntity)entity.wrap();
    }
    catch(IOException ioe) {
      log.error("Can't load fedora config", ioe);
    }
    catch(ServerOverloadException soe) {
      log.error("Can't get Fedora mountpoint content", soe);
    }

    return null;
	}

	/**
	 * perform a wastebasket operation on the names id, if the implementation supports the operation otherwise its safe to ignore.
	 * 
	 * @param id
	 * @param uuid
	 * @param userId
	 * @return
	 */
	public ContentResourceEdit putDeleteResource(String id, String uuid, String userId) {
    return null;
	}

	/**
	 * remove the supplied collection
	 * 
	 * @param edit
	 */
	public void removeCollection(ContentCollectionEdit edit) {
	}

	/**
	 * remove the resource
	 * 
	 * @param edit
	 */
	public void removeResource(ContentResourceEdit edit) {
	}

	/**
	 * stream the body of the resource
	 * 
	 * @param resource
	 * @return
	 * @throws ServerOverloadException
	 */
	public InputStream streamResourceBody(ContentResource resource) throws ServerOverloadException {
    log.info(LOG_MARKER + "streamResourceBody : id = " + resource.getId());
    return null;
	}

	/**
	 * get the number of members
	 * @param ce
	 * @return
	 */
	public int getMemberCount(ContentEntity ce) {
    log.info(LOG_MARKER + "getMemberCount : id = " + ce.getId());

    if (ce instanceof ContentCollectionFedora)
      return ((ContentCollectionFedora)ce).getMemberCount();

    if (ce.getVirtualContentEntity() instanceof ContentCollectionFedora)
      return ((ContentCollectionFedora)(ce.getVirtualContentEntity())).getMemberCount();
    
    return 0;
  }

	/**
	 * @param ce
	 * @return
	 */
	public Collection<String> getMemberCollectionIds(ContentEntity ce) {
    log.info(LOG_MARKER + "getMemberCollectionIds : id = " + ce.getId());
    return null;
	}

	/**
	 * @param ce
	 * @return
	 */
	public Collection<String> getMemberResourceIds(ContentEntity ce) {
    log.info(LOG_MARKER + "getMemberResourceIds : id = " + ce.getId());
    return null;
	}

	/**
	 * @param thisResource
	 * @param new_id
	 * @return
	 */
	public String moveResource(ContentResourceEdit thisResource, String new_id) {
		return null;
	}

	/**
	 * @param thisCollection
	 * @param new_folder_id
	 * @return
	 */
	public String moveCollection(ContentCollectionEdit thisCollection, String new_folder_id) {
		return null;
	}

	/**
	 * @param resourceId
	 * @param uuid
	 * @return
	 */
	 public void setResourceUuid(String resourceId, String uuid) {
    log.info(LOG_MARKER + "setResourceUuid : resourceId = " + resourceId + " uuid = " + uuid);
   }

	/**
	 * @param id
	 */
	public void getUuid(String id) {
	}

}
