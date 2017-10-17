package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.commandrouters.types.CommandCall;
import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 22-5-2017.
 * Biliomi v3
 */
@Default
public class CommandHistoryRecordDao extends JpaDao<CommandHistoryRecord> {

  public CommandHistoryRecordDao() {
    super(CommandHistoryRecord.class);
  }

  public void recordCommand(CommandCall commandCall, Command actualCommand, User user, boolean success) {
    CommandHistoryRecord record = new CommandHistoryRecord();

    record.setCommand(actualCommand.getCommand());
    record.setTriggeredBy(commandCall.getCommand());
    record.setUser(user);
    record.setSuccess(success);
    record.setDate(DateTime.now());
    
    if (!commandCall.getArguments().isEmpty()) {
      record.setArguments(commandCall.getArguments().toString());
    }

    save(record);
  }

  public List<CommandHistoryRecord> getLatestHistoryForCommand(String command) {
    return criteria()
        .add(Restrictions.eq("command", command))
        .addOrder(Order.desc("id"))
        .setMaxResults(20)
        .getList();
  }
}
