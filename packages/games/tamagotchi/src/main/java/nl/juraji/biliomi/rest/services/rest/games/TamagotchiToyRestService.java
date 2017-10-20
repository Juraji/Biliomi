package nl.juraji.biliomi.rest.services.rest.games;

import nl.juraji.biliomi.components.games.tamagotchi.services.ToyFactoryService;
import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.model.games.TamagotchiDao;
import nl.juraji.biliomi.model.games.TamagotchiToy;
import nl.juraji.biliomi.rest.config.ModelRestService;
import nl.juraji.biliomi.rest.config.Responses;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/games/tamagotchis/toys")
public class TamagotchiToyRestService extends ModelRestService<TamagotchiToy> {

  @Inject
  private TamagotchiDao tamagotchiDao;

  @Inject
  private ToyFactoryService toyFactoryService;

  @POST
  @Path("/assign/{tamagotchiid}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response assignToyToTamagotchi(TamagotchiToy tamagotchiToy, @PathParam("tamagotchiid") long tamagotchiId) {
    if (tamagotchiToy == null) {
      return Responses.badRequest();
    }

    Tamagotchi tamagotchi = tamagotchiDao.get(tamagotchiId);
    TamagotchiToy toy = toyFactoryService.getToy(tamagotchiToy.getToyName());

    if (tamagotchi == null || toy == null || toyFactoryService.tamagotchiHasToy(tamagotchi, toy)) {
      return Responses.notModified();
    }

    tamagotchi.getToys().add(toy);
    tamagotchiDao.save(tamagotchi);
    return Responses.ok(tamagotchi);
  }

  @Override
  public List<TamagotchiToy> getEntities() {
    return toyFactoryService.getList();
  }

  @Override
  public TamagotchiToy getEntity(long id) {
    throw new ForbiddenException();
  }

  @Override
  public TamagotchiToy createEntity(TamagotchiToy e) {
    throw new ForbiddenException();
  }

  @Override
  public TamagotchiToy updateEntity(TamagotchiToy e, long id) {
    throw new ForbiddenException();
  }

  @Override
  public boolean deleteEntity(long id) {
    throw new ForbiddenException();
  }
}
