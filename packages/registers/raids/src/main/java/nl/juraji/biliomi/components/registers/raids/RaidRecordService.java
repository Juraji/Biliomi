package nl.juraji.biliomi.components.registers.raids;

import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchChannel;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchStream;
import nl.juraji.biliomi.model.core.Direction;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.registers.RaidRecord;
import nl.juraji.biliomi.model.registers.RaidRecordDao;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
@Default
public class RaidRecordService {
    @Inject
    private Logger logger;

    @Inject
    private RaidRecordDao raidRecordDao;

    @Inject
    private ChannelService channelService;

    /**
     * Register an outgoing raid
     *
     * @param channel The channel to be raided
     * @return The persisted RaidRecord or null if the channel is not online
     */
    public RaidRecord registerOutgoingRaid(User channel) {
        RaidRecord raidRecord = new RaidRecord();

        TwitchStream stream = channelService.getStream(channel.getTwitchUserId());
        if (stream == null) {
            return null;
        }

        raidRecord.setChannel(channel);
        raidRecord.setDirection(Direction.OUTGOING);
        raidRecord.setGameAtMoment(stream.getGame());
        raidRecord.setDate(DateTime.now());

        raidRecordDao.save(raidRecord);

        return raidRecord;
    }

    /**
     * Register an incoming raid
     *
     * @param channel The raiding channel
     * @return The persisted RaidRecord or NULL if an error occurred
     */
    public RaidRecord registerIncomingRaid(User channel) {
        RaidRecord raidRecord = new RaidRecord();
        TwitchChannel twitchChannel = channelService.getChannel(channel.getTwitchUserId());

        // If channel is NULL, something went wrong
        if (twitchChannel == null) {
            return null;
        }

        raidRecord.setChannel(channel);
        raidRecord.setDirection(Direction.INCOMING);
        raidRecord.setDate(DateTime.now());
        raidRecord.setGameAtMoment(twitchChannel.getGame());

        raidRecordDao.save(raidRecord);

        return raidRecord;
    }

    public long getIncomingRaidCount(User channel) {
        return raidRecordDao.getCount(channel, Direction.INCOMING);
    }
}
