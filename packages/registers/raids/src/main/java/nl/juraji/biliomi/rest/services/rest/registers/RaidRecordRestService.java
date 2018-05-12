package nl.juraji.biliomi.rest.services.rest.registers;

import nl.juraji.biliomi.model.registers.RaidRecord;
import nl.juraji.biliomi.model.registers.RaidRecordDao;
import nl.juraji.biliomi.rest.config.ModelRestService;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/registers/raidrecords")
public class RaidRecordRestService extends ModelRestService<RaidRecord> {

    @Inject
    private RaidRecordDao raidRecordDao;

    @Override
    public List<RaidRecord> getEntities() {
        return raidRecordDao.getList();
    }

    @Override
    public RaidRecord getEntity(long id) {
        throw new ForbiddenException();
    }

    @Override
    public RaidRecord createEntity(RaidRecord e) {
        raidRecordDao.save(e);
        return e;
    }

    @Override
    public RaidRecord updateEntity(RaidRecord e, long id) {
        throw new ForbiddenException();
    }

    @Override
    public boolean deleteEntity(long id) {
        RaidRecord raidRecord = raidRecordDao.get(id);

        if (raidRecord == null) {
            return false;
        }

        raidRecordDao.delete(raidRecord);
        return true;
    }
}
