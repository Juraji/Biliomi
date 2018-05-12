package nl.juraji.biliomi.components.system.users;

import nl.juraji.biliomi.model.core.UserGroup;
import nl.juraji.biliomi.model.core.UserGroupDao;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by Juraji on 4-5-2017.
 * Biliomi v3
 */
@Default
public class UserGroupService {
    public static final int MIN_WEIGHT = 0;
    public static final int MAX_WEIGHT = 1000;

    @Inject
    private UserGroupDao userGroupDao;

    public UserGroup getByName(String name) {
        return userGroupDao.getByName(name);
    }

    public List<UserGroup> getList() {
        return userGroupDao.getList();
    }

    public boolean groupExists(String name) {
        return userGroupDao.groupExists(name);
    }

    public boolean groupExists(int weight) {
        return userGroupDao.groupExists(weight);
    }

    public UserGroup getDefaultGroup() {
        return userGroupDao.getDefaultGroup();
    }

    public void save(UserGroup entity) {
        userGroupDao.save(entity);
    }

    public void delete(UserGroup entity) {
        userGroupDao.delete(entity);
    }

    public UserGroup createNewGroup(String name, int weight) {
        if (userGroupDao.groupExists(name) || userGroupDao.groupExists(weight)) {
            throw new IllegalStateException("A group with name \"" + name + "\" and/or weight \"" + weight + "\" already exists");
        }

        UserGroup userGroup = new UserGroup();
        userGroup.setName(name);
        userGroup.setWeight(weight);
        this.save(userGroup);
        return userGroup;
    }

    public List<UserGroup> getTimeBasedGroups() {
        return userGroupDao.getTimeBasedGroups();
    }
}
