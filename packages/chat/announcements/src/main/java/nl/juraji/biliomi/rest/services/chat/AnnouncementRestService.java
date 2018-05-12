package nl.juraji.biliomi.rest.services.chat;

import nl.juraji.biliomi.components.chat.announcements.AnnouncementTimerService;
import nl.juraji.biliomi.model.chat.Announcement;
import nl.juraji.biliomi.model.chat.AnnouncementDao;
import nl.juraji.biliomi.rest.config.ModelRestService;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/chat/announcements")
public class AnnouncementRestService extends ModelRestService<Announcement> {

    @Inject
    private AnnouncementDao announcementDao;

    @Inject
    private AnnouncementTimerService announcementTimerService;

    @Override
    public List<Announcement> getEntities() {
        return announcementDao.getList();
    }

    @Override
    public Announcement getEntity(long id) {
        return announcementDao.get(id);
    }

    @Override
    public Announcement createEntity(Announcement e) {
        announcementDao.save(e);
        announcementTimerService.restart();
        return e;
    }

    @Override
    public Announcement updateEntity(Announcement e, long id) {
        Announcement announcement = announcementDao.get(id);

        if (announcement == null) {
            return null;
        }

        announcement.setMessage(e.getMessage());
        announcementDao.save(announcement);
        announcementTimerService.restart();
        return announcement;
    }

    @Override
    public boolean deleteEntity(long id) {
        Announcement announcement = announcementDao.get(id);

        if (announcement == null) {
            return false;
        }

        announcementDao.delete(announcement);
        announcementTimerService.restart();
        return true;
    }
}
