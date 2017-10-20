package nl.juraji.biliomi.components.registers.donations;

import nl.juraji.biliomi.components.shared.TemplateSetup;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.registers.Donation;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class DonationRegisterComponent extends Component {

  @Inject
  private DonationsService donationsService;

  @Inject
  private DonationsEventService donationsEventService;

  @Inject
  private TemplateDao templateDao;

  @Override
  public void init() {
    donationsEventService.init();
  }

  /**
   * Manage donations
   * Usage !donations [add|list|remove] [more...]
   */
  @CommandRoute(command = "donations", systemCommand = true)
  public boolean donationsCommand(User user, Arguments arguments) {
    return captureSubCommands("donations", l10n.supply("ChatCommand.donations.usage"), user, arguments);
  }

  /**
   * Add a donation
   * Usage: !donations add [username] [donation...]
   */
  @SubCommandRoute(parentCommand = "donations", command = "add")
  public boolean donationsCommandAdd(User user, Arguments arguments) {
    if (!arguments.assertMinSize(2)) {
      chat.whisper(user, l10n.get("ChatCommand.donations.add.usage"));
      return false;
    }

    String donatorUsername = arguments.pop();
    User donator = usersService.getUser(donatorUsername, true);
    if (donator == null) {
      chat.whisper(user, l10n.getUserNonExistent(donatorUsername));
      return false;
    }

    String donationString = arguments.toString();
    Donation donation = donationsService.registerDonation(donator, donationString);
    long count = donationsService.getCurrentDonationCount(donator);

    chat.say(l10n.get("ChatCommand.donations.add.added")
        .add("username", donator::getNameAndTitle)
        .add("donation", donation::getDonation));

    chat.say(l10n.get("ChatCommand.donations.add.added.donationCount")
        .add("ordinalcount", MathUtils.getOrdinal(count))
        .add("username", donator::getNameAndTitle));
    return true;
  }

  /**
   * List donations from a specific user
   * Usage: !donations list [donator username]
   */
  @SubCommandRoute(parentCommand = "donations", command = "list")
  public boolean donationsCommandList(User user, Arguments arguments) {
    if (!arguments.assertSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.donations.list.usage"));
      return false;
    }

    String donatorUsername = arguments.get(0);
    User donator = usersService.getUser(donatorUsername);
    if (donator == null) {
      chat.whisper(user, l10n.getUserNonExistent(donatorUsername));
      return false;
    }

    String donationList = donationsService.getDonationList(donator);
    if (donationList == null) {
      chat.whisper(user, l10n.get("ChatCommand.donations.list.noDonations")
          .add("username", donator::getDisplayName));
    } else {
      chat.say(l10n.get("ChatCommand.donations.list.list")
          .add("username", donator::getNameAndTitle)
          .add("donationlist", donationList));
    }

    return true;
  }

  /**
   * Remove a donation by id
   * Usage: !donations remove [donation id]
   */
  @SubCommandRoute(parentCommand = "donations", command = "remove")
  public boolean donationsCommandRemove(User user, Arguments arguments) {
    Long donationId = Numbers.asNumber(arguments.get(0)).toLong();

    if (donationId == null) {
      chat.whisper(user, l10n.get("ChatCommand.donations.remove.usage"));
      return false;
    }

    Donation donation = donationsService.removeDonation(donationId);
    if (donation == null) {
      chat.whisper(user, l10n.get("ChatCommand.donations.remove.notFound")
          .add("id", donationId));
      return false;
    } else {
      chat.whisper(user, l10n.get("ChatCommand.donations.remove.removed")
          .add("id", donation::getId)
          .add("username", donation.getUser().getDisplayName()));
    }

    return true;
  }

  /**
   * Set the notice to post in the chat when a new donation is registered manually
   * Usage: !donations setexternalnotice [message...]
   */
  @SubCommandRoute(command = "setmanualnotice", parentCommand = "donations")
  public boolean donationsSetNoticeCommand(User user, Arguments arguments) {
    return new TemplateSetup(templateDao, chat)
        .withCommandUsageMessage(l10n.getString("ChatCommand.donations.setManualNotice.usage"))
        .withTemplatedSavedMessage(l10n.getString("Common.noticeTemplate.saved"))
        .apply(user, arguments.toString(), DonationRegisterConstants.MANUAL_DONATION_NOTICE);
  }

  /**
   * Set the notice to post in the chat when a new donation is registered automatically (E.g. Via Stream Labs)
   * Usage: !donations setexternalnotice [message...]
   */
  @SubCommandRoute(command = "setexternalnotice", parentCommand = "donations")
  public boolean donationsSetStreamLabsNoticeCommand(User user, Arguments arguments) {
    return new TemplateSetup(templateDao, chat)
        .withCommandUsageMessage(l10n.getString("ChatCommand.donations.setExternalNotice.usage"))
        .withTemplatedSavedMessage(l10n.getString("Common.noticeTemplate.saved"))
        .apply(user, arguments.toString(), DonationRegisterConstants.INCOMING_DONATION_NOTICE);
  }
}
