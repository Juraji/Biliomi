package nl.juraji.biliomi.components.system.users;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.test.mockmodel.jpa.TestUser;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.collections.L10nMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Juraji on 8-5-2017.
 * Biliomi v3
 */
public class UserInfoComponentTest {

  private final String controlHash = MathUtils.newUUID();

  @Captor
  private ArgumentCaptor<String> stringArgumentCaptor;

  @Captor
  private ArgumentCaptor<User> userArgumentCaptor;

  @Captor
  private ArgumentCaptor<Templater> templaterArgumentCaptor;

  @Mock
  private PointsService pointsService;

  @Mock
  private TimeFormatter timeFormatter;

  @Mock
  private ChatService chatService;

  @Mock
  private L10nMap l10nMap;

  @InjectMocks
  private UserInfoComponent userInfoComponent;

  @Before
  public void setUp() throws Exception {
    userInfoComponent = new UserInfoComponent();
    MockitoAnnotations.initMocks(this);

    // Cut off ChatService
    Mockito.doNothing().when(chatService).whisper(Mockito.any(User.class), Mockito.anyString());
    Mockito.doNothing().when(chatService).say(Mockito.anyString());

    // Cut off L10nMap
    Mockito.doReturn(Templater.template(controlHash)).when(l10nMap).get(Mockito.anyString());
  }

  @Test
  public void groupCommand() throws Exception {
    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("mygroup");

    boolean b = userInfoComponent.groupCommand(testUser, arguments);
    assertTrue(b);

    // To get the language string
    Mockito.verify(l10nMap).get(stringArgumentCaptor.capture());
    // To whisper the message to the calling user
    Mockito.verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.myGroup.message", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object groupName = usedTemplater.get("groupname").get();

    assertEquals(testUser.getUserGroup().getName(), groupName);
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void myTitleCommandNoTitle() throws Exception {

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("mytitle");

    boolean b = userInfoComponent.myTitleCommand(testUser, arguments);
    assertTrue(b);

    Mockito.verify(l10nMap).get(stringArgumentCaptor.capture());
    Mockito.verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    List<String> strings = stringArgumentCaptor.getAllValues();
    assertEquals("ChatCommand.myTitle.noTitle", strings.get(0));
    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void myTitleCommandWithTitle() throws Exception {
    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("mytitle");

    testUser.setTitle("Test Title");

    boolean b = userInfoComponent.myTitleCommand(testUser, arguments);
    assertTrue(b);

    Mockito.verify(l10nMap).get(stringArgumentCaptor.capture());
    Mockito.verify(chatService).say(templaterArgumentCaptor.capture());

    assertEquals("ChatCommand.myTitle.message", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object titledusername = usedTemplater.get("titledusername").get();

    assertEquals(testUser.getTitle() + ' ' + testUser.getDisplayName(), titledusername);
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void myPointsCommand() throws Exception {
    // Setup mocking pointsService
    Mockito.when(pointsService.asString(Mockito.anyObject())).thenAnswer(invocationOnMock -> {
      Number argumentAt = invocationOnMock.getArgumentAt(0, Number.class);
      return argumentAt + " points";
    });

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("mypoints");

    testUser.setPoints(35);

    boolean b = userInfoComponent.myPointsCommand(testUser, arguments);
    assertTrue(b);

    Mockito.verify(l10nMap).get(stringArgumentCaptor.capture());
    Mockito.verify(chatService).say(templaterArgumentCaptor.capture());

    assertEquals("ChatCommand.myPoints.message", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object username = usedTemplater.get("username").get();
    Object points = usedTemplater.get("points").get();

    assertEquals(testUser.getDisplayName(), username);
    assertEquals("35 points", points);
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void myTimeCommand() throws Exception {
    // Setup mocking timeFormatter
    Mockito.when(timeFormatter.timeQuantity(Mockito.anyLong(), Mockito.any(TimeUnit.class))).thenAnswer(invocationOnMock -> {
      Long argumentAt = invocationOnMock.getArgumentAt(0, Long.class);
      return String.valueOf(argumentAt);
    });

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("mytime");

    testUser.setRecordedTime(4646454);

    boolean b = userInfoComponent.myTimeCommand(testUser, arguments);
    assertTrue(b);

    Mockito.verify(l10nMap).get(stringArgumentCaptor.capture());
    Mockito.verify(chatService).say(templaterArgumentCaptor.capture());

    assertEquals("ChatCommand.myTime.message", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object username = usedTemplater.get("username").get();
    Object time = usedTemplater.get("time").get();

    assertEquals(testUser.getDisplayName(), username);
    assertEquals(String.valueOf(testUser.getRecordedTime()), time);
    assertEquals(controlHash, usedTemplater.apply());
  }

}