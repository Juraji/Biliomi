package nl.juraji.biliomi.utility.types.collections;

import nl.juraji.biliomi.utility.estreams.EBiStream;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.enums.OnOff;
import nl.juraji.biliomi.utility.types.enums.StreamState;

import javax.enterprise.inject.Vetoed;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 8-5-2017.
 * Biliomi v3
 */
@Vetoed // Do not inject directly, use producer instead
public class I18NMapImpl extends Properties implements I18nMap {

    public I18NMapImpl() {
        // Default constructor for CDI spec compliance
    }

    public I18NMapImpl(Properties defaults) {
        super.putAll(defaults);
    }

    @Override
    public Templater get(String key) {
        return Templater.template(getString(key));
    }

    @Override
    public String getString(String key) {
        if (!this.containsKey(key)) {
            throw new IllegalArgumentException("Key " + key + " does not exist");
        }

        return this.getProperty(key);
    }

    @Override
    public Supplier<String> supply(String key) {
        return () -> getString(key);
    }

    @Override
    public List<String> getKeyStartsWith(String keyPrefix) {
        return EBiStream.from(this)
                .filterKey(key -> ((String) key).startsWith(keyPrefix))
                .map((key, value) -> (String) value)
                .collect(Collectors.toList());
    }

    @Override
    public String getIfElse(boolean state, String trueKey, String falseKey) {
        return state ? getString(trueKey) : getString(falseKey);
    }

    @Override
    public String getEnabledDisabled(OnOff state) {
        return getEnabledDisabled(OnOff.ON.equals(state));
    }

    @Override
    public String getEnabledDisabled(boolean state) {
        return getIfElse(state, "Common.state.enabled", "Common.state.disabled");
    }

    @Override
    public String getAllowedDisallowed(boolean state) {
        return getIfElse(state, "Common.allow.allowed", "Common.allow.disallowed");
    }

    @Override
    public String getStreamState(StreamState streamState) {
        return getIfElse(StreamState.ONLINE.equals(streamState), "Common.stream.online", "Common.stream.offline");
    }

    @Override
    public String getUserNonExistent(String username) {
        return this.get("Common.user.nonExistent")
                .add("username", username)
                .apply();
    }

    @Override
    public String getCommandNonExistent(String command) {
        return this.get("Common.command.nonExistent")
                .add("command", command)
                .apply();
    }

    @Override
    public String getGroupNonExistent(String groupName) {
        return this.get("Common.groups.nonExistent")
                .add("groupname", groupName)
                .apply();
    }

    @Override
    public String getInputContainsBadWords() {
        return getString("Common.input.containsBadWords");
    }

    @Override
    public String getGenericError(String username, String command, String errorMessage) {
        return this.get("Common.errors.catchedFatalError")
                .add("username", username)
                .add("command", command)
                .add("errormessage", errorMessage)
                .apply();
    }
}
