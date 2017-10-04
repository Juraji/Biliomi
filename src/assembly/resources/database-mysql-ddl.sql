create table AdventureRecord
(
	id bigint auto_increment
		primary key,
	bet bigint null,
	byTamagotchi bit null,
	date varchar(255) null,
	payout bigint null,
	adventurer_id bigint null
)
;

create index FK_qbop5pjpdk1j9x70vmfb09spk
	on AdventureRecord (adventurer_id)
;

create table AdventureSettings
(
	id varchar(255) not null
		primary key,
	cooldown bigint null,
	joinTimeout bigint null,
	maximumBet bigint null,
	minimumBet bigint null,
	winMultiplier double null
)
;

create table Announcement
(
	id bigint auto_increment
		primary key,
	message varchar(255) null
)
;

create table AnnouncementsSettings
(
	id varchar(255) not null
		primary key,
	enabled bit null,
	minChatMessages int null,
	runInterval bigint null,
	shuffle bit null
)
;

create table ApiLogin
(
	settings_type varchar(255) not null,
	password tinyblob null,
	salt tinyblob null,
	user_id bigint null
)
;

create index FK_79l5wyl5pp20554omivw4s4qf
	on ApiLogin (settings_type)
;

create index FK_np0cgxtsjx57h0cb0glbjuajp
	on ApiLogin (user_id)
;

create table ApiSecuritySettings
(
	id varchar(255) not null
		primary key,
	secret tinyblob null
)
;

alter table ApiLogin
	add constraint FK_79l5wyl5pp20554omivw4s4qf
		foreign key (settings_type) references ApiSecuritySettings (id)
;

create table AuthToken
(
	id bigint auto_increment
		primary key,
	issuedAt varchar(255) null,
	name varchar(255) null,
	token varchar(1024) null,
	tokenGroup varchar(255) null,
	secret varchar(1024) null,
	refreshToken varchar(1024) null,
	userId varchar(255) null,
	timeToLive bigint null
)
;

create table BitsSettings
(
	id varchar(255) not null
		primary key,
	bitsToPointsMultiplier double not null,
	bitsToPointsPayoutToAllChatters bit not null,
	enableBitsToPoints bit not null
)
;

create table ChatModeratorLinkWhitelist
(
	ChatModeratorSettings_id varchar(255) not null,
	linkWhitelist varchar(255) null
)
;

create index FK_kxi06yvjylbo4l7ybqt9rp08f
	on ChatModeratorLinkWhitelist (ChatModeratorSettings_id)
;

create table ChatModeratorSettings
(
	id varchar(255) not null
		primary key,
	capsTrigger int null,
	capsTriggerRatio double null,
	excessiveCapsAllowed bit null,
	firstStrike varchar(255) null,
	linkPermitDuration bigint null,
	linksAllowed bit null,
	repeatedCharacterTrigger int null,
	repeatedCharactersAllowed bit null,
	secondStrike varchar(255) null,
	thirdStrike varchar(255) null,
	exemptedGroup_id bigint null
)
;

create index FK_ksca2jivs13p3kfhov4ni03re
	on ChatModeratorSettings (exemptedGroup_id)
;

alter table ChatModeratorLinkWhitelist
	add constraint FK_kxi06yvjylbo4l7ybqt9rp08f
		foreign key (ChatModeratorSettings_id) references ChatModeratorSettings (id)
;

create table Command
(
	id bigint auto_increment
		primary key,
	command varchar(255) null,
	cooldown bigint default '0' null,
	moderatorCanActivate bit default b'0' null,
	price bigint default '0' null,
	systemCommand bit default b'0' null,
	userGroup_id bigint null,
	constraint UK_3c0aev9r05bfauwu3uo2a7tix
		unique (command)
)
;

create index FK_grfvxvy0dqe66yxolmhxk2m4o
	on Command (userGroup_id)
;

create table CommandAliasses
(
	Command_id bigint not null,
	aliasses varchar(255) null,
	constraint FK_rhnaan9c1i1epyo7sfiu01ogl
		foreign key (Command_id) references Command (id)
)
;

