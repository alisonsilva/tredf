package br.jus.tredf.justicanumeros.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.springframework.stereotype.Controller;

@Controller
@SuppressWarnings("all")
public class PropertiesServiceController implements Serializable {
  private static final long                  serialVersionUID = 1L;
  private static PropertiesServiceController PROPS;

  private Properties                          prop;

  /**
   * Builds auxiliary class to control properties information.
   */
  private PropertiesServiceController() {
    if(prop == null) {
      prop = new Properties();
      try {
        prop.load(this.getClass().getResourceAsStream("/formstredf.properties"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Static method to get an instance of the properties controller.
   * 
   * @return Instance of properties controller.
   */
  public static PropertiesServiceController getInstance() {
    if (PROPS == null) {
      PROPS = new PropertiesServiceController();
      PROPS.prop = new Properties();
      try {
        PROPS.prop.load(PROPS.getClass().getResourceAsStream("/formstredf.properties"));
      } catch (IOException except) {
        except.printStackTrace();
      }      
    }
    return PROPS;
  }

  public String getProperty(String propriedade) {
    return prop.getProperty(propriedade);
  }
}
