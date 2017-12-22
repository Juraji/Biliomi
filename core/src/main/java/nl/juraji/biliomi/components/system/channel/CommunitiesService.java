package nl.juraji.biliomi.components.system.channel;

import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.io.api.twitch.v5.TwitchApi;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchCommunity;
import nl.juraji.biliomi.io.api.twitch.v5.model.wrappers.TwitchCommunities;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.Community;
import nl.juraji.biliomi.model.core.CommunityDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.estreams.EStream;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Juraji on 15-12-2017.
 * Biliomi
 */
public class CommunitiesService {

  @Inject
  private Logger logger;

  @Inject
  private CommunityDao communityDao;

  @Inject
  private UsersService usersService;

  @Inject
  private ChannelService channelService;

  @Inject
  private TwitchApi twitchApi;

  public Community getCommunity(long id) {
    return communityDao.get(id);
  }

  public List<Community> getCommunities() {
    return communityDao.getList();
  }

  public List<Community> getChannelCommunities() {
    List<Community> communities = new ArrayList<>();

    try {
      Response<TwitchCommunities> response = twitchApi.getChannelCommunities(channelService.getChannelId());

      if (response.isOK()) {
        EStream.from(response.getData().getCommunities())
            .mapToBiEStream(twitchCommunity -> communityDao.getByTwitchID(twitchCommunity.getId()))
            .map((tc, c) -> (c == null ? convertTwitchCommunity(tc) : c))
            .forEach(communities::add);
      }

    } catch (Exception e) {
      logger.error("Failed retrieving current channel communities", e);
    }

    return communities;
  }

  public Community getCommunityByName(String communityName) {
    Community community = communityDao.getByName(communityName);

    if (community == null) {
      try {
        Response<TwitchCommunity> response = twitchApi.getCommunityByName(communityName);

        if (response.isOK()) {
          community = convertTwitchCommunity(response.getData());
        }
      } catch (Exception e) {
        logger.error("Could not find a community by the name " + communityName, e);
      }
    }

    return community;
  }

  public boolean updateTwitchCommunities(Set<Community> communities) {
    Response<Void> response = null;

    try {
      response = twitchApi.updateChannelCommunities(channelService.getChannelId(), communities);
    } catch (Exception e) {
      logger.error("Failed updating channel communities", e);
    }

    return (response != null && response.isOK());
  }

  private Community convertTwitchCommunity(TwitchCommunity twitchCommunity) {
    User userByTwitchId = usersService.getUserByTwitchId(twitchCommunity.getOwnerId());
    Community community = new Community();

    if (userByTwitchId != null) {
      community.setTwitchId(twitchCommunity.getId());
      community.setName(twitchCommunity.getName());
      community.setOwner(userByTwitchId);
      communityDao.save(community);
    }

    return community;
  }
}
