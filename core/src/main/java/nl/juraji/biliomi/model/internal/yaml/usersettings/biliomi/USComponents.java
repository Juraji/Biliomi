package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi;

import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components.*;

import java.util.List;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USComponents {
  private USAdventures adventures;
  private List<String> badwords;
  private USGameMessages gameMessages;
  private USInvestmentGame investmentGame;
  private USKillGame killGame;
  private USRouletteGame rouletteGame;
  private USSlotMachineGame slotMachineGame;
  private USTamagotchis tamagotchis;

  public USAdventures getAdventures() {
    return adventures;
  }

  public void setAdventures(USAdventures adventures) {
    this.adventures = adventures;
  }

  public List<String> getBadwords() {
    return badwords;
  }

  public void setBadwords(List<String> badwords) {
    this.badwords = badwords;
  }

  public USGameMessages getGameMessages() {
    return gameMessages;
  }

  public void setGameMessages(USGameMessages gameMessages) {
    this.gameMessages = gameMessages;
  }

  public USInvestmentGame getInvestmentGame() {
    return investmentGame;
  }

  public void setInvestmentGame(USInvestmentGame investmentGame) {
    this.investmentGame = investmentGame;
  }

  public USKillGame getKillGame() {
    return killGame;
  }

  public void setKillGame(USKillGame killGame) {
    this.killGame = killGame;
  }

  public USRouletteGame getRouletteGame() {
    return rouletteGame;
  }

  public void setRouletteGame(USRouletteGame rouletteGame) {
    this.rouletteGame = rouletteGame;
  }

  public USSlotMachineGame getSlotMachineGame() {
    return slotMachineGame;
  }

  public void setSlotMachineGame(USSlotMachineGame slotMachineGame) {
    this.slotMachineGame = slotMachineGame;
  }

  public USTamagotchis getTamagotchis() {
    return tamagotchis;
  }

  public void setTamagotchis(USTamagotchis tamagotchis) {
    this.tamagotchis = tamagotchis;
  }
}
