package nl.juraji.biliomi.components.shared;

import nl.juraji.biliomi.components.interfaces.enums.OnOff;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.types.collections.L10nMap;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Juraji on 10-10-2017.
 * Biliomi
 * <p>
 * Used by commands to setup or change component templates
 */
public class TemplateSetup {

  private final TemplateDao templateDao;
  private final ChatService chat;
  private final L10nMap l10n;
  private String commandUsageKey;
  private String templateDisabledKey;
  private String templatedSavedKey;

  public TemplateSetup(TemplateDao templateDao, ChatService chat, L10nMap l10n) {
    this.templateDao = templateDao;
    this.chat = chat;
    this.l10n = l10n;
  }

  /**
   * @param l10nKey The l10n key to whisper to the user when input is empty
   * @return this
   */
  public TemplateSetup withCommandUsageKey(String l10nKey) {
    this.commandUsageKey = l10nKey;
    return this;
  }

  /**
   * @param l10nKey The l10n key to whisper to the user when the template is disabled by using "OFF"
   *                Note: Omit usage of this setter to disable disabling the template
   * @return this
   */
  public TemplateSetup withTemplateDisabledKey(String l10nKey) {
    this.templateDisabledKey = l10nKey;
    return this;
  }

  /**
   * @param l10nKey The l10n key to whisper to the user when the template has been updated
   * @return this
   */
  public TemplateSetup withTemplatedSavedKey(String l10nKey) {
    this.templatedSavedKey = l10nKey;
    return this;
  }

  /**
   * Apply this factory to a template
   * @param user The caller's (used to send whispers)
   * @param input The caller's input
   * @param templateKey The template key of the template to update
   * @return True on command success else False
   */
  public boolean apply(User user, String input, String templateKey) {
    if (StringUtils.isEmpty(input)) {
      chat.whisper(user, l10n.get(commandUsageKey));
      return false;
    }

    Template template = templateDao.getByKey(templateKey);
    if (templateDisabledKey == null) {
      updateTemplate(user, template, input);
    } else {
      OnOff onOff = EnumUtils.toEnum(input, OnOff.class);
      assert template != null; // Template cannot be null since it's set during install/update
      if (OnOff.OFF.equals(onOff)) {
        disableTemplate(user, template);
      } else {
        updateTemplate(user, template, input);
      }
    }

    return true;
  }

  private void updateTemplate(User user, Template template, String input) {
    template.setTemplate(input);
    templateDao.save(template);
    chat.whisper(user, l10n.get(templatedSavedKey));
  }

  private void disableTemplate(User user, Template template) {
    template.setTemplate("");
    templateDao.save(template);
    chat.whisper(user, l10n.get(templateDisabledKey));
  }
}
