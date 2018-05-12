package nl.juraji.biliomi.rest.services;

import nl.juraji.biliomi.model.games.AchievementRecord;
import nl.juraji.biliomi.model.games.AchievementRecordDao;
import nl.juraji.biliomi.rest.config.ModelRestService;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Path("/games/achievements")
public class AchievementRecordRestService extends ModelRestService<AchievementRecord> {

    @Inject
    private AchievementRecordDao recordDao;

    @Override
    public List<AchievementRecord> getEntities() {
        return recordDao.getList();
    }

    @Override
    public AchievementRecord getEntity(long id) {
        return recordDao.get(id);
    }

    @Override
    public AchievementRecord createEntity(AchievementRecord e) {
        recordDao.save(e);
        return e;
    }

    @Override
    public AchievementRecord updateEntity(AchievementRecord e, long id) {
        return null;
    }

    @Override
    public boolean deleteEntity(long id) {
        AchievementRecord record = recordDao.get(id);

        if (record == null) {
            return false;
        }

        recordDao.delete(record);
        return true;
    }
}
