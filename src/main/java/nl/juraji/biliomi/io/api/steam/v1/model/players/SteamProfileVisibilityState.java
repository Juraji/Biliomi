package nl.juraji.biliomi.io.api.steam.v1.model.players;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

/**
 * Created by Juraji on 5-10-2017.
 * Biliomi
 */
public enum SteamProfileVisibilityState {
  NOT_VISIBLE(1), VISIBLE(3);

  private int visibiltyState;

  SteamProfileVisibilityState(int visibiltyState) {
    this.visibiltyState = visibiltyState;
  }

  public int getVisibiltyState() {
    return visibiltyState;
  }

  @JsonCreator
  public static SteamProfileVisibilityState fromJson(int visibiltyState) {
    return Arrays.stream(SteamProfileVisibilityState.values())
        .filter(state -> state.getVisibiltyState() == visibiltyState)
        .findFirst()
        .orElse(null);
  }
}
