/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/content/trunk/content-api/api/src/java/org/sakaiproject/content/api/ContentEntity.java $
 * $Id: ContentEntity.java 22313 2007-03-08 04:28:29Z jimeng@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2006 The Sakai Foundation.
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

import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentHostingHandler;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.entity.api.ResourceProperties;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.Stack;

import info.fedora.definitions.x1.x0.types.ObjectFields;

public abstract class ContentEntityFedora implements ContentEntity {
  protected ContentHostingHandler chh = null;
  protected ContentEntity ce = null;
  protected ContentEntity realParent = null;
  protected ObjectFields[] fields = null;

  /**
	 * Access this ContentEntity's containing collection, or null if this entity is the site collection.
	 * @return
	 */
	public ContentCollection getContainingCollection() {
		return realParent.getContainingCollection();
	}
	
	/**
	 * Check whether an entity is a ContentResource.
	 * @return true if the entity implements the ContentResource interface, false otherwise.
	 */
	public boolean isResource() {
		return false;
	}
	
	/**
	 * Check whether an entity is a ContentCollection.
	 * @return true if the entity implements the ContentCollection interface, false otherwise.
	 */
	public boolean isCollection() {
		return false;
	}
	
	/**
	 * Access the "type" of this ContentEntity, which defines which ResourceType registration defines
	 * its properties.
	 * @return
	 */
	public String getResourceType() {
		return realParent.getResourceType();
	}
	
	/**
	 * 
	 * @return
	 */
	public ContentHostingHandler getContentHandler() {
		return chh;
	}
	
	/**
	 * 
	 * @param chh
	 */
	public void setContentHandler(ContentHostingHandler chh) {
    this.chh = chh;
  }
	
	/**
	 * 
	 * @return
	 */
	public ContentEntity getVirtualContentEntity() {
		return ce;
	}
	
	/**
	 * 
	 * @param ce
	 */
	public void setVirtualContentEntity(ContentEntity ce) {
    this.ce = ce;
  }
	
	/**
	 * 
	 * @param nextId
	 * @return
	 */
	public ContentEntity getMember(String nextId) {
		return null;
	}
	
  /* Junk required by GroupAwareEntity superinterface */
  public Collection getGroups() {
    return realParent.getGroups();
  }

  public Collection getGroupObjects() {
    return realParent.getGroupObjects();
  }

  public AccessMode getAccess() {
    return realParent.getAccess();
  }

  public Collection getInheritedGroups() {
    return realParent.getInheritedGroups();
  }

  public Collection getInheritedGroupObjects() {
    return realParent.getInheritedGroupObjects();
  }

  public AccessMode getInheritedAccess() {
    return realParent.getInheritedAccess();
  }

  public Time getReleaseDate() {
    return realParent.getReleaseDate();
  }

  public Time getRetractDate() {
    return realParent.getRetractDate();
  }

  public boolean isHidden() {
    return false;
  }

  public boolean isAvailable() {
    return true;
  }

  public String getUrl() {
    return realParent.getUrl();
  }

  public String getUrl(boolean b) {
    return realParent.getUrl(b);
  }

  public String getReference() {
    return realParent.getReference();
  }

  public String getUrl(String rootProperty) {
    return realParent.getUrl(rootProperty);
  }

  public String getReference(String rootProperty) {
    return realParent.getReference(rootProperty);
  }

  public String getId() {
    return realParent.getId();
  }

  public ResourceProperties getProperties() {
    return null;
  }

  public Element toXml(Document doc, Stack stack) {
    return null;
  }


}
