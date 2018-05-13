package nl.juraji.biliomi.components.games.investments;

import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.config.investments.InvestmentsConfigService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.InvestmentRecord;
import nl.juraji.biliomi.model.games.InvestmentRecordDao;
import nl.juraji.biliomi.model.games.InvestmentSettings;
import nl.juraji.biliomi.model.games.UserInvestRecordStats;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.cdi.annotations.modifiers.I18nData;
import nl.juraji.biliomi.utility.types.collections.I18nMap;
import nl.juraji.biliomi.utility.types.components.TimerService;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 27-5-2017.
 * Biliomi v3
 */
@Default
public class InvestmentService extends TimerService {
    private static final long MARKET_FLIP_INTERVAL_MINS = 30;

    @Inject
    private InvestmentRecordDao investmentRecordDao;

    @Inject
    private ChatService chat;

    @Inject
    private InvestmentsConfigService configService;

    @Inject
    @I18nData(InvestmentGameComponent.class)
    private I18nMap i18n;

    @Inject
    private PointsService pointsService;

    @Inject
    private UsersService usersService;

    @Inject
    private SettingsService settingsService;
    private InvestmentSettings settings;
    private boolean marketStateGood;

    @Override
    public void start() {
        super.start();
        if (settings == null) {
            settings = settingsService.getSettings(InvestmentSettings.class, s -> settings = s);
        }

        scheduleAtFixedRate(() -> marketStateGood = MathUtils.randChoice(), MARKET_FLIP_INTERVAL_MINS, TimeUnit.MINUTES);
    }

    public boolean isMarketStateGood() {
        return marketStateGood;
    }

    public InvestmentRecord startInvestment(User invester, long invested, double interest) {
        InvestmentRecord record = new InvestmentRecord();
        record.setInvester(invester);
        record.setInvested(invested);
        record.setInterest(interest);
        record.setProject(configService.getRandomProject());
        record.setMarketStateGood(marketStateGood);
        record.setDate(new DateTime());
        investmentRecordDao.save(record);

        pointsService.take(invester, invested);

        schedule(() -> investmentResult(record), settings.getInvestmentDuration(), TimeUnit.MILLISECONDS);
        return record;
    }

    public void save(InvestmentRecord record) {
        investmentRecordDao.save(record);
    }

    public UserInvestRecordStats getRecordInfo(User user) {
        List<InvestmentRecord> records = investmentRecordDao.getRecords(user);

        if (records.size() == 0) {
            return null;
        }

        long losses = records.stream()
                .filter(record -> record.getPayout() == 0)
                .count();
        long wins = records.stream()
                .filter(record -> record.getPayout() > 0)
                .count();
        long totalInvested = records.stream()
                .mapToLong(InvestmentRecord::getInvested)
                .sum();
        long totalEarned = records.stream()
                .mapToLong(r -> r.getPayout() - r.getInvested())
                .sum();

        return new UserInvestRecordStats(records.size(), losses, wins, totalInvested, totalEarned);
    }

    private void investmentResult(InvestmentRecord record) {
        double chance = 100 - record.getInterest();
        int marketStateMod = (record.isMarketStateGood() ? 25 : -25);

        // Calculate chance
        boolean success = MathUtils.randRange(0, 100) <= (chance + marketStateMod);

        if (success) {
            long interestAmount = (long) (record.getInvested() * record.getInterest());
            long fullPayout = record.getInvested() + interestAmount;

            // Add payout to investment record and save
            record.setPayout(fullPayout);
            investmentRecordDao.save(record);

            // Add payout to user points and save user
            User invester = usersService.getUser(record.getInvester().getId());
            invester.setPoints(invester.getPoints() + fullPayout);
            usersService.save(invester);

            chat.say(i18n.get("Investment.result.success")
                    .add("username", invester::getDisplayName)
                    .add("project", record::getProject)
                    .add("invested", () -> pointsService.asString(record.getInvested()))
                    .add("interest", () -> pointsService.asString(interestAmount))
                    .add("payout", () -> pointsService.asString(record.getPayout())));
        } else {
            chat.say(i18n.get("Investment.result.failed")
                    .add("username", record.getInvester().getDisplayName())
                    .add("project", record::getProject)
                    .add("invested", () -> pointsService.asString(record.getInvested())));
        }
    }
}
