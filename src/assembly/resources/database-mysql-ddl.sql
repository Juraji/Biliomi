CREATE TABLE AdventureRecord
(
  id            BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  bet           BIGINT       NULL,
  byTamagotchi  BIT          NULL,
  date          VARCHAR(255) NULL,
  payout        BIGINT       NULL,
  adventurer_id BIGINT       NULL
);

CREATE INDEX FK_qbop5pjpdk1j9x70vmfb09spk
  ON AdventureRecord (adventurer_id);

CREATE TABLE AdventureSettings
(
  id            VARCHAR(255) NOT NULL
    PRIMARY KEY,
  cooldown      BIGINT       NULL,
  joinTimeout   BIGINT       NULL,
  maximumBet    BIGINT       NULL,
  minimumBet    BIGINT       NULL,
  winMultiplier DOUBLE       NULL
);

CREATE TABLE Announcement
(
  id      BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  message VARCHAR(255) NULL
);

CREATE TABLE AnnouncementsSettings
(
  id              VARCHAR(255) NOT NULL
    PRIMARY KEY,
  enabled         BIT          NULL,
  minChatMessages INT          NULL,
  runInterval     BIGINT       NULL,
  shuffle         BIT          NULL
);

CREATE TABLE ApiLogin
(
  settings_type VARCHAR(255) NOT NULL,
  password      TINYBLOB     NULL,
  salt          TINYBLOB     NULL,
  user_id       BIGINT       NULL
);

CREATE INDEX FK_79l5wyl5pp20554omivw4s4qf
  ON ApiLogin (settings_type);

CREATE INDEX FK_np0cgxtsjx57h0cb0glbjuajp
  ON ApiLogin (user_id);

CREATE TABLE ApiSecuritySettings
(
  id     VARCHAR(255) NOT NULL
    PRIMARY KEY,
  secret TINYBLOB     NULL
);

ALTER TABLE ApiLogin
  ADD CONSTRAINT FK_79l5wyl5pp20554omivw4s4qf
FOREIGN KEY (settings_type) REFERENCES ApiSecuritySettings (id);

CREATE TABLE AuthToken
(
  id           BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  issuedAt     VARCHAR(255)  NULL,
  name         VARCHAR(255)  NULL,
  token        VARCHAR(1024) NULL,
  tokenGroup   VARCHAR(255)  NULL,
  secret       VARCHAR(1024) NULL,
  refreshToken VARCHAR(1024) NULL,
  userId       VARCHAR(255)  NULL,
  timeToLive   BIGINT        NULL
);

CREATE TABLE BitsSettings
(
  id                              VARCHAR(255) NOT NULL
    PRIMARY KEY,
  bitsToPointsMultiplier          DOUBLE       NOT NULL,
  bitsToPointsPayoutToAllChatters BIT          NOT NULL,
  enableBitsToPoints              BIT          NOT NULL
);

CREATE TABLE ChatModeratorLinkWhitelist
(
  ChatModeratorSettings_id VARCHAR(255) NOT NULL,
  linkWhitelist            VARCHAR(255) NULL
);

CREATE INDEX FK_kxi06yvjylbo4l7ybqt9rp08f
  ON ChatModeratorLinkWhitelist (ChatModeratorSettings_id);

CREATE TABLE ChatModeratorSettings
(
  id                        VARCHAR(255) NOT NULL
    PRIMARY KEY,
  capsTrigger               INT          NULL,
  capsTriggerRatio          DOUBLE       NULL,
  excessiveCapsAllowed      BIT          NULL,
  firstStrike               VARCHAR(255) NULL,
  linkPermitDuration        BIGINT       NULL,
  linksAllowed              BIT          NULL,
  repeatedCharacterTrigger  INT          NULL,
  repeatedCharactersAllowed BIT          NULL,
  secondStrike              VARCHAR(255) NULL,
  thirdStrike               VARCHAR(255) NULL,
  exemptedGroup_id          BIGINT       NULL
);

CREATE INDEX FK_ksca2jivs13p3kfhov4ni03re
  ON ChatModeratorSettings (exemptedGroup_id);

ALTER TABLE ChatModeratorLinkWhitelist
  ADD CONSTRAINT FK_kxi06yvjylbo4l7ybqt9rp08f
