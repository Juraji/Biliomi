package nl.juraji.biliomi.components.games.roulette;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.RouletteRecord;
import nl.juraji.biliomi.model.games.RouletteRecordDao;
import nl.juraji.biliomi.model.games.UserRecordStats;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
@Default
public class RouletteRecordService {

    @Inject
    private RouletteRecordDao rouletteRecordDao;

    public void recordRouletteRun(User user, boolean died) {
        RouletteRecord record = new RouletteRecord();
        record.setUser(user);
        record.setFatal(died);
        record.setDate(DateTime.now());
        rouletteRecordDao.save(record);
    }

    public UserRecordStats getRecordInfo(User user) {
        List<RouletteRecord> records = rouletteRecordDao.getRecords(user);
        long losses = records.stream().filter(RouletteRecord::isFatal).count();
        long wins = records.stream().filter(rouletteRecord -> !rouletteRecord.isFatal()).count();

        return new UserRecordStats(records.size(), losses, wins);
    }
}
