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

public class ContentEntityFedora implements ContentEntity {
	/**
	 * Access this ContentEntity's containing collection, or null if this entity is the site collection.
	 * @return
	 */
	public ContentCollection getContainingCollection() {
		return null;
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
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public ContentHostingHandlerImplFedora getContentHandler() {
		return new ContentHostingHandlerImplFedora();
	}
	
	/**
	 * 
	 * @param chh
	 */
	public void setContentHandler(ContentHostingHandler chh) {
	}
	
	/**
	 * 
	 * @return
	 */
	public ContentEntity getVirtualContentEntity() {
		return null;
	}
	
	/**
	 * 
	 * @param ce
	 */
	public void setVirtualContentEntity(ContentEntity ce) {
	}
	
	/**
	 * 
	 * @param nextId
	 * @return
	 */
	public ContentEntity getMember(String nextId) {
		return null;
	}
	
	/**
	 * Access the URL which can be used to access the entity. Will return a relative or absolute url, 
	 * depending on value of the parameter. If parameter is true, URL will be relative to the server's
	 * root.  Otherwise, it will be a complete URL starting with the base URL of the server. 
	 * @param relative 
	 * @return The URL which can be used to access the resource.
	 */
	public String getUrl(boolean relative) {
		return null;
	}
	
}
