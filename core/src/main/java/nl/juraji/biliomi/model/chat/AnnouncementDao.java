package nl.juraji.biliomi.model.chat;

import nl.juraji.biliomi.utility.jpa.JpaDao;

/**
 * Created by Juraji on 21-5-2017.
 * Biliomi v3
 */
public class AnnouncementDao extends JpaDao<Announcement> {
  public AnnouncementDao() {
    super(Announcement.class);
  }

  public void create(String message) {
    Announcement announcement = new Announcement();
    announcement.setMessage(message);
    save(announcement);
  }
}
