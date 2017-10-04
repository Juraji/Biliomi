package nl.juraji.biliomi.components.registers.donations;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.registers.Donation;
import nl.juraji.biliomi.model.registers.DonationDao;
import nl.juraji.biliomi.utility.types.Templater;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
@Default
public class DonationsService {
  private static final String DONATION_WITH_ID_TEMPLATE = "({{id}}) {{donation}}";
  private static final String DONATION_LIST_SEPARATOR = ", ";

  @Inject
  private DonationDao donationDao;

  /**
   * Register a simple donation
   *
   * @param user     The user sending the donation
   * @param donation The donated item, this is a free field, knock yourself out
   * @return The registered Donation
   */
  public Donation registerDonation(User user, String donation) {
    return registerDonation(user, donation, null);
  }

  /**
   * Register a donation with a note
   *
   * @param user     The user sending the donation
   * @param donation The donated item, this is a free field, knock yourself out
   * @param note     The accompanying note
   * @return The registered Donation
   */
  public Donation registerDonation(User user, String donation, String note) {
    Donation record = new Donation();
    record.setUser(user);
    record.setDonation(donation);
    record.setNote(note);
    record.setDate(DateTime.now());
    donationDao.save(record);

    return record;
  }

  public long getCurrentDonationCount(User user) {
    return donationDao.getCount(user);
  }

  /**
   * Get a comma separated list of donations from a specific user
   *
   * @param user The user of which to retrieve the donations
   * @return The textual list of donations or NULL if no donations where found
   */
  public String getDonationList(User user) {
    List<Donation> donations = donationDao.getDonationsFor(user);
    Templater t = Templater.template(DONATION_WITH_ID_TEMPLATE);
    return donations.stream()
        .map(donation -> t
            .add("id", donation::getId)
            .add("donation", donation::getDonation)
            .apply())
        .reduce((l, r) -> l + DONATION_LIST_SEPARATOR + r)
        .orElse(null);
  }

  public Donation removeDonation(Long donationId) {
    Donation donation = donationDao.get(donationId);
    donationDao.delete(donation);
    return donation;
  }
}
