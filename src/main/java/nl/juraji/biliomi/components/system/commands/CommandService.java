package nl.juraji.biliomi.components.system.commands;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import nl.juraji.biliomi.model.core.*;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.commandrouters.CommandRouterRegistry;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by Juraji on 4-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class CommandService {
  private final Table<Long, Long, DateTime> cooldownRegister = HashBasedTable.create();

  @Inject
  private CommandDao commandDao;

  @Inject
  private CustomCommandDao customCommandDao;

  @Inject
  private CommandRouterRegistry commandRouterRegistry;

  public Command getCommand(String command) {
    return commandDao.getCommand(command);
  }

  public CustomCommand getCustomCommand(String command) {
    return customCommandDao.getCommand(command);
  }

  public List<CustomCommand> getAllCustomCommands() {
    return customCommandDao.getList();
  }

  public boolean commandExists(String command) {
    return commandRouterRegistry.containsKey(command);
  }

  public String translateAlias(String command) {
    return commandRouterRegistry.translateAlias(command);
  }

  public void save(Command entity) {
    commandDao.save(entity);
  }

  public void delete(Command entity) {
    commandDao.delete(entity);
  }

  public void registerAlias(String alias, String command) {
    Command commandObj = commandDao.getCommand(command);
    if (commandObj != null) {
      registerAlias(alias, commandObj);
    }
  }

  public boolean registerAlias(String alias, Command command) {
    if (commandRouterRegistry.containsKey(alias)) {
      return false;
    }

    command.getAliasses().add(alias);
    commandDao.save(command);
    commandRouterRegistry.putAlias(alias, command.getCommand());
    return true;
  }

  public void removeAlias(String alias) {
    Command commandObj = commandDao.getByAlias(alias);
    removeAlias(alias, commandObj);
  }

  public void removeAlias(String alias, Command command) {
    commandRouterRegistry.removeAlias(alias);

    if (command != null) {
      command.getAliasses().remove(alias);
      commandDao.save(command);
    }
  }

  public void removeAllAliasses(Command command) {
    if (command != null) {
      command.getAliasses().forEach(alias -> commandRouterRegistry.removeAlias(alias));
      command.getAliasses().clear();
      commandDao.save(command);
    }
  }

  public void setCooldown(User user, Command command) {
    DateTime expiryDT = new DateTime().withDurationAdded(command.getCooldown(), 1);
    cooldownRegister.put(user.getId(), command.getId(), expiryDT);
  }

  public long getRemainingCooldown(User user, Command command) {
    DateTime expiry = cooldownRegister.get(user.getId(), command.getId());
    long remaining = 0;

    if (expiry != null) {
      remaining = calculateRemaining(expiry);
      if (remaining <= 0) {
        clearCooldown(user, command);
      }
    }

    return remaining;
  }

  public void clearCooldown(User user, Command command) {
    cooldownRegister.remove(user.getId(), command.getId());
  }

  public void clearCooldownFor(User user) {
    if (cooldownRegister.containsRow(user.getId())) {
      cooldownRegister.row(user.getId()).clear();
    }
  }

  private long calculateRemaining(DateTime expiry) {
    return MathUtils.minMax(new Duration(DateTime.now(), expiry).getMillis(), 0, Long.MAX_VALUE);
  }
}
