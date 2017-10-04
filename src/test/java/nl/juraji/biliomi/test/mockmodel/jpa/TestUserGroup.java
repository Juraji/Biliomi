package nl.juraji.biliomi.test.mockmodel.jpa;

import nl.juraji.biliomi.model.core.UserGroup;

/**
 * Created by Juraji on 8-5-2017.
 * Biliomi v3
 */
public class TestUserGroup extends UserGroup {
  public TestUserGroup(int i) {
    this.setId(i);
    this.setName("TestGroup" + i);
    this.setWeight(i);
    this.setDefaultGroup(false);
  }
}
