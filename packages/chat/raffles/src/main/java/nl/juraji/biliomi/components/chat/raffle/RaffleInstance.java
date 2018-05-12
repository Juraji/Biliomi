package nl.juraji.biliomi.components.chat.raffle;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.calculate.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraji on 28-5-2017.
 * Biliomi v3
 */
public class RaffleInstance {
    private final String keyword;
    private final long joinCost;
    private final boolean followersOnly;
    private final List<User> joinedUsers;
    private final RaffleLogFile raffleLogFile;
    private boolean isEnded;

    public RaffleInstance(String keyword, long joinCost, boolean followersOnly) {
        this.keyword = keyword;
        this.joinCost = joinCost;
        this.followersOnly = followersOnly;
        this.joinedUsers = new ArrayList<>();
        this.raffleLogFile = new RaffleLogFile(keyword, joinCost, followersOnly);
        this.isEnded = false;
    }

    public String getKeyword() {
        return keyword;
    }

    public long getJoinCost() {
        return joinCost;
    }

    public boolean isFollowersOnly() {
        return followersOnly;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public boolean hasUser(User user) {
        return joinedUsers.stream().anyMatch(u -> user.getId() == u.getId());
    }

    public int addUser(User user) {
        if (isEnded) {
            return -1;
        }
        joinedUsers.add(user);
        raffleLogFile.userJoin(user);
        return joinedUsers.size();
    }

    public User endRaffle() {
        this.isEnded = true;
        raffleLogFile.raffleEnd();
        return repickUser();
    }

    public User repickUser() {
        User user = MathUtils.listRand(joinedUsers);

        if (user != null) {
            joinedUsers.remove(user);
            raffleLogFile.userPicked(user);
        }

        return user;
    }
}