FOREIGN KEY (ChatModeratorSettings_id) REFERENCES ChatModeratorSettings (id);

CREATE TABLE Command
(
  id                   BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  command              VARCHAR(255)       NULL,
  cooldown             BIGINT DEFAULT '0' NULL,
  moderatorCanActivate BIT DEFAULT b'0'   NULL,
  price                BIGINT DEFAULT '0' NULL,
  systemCommand        BIT DEFAULT b'0'   NULL,
  userGroup_id         BIGINT             NULL,
  CONSTRAINT UK_3c0aev9r05bfauwu3uo2a7tix
  UNIQUE (command)
);

CREATE INDEX FK_grfvxvy0dqe66yxolmhxk2m4o
  ON Command (userGroup_id);

CREATE TABLE CommandAliasses
(
  Command_id BIGINT       NOT NULL,
  aliasses   VARCHAR(255) NULL,
  CONSTRAINT FK_rhnaan9c1i1epyo7sfiu01ogl
  FOREIGN KEY (Command_id) REFERENCES Command (id)
);

CREATE INDEX FK_rhnaan9c1i1epyo7sfiu01ogl
  ON CommandAliasses (Command_id);

CREATE TABLE CommandHistoryRecord
(
  id          BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  arguments   VARCHAR(255) NULL,
  command     VARCHAR(255) NULL,
  date        VARCHAR(255) NULL,
  success     BIT          NULL,
  triggeredBy VARCHAR(255) NULL,
  user_id     BIGINT       NULL
);

CREATE INDEX FK_fbv6v2shlcjdisy3xasuwli7e
  ON CommandHistoryRecord (user_id);

CREATE TABLE CustomCommand
(
  message VARCHAR(255) NULL,
  id      BIGINT       NOT NULL
    PRIMARY KEY,
  CONSTRAINT FK_ofidj6homyeu550jufkuyu4r9
  FOREIGN KEY (id) REFERENCES Command (id)
    ON DELETE CASCADE
);

CREATE TABLE Donation
(
  id       BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  date     VARCHAR(255) NULL,
  donation VARCHAR(255) NULL,
  note     VARCHAR(255) NULL,
  user_id  BIGINT       NULL
);

CREATE INDEX FK_e1wf9faewj8ipo2200nc9qw1p
  ON Donation (user_id);

CREATE TABLE FollowerWatchSettings
(
  id               VARCHAR(255) NOT NULL
    PRIMARY KEY,
  notifyOnUnFollow BIT          NULL,
  reward           BIGINT       NULL
);

CREATE TABLE Game
(
  id            BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  firstPlayedOn VARCHAR(255) NULL,
  name          VARCHAR(255) NULL,
  CONSTRAINT UK_gdtr9fjw6icy8hhf02kpmvqpc
  UNIQUE (name)
);

CREATE TABLE HostRecord
(
  id        BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  autoHost  BIT          NULL,
  date      VARCHAR(255) NULL,
  direction VARCHAR(255) NULL,
  user_id   BIGINT       NULL
);

CREATE INDEX FK_jx1ey4a1i21et2gckdwirgrub
  ON HostRecord (user_id);

CREATE TABLE HostWatchSettings
(
  id     VARCHAR(255) NOT NULL
    PRIMARY KEY,
  reward BIGINT       NULL
);

CREATE TABLE InvestmentRecord
(
  id          BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  date        VARCHAR(255) NULL,
  interest    DOUBLE       NULL,
  invested    BIGINT       NULL,
  payout      BIGINT       NULL,
  project     VARCHAR(255) NULL,
  invester_id BIGINT       NULL
);

CREATE INDEX FK_5y4kycevwkvb7b2m41d9xf91r
  ON InvestmentRecord (invester_id);

CREATE TABLE InvestmentSettings
(
  id                 VARCHAR(255) NOT NULL
    PRIMARY KEY,
  investmentDuration BIGINT       NULL,
  maxInterest        DOUBLE       NULL,
  minInterest        DOUBLE       NULL
);

CREATE TABLE KillRecord
(
  id        BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  date      VARCHAR(255) NULL,
  isSuicide BIT          NULL,
  killer_id BIGINT       NULL,
  target_id BIGINT       NULL
);

