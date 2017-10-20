package nl.juraji.biliomi.components.games.creativemurders;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.KillRecord;
import nl.juraji.biliomi.model.games.KillRecordDao;
import nl.juraji.biliomi.model.games.UserKDRRecordStats;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 22-5-2017.
 * Biliomi v3
 */
@Default
public class KillRecordService {

  @Inject
  private KillRecordDao killRecordDao;

  public void recordKill(User killer, User target) {
    KillRecord record = new KillRecord();
    record.setKiller(killer);
    record.setTarget(target);
    record.setSuicide(false);
    record.setDate(DateTime.now());
    killRecordDao.save(record);
  }

  public void recordSuicicide(User killer) {
    KillRecord record = new KillRecord();
    record.setKiller(killer);
    record.setTarget(killer);
    record.setSuicide(true);
    record.setDate(DateTime.now());
    killRecordDao.save(record);
  }

  /**
   * Get the kill/death/suicide ratio for a User
   *
   * @param user The User for which to perform the calculation
   * @return A UserKDRRecordStats object initialized with the current records
   */
  public UserKDRRecordStats getKDR(User user) {
    List<KillRecord> records = killRecordDao.getRecords(user);

    if (records.size() > 0) {
      long kills = records.stream()
          .filter(killRecord -> killRecord.getKiller().getId() == user.getId())
          .count();

      long deaths = records.stream()
          .filter(killRecord -> killRecord.getTarget().getId() == user.getId())
          .count();

      long suicides = records.stream()
          .filter(KillRecord::isSuicide)
          .count();

      User favoriteTarget = records.stream()
          .filter(killRecord -> killRecord.getKiller().getId() == user.getId())
          .filter(killRecord -> killRecord.getTarget().getId() != user.getId())
          .collect(Collectors.groupingBy(KillRecord::getTarget))
          .entrySet().stream()
          .sorted((e1, e2) -> e2.getValue().size() - e1.getValue().size())
          .map(Map.Entry::getKey)
          .findFirst()
          .orElse(null);

      return new UserKDRRecordStats(records.size(), kills, deaths, suicides, favoriteTarget);
    }
    
    return null;
  }
}