create index FK_rhnaan9c1i1epyo7sfiu01ogl
	on CommandAliasses (Command_id)
;

create table CommandHistoryRecord
(
	id bigint auto_increment
		primary key,
	arguments varchar(255) null,
	command varchar(255) null,
	date varchar(255) null,
	success bit null,
	triggeredBy varchar(255) null,
	user_id bigint null
)
;

create index FK_fbv6v2shlcjdisy3xasuwli7e
	on CommandHistoryRecord (user_id)
;

create table CustomCommand
(
	message varchar(255) null,
	id bigint not null
		primary key,
	constraint FK_ofidj6homyeu550jufkuyu4r9
		foreign key (id) references Command (id)
			on delete cascade
)
;

create table Donation
(
	id bigint auto_increment
		primary key,
	date varchar(255) null,
	donation varchar(255) null,
	note varchar(255) null,
	user_id bigint null
)
;

create index FK_e1wf9faewj8ipo2200nc9qw1p
	on Donation (user_id)
;

create table FollowerWatchSettings
(
	id varchar(255) not null
		primary key,
	notifyOnUnFollow bit null,
	reward bigint null
)
;

create table Game
(
	id bigint auto_increment
		primary key,
	firstPlayedOn varchar(255) null,
	name varchar(255) null,
	constraint UK_gdtr9fjw6icy8hhf02kpmvqpc
		unique (name)
)
;

create table HostRecord
(
	id bigint auto_increment
		primary key,
	autoHost bit null,
	date varchar(255) null,
	direction varchar(255) null,
	user_id bigint null
)
;

create index FK_jx1ey4a1i21et2gckdwirgrub
	on HostRecord (user_id)
;

create table HostWatchSettings
(
	id varchar(255) not null
		primary key,
	reward bigint null
)
;

create table InvestmentRecord
(
	id bigint auto_increment
		primary key,
	date varchar(255) null,
	interest double null,
	invested bigint null,
	payout bigint null,
	project varchar(255) null,
	invester_id bigint null
)
;

create index FK_5y4kycevwkvb7b2m41d9xf91r
	on InvestmentRecord (invester_id)
;

create table InvestmentSettings
(
	id varchar(255) not null
		primary key,
	investmentDuration bigint null,
	maxInterest double null,
	minInterest double null
)
;

create table KillRecord
(
	id bigint auto_increment
		primary key,
	date varchar(255) null,
	isSuicide bit null,
	killer_id bigint null,
	target_id bigint null
)
;

create index FK_b0a45amhihmdfvcydtpqqoyjo
	on KillRecord (killer_id)
;

create index FK_jhe8nxu5clcxyig7fcd9oyok6
	on KillRecord (target_id)
;

create table ModerationRecord
(
	id bigint auto_increment
		primary key,
	action varchar(255) null,
	date varchar(255) null,
	message varchar(255) null,
	reason varchar(255) null,
	user_id bigint null
)
;

create index FK_k7johwbn1colaelentdpkalav
	on ModerationRecord (user_id)
;

create table MusicPlayerSettings
(
	id varchar(255) not null
		primary key,
	playerVolume int null,
	requestMaxConcurrent int null,
	requestMaxSongDuration bigint null,
	requestsEnabled bit null,
	shuffle bit null
)
;

create table MusicPlayerSong
(
	id bigint auto_increment
		primary key,
	duration bigint null,
	title varchar(255) null,
	videoID varchar(255) null,
	constraint UK_hm96d44lout94fau61vi5etb2
		unique (videoID)
)
;

create table PointsSettings
(
	id varchar(255) not null
		primary key,
	offlinePayoutAmount bigint null,
	offlinePayoutInterval bigint null,
	onlinePayoutAmount bigint null,
	onlinePayoutInterval bigint null,
	pointsNamePlural varchar(255) null,
	pointsNameSingular varchar(255) null,
	trackOffline bit null,
	trackOnline bit null
)
;

create table Quote
(
	id bigint auto_increment
		primary key,
	date varchar(255) null,
	message varchar(255) null,
	gameAtMoment_id bigint null,
	user_id bigint null,
	constraint FK_do9c725kihaygbg1wu8wheyku
		foreign key (gameAtMoment_id) references Game (id)
)
;

