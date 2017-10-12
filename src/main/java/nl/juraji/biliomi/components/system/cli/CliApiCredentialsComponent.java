package nl.juraji.biliomi.components.system.cli;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.model.core.security.ApiLogin;
import nl.juraji.biliomi.model.core.security.ApiSecuritySettings;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRoute;
import nl.juraji.biliomi.utility.security.PasswordEncryptor;
import nl.juraji.biliomi.utility.types.MutableString;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * Created by Juraji on 16-6-2017.
 * Biliomi v3
 */
@Default
@Singleton
@SystemComponent
public class CliApiCredentialsComponent extends Component {

  @Inject
  private SettingsService settingsService;

  @Inject
  private UsersService usersService;

  @CliCommandRoute(command = "apilistusers", description = "List REST api users")
  public boolean apiListUsersCommand(ConsoleInputEvent event) {
    ApiSecuritySettings settings = settingsService.getSettings(ApiSecuritySettings.class);
    MutableString output = new MutableString("Current REST api users:").appendNewLine();

    if (settings.getLogins().isEmpty()) {
      logger.info("No REST api credentials added yet");
      return false;
    }

    settings.getLogins().stream()
        .map(apiLogin -> apiLogin.getUser().getUsername())
        .forEach(username -> output.appendSpace(2).append(username).appendNewLine());

    logger.info(output.toString());
    return true;
  }

  @CliCommandRoute(command = "apiadduser", description = "Add a REST api user")
  public boolean apiAddUserCommand(ConsoleInputEvent event) {
    List<String> inputSplit = event.getInputSplit();

    if (inputSplit.size() < 3) {
      logger.info("Usage /apiadduser [Twitch username] [password]");
      return false;
    }

    String username = inputSplit.get(1);
    String password = inputSplit.get(2);
    User user = usersService.getUser(username);

    if (user == null) {
      logger.info("No user found by username \"{}\", make sure to use the Twitch username!", username);
      return false;
    }

    if (!user.isCaster() && !user.isModerator()) {
      logger.info("User \"{}\" is not a caster or moderator, they can not have api credentials!", username);
      return false;
    }

    try {
      PasswordEncryptor encryptor = new PasswordEncryptor();
      byte[] salt = encryptor.generateSalt();
      byte[] encryptedPassword = encryptor.encrypt(password, salt);

      ApiLogin credentials = new ApiLogin();
      credentials.setUser(user);
      credentials.setPassword(encryptedPassword);
      credentials.setSalt(salt);

      ApiSecuritySettings settings = settingsService.getSettings(ApiSecuritySettings.class);
      settings.getLogins().add(credentials);

      settingsService.save(settings);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      logger.error("Failed encrypting password", e);
      return false;
    }

    logger.info("Successfully saved {}'s credentials for the REST api", user.getDisplayName());
    return true;
  }

  @CliCommandRoute(command = "apideleteuser", description = "Delete a REST api user")
  public boolean apiDeleteCommand(ConsoleInputEvent event) {
    List<String> inputSplit = event.getInputSplit();

    if (inputSplit.size() < 2) {
      logger.info("Usage /apideleteuser [username]");
      return false;
    }

    ApiSecuritySettings settings = settingsService.getSettings(ApiSecuritySettings.class);
    String username = inputSplit.get(1);

    ApiLogin credentials = settings.getLogins().stream()
        .filter(apiLogin -> username.equalsIgnoreCase(apiLogin.getUser().getUsername()))
        .findFirst()
        .orElse(null);

    if (credentials == null) {
      logger.info("No credentials found for username \"{}\"", username);
      return false;
    }

    settings.getLogins().remove(credentials);
    settingsService.save(settings);

    logger.info("Successfully deleted {}'s credentials for the REST api", credentials.getUser().getDisplayName());
    return true;
  }
}
