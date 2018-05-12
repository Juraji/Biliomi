import base64
import json
import requests
import time


class BiliomiApiException(Exception):
    pass


class OBSBiliomiApi:
    """
    Used by the other OBS scripts to communicate with Biliomi
    """
    api_base = ""
    username = ""
    password = ""
    auth_token = ""
    refresh_token = ""

    def __get_url__(self, path):
        return self.api_base + path

    def __get_request_headers__(self):
        return {"Authorization": self.auth_token}

    @staticmethod
    def __is_token_expired__(token):
        if token is None or token == "":
            return True
        else:
            data = json.loads(base64.urlsafe_b64decode(token.split(".", 2)[1] + "=="))
            return data["exp"] <= time.time()

    def __authorize__(self):
        if self.auth_token == "" or self.__is_token_expired__(self.auth_token):
            request_payload = {"Username": self.username, "Password": self.password}
            r = requests.post(self.__get_url__("/api/auth/login"), json=request_payload)
            if r.status_code == 200:
                data = r.json()
                if data["AuthorizationToken"] == "":
                    raise BiliomiApiException("Failed retrieving token: " + data["Message"])
                else:
                    self.auth_token = data["AuthorizationToken"]
                    self.refresh_token = data["RefreshToken"]
            else:
                raise BiliomiApiException("Failed retrieving token: " + r.text)

    def set_api_base(self, api_base):
        self.api_base = api_base

    def set_username(self, username):
        self.username = username

    def set_password(self, password):
        self.password = password

    def get_latest_follower(self):
        self.__authorize__()
        r = requests.get(self.__get_url__("/api/core/users/latest/followers"), headers=self.__get_request_headers__())
        if r.status_code == 200:
            users = r.json()
            if len(users["Entities"]) > 0:
                return users["Entities"][0]["DisplayName"]
            else:
                return None

    def get_latest_subscriber(self):
        self.__authorize__()
        r = requests.get(self.__get_url__("/api/core/users/latest/subscribers"), headers=self.__get_request_headers__())
        if r.status_code == 200:
            users = r.json()
            if len(users["Entities"]) > 0:
                return users["Entities"][0]["DisplayName"]
            else:
                return None
