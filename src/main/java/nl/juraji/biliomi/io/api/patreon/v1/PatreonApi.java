package nl.juraji.biliomi.io.api.patreon.v1;

import nl.juraji.biliomi.io.api.patreon.v1.model.responses.PatreonPledgesResponse;
import nl.juraji.biliomi.io.api.patreon.v1.model.responses.PatreonUserResponse;
import nl.juraji.biliomi.io.web.Response;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
public interface PatreonApi {

  /**
   * Check if the Patreon Api is available
   */
  boolean isAvailable() throws Exception;

  /**
   * Fetch information about the current user and the campaign
   *
   * @see <a href="https://docs.patreon.com/#fetch-your-own-profile-and-campaign-info">Fetch your own profile and campaign info</a>
   */
  Response<PatreonUserResponse> getUserAndCampaignInfo() throws Exception;

  /**
   * Get a list of pledges for the current user's campaign
   *
   * @see <a href="https://docs.patreon.com/?java#paging-through-a-list-of-pledges-to-you">Paging through a list of pledges to you</a>
   */
  Response<PatreonPledgesResponse> getPledges(String campaignId, String nextLink) throws Exception;
}
