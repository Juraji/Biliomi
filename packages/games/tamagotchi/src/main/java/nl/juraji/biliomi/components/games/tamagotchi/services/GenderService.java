package nl.juraji.biliomi.components.games.tamagotchi.services;

import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.types.collections.I18nMap;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 28-5-2017.
 * Biliomi v3
 */
@Default
public class GenderService {

    @Inject
    private I18nMap i18n;

    /**
     * Retrieve the i18n name for a gender
     *
     * @param gender The gender to retrieve
     * @return The corresponding name from the I18nMap
     */
    public String getGenderName(Gender gender) {
        switch (gender) {
            case MALE:
                return i18n.getString("Gender.gender.male");
            case FEMALE:
                return i18n.getString("Gender.gender.female");
            case NEUTRAL:
                return i18n.getString("Gender.gender.neutral");
            default:
                return null;
        }
    }

    /**
     * Retrieve the i18n addressing for a gender
     *
     * @param gender The gender to retrieve
     * @return The corresponding name from the I18nMap
     */
    public String getAddress(Gender gender) {
        switch (gender) {
            case MALE:
                return i18n.getString("Gender.address.male");
            case FEMALE:
                return i18n.getString("Gender.address.female");
            case NEUTRAL:
                return i18n.getString("Gender.address.neutral");
            default:
                return null;
        }
    }

    /**
     * Retrieve the i18n referal for a gender
     *
     * @param gender The gender to retrieve
     * @return The corresponding name from the I18nMap
     */
    public String getReferal(Gender gender) {
        switch (gender) {
            case MALE:
                return i18n.getString("Gender.refer.male");
            case FEMALE:
                return i18n.getString("Gender.refer.female");
            case NEUTRAL:
                return i18n.getString("Gender.refer.neutral");
            default:
                return null;
        }
    }

    /**
     * Retrieve the i18n posessive referal for a gender
     *
     * @param gender The gender to retrieve
     * @return The corresponding name from the I18nMap
     */
    public String getPosessiveReferal(Gender gender) {
        switch (gender) {
            case MALE:
                return i18n.getString("Gender.refer.possesive.male");
            case FEMALE:
                return i18n.getString("Gender.refer.possesive.female");
            case NEUTRAL:
                return i18n.getString("Gender.refer.possesive.neutral");
            default:
                return null;
        }
    }

    /**
     * Get a random gender
     *
     * @return The chosen Gender
     */
    public Gender getRandom() {
        return MathUtils.arrayRand(Gender.values());
    }
}
