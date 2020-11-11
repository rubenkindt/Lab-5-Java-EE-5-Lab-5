package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
@TransactionAttribute(value=REQUIRED)
public class ReservationSession implements ReservationSessionRemote {
    
    @PersistenceContext
    EntityManager em;
        
    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        return  em.createNamedQuery("car.getAvailableCarTypes").setParameter("start", start).setParameter("end", end).getResultList();
    
        /*
        List<CarType> availableCarTypes = new LinkedList<CarType>();
        for(CarRentalCompany crc : getRentalCompanies()) {
            for(CarType ct : crc.getAvailableCarTypes(start, end)) {
                if(!availableCarTypes.contains(ct))
                    availableCarTypes.add(ct);
            }
        }
        return availableCarTypes;*/
    }
    protected CarRentalCompany getCompanyByName(String company){
        CarRentalCompany compa =em.find(CarRentalCompany.class, company);
        if (compa==null){
            throw new IllegalArgumentException("Company doesn't exist!: " + company);
         
        }
        return compa;
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
        try {
            Quote out = getCompanyByName(company).createQuote(constraints, renter);
            quotes.add(out);
            return out;
        } catch(Exception e) {
            throw new ReservationException(e);
        }
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
        try {
            for (Quote quote : quotes) {
                done.add(getCompanyByName(quote.getRentalCompany()).confirmQuote(quote));
            }
        } catch (Exception e) {
            for(Reservation r:done)
                getCompanyByName(r.getRentalCompany()).cancelReservation(r);
            throw new ReservationException(e);
        }
        for (Reservation r: done)
            em.persist(r);
        //  Reservations were made and  are now connected to the entity manager
        quotes.clear();
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public String getRenterName() {
        return renter;
    }

    @Override
    public List<String> getAllRentalCompanies() {
        return  em.createNamedQuery("CarRentalCompany.getAllRentalCompanyNames").getResultList();
    }

    @Override
    public String getCheapest(Date start, Date end, String region) {
        return (String) em.createNamedQuery("CarRentalCompany.getCheapest").setParameter("start", start).setParameter("end", end).setParameter("region", region).getSingleResult();
    }
    
}