package nl.juraji.biliomi.io.api.twitch.irc.utils;

import nl.juraji.biliomi.io.api.twitch.irc.IrcSession;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Juraji on 6-9-2017.
 * Biliomi v3
 */
public class MessageReaderTest {

  private final MessageReader capMessage;
  private final MessageReader doubleOMessage;
  private final MessageReader userStateMessage;
  private final MessageReader joinMessage;
  private final MessageReader chatterMessage;
  private final MessageReader hostMessage;
  private final MessageReader userNoticeMessage;

  public MessageReaderTest() {
    capMessage = new MessageReader(":tmi.twitch.tv CAP * ACK :twitch.tv/membership");
    doubleOMessage = new MessageReader(":tmi.twitch.tv 372 biliomi :You are in a maze of twisty passages, all alike.");
    userStateMessage = new MessageReader("@badges=;color=#FF69B4;display-name=Biliomi;emote-sets=0;user-id=78442092;user-type= :tmi.twitch.tv GLOBALUSERSTATE");
    joinMessage = new MessageReader(":biliomi_651641!biliomi_651641@biliomi_651641.tmi.twitch.tv JOIN #jurajibot");
    chatterMessage = new MessageReader("@badges=;color=#008000;display-name=Juraji;emotes=;id=2a6160b4-870f-4336-a105-e826e1b3f160;mod=0;room-id=113182959;sent-ts=1504718855681;subscriber=0;tmi-sent-ts=1504718860203;turbo=0;user-id=46912319;user-type= :juraji!juraji@juraji.tmi.twitch.tv PRIVMSG #jurajibot :This is a :testmessage");
    hostMessage = new MessageReader(":jtv!jtv@jtv.tmi.twitch.tv PRIVMSG jurajibot :Juraji_651641 is now hosting you.");
    userNoticeMessage = new MessageReader("@badges=<badges>;color=<color>;display-name=<display-name>;emotes=<emotes>;mod=<mod>;msg-id=<msg-id>;msg-param-months=<msg-param-months>;msg-param-sub-plan=<msg-param-sub-plan>;msg-param-sub-plan-name=<msg-param-sub-plan-name>;room-id=<room-id>;subscriber=<subscriber>;system-msg=<system-msg>;login=<user>;turbo=<turbo>;user-id=<user-id>;user-type=<user-type> :tmi.twitch.tv USERNOTICE #jurajibot :This is a notice");
  }

  @Test
  public void getIrcCommand() throws Exception {
    IrcCommand capCommand = capMessage.getIrcCommand();
    IrcCommand doubleOCommand = doubleOMessage.getIrcCommand();
    IrcCommand userStateCommand = userStateMessage.getIrcCommand();
    IrcCommand privMsgCommand = chatterMessage.getIrcCommand();

    assertEquals(IrcCommand.CAP, capCommand);
    assertEquals(IrcCommand.C372, doubleOCommand);
    assertEquals(IrcCommand.GLOBALUSERSTATE, userStateCommand);
    assertEquals(IrcCommand.PRIVMSG, privMsgCommand);
  }

  @Test
  public void getUsername() throws Exception {
    String doubleOUsername = doubleOMessage.getUsername();
    String joinUsername = joinMessage.getUsername();
    String chatterUsername = chatterMessage.getUsername();
    String hostUsername = hostMessage.getUsername();

    assertNull(doubleOUsername);
    assertEquals("biliomi_651641", joinUsername);
    assertEquals("juraji", chatterUsername);
    assertEquals(IrcSession.SYSTEM_USER, hostUsername);
  }

  @Test
  public void getTags() throws Exception {
    Tags joinMessageTags = joinMessage.getTags();
    Tags chatterMessageTags = chatterMessage.getTags();
    Tags userNoticeMessageTags = userNoticeMessage.getTags();

    assertNull(joinMessageTags);
    assertNotNull(chatterMessageTags);
    assertNotNull(userNoticeMessageTags);

    assertEquals("Juraji", chatterMessageTags.getDisplayName());
    assertEquals("#008000", chatterMessageTags.getColor());

    assertEquals("<badges>", userNoticeMessageTags.getBadges());
    assertFalse(userNoticeMessageTags.getMod());
  }

  @Test
  public void getMessage() throws Exception {
    String joinMessageMessage = joinMessage.getMessage();
    String chatterMessageMessage = chatterMessage.getMessage();
    String hostMessageMessage = hostMessage.getMessage();
    String userNoticeMessageMessage = userNoticeMessage.getMessage();

    assertNull(joinMessageMessage);
    assertEquals("This is a :testmessage" ,chatterMessageMessage);
    assertEquals("Juraji_651641 is now hosting you.", hostMessageMessage);
    assertEquals("This is a notice", userNoticeMessageMessage);
  }
}