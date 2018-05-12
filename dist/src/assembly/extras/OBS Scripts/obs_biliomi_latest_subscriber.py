import obspython as obs

from obs_biliomi_api import OBSBiliomiApi, BiliomiApiException

# Biliomi for OBS.
# Facilitates authentication for the other OBS scripts
#
# Written by Juraji

# - Script Variables ---------------------------------------------------------------------------------------------------
BILIOMI = None
UPDATE_INTERVAL = 0
PREFIX = None
TRACKING_SUBSCRIBERS = False
LATEST_SUBSCRIBER_SOURCE = None


# - Script Methods -----------------------------------------------------------------------------------------------------

def update_latest_subscriber():
    global BILIOMI
    global LATEST_SUBSCRIBER_SOURCE
    global PREFIX

    try:
        if LATEST_SUBSCRIBER_SOURCE != "":
            latest_subscriber = BILIOMI.get_latest_subscriber()
            if latest_subscriber is not None:
                print("Updating latest subscriber: " + latest_subscriber)
                if PREFIX is not None and PREFIX != "":
                    latest_subscriber = PREFIX + latest_subscriber

                source = obs.obs_get_source_by_name(LATEST_SUBSCRIBER_SOURCE)
                if source is not None:
                    settings = obs.obs_data_create()
                    obs.obs_data_set_string(settings, "text", latest_subscriber)
                    obs.obs_source_update(source, settings)
                    obs.obs_data_release(settings)
                    obs.obs_source_release(source)

    except BiliomiApiException as e:
        print("Failed fetching latest follower: " + str(e))


def source_activated(calldata):
    source = obs.calldata_source(calldata, "source")

    if source is not None:
        name = obs.obs_source_get_name(source)
        if name == LATEST_SUBSCRIBER_SOURCE and not TRACKING_SUBSCRIBERS:
            update_latest_subscriber()
            obs.timer_add(update_latest_subscriber, UPDATE_INTERVAL * 1000)


def source_deactivated(calldata):
    source = obs.calldata_source(calldata, "source")

    if source is not None:
        name = obs.obs_source_get_name(source)
        if name == LATEST_SUBSCRIBER_SOURCE and TRACKING_SUBSCRIBERS:
            obs.timer_remove(update_latest_subscriber)


# - OBS Methods --------------------------------------------------------------------------------------------------------

def script_description():
    return "Biliomi for OBS: Latest follower.\n" \
           + "Fetch the latest subscriber via Biliomi's Api.\n\n" \
           + "Written by Juraji"


def script_update(settings):
    global BILIOMI
    global UPDATE_INTERVAL
    global LATEST_SUBSCRIBER_SOURCE
    global PREFIX

    if BILIOMI is None:
        BILIOMI = OBSBiliomiApi()

    BILIOMI.set_api_base(obs.obs_data_get_string(settings, "api_base"))
    BILIOMI.set_username(obs.obs_data_get_string(settings, "username"))
    BILIOMI.set_password(obs.obs_data_get_string(settings, "password"))

    PREFIX = obs.obs_data_get_string(settings, "prefix")
    UPDATE_INTERVAL = obs.obs_data_get_int(settings, "update_interval")
    LATEST_SUBSCRIBER_SOURCE = obs.obs_data_get_string(settings, "latest_subscriber_source")


def script_defaults(settings):
    obs.obs_data_set_default_string(settings, "api_base", "http://localhost:30000")
    obs.obs_data_set_default_int(settings, "update_interval", 300)
    obs.obs_data_set_default_string(settings, "prefix", "Latest subscriber: ")


def script_properties():
    properties = obs.obs_properties_create()

    obs.obs_properties_add_text(properties, "api_base", "Biliomi's Api base url", obs.OBS_TEXT_DEFAULT)
    obs.obs_properties_add_text(properties, "username", "Username", obs.OBS_TEXT_DEFAULT)
    obs.obs_properties_add_text(properties, "password", "Password", obs.OBS_TEXT_PASSWORD)
    obs.obs_properties_add_int(properties, "update_interval", "Update Interval (seconds)", 30, 3600, 1)

    subscriber_source = obs.obs_properties_add_list(properties, "latest_subscriber_source", "Latest Subscriber Source",
                                                    obs.OBS_COMBO_TYPE_EDITABLE, obs.OBS_COMBO_FORMAT_STRING)

    obs.obs_properties_add_text(properties, "prefix", "Prefix", obs.OBS_TEXT_DEFAULT)

    sources = obs.obs_enum_sources()
    if sources is not None:
        for source in sources:
            source_id = obs.obs_source_get_id(source)
            if source_id == "text_gdiplus" or source_id == "text_ft2_source":
                name = obs.obs_source_get_name(source)
                obs.obs_property_list_add_string(subscriber_source, name, name)

        obs.source_list_release(sources)

    return properties


def script_load(settings):
    script_update(settings)
    sig = obs.obs_get_signal_handler()
    obs.signal_handler_connect(sig, "source_activate", source_activated)
    obs.signal_handler_connect(sig, "source_deactivate", source_deactivated)
