package nl.juraji.biliomi.components.system.points;

import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.settings.PointsSettings;
import nl.juraji.biliomi.utility.calculate.MathUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@Default
public class PointsService {

    @Inject
    private SettingsService settingsService;

    @Inject
    private UsersService usersService;
    private PointsSettings settings;

    @PostConstruct
    private void initPointsService() {
        settings = settingsService.getSettings(PointsSettings.class, s -> settings = s);
    }

    /**
     * Take points from a user.
     *
     * @param user   The user to modify
     * @param amount The amount to take
     * @return The user's final balance or -1 if the user did not have enough
     */
    public long take(User user, Number amount) {
        long realAmount = amount.longValue();
        if (user.getPoints() >= realAmount) {
            user.setPoints(user.getPoints() - realAmount);
            usersService.save(user);
            return user.getPoints();
        }
        return -1;
    }

    /**
     * Give points to a user
     *
     * @param user   The user to modify
     * @param amount The amount to give
     */
    public void give(User user, Number amount) {
        long realAmount = amount.longValue();
        user.setPoints(user.getPoints() + realAmount);
        usersService.save(user);
    }

    public String asString(Number points) {
        long realPoints = points.longValue();
        if (MathUtils.isPlural(realPoints)) {
            return realPoints + " " + settings.getPointsNamePlural();
        }
        return realPoints + " " + settings.getPointsNameSingular();
    }

    public String pointsName() {
        return settings.getPointsNamePlural();
    }
}
