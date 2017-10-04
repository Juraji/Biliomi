package nl.juraji.biliomi.test.mockmodel.jpa;

import nl.juraji.biliomi.model.core.User;

/**
 * Created by Juraji on 8-5-2017.
 * Biliomi v3
 */
public class TestUser extends User {
  public TestUser(int i) {
    this.setUsername("testuser" + i);
    this.setDisplayName("TestUser" + i);
    this.setTwitchUserId(i);
    this.setUserGroup(new TestUserGroup(i));
    this.setRecordedTime(0);
    this.setPoints(0);
    this.setCaster(false);
    this.setModerator(false);
    this.setFollower(false);
  }
}