CREATE INDEX FK_b0a45amhihmdfvcydtpqqoyjo
  ON KillRecord (killer_id);

CREATE INDEX FK_jhe8nxu5clcxyig7fcd9oyok6
  ON KillRecord (target_id);

CREATE TABLE ModerationRecord
(
  id      BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  action  VARCHAR(255) NULL,
  date    VARCHAR(255) NULL,
  message VARCHAR(255) NULL,
  reason  VARCHAR(255) NULL,
  user_id BIGINT       NULL
);

CREATE INDEX FK_k7johwbn1colaelentdpkalav
  ON ModerationRecord (user_id);

CREATE TABLE MusicPlayerSettings
(
  id                     VARCHAR(255) NOT NULL
    PRIMARY KEY,
  playerVolume           INT          NULL,
  requestMaxConcurrent   INT          NULL,
  requestMaxSongDuration BIGINT       NULL,
  requestsEnabled        BIT          NULL,
  shuffle                BIT          NULL
);

CREATE TABLE MusicPlayerSong
(
  id       BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  duration BIGINT       NULL,
  title    VARCHAR(255) NULL,
  videoID  VARCHAR(255) NULL,
  CONSTRAINT UK_hm96d44lout94fau61vi5etb2
  UNIQUE (videoID)
);

CREATE TABLE PointsSettings
(
  id                    VARCHAR(255) NOT NULL
    PRIMARY KEY,
  offlinePayoutAmount   BIGINT       NULL,
  offlinePayoutInterval BIGINT       NULL,
  onlinePayoutAmount    BIGINT       NULL,
  onlinePayoutInterval  BIGINT       NULL,
  pointsNamePlural      VARCHAR(255) NULL,
  pointsNameSingular    VARCHAR(255) NULL,
  trackOffline          BIT          NULL,
  trackOnline           BIT          NULL
);

CREATE TABLE Quote
(
  id              BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  date            VARCHAR(255) NULL,
  message         VARCHAR(255) NULL,
  gameAtMoment_id BIGINT       NULL,
  user_id         BIGINT       NULL,
  CONSTRAINT FK_do9c725kihaygbg1wu8wheyku
  FOREIGN KEY (gameAtMoment_id) REFERENCES Game (id)
);

CREATE INDEX FK_do9c725kihaygbg1wu8wheyku
  ON Quote (gameAtMoment_id);

CREATE INDEX FK_j4nuv5yfqags6x38ji917iwnd
  ON Quote (user_id);

CREATE TABLE RaidRecord
(
  id           BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  date         VARCHAR(255) NULL,
  direction    VARCHAR(255) NULL,
  gameAtMoment VARCHAR(255) NULL,
  channel_id   BIGINT       NULL
);

CREATE INDEX FK_9af25maq9y4lcg730povpni5
  ON RaidRecord (channel_id);

CREATE TABLE RouletteRecord
(
  id      BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  date    VARCHAR(255) NULL,
  fatal   BIT          NULL,
  user_id BIGINT       NULL
);

CREATE INDEX FK_8xadv5lr84vxhhej6xdaqro3f
  ON RouletteRecord (user_id);

CREATE TABLE RouletteSettings
(
  id                    VARCHAR(255) NOT NULL
    PRIMARY KEY,
  timeoutOnDeath        BIGINT       NULL,
  timeoutOnDeathEnabled BIT          NULL
);

CREATE TABLE SubscriberWatchSettings
(
  id          VARCHAR(255) NOT NULL
    PRIMARY KEY,
  rewardTier1 BIGINT       NULL,
  rewardTier2 BIGINT       NULL,
  rewardTier3 BIGINT       NULL
);

CREATE TABLE SystemSettings
(
  id             VARCHAR(255) NOT NULL
    PRIMARY KEY,
  enableWhispers BIT          NULL,
  muted          BIT          NULL
);

