/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl;

public class FedoraPrivateItemInfo {
  private String pid = null;
  private String ownerId = null;

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }
}
