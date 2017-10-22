package nl.juraji.biliomi.config.creativemurders;

import java.util.List;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
public class YamlCreativeMurdersConfig {

  private List<String> murders;
  private List<String> suicides;

  public List<String> getMurders() {
    return murders;
  }

  public void setMurders(List<String> murders) {
    this.murders = murders;
  }

  public List<String> getSuicides() {
    return suicides;
  }

  public void setSuicides(List<String> suicides) {
    this.suicides = suicides;
  }
}
