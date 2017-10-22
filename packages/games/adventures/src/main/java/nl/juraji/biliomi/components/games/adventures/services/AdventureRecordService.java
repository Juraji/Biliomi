package nl.juraji.biliomi.components.games.adventures.services;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.AdventureRecord;
import nl.juraji.biliomi.model.games.AdventureRecordDao;
import nl.juraji.biliomi.model.games.UserAdventureRecordStats;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
@Default
public class AdventureRecordService {

  @Inject
  private AdventureRecordDao adventureRecordDao;

  public void recordAdventureRun(User user, long bet, long payout, boolean isByTamagotchi) {
    AdventureRecord record = new AdventureRecord();
    record.setAdventurer(user);
    record.setBet(bet);
    record.setPayout(payout);
    record.setByTamagotchi(isByTamagotchi);
    record.setDate(DateTime.now());
    adventureRecordDao.save(record);
  }

  public long getRecordCount(User user) {
    return adventureRecordDao.getRecordCount(user);
  }

  public UserAdventureRecordStats getRecordInfo(User user) {
    List<AdventureRecord> records = adventureRecordDao.getRecords(user);

    if (records.size() == 0) {
      return null;
    }

    long losses = records.stream().filter(record -> record.getPayout() == 0).count();
    long wins = records.stream().filter(record -> record.getPayout() > 0).count();
    long totalPayout = records.stream().mapToLong(AdventureRecord::getPayout).sum();
    long byTamagotchiCount = records.stream().filter(AdventureRecord::isByTamagotchi).filter(adventureRecord -> adventureRecord.getPayout() > 0).count();

    return new UserAdventureRecordStats(records.size(), losses, wins, totalPayout, byTamagotchiCount);
  }
}
