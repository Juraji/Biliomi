package nl.juraji.biliomi.components.chat.chatmoderator;

import nl.juraji.biliomi.model.chat.ModerationRecordDao;
import nl.juraji.biliomi.model.chat.ModerationAction;
import nl.juraji.biliomi.model.chat.ModerationReason;
import nl.juraji.biliomi.model.chat.ModerationRecord;
import nl.juraji.biliomi.model.core.User;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by Juraji on 14-5-2017.
 * Biliomi v3
 */
@Default
public class ModerationRecordService {

  @Inject
  private ModerationRecordDao moderationRecordDao;

  public void recordModeration(User user, ModerationAction action) {
    recordModeration(user, ModerationReason.UNKNOWN, action, null);
  }

  public void recordModeration(User user, ModerationReason reason, ModerationAction action, String message) {
    ModerationRecord record = new ModerationRecord();
    record.setUser(user);
    record.setReason(reason);
    record.setAction(action);
    record.setMessage(message);
    record.setDate(DateTime.now());
    moderationRecordDao.save(record);
  }

  public List<ModerationRecord> getRecords(User user) {
    return moderationRecordDao.getRecords(user);
  }
}
