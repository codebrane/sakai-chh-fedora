/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl;

import uk.ac.uhi.ral.DigitalItemInfo;

public class FedoraItemInfo implements DigitalItemInfo {
  private String displayName = null;
  private String creator = null;
  private String modifiedDate = null;
  private String originalFilename = null;
  private String description = null;
  private String type = null;
  private long contentLength;
  private boolean resource;
  private boolean collection;
  private Object privateInfo;

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getCreator() {
    return creator;
  }

  public void setModifiedDate(String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedDate() {
    return modifiedDate;
  }

  public void setOriginalFilename(String originalFilename) {
    this.originalFilename = originalFilename;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setContentLength(long contentLength) {
    this.contentLength = contentLength;
  }

  public long getContentLength() {
    return contentLength;
  }

  public void setIsResource(boolean resource) {
    this.resource = resource;
  }

  public void setIsCollection(boolean collection) {
    this.collection = collection;
  }

  public boolean isResource() {
    return resource;
  }

  public boolean isCollection() {
    return collection;
  }

  public void setPrivateInfo(Object privateInfo) {
    this. privateInfo = privateInfo;
  }

  public Object getPrivateInfo() {
    return privateInfo;
  }
}
