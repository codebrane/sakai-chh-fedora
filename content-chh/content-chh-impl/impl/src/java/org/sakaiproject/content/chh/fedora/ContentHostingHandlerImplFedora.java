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
import uk.ac.uhi.ral.impl.fedora.FedoraPrivateItemInfo;
import uk.ac.uhi.ral.impl.fedora.util.TypeMapper;
import uk.ac.uhi.ral.impl.fedora.util.TypeResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
    log.info(LOG_MARKER + "cancel:ContentCollectionEdit");
  }

	/**
	 * cancel an edit to a resource ( if this needs to be done )
	 * 
	 * @param edit
	 */
	public void cancel(ContentResourceEdit edit) {
    log.info(LOG_MARKER + "cancel:ContentResourceEdit");
  }

	/**
	 * commit a collection
	 * 
	 * @param edit
	 */
	public void commit(ContentCollectionEdit edit) {
    log.info(LOG_MARKER + "commit:ContentCollectionEdit");
  }

	/**
	 * commit a resource
	 * 
	 * @param edit
	 */
	public void commit(ContentResourceEdit edit) {
    log.info(LOG_MARKER + "commit:ContentResourceEdit");

    ContentResourceFedora crFedora = null;
    if (edit instanceof ContentResourceFedora)
      crFedora = (ContentResourceFedora)edit;
    else {
      if (edit.getVirtualContentEntity() instanceof ContentResourceFedora) {
        crFedora = (ContentResourceFedora)edit.getVirtualContentEntity();
      }
    }
    if (crFedora == null) return;

    crFedora.getRepository().commitObject(TypeMapper.updateDigitalItemInfo(crFedora.getItem(), edit));
  }

	/**
	 * commit a deleted resource
	 * 
	 * @param edit
	 * @param uuid
	 */
	public void commitDeleted(ContentResourceEdit edit, String uuid) {
    log.info(LOG_MARKER + "commitDeleted");
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

    // Find all collections
    String pid = fedoraCollection.getId().substring(fedoraCollection.realParent.getId().length() + 1);
    if (pid.equals(""))
      pid = null;
    DigitalItemInfo[] items = fedoraCollection.getRepository().queryFedora(pid, true, null);
    ContentEntity[] entities = TypeMapper.toContentEntity(items, fedoraCollection.realParent,
                                                          fedoraCollection.getId().substring(fedoraCollection.realParent.getId().length() + 1),
                                                          this, contentHostingHandlerResolver,
                                                          fedoraCollection.getRepository());
    ArrayList<Edit> collections = new ArrayList<Edit>(entities.length);
    for (ContentEntity entity : entities) {
      collections.add(((ContentCollectionFedora)(entity)).wrap());
    }

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
    ContentResourceEdit cre = (ContentResourceEdit)contentHostingHandlerResolver.newResourceEdit(id);
    cre.setContentHandler(this);
    return cre;
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

      ContentEntityFedora entity = (ContentEntityFedora)TypeResolver.resolveEntity(edit, finalId.substring(edit.getId().length()), this,
                                                                                   contentHostingHandlerResolver, repo);
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
    log.info(LOG_MARKER + "putDeleteResource");

    ContentResourceEdit cre = (ContentResourceEdit)contentHostingHandlerResolver.newResourceEdit(id);
    cre.setContentHandler(this);
    return cre;
	}

	/**
	 * remove the supplied collection
	 * 
	 * @param edit
	 */
	public void removeCollection(ContentCollectionEdit edit) {
    log.info(LOG_MARKER + "removeCollection");
  }

	/**
	 * remove the resource
	 * 
	 * @param edit
	 */
	public void removeResource(ContentResourceEdit edit) {
    log.info(LOG_MARKER + "removeResource");

    ContentResourceFedora crf = null;
    if (edit instanceof ContentResourceFedora)
      crf = (ContentResourceFedora)edit;
    else {
      if (edit.getVirtualContentEntity() instanceof ContentResourceFedora) {
        crf = (ContentResourceFedora)edit.getVirtualContentEntity();
      }
    }
    if (crf == null) return;

    crf.getRepository().deleteObject(((FedoraPrivateItemInfo)(crf.getItem().getPrivateInfo())).getPid());
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

    ContentEntity ce = resource.getVirtualContentEntity();
    if (!(ce instanceof ContentResourceFedora)) return null;
    ContentResourceFedora crfd = (ContentResourceFedora) ce;
    return crfd.streamContent();
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
    log.info(LOG_MARKER + "removeResource");
    return null;
	}

	/**
	 * @param thisCollection
	 * @param new_folder_id
	 * @return
	 */
	public String moveCollection(ContentCollectionEdit thisCollection, String new_folder_id) {
    log.info(LOG_MARKER + "moveCollection");
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
    log.info(LOG_MARKER + "getUuid");
  }

}
