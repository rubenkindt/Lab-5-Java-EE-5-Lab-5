package rental;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Car2 {

    private int id;
    private CarType2 type;
    private Set<Reservation2> reservations;

    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Car2(int uid, CarType2 type) {
    	this.id = uid;
        this.type = type;
        this.reservations = new HashSet<Reservation2>();
    }

    /******
     * ID *
     ******/
    
    public int getId() {
    	return id;
    }
    
    /************
     * CAR TYPE *
     ************/
    
    public CarType2 getType() {
        return type;
    }
	
	public void setType(CarType2 type) {
		this.type = type;
	}
    /****************
     * RESERVATIONS *
     ****************/

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        for(Reservation2 reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation2 res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation2 reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }

    public Set<Reservation2> getReservations() {
        return reservations;
    }
}