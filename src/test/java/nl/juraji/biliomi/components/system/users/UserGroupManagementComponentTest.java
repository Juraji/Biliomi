package nl.juraji.biliomi.components.system.users;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.UserGroup;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.test.TestUtils;
import nl.juraji.biliomi.test.mockmodel.jpa.TestUser;
import nl.juraji.biliomi.test.mockmodel.jpa.TestUserGroup;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.collections.L10nMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

/**
 * Created by Juraji on 9-5-2017.
 * Biliomi v3
 */
public class UserGroupManagementComponentTest {
  private final String controlHash = MathUtils.newUUID();

  @Captor
  private ArgumentCaptor<String> stringArgumentCaptor;

  @Captor
  private ArgumentCaptor<User> userArgumentCaptor;

  @Captor
  private ArgumentCaptor<Templater> templaterArgumentCaptor;

  @Mock
  private UserGroupService userGroupService;

  @Mock
  private UsersService usersService;

  @Mock
  private ChatService chatService;

  @Mock
  private L10nMap l10nMap;

  @InjectMocks
  private UserGroupManagementComponent userGroupManagementComponent;

  @Before
  public void setUp() throws Exception {
    userGroupManagementComponent = new UserGroupManagementComponent();
    MockitoAnnotations.initMocks(this);

    // Init component, so the subcommandrouter can build routes
    TestUtils.callPostConstruct(userGroupManagementComponent);

    // Cut off ChatService
    doNothing().when(chatService).whisper(any(User.class), anyString());
    doNothing().when(chatService).say(anyString());

    // Cut off L10nMap
    doReturn(Templater.template(controlHash)).when(l10nMap).get(anyString());
    doReturn((Supplier<String>) () -> controlHash).when(l10nMap).supply(anyString());
  }

  @Test
  public void groupsCommand() throws Exception {
    List<UserGroup> groupList = new ArrayList<>();
    groupList.add(new TestUserGroup(2));
    groupList.add(new TestUserGroup(3));
    groupList.add(new TestUserGroup(1));
    when(userGroupService.getList()).thenReturn(groupList);

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertTrue(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.groups.list", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object grouplist = usedTemplater.get("grouplist").get();

    assertEquals("[TestGroup1 (1), TestGroup2 (2), TestGroup3 (3)]", grouplist.toString());
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void groupsCommandWithArg() throws Exception {
    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "any", "argument");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(chatService).whisper(userArgumentCaptor.capture(), stringArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals(controlHash, stringArgumentCaptor.getValue());
  }

  @Test
  public void helpCommand() throws Exception {
    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "help");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertTrue(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.groups.help", stringArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void groupsCommandAdd() throws Exception {
    // Setup UserGroupService
    doReturn(false).when(userGroupService).groupExists(anyInt());
    doReturn(false).when(userGroupService).groupExists(anyString());
    doReturn(new TestUserGroup(5)).when(userGroupService).createNewGroup(anyString(), anyInt());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "add", "NewGroup", "5");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertTrue(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals("ChatCommand.groups.add.created", stringArgumentCaptor.getValue());
    assertEquals(testUser, userArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object groupname = usedTemplater.get("groupname").get();
    Object weight = usedTemplater.get("weight").get();

    assertEquals("TestGroup5", groupname);
    assertEquals(5, weight);
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void groupsCommandAddInvalidArgCount() throws Exception {
    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "add", "NewGroup");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.groups.add.usage", stringArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void groupsCommandAddNaNWeight() throws Exception {
    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "add", "NewGroup", "jhkbfds");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.groups.help", stringArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void groupsCommandAddGroupStartsWithNumber() throws Exception {
    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "add", "0NewGroup", "5");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.groups.help", stringArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void groupsCommandAddGroupWeightExists() throws Exception {
    // Setup UserGroupService
    doReturn(true).when(userGroupService).groupExists(anyInt());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "add", "NewGroup", "5");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());


    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("Common.groups.duplicateGroupWeight", stringArgumentCaptor.getValue());

    Templater usedTemplate = templaterArgumentCaptor.getValue();
    Object weight = usedTemplate.get("weight").get();

    assertEquals(5, weight);
    assertEquals(controlHash, usedTemplate.apply());
  }

  @Test
  public void groupsCommandAddGroupNameExists() throws Exception {
    // Setup UserGroupService
    doReturn(false).when(userGroupService).groupExists(anyInt());
    doReturn(true).when(userGroupService).groupExists(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "add", "NewGroup", "5");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("Common.groups.duplicateGroupName", stringArgumentCaptor.getValue());

    Templater usedTemplate = templaterArgumentCaptor.getValue();
    Object groupname = usedTemplate.get("groupname").get();

    assertEquals("NewGroup", groupname);
    assertEquals(controlHash, usedTemplate.apply());
  }

  @Test
  public void groupsCommandEditNewName() throws Exception {
    // Setup userGroupsService
    doReturn(new TestUserGroup(5)).when(userGroupService).getByName(anyString());
    doReturn(false).when(userGroupService).groupExists(anyString());
    doNothing().when(userGroupService).save(any(UserGroup.class));

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "edit", "TestGroup5", "NewGroup");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertTrue(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.groups.edit.changedName", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object oldgroupname = usedTemplater.get("oldgroupname").get();
    Object newgroupname = usedTemplater.get("newgroupname").get();

    assertEquals("TestGroup5", oldgroupname);
    assertEquals("NewGroup", newgroupname);
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void groupsCommandEditNewNameStartsWithNumber() throws Exception {
    // Setup userGroupsService
    doReturn(new TestUserGroup(5)).when(userGroupService).getByName(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "edit", "NewGroup", "6Group");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.groups.help", stringArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void groupsCommandEditNewNameNewNameIsEqual() throws Exception {
    // Setup userGroupsService
    doReturn(new TestUserGroup(5)).when(userGroupService).getByName(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "edit", "TestGroup5", "TestGroup5");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("Common.groups.duplicateGroupName", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object groupname = usedTemplater.get("groupname").get();

    assertEquals("TestGroup5", groupname);
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void groupsCommandEditNewNameNewNameExists() throws Exception {
    // Setup userGroupsService
    doReturn(new TestUserGroup(5)).when(userGroupService).getByName(anyString());
    doReturn(true).when(userGroupService).groupExists(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "edit", "TestGroup5", "NewGroup");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("Common.groups.duplicateGroupName", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object groupname = usedTemplater.get("groupname").get();

    assertEquals("NewGroup", groupname);
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void groupsCommandEditNewWeight() throws Exception {
    // Setup userGroupsService
    doReturn(new TestUserGroup(5)).when(userGroupService).getByName(anyString());
    doReturn(false).when(userGroupService).groupExists(anyInt());
    doNothing().when(userGroupService).save(any(UserGroup.class));

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "edit", "TestGroup5", "65");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertTrue(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.groups.edit.changedWeight", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object groupname = usedTemplater.get("groupname").get();
    Object oldweight = usedTemplater.get("oldweight").get();
    Object newweight = usedTemplater.get("newweight").get();

    assertEquals("TestGroup5", groupname);
    assertEquals(5, oldweight);
    assertEquals(65, newweight);
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void groupsCommandEditNewWeightWeightOutOfBounds() throws Exception {
    // Setup userGroupsService
    doReturn(new TestUserGroup(5)).when(userGroupService).getByName(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "edit", "NewGroup", "68464545");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.groups.help", stringArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void groupsCommandEditNewWeightWeightEquals() throws Exception {
    // Setup userGroupsService
    doReturn(new TestUserGroup(5)).when(userGroupService).getByName(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "edit", "TestGroup5", "5");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("Common.groups.duplicateGroupWeight", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object weight = usedTemplater.get("weight").get();

    assertEquals("5", weight);
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void groupsCommandEditNewWeightWeightExists() throws Exception {
    // Setup userGroupsService
    doReturn(new TestUserGroup(5)).when(userGroupService).getByName(anyString());
    doReturn(true).when(userGroupService).groupExists(anyInt());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "edit", "TestGroup5", "514");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("Common.groups.duplicateGroupWeight", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object weight = usedTemplater.get("weight").get();

    assertEquals("514", weight);
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void groupsCommandEditGroupNonExistent() throws Exception {
    // Setup userGroupsService
    doReturn(null).when(userGroupService).getByName(anyString());
    doAnswer(invocationOnMock -> invocationOnMock.getArgumentAt(0, String.class)).when(l10nMap).getGroupNonExistent(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "edit", "TestGroup5", "514");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).getGroupNonExistent(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), stringArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());

    List<String> stringArgs = stringArgumentCaptor.getAllValues();

    assertEquals("TestGroup5", stringArgs.get(0));
    assertEquals("TestGroup5", stringArgs.get(1));
  }

  @Test
  public void groupsCommandEditGroupIsDefault() throws Exception {
    // Setup userGroupsService
    TestUserGroup testUserGroup = new TestUserGroup(5);
    testUserGroup.setDefaultGroup(true);
    doReturn(testUserGroup).when(userGroupService).getByName(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "edit", "TestGroup5", "514");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals("Common.groups.defaultGroupNotEditable", stringArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void groupsCommandEditInvalidArgCount() throws Exception {
    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "edit", "NewGroup");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.groups.edit.usage", stringArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void groupsCommandRemoveWithoutUsersInGroup() throws Exception {
    // Setup groupsService
    TestUserGroup testUserGroup = new TestUserGroup(5);
    doReturn(testUserGroup).when(userGroupService).getByName(anyString());
    doReturn(new TestUserGroup(2)).when(userGroupService).getDefaultGroup();
    doNothing().when(userGroupService).save(any(UserGroup.class));

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "remove", "TestGroup5");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertTrue(b);

    verify(l10nMap, times(1)).get(stringArgumentCaptor.capture());
    verify(chatService, times(1)).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object groupname = usedTemplater.get("groupname").get();

    assertEquals("ChatCommand.groups.remove.removed", stringArgumentCaptor.getValue());
    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("TestGroup5", groupname);
    assertEquals(controlHash, usedTemplater.apply());
  }

  @Test
  public void groupsCommandRemoveWithUsersInGroup() throws Exception {
    // Setup groupsService
    TestUserGroup testUserGroup = new TestUserGroup(5);
    doReturn(testUserGroup).when(userGroupService).getByName(anyString());
    doReturn(new TestUserGroup(2)).when(userGroupService).getDefaultGroup();
    doNothing().when(userGroupService).save(any(UserGroup.class));
    // Setup usersService
    List<User> users = IntStream.range(0, 5).mapToObj(TestUser::new).collect(Collectors.toList());
    doReturn(users).when(usersService).getUsersByGroup(any(UserGroup.class));
    doNothing().when(usersService).save(any(User.class));

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "remove", "TestGroup5");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertTrue(b);

    // Verification of call is enough
    verify(usersService, times(1)).save(anyListOf(User.class));
    verify(userGroupService, times(1)).delete(testUserGroup);

    verify(l10nMap, times(2)).get(stringArgumentCaptor.capture());
    verify(chatService, times(2)).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    List<String> tplGets = stringArgumentCaptor.getAllValues();
    List<User> whisperedUsers = userArgumentCaptor.getAllValues();
    List<Templater> usedTemplates = templaterArgumentCaptor.getAllValues();

    String movedUsersTplGet = tplGets.get(0);
    User movedUsersWhisperTarget = whisperedUsers.get(0);
    Templater movedUsersUsedTemplater = usedTemplates.get(0);
    Object movedUsersCount = movedUsersUsedTemplater.get("count").get();
    Object defaultgroupname = movedUsersUsedTemplater.get("defaultgroupname").get();

    assertEquals("ChatCommand.groups.remove.movedUsersToDefault", movedUsersTplGet);
    assertEquals(testUser, movedUsersWhisperTarget);
    assertEquals(5, movedUsersCount);
    assertEquals("TestGroup2", defaultgroupname);
    assertEquals(controlHash, movedUsersUsedTemplater.apply());

    String removedGroupTplGet = tplGets.get(1);
    User removedGroupWhisperTarget = whisperedUsers.get(1);
    Templater removedGroupUsedTemplater = usedTemplates.get(1);
    Object groupname = removedGroupUsedTemplater.get("groupname").get();

    assertEquals("ChatCommand.groups.remove.removed", removedGroupTplGet);
    assertEquals(testUser, removedGroupWhisperTarget);
    assertEquals("TestGroup5", groupname);
    assertEquals(controlHash, removedGroupUsedTemplater.apply());
  }

  @Test
  public void groupsCommandRemoveInvalidArgCount() throws Exception {
    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "remove");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.groups.remove.usage", stringArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void groupsCommandRemoveGroupNonExistent() throws Exception {
    // Setup userGroupsService
    doReturn(null).when(userGroupService).getByName(anyString());
    doAnswer(invocationOnMock -> invocationOnMock.getArgumentAt(0, String.class)).when(l10nMap).getGroupNonExistent(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "remove", "TestGroup5");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).getGroupNonExistent(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), stringArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());

    List<String> stringArgs = stringArgumentCaptor.getAllValues();

    assertEquals("TestGroup5", stringArgs.get(0));
    assertEquals("TestGroup5", stringArgs.get(1));
  }

  @Test
  public void groupsCommandRemoveGroupIsDefault() throws Exception {
    // Setup userGroupsService
    TestUserGroup testUserGroup = new TestUserGroup(5);
    testUserGroup.setDefaultGroup(true);
    doReturn(testUserGroup).when(userGroupService).getByName(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("groups", "remove", "TestGroup5");

    boolean b = userGroupManagementComponent.groupsCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());

    assertEquals("Common.groups.defaultGroupNotEditable", stringArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void setUserGroupCommand() throws Exception {
    // Setup mocking
    doReturn(new TestUser(2)).when(usersService).getUser(anyString());
    doReturn(new TestUserGroup(5)).when(userGroupService).getByName(anyString());
    doNothing().when(usersService).save(any(User.class));

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("setusergroup", "TestUser2", "TestGroup5");

    boolean b = userGroupManagementComponent.setUserGroupCommand(testUser, arguments);
    assertTrue(b);

    Mockito.verify(l10nMap).get(stringArgumentCaptor.capture());
    Mockito.verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.setusergroup.changed", stringArgumentCaptor.getValue());

    Templater usedTemplater = templaterArgumentCaptor.getValue();
    Object targetUsername = usedTemplater.get("username").get();
    Object oldgroupname = usedTemplater.get("oldgroupname").get();
    Object newgroupname = usedTemplater.get("newgroupname").get();

    assertEquals("TestUser2", targetUsername);
    assertEquals("TestGroup2", oldgroupname);
    assertEquals("TestGroup5", newgroupname);
  }

  @Test
  public void setUserGroupCommandInvalidArgCount() throws Exception {
    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("setusergroup");

    boolean b = userGroupManagementComponent.setUserGroupCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).get(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), templaterArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());
    assertEquals("ChatCommand.setusergroup.usage", stringArgumentCaptor.getValue());
    assertEquals(controlHash, templaterArgumentCaptor.getValue().apply());
  }

  @Test
  public void setUserGroupCommandUserNonExistent() throws Exception {
    // Setup usersService
    Mockito.doReturn(null).when(usersService).getUser(anyString());
    doAnswer(invocationOnMock -> invocationOnMock.getArgumentAt(0, String.class)).when(l10nMap).getUserNonExistent(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("setusergroup", "TestUser2", "TestGroup5");

    boolean b = userGroupManagementComponent.setUserGroupCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).getUserNonExistent(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), stringArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());

    List<String> stringArgs = stringArgumentCaptor.getAllValues();

    assertEquals("TestUser2", stringArgs.get(0));
    assertEquals("TestUser2", stringArgs.get(1));
  }

  @Test
  public void setUserGroupCommandGroupNonExistent() throws Exception {
    // Setup userGroupsService
    doReturn(new TestUser(2)).when(usersService).getUser(anyString());
    doReturn(null).when(userGroupService).getByName(anyString());
    doAnswer(invocationOnMock -> invocationOnMock.getArgumentAt(0, String.class)).when(l10nMap).getGroupNonExistent(anyString());

    TestUser testUser = new TestUser(1);
    Arguments arguments = new Arguments("setusergroup", "TestUser2", "TestGroup5");

    boolean b = userGroupManagementComponent.setUserGroupCommand(testUser, arguments);
    assertFalse(b);

    verify(l10nMap).getGroupNonExistent(stringArgumentCaptor.capture());
    verify(chatService).whisper(userArgumentCaptor.capture(), stringArgumentCaptor.capture());

    assertEquals(testUser, userArgumentCaptor.getValue());

    List<String> stringArgs = stringArgumentCaptor.getAllValues();

    assertEquals("TestGroup5", stringArgs.get(0));
    assertEquals("TestGroup5", stringArgs.get(1));
  }
}