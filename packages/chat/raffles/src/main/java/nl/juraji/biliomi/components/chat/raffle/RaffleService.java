package nl.juraji.biliomi.components.chat.raffle;

import nl.juraji.biliomi.model.core.User;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

/**
 * Created by Juraji on 28-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class RaffleService {

    private RaffleInstance currentRaffle;
    private User lastPickedUser;

    public void startNewRaffle(String keyword, long joinCost, boolean followersOnly) {
        currentRaffle = new RaffleInstance(keyword, joinCost, followersOnly);
    }

    public String getKeyword() {
        return currentRaffle.getKeyword();
    }

    public boolean isFollowersOnly() {
        return currentRaffle.isFollowersOnly();
    }

    public long getJoinCost() {
        return currentRaffle.getJoinCost();
    }

    public boolean isUnstarted() {
        return currentRaffle == null;
    }

    public boolean isEnded() {
        return currentRaffle.isEnded();
    }

    public User getLastPickedUser() {
        return lastPickedUser;
    }

    public boolean isNonFollowerCannotJoin(User user) {
        return isFollowersOnly() && !user.isFollower();
    }

    public boolean isUserAlreadyJoined(User user) {
        return currentRaffle.hasUser(user);
    }

    public boolean isUserNotHasEnoughPoints(User user) {
        return currentRaffle.getJoinCost() > 0 && user.getPoints() < currentRaffle.getJoinCost();
    }

    public int addUser(User user) {
        return currentRaffle.addUser(user);
    }

    public User endRaffle() {
        lastPickedUser = currentRaffle.endRaffle();
        return lastPickedUser;
    }

    public User repick() {
        lastPickedUser = currentRaffle.repickUser();
        return lastPickedUser;
    }
}
