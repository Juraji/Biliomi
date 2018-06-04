package nl.juraji.biliomi.components.system.cli;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.security.ApiLogin;
import nl.juraji.biliomi.model.core.security.ApiSecuritySettings;
import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRoute;
import nl.juraji.biliomi.utility.security.JWTGenerator;
import nl.juraji.biliomi.utility.security.PasswordEncryptor;
import nl.juraji.biliomi.utility.types.MutableString;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
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

    @CliCommandRoute(command = "apigenerateapptoken", description = "Generate a new application token")
    public boolean apiGenerateAppToken(ConsoleInputEvent event) {
        final List<String> inputSplit = event.getInputSplit();

        if (inputSplit.size() < 2) {
            logger.info("Usage /apigenerateapptoken [application name]");
            return false;
        }

        final JWTGenerator generator = CDI.current().select(JWTGenerator.class).get();
        final ApiSecuritySettings settings = settingsService.getSettings(ApiSecuritySettings.class);
        String applicationName = event.getInput().split(" ", 1)[1];

        final String token = generator.generateApplicationToken(settings.getSecret(), applicationName);

        logger.info("New application token for {}: {}", applicationName, token);
        logger.info("Keep this token ABSOLUTELY SECRET, as it grants read access to ALL endpoints and NEVER expires");
        logger.info("If you wish to invalidate this token, regenerate your apisecret using \"/apiresetsecret\"");

        return false;
    }

    @CliCommandRoute(command = "apiresetsecret", description = "Reset the api token secret, invalidating ALL current tokens")
    public boolean apiResetSecret(ConsoleInputEvent event) {
        final List<String> inputSplit = event.getInputSplit();

        if (inputSplit.size() < 2 || !"YES".equals(inputSplit.get(1))) {
            logger.info("This will reset the api security secret, invalidating ALL current authorization and refresh tokens!");
            logger.info("Usage: /apiresetsecret YES");
            return false;
        }

        final ApiSecuritySettings settings = settingsService.getSettings(ApiSecuritySettings.class);
        settings.setDefaultValues();
        settingsService.save(settings);

        logger.info("Api security secret reset, ALL tokens are now invalidated");
        return true;
    }
}
