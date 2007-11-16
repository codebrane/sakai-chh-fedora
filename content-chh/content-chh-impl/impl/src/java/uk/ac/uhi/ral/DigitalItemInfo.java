/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral;

public interface DigitalItemInfo {
  public void setDisplayName(String displayName);
  public String getDisplayName();

  public void setCreator(String creator);
  public String getCreator();

  public void setModifiedDate(String modifiedDate);
  public String getModifiedDate();

  public void setOriginalFilename(String originalFilename);
  public String getOriginalFilename();

  public void setDescription(String description);
  public String getDescription();

  public void setType(String type);
  public String getType();

  public void setContentLength(long contentLength);
  public long getContentLength();

  public void setIsResource(boolean resource);
  public void setIsCollection(boolean collection);

  public boolean isResource();
  public boolean isCollection();

  public void setPrivateInfo(Object privateInfo);
  public Object getPrivateInfo();
}
