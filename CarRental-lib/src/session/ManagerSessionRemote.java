package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Reservation;

@Remote
public interface ManagerSessionRemote {
    
    public List<CarType> getCarTypes(String company);
    
    public List<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
    
    public int getNrOfReservationsByClient(String client);
    
    public void loadCompanyInDb(String csv);
    
    public List<String> getAllRentalCompanies();
    
    public List<CarType> getAvailableCarTypes(Date start,Date end);
    
    public List<String> bestClients();
    
    public CarType mostPopular(String company,int year);
    
    
    
    
    
}