create index FK_do9c725kihaygbg1wu8wheyku
	on Quote (gameAtMoment_id)
;

create index FK_j4nuv5yfqags6x38ji917iwnd
	on Quote (user_id)
;

create table RaidRecord
(
	id bigint auto_increment
		primary key,
	date varchar(255) null,
	direction varchar(255) null,
	gameAtMoment varchar(255) null,
	channel_id bigint null
)
;

create index FK_9af25maq9y4lcg730povpni5
	on RaidRecord (channel_id)
;

create table RouletteRecord
(
	id bigint auto_increment
		primary key,
	date varchar(255) null,
	fatal bit null,
	user_id bigint null
)
;

create index FK_8xadv5lr84vxhhej6xdaqro3f
	on RouletteRecord (user_id)
;

create table RouletteSettings
(
	id varchar(255) not null
		primary key,
	timeoutOnDeath bigint null,
	timeoutOnDeathEnabled bit null
)
;

create table SubscriberWatchSettings
(
	id varchar(255) not null
		primary key,
	rewardTier1 bigint null,
	rewardTier2 bigint null,
	rewardTier3 bigint null
)
;

create table SystemSettings
(
	id varchar(255) not null
		primary key,
	enableWhispers bit null,
	muted bit null
)
;

create table Tamagotchi
(
	id bigint auto_increment
		primary key,
	affection int null,
	dateOfBirth varchar(255) null,
	dateOfDeath varchar(255) null,
	deceased bit default b'0' null,
	foodStack double null,
	gender varchar(255) null,
	hygieneLevel double null,
	moodLevel double null,
	name varchar(255) null,
	species varchar(255) null,
	owner_id bigint null
)
;

create index FK_tnl3xr2wtg65skosq83p1dcjh
	on Tamagotchi (owner_id)
;

create table TamagotchiSettings
(
	id varchar(255) not null
		primary key,
	botTamagotchiEnabled bit null,
	foodPrice bigint null,
	maxFood double null,
	maxHygiene double null,
	maxMood double null,
	nameMaxLength int null,
	newPrice bigint null,
	soapPrice bigint null
)
;

create table TamagotchiToys
(
	tamagotchi_id bigint not null,
	expiresAt varchar(255) null,
	foodModifier double null,
	hygieneModifier double null,
	moodModifier double null,
	toyName varchar(255) null,
	constraint FK_bvetm8utj6imjfuek5jntb4b5
		foreign key (tamagotchi_id) references Tamagotchi (id)
)
;

create index FK_bvetm8utj6imjfuek5jntb4b5
	on TamagotchiToys (tamagotchi_id)
;

create table Template
(
	id bigint auto_increment
		primary key,
	description varchar(255) null,
	template varchar(255) null,
	templateKey varchar(255) null,
	constraint UK_al34jm30j5sri8b7b7ajqn4yr
		unique (templateKey)
)
;

create table TemplateKeys
(
	Template_id bigint not null,
	keyDescriptions varchar(255) null,
	keyDescriptions_KEY varchar(255) default '' not null,
	primary key (Template_id, keyDescriptions_KEY),
	constraint FK_5ueufbwc110mrriocgam5vhf8
		foreign key (Template_id) references Template (id)
)
;

create table TimeTrackingSettings
(
	id varchar(255) not null
		primary key,
	trackOffline bit null,
	trackOnline bit null
)
;

create table TwitchOauthData
(
	id varchar(255) not null
		primary key,
	botAccessToken varchar(255) null,
	channelAccessToken varchar(255) null
)
;

create table TwitterSettings
(
	id varchar(255) not null
		primary key
)
;

create table TwitterSettingsTrackedKeywords
(
	settings_type varchar(255) not null,
	trackedKeywords varchar(255) null,
	constraint FK_2w75c5kf4345tn3hel43pqogj
		foreign key (settings_type) references TwitterSettings (id)
)
;

create index FK_2w75c5kf4345tn3hel43pqogj
	on TwitterSettingsTrackedKeywords (settings_type)
