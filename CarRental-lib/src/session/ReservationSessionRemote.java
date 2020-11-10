package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType2;
import rental.Quote;
import rental.Reservation2;
import rental.ReservationConstraints;
import rental.ReservationException;

@Remote
public interface ReservationSessionRemote {
    
    public void setRenterName(String name);
    
    public String getRenterName();
    
    public Set<String> getAllRentalCompanies();
    
    public List<CarType2> getAvailableCarTypes(Date start, Date end);
    
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException;
    
    public List<Quote> getCurrentQuotes();
    
    public List<Reservation2> confirmQuotes() throws ReservationException;
    
}