/* CVS Header
   $
   $
*/

package uk.ac.uhi.ral.impl;

import uk.ac.uhi.ral.DigitalRepositoryFactory;
import uk.ac.uhi.ral.DigitalRepository;

public class FedoraDigitalRepositoryFactory implements DigitalRepositoryFactory {
  private String keystorePath = null;
  private String keystorePassword = null;
  private String truststorePath = null;
  private String truststorePassword = null;

  public DigitalRepository create() {
    return new FedoraDigitalRepositoryImpl(keystorePath, keystorePassword, truststorePath, truststorePassword);
  }

  // Spring injection
  public void setKeystorePath(String keystorePath) { this.keystorePath = keystorePath; }
  public void setKeystorePassword(String keystorePassword) { this.keystorePassword = keystorePassword; }
  public void setTruststorePath(String truststorePath) { this.truststorePath = truststorePath; }
  public void setTruststorePassword(String truststorePassword) { this.truststorePassword = truststorePassword; }
}
