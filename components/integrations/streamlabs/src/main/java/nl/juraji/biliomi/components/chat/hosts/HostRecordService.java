package nl.juraji.biliomi.components.chat.hosts;

import nl.juraji.biliomi.model.chat.HostRecord;
import nl.juraji.biliomi.model.chat.HostRecordDao;
import nl.juraji.biliomi.model.core.Direction;
import nl.juraji.biliomi.model.core.User;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 18-5-2017.
 * Biliomi v3
 */
@Default
public class HostRecordService {

  @Inject
  private HostRecordDao hostRecordDao;

  public DateTime getPreviousHostInDate(User channel) {
    HostRecord record = hostRecordDao.getLatestRecord(channel, Direction.INCOMING);
    return (record == null ? null : record.getDate());
  }

  public void recordIncomingHost(User channel, boolean isAutoHost) {
    HostRecord record = new HostRecord();
    record.setUser(channel);
    record.setAutoHost(isAutoHost);
    record.setDate(DateTime.now());
    record.setDirection(Direction.INCOMING);
    hostRecordDao.save(record);
  }

  public void recordOutgoingHost(User channel) {
    HostRecord record = new HostRecord();
    record.setUser(channel);
    record.setAutoHost(false);
    record.setDate(DateTime.now());
    record.setDirection(Direction.OUTGOING);
    hostRecordDao.save(record);
  }
}
