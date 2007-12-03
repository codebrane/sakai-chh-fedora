/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/content/trunk/content-api/api/src/java/org/sakaiproject/content/api/ContentCollection.java $
 * $Id: ContentCollection.java 19673 2006-12-18 19:26:26Z jimeng@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.content.chh.fedora;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.*;
import org.sakaiproject.entity.api.Edit;
import org.sakaiproject.entity.api.ResourceProperties;
import uk.ac.uhi.ral.DigitalItemInfo;
import uk.ac.uhi.ral.DigitalRepository;
import uk.ac.uhi.ral.impl.fedora.FedoraPrivateItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
* <p>ContentCollection is the core interface for a Collection object in the GenericContentHostingService.</p>
* <p>A Collection has a list of internal members, each a resource id.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 19673 $
*/
public class ContentCollectionFedora extends ContentEntityFedora implements ContentCollection {
  private static final String LOG_MARKER = "[CTREP:ContentCollectionFedora] ";

  private static final Log log = LogFactory.getLog(ContentCollectionFedora.class);

  public ContentCollectionFedora(ContentEntity realParent, String relativePath,
                                 ContentHostingHandler chh,
                                 ContentHostingHandlerResolver chhResolver,
                                 DigitalRepository repo,
                                 DigitalItemInfo item) {
    super(realParent, (relativePath.length() == 0 || relativePath.charAt(relativePath
						.length() - 1) != '/') ? relativePath + "/" : relativePath, chh, chhResolver, repo, item);
  }

  public Edit wrap() {
    if (wrapped == null) {
      wrapped = chhResolver.newCollectionEdit(join(realParent.getId(), relativePath));
      ((ContentEntity)wrapped).setContentHandler(chh);
      ((ContentEntity)wrapped).setVirtualContentEntity(this);
      ((GroupAwareEdit)wrapped).setResourceType(this.getResourceType());
      wrapped.getProperties().addAll(((Edit)realParent).getProperties());
      wrapped.getProperties().removeProperty(ContentHostingHandlerResolver.CHH_BEAN_NAME);
      setVirtualProperties();
    }

    return wrapped;
  }

  protected void setVirtualProperties() {
    wrapped.getProperties().addProperty(ResourceProperties.PROP_DISPLAY_NAME, item.getDisplayName());
    wrapped.getProperties().addProperty(ResourceProperties.PROP_CREATOR, item.getCreator());
    wrapped.getProperties().addProperty(ResourceProperties.PROP_MODIFIED_DATE, item.getModifiedDate());
    wrapped.getProperties().addProperty(ResourceProperties.PROP_ORIGINAL_FILENAME, relativePath);
    wrapped.getProperties().addProperty(ResourceProperties.PROP_DESCRIPTION, item.getDescription());

    // collection-only properties
    wrapped.getProperties().addProperty(ResourceProperties.PROP_IS_COLLECTION, Boolean.TRUE.toString());
  }

  public boolean isResource() {
    return false;
  }

  public boolean isCollection() {
    return true;
  }

  /**
	* Access a List of the collection's internal members, each a resource id String.
	* @return a List of the collection's internal members, each a resource id String (may be empty).
	*/
	public List getMembers() {
    // do the fedora ws search here - but from which repo?
    log.info(LOG_MARKER + "getMembers");

    DigitalItemInfo[] items = repo.list();

    List<String> members = new ArrayList<String>(items.length);
    for (DigitalItemInfo item : items) {
      members.add(((FedoraPrivateItemInfo)(item.getPrivateInfo())).getPid());
    }
    
    return members;
  }

  /**
	* Access a List of the collections' internal members as full ContentResource or
	* ContentCollection objects.
	* @return a List of the full objects of the members of the collection.
	*/
	public List getMemberResources() {
    log.info(LOG_MARKER + "getMemberResources");

    List members = getMembers();

    List<Edit> resources = new ArrayList<Edit>(members.size());

    for (Object obj : members) {
      String pid = (String)obj;
      ContentEntity ce = getMember(pid);
      if (ce instanceof ContentResource) {
        resources.add(((ContentResourceFedora)ce).wrap());
      }
    }

    return resources;
	}

	/**
	* Access the size of all the resource body bytes within this collection in Kbytes.
	* @return The size of all the resource body bytes within this collection in Kbytes.
	*/
	public long getBodySizeK() {
    log.info(LOG_MARKER + "getBodySizeK");
    return -1;
	}
	
	/**
	 * Access a count of the number of members (resources and collections) within this
	 * collection.  This count is not recursive.  Only items whose immediate parent is
	 * the current collection are counted.
	 * @return
	 */
	public int getMemberCount() {
    log.info(LOG_MARKER + "getMemberCount : returning " + getMembers().size());
    return getMembers().size();
	}
	
  public String getResourceType() {
    return ResourceType.TYPE_FOLDER;
  }

}



