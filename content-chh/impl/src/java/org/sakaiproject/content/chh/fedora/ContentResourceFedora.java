/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/content/trunk/content-api/api/src/java/org/sakaiproject/content/api/ContentResource.java $
 * $Id: ContentResource.java 13360 2006-08-03 21:35:13Z jimeng@umich.edu $
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

import java.io.InputStream;

import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.exception.ServerOverloadException;

/**
* <p>ContentResource is the core interface for a Resource object in the GenericContentHostingService.</p>
*/
public class ContentResourceFedora implements ContentResource {
	/**
	* Access the content byte length.
	* @return The content byte length.
	*/
	public int getContentLength() {
		return -1;
	}

	/**
	* Access the resource MIME type.
	* @return The resource MIME type.
	*/
	public String getContentType() {
		return null;
	}

	/**
	* Access an array of the bytes of the resource.
	* @return An array containing the bytes of the resource's content.
	* @exception ServerOverloadException
	* 			if server is configured to save resource body in filesystem and an error occurs while 
	* 			trying to access the filesystem.
	*/
	public byte[] getContent() throws ServerOverloadException {
		return null;
	}

	/**
	 * Access the content as a stream.
	 * Please close the stream when done as it may be holding valuable system resources.
	 * @return an InputStream through which the bytes of the resource can be read.
	 * @throws ServerOverloadException if the server cannot produce the content stream at this time.
	 */
	public InputStream streamContent() throws ServerOverloadException {
		return null;
	}
	
}	// ContentResource



