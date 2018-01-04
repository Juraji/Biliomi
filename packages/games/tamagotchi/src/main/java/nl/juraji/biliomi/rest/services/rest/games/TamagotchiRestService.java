package nl.juraji.biliomi.rest.services.rest.games;

import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.model.games.TamagotchiDao;
import nl.juraji.biliomi.model.internal.rest.PaginatedResponse;
import nl.juraji.biliomi.rest.config.ModelRestService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/games/tamagotchis")
public class TamagotchiRestService extends ModelRestService<Tamagotchi> {

  @Inject
  private TamagotchiDao tamagotchiDao;

  @GET
  @Path("/deceased")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getDeceasedTamagotchis() {
    List<Tamagotchi> tamagotchis = tamagotchiDao.getDeceasedTamagotchis();
    return PaginatedResponse.create(tamagotchis);
  }

  @Override
  public List<Tamagotchi> getEntities() {
    return tamagotchiDao.getAliveTamagotchis();
  }

  @Override
  public Tamagotchi getEntity(long id) {
    return tamagotchiDao.get(id);
  }

  @Override
  public Tamagotchi createEntity(Tamagotchi e) {
    tamagotchiDao.save(e);
    return e;
  }

  @Override
  public Tamagotchi updateEntity(Tamagotchi e, long id) {
    Tamagotchi tamagotchi = tamagotchiDao.get(id);

    if (tamagotchi == null) {
      return null;
    }

    tamagotchi.setName(e.getName());
    tamagotchi.setSpecies(e.getSpecies());
    tamagotchi.setOwner(e.getOwner());
    tamagotchi.setGender(e.getGender());
    tamagotchi.setFoodStack(e.getFoodStack());
    tamagotchi.setMoodLevel(e.getMoodLevel());
    tamagotchi.setHygieneLevel(e.getHygieneLevel());
    tamagotchi.setAffection(e.getAffection());
    tamagotchi.setDeceased(e.isDeceased());
    tamagotchi.setDateOfBirth(e.getDateOfBirth());
    tamagotchi.setDateOfDeath(e.getDateOfDeath());

    tamagotchi.getToys().clear();
    tamagotchi.getToys().addAll(e.getToys());

    tamagotchiDao.save(tamagotchi);
    return tamagotchi;
  }

  @Override
  public boolean deleteEntity(long id) {
    Tamagotchi tamagotchi = tamagotchiDao.get(id);

    if (tamagotchi == null) {
      return false;
    }

    tamagotchiDao.delete(tamagotchi);
    return true;
  }
}
