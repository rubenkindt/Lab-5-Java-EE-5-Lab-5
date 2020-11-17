/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rental;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.AUTO;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import rental.Quote;

/**
 *
 * @author Ruben Kindt R0656495
 */
@Entity
public class Reservation extends Quote implements Serializable {

    private static final long serialVersionUID = 1L;

    
    private int carId;
    
    
    public Reservation() {
    
    }
    
    public Reservation(Quote quote, int carId) {
        super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(), 
    		quote.getRentalCompany(), quote.getCarType(), quote.getRentalPrice());
        this.carId = carId;
    }

    public int getCarId() {
    	return carId;
    }
  

    @Override
    public String toString() {
        return String.format("Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f", 
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getCarId(), getRentalPrice());
    }	
    
}
