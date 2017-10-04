package nl.juraji.biliomi.boot;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
public interface SetupTask {

  /**
   * Called when update mode is set to INSTALL
   */
  void install();

  /**
   * Called when update mode is set to UPDATE
   * By default it calls this.install()
   */
  default void update() {
    this.install();
  }

  /**
   * @return The name to display in the console when running this task
   */
  default String getDisplayName() {
    return Joiner.on(' ').join(StringUtils.splitByCharacterTypeCamelCase(getClass().getSimpleName()));
  }
}