;

create table User
(
	id bigint auto_increment
		primary key,
	blacklistedSince varchar(255) null,
	caster bit default b'0' null,
	displayName varchar(255) null,
	followDate varchar(255) null,
	follower bit default b'0' null,
	moderator bit default b'0' null,
	points bigint default '0' null,
	recordedTime bigint default '0' null,
	subscribeDate varchar(255) null,
	subscriber bit null,
	title varchar(255) null,
	twitchUserId bigint null,
	username varchar(255) null,
	userGroup_id bigint null,
	constraint UK_7xw0shkd9nnp9v3qg6da7vnmf
		unique (twitchUserId),
	constraint UK_jreodf78a7pl5qidfh43axdfb
		unique (username)
)
;

create index FK_fgyb05t42ekbvvim2bj3cd2h7
	on User (userGroup_id)
;

alter table AdventureRecord
	add constraint FK_qbop5pjpdk1j9x70vmfb09spk
		foreign key (adventurer_id) references User (id)
;

alter table ApiLogin
	add constraint FK_np0cgxtsjx57h0cb0glbjuajp
		foreign key (user_id) references User (id)
;

alter table CommandHistoryRecord
	add constraint FK_fbv6v2shlcjdisy3xasuwli7e
		foreign key (user_id) references User (id)
;

alter table Donation
	add constraint FK_e1wf9faewj8ipo2200nc9qw1p
		foreign key (user_id) references User (id)
;

alter table HostRecord
	add constraint FK_jx1ey4a1i21et2gckdwirgrub
		foreign key (user_id) references User (id)
;

alter table InvestmentRecord
	add constraint FK_5y4kycevwkvb7b2m41d9xf91r
		foreign key (invester_id) references User (id)
;

alter table KillRecord
	add constraint FK_b0a45amhihmdfvcydtpqqoyjo
		foreign key (killer_id) references User (id)
;

alter table KillRecord
	add constraint FK_jhe8nxu5clcxyig7fcd9oyok6
		foreign key (target_id) references User (id)
;

alter table ModerationRecord
	add constraint FK_k7johwbn1colaelentdpkalav
		foreign key (user_id) references User (id)
;

alter table Quote
	add constraint FK_j4nuv5yfqags6x38ji917iwnd
		foreign key (user_id) references User (id)
;

alter table RaidRecord
	add constraint FK_9af25maq9y4lcg730povpni5
		foreign key (channel_id) references User (id)
;

alter table RouletteRecord
	add constraint FK_8xadv5lr84vxhhej6xdaqro3f
		foreign key (user_id) references User (id)
;

alter table Tamagotchi
	add constraint FK_tnl3xr2wtg65skosq83p1dcjh
		foreign key (owner_id) references User (id)
;

create table UserGreeting
(
	id bigint auto_increment
		primary key,
	message varchar(255) null,
	user_id bigint null,
	constraint FK_4ecg31enlo3b8xabob7vpn0y3
		foreign key (user_id) references User (id)
)
;

create index FK_4ecg31enlo3b8xabob7vpn0y3
	on UserGreeting (user_id)
;

create table UserGreetingSettings
(
	id varchar(255) not null
		primary key,
	enableGreetings bit null,
	greetingTimeout bigint null
)
;

create table UserGroup
(
	id bigint auto_increment
		primary key,
	defaultGroup bit null,
	levelUpHours int null,
	name varchar(255) null,
	weight int null,
	constraint UK_goltv3uluasxk8pug2y7x0f1h
		unique (name),
	constraint UK_p318n1yxjlbxl7l0g4elrnkf5
		unique (weight)
)
;

alter table ChatModeratorSettings
	add constraint FK_ksca2jivs13p3kfhov4ni03re
		foreign key (exemptedGroup_id) references UserGroup (id)
;

alter table Command
	add constraint FK_grfvxvy0dqe66yxolmhxk2m4o
		foreign key (userGroup_id) references UserGroup (id)
;

alter table User
	add constraint FK_fgyb05t42ekbvvim2bj3cd2h7
		foreign key (userGroup_id) references UserGroup (id)
;

