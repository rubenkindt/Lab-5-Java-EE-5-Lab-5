/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rental;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Ruben Kindt R065649
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "car.findAll", query = "SELECT w FROM Car w")
    , @NamedQuery(name = "car.findById", query = "SELECT w FROM Car w WHERE w.type = :cartyp")
    , @NamedQuery(name = "car.getAvailableCarTypes", 
            query = "SELECT DISTINCT ca.type FROM Car ca JOIN ca.reservations res WHERE res is NULL OR res.startDate NOT BETWEEN :start AND :end AND res.endDate NOT BETWEEN :start AND :end")
    
})
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int carid;
    
    @ManyToOne(cascade=PERSIST)
    private CarType type;
    
    @OneToMany(cascade=REMOVE)
    private Set<Reservation> reservations;
    
    
    public Car() {
    
    }
    
    public Car(int uid, CarType type) {
    	this.carid = uid;
        this.type = type;
        this.reservations = new HashSet<Reservation>();
    }
    
    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        for(Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public CarType getType() {
        return type;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public int getId() {
        return carid;
    }

    @Override
    public String toString() {
        return "rental.Car[ id=" + carid + " ]";
    }
   
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }
}
