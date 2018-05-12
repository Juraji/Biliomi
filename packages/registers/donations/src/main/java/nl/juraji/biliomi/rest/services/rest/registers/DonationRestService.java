package nl.juraji.biliomi.rest.services.rest.registers;

import nl.juraji.biliomi.model.registers.Donation;
import nl.juraji.biliomi.model.registers.DonationDao;
import nl.juraji.biliomi.rest.config.ModelRestService;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/registers/donations")
public class DonationRestService extends ModelRestService<Donation> {

    @Inject
    private DonationDao donationDao;

    @Override
    public List<Donation> getEntities() {
        return donationDao.getList();
    }

    @Override
    public Donation getEntity(long id) {
        return donationDao.get(id);
    }

    @Override
    public Donation createEntity(Donation e) {
        donationDao.save(e);
        return e;
    }

    @Override
    public Donation updateEntity(Donation e, long id) {
        Donation donation = donationDao.get(id);

        donation.setDonation(e.getDonation());
        donation.setNote(e.getNote());
        donation.setDate(e.getDate());

        donationDao.save(donation);
        return donation;
    }

    @Override
    public boolean deleteEntity(long id) {
        Donation donation = donationDao.get(id);

        if (donation == null) {
            return false;
        }

        donationDao.delete(donation);
        return true;
    }
}
