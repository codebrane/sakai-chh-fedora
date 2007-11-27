/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl;

import uk.ac.uhi.ral.DigitalItemInfo;

public class FedoraItemInfo implements DigitalItemInfo {
  private String title = null;
  private String subject = null;
  private String description = null;
  private String publisher = null;
  private String identifier = null;
  private String mimeType = null;
  private byte[] binaryContent = null;
  private String displayName = null;
  private String creator = null;
  private String modifiedDate = null;
  private String originalFilename = null;
  private String type = null;
  private boolean resource;
  private boolean collection;
  private Object privateInfo;
  private String url = null;

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getSubject() {
    return subject;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setBinaryContent(byte[] binaryContent) {
    this.binaryContent = binaryContent;
  }

  public byte[] getBinaryContent() {
    return binaryContent;
  }

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

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public int getContentLength() {
    return binaryContent.length;
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

  public void setURL(String url) {
    this.url = url;
  }

  public String getURL() {
    return url;
  }
}
