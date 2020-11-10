package session;

import java.util.Set;
import javax.ejb.Remote;
import rental.CarType2;
import rental.Reservation2;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType2> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
      
}