CREATE TABLE Tamagotchi
(
  id           BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  affection    INT              NULL,
  dateOfBirth  VARCHAR(255)     NULL,
  dateOfDeath  VARCHAR(255)     NULL,
  deceased     BIT DEFAULT b'0' NULL,
  foodStack    DOUBLE           NULL,
  gender       VARCHAR(255)     NULL,
  hygieneLevel DOUBLE           NULL,
  moodLevel    DOUBLE           NULL,
  name         VARCHAR(255)     NULL,
  species      VARCHAR(255)     NULL,
  owner_id     BIGINT           NULL
);

CREATE INDEX FK_tnl3xr2wtg65skosq83p1dcjh
  ON Tamagotchi (owner_id);

CREATE TABLE TamagotchiSettings
(
  id                   VARCHAR(255) NOT NULL
    PRIMARY KEY,
  botTamagotchiEnabled BIT          NULL,
  foodPrice            BIGINT       NULL,
  maxFood              DOUBLE       NULL,
  maxHygiene           DOUBLE       NULL,
  maxMood              DOUBLE       NULL,
  nameMaxLength        INT          NULL,
  newPrice             BIGINT       NULL,
  soapPrice            BIGINT       NULL
);

CREATE TABLE TamagotchiToys
(
  tamagotchi_id   BIGINT       NOT NULL,
  expiresAt       VARCHAR(255) NULL,
  foodModifier    DOUBLE       NULL,
  hygieneModifier DOUBLE       NULL,
  moodModifier    DOUBLE       NULL,
  toyName         VARCHAR(255) NULL,
  CONSTRAINT FK_bvetm8utj6imjfuek5jntb4b5
  FOREIGN KEY (tamagotchi_id) REFERENCES Tamagotchi (id)
);

CREATE INDEX FK_bvetm8utj6imjfuek5jntb4b5
  ON TamagotchiToys (tamagotchi_id);

CREATE TABLE Template
(
  id          BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  description VARCHAR(255) NULL,
  template    VARCHAR(255) NULL,
  templateKey VARCHAR(255) NULL,
  CONSTRAINT UK_al34jm30j5sri8b7b7ajqn4yr
  UNIQUE (templateKey)
);

CREATE TABLE TemplateKeys
(
  Template_id         BIGINT                  NOT NULL,
  keyDescriptions     VARCHAR(255)            NULL,
  keyDescriptions_KEY VARCHAR(255) DEFAULT '' NOT NULL,
  PRIMARY KEY (Template_id, keyDescriptions_KEY),
  CONSTRAINT FK_5ueufbwc110mrriocgam5vhf8
  FOREIGN KEY (Template_id) REFERENCES Template (id)
);

CREATE TABLE TimeTrackingSettings
(
  id           VARCHAR(255) NOT NULL
    PRIMARY KEY,
  trackOffline BIT          NULL,
  trackOnline  BIT          NULL
);

CREATE TABLE TwitchOauthData
(
  id                 VARCHAR(255) NOT NULL
    PRIMARY KEY,
  botAccessToken     VARCHAR(255) NULL,
  channelAccessToken VARCHAR(255) NULL
);

CREATE TABLE TwitterSettings
(
  id VARCHAR(255) NOT NULL
    PRIMARY KEY
);

CREATE TABLE TwitterSettingsTrackedKeywords
(
  settings_type   VARCHAR(255) NOT NULL,
  trackedKeywords VARCHAR(255) NULL,
  CONSTRAINT FK_2w75c5kf4345tn3hel43pqogj
  FOREIGN KEY (settings_type) REFERENCES TwitterSettings (id)
);

CREATE INDEX FK_2w75c5kf4345tn3hel43pqogj
  ON TwitterSettingsTrackedKeywords (settings_type);

CREATE TABLE User
(
  id               BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  blacklistedSince VARCHAR(255)       NULL,
  caster           BIT DEFAULT b'0'   NULL,
  displayName      VARCHAR(255)       NULL,
  followDate       VARCHAR(255)       NULL,
  follower         BIT DEFAULT b'0'   NULL,
  moderator        BIT DEFAULT b'0'   NULL,
  points           BIGINT DEFAULT '0' NULL,
  recordedTime     BIGINT DEFAULT '0' NULL,
  subscribeDate    VARCHAR(255)       NULL,
  subscriber       BIT                NULL,
  title            VARCHAR(255)       NULL,
  twitchUserId     BIGINT             NULL,
  username         VARCHAR(255)       NULL,
  userGroup_id     BIGINT             NULL,
  CONSTRAINT UK_7xw0shkd9nnp9v3qg6da7vnmf
  UNIQUE (twitchUserId),
  CONSTRAINT UK_jreodf78a7pl5qidfh43axdfb
  UNIQUE (username)
);

CREATE INDEX FK_fgyb05t42ekbvvim2bj3cd2h7
  ON User (userGroup_id);

ALTER TABLE AdventureRecord
  ADD CONSTRAINT FK_qbop5pjpdk1j9x70vmfb09spk
FOREIGN KEY (adventurer_id) REFERENCES User (id);

ALTER TABLE ApiLogin
  ADD CONSTRAINT FK_np0cgxtsjx57h0cb0glbjuajp
FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE CommandHistoryRecord
  ADD CONSTRAINT FK_fbv6v2shlcjdisy3xasuwli7e
FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE Donation
  ADD CONSTRAINT FK_e1wf9faewj8ipo2200nc9qw1p
FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE HostRecord
  ADD CONSTRAINT FK_jx1ey4a1i21et2gckdwirgrub
FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE InvestmentRecord
  ADD CONSTRAINT FK_5y4kycevwkvb7b2m41d9xf91r
FOREIGN KEY (invester_id) REFERENCES User (id);

ALTER TABLE KillRecord
  ADD CONSTRAINT FK_b0a45amhihmdfvcydtpqqoyjo
FOREIGN KEY (killer_id) REFERENCES User (id);

ALTER TABLE KillRecord
  ADD CONSTRAINT FK_jhe8nxu5clcxyig7fcd9oyok6
FOREIGN KEY (target_id) REFERENCES User (id);

ALTER TABLE ModerationRecord
  ADD CONSTRAINT FK_k7johwbn1colaelentdpkalav
FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE Quote
  ADD CONSTRAINT FK_j4nuv5yfqags6x38ji917iwnd
FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE RaidRecord
  ADD CONSTRAINT FK_9af25maq9y4lcg730povpni5
FOREIGN KEY (channel_id) REFERENCES User (id);

ALTER TABLE RouletteRecord
  ADD CONSTRAINT FK_8xadv5lr84vxhhej6xdaqro3f
FOREIGN KEY (user_id) REFERENCES User (id);

ALTER TABLE Tamagotchi
  ADD CONSTRAINT FK_tnl3xr2wtg65skosq83p1dcjh
FOREIGN KEY (owner_id) REFERENCES User (id);

CREATE TABLE UserGreeting
(
  id      BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  message VARCHAR(255) NULL,
  user_id BIGINT       NULL,
  CONSTRAINT FK_4ecg31enlo3b8xabob7vpn0y3
  FOREIGN KEY (user_id) REFERENCES User (id)
);

CREATE INDEX FK_4ecg31enlo3b8xabob7vpn0y3
  ON UserGreeting (user_id);

CREATE TABLE UserGreetingSettings
(
  id              VARCHAR(255) NOT NULL
    PRIMARY KEY,
  enableGreetings BIT          NULL,
  greetingTimeout BIGINT       NULL
);

CREATE TABLE UserGroup
(
  id           BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  defaultGroup BIT          NULL,
  levelUpHours INT          NULL,
  name         VARCHAR(255) NULL,
  weight       INT          NULL,
  CONSTRAINT UK_goltv3uluasxk8pug2y7x0f1h
  UNIQUE (name),
  CONSTRAINT UK_p318n1yxjlbxl7l0g4elrnkf5
  UNIQUE (weight)
);

ALTER TABLE ChatModeratorSettings
  ADD CONSTRAINT FK_ksca2jivs13p3kfhov4ni03re
FOREIGN KEY (exemptedGroup_id) REFERENCES UserGroup (id);

ALTER TABLE Command
  ADD CONSTRAINT FK_grfvxvy0dqe66yxolmhxk2m4o
FOREIGN KEY (userGroup_id) REFERENCES UserGroup (id);

ALTER TABLE User
  ADD CONSTRAINT FK_fgyb05t42ekbvvim2bj3cd2h7
FOREIGN KEY (userGroup_id) REFERENCES UserGroup (id);

