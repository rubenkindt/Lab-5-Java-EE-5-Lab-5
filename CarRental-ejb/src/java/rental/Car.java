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
import static javax.persistence.CascadeType.REMOVE;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ruben
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "car.findAll", query = "SELECT w FROM Car w")
    , @NamedQuery(name = "car.findById", query = "SELECT w FROM Car w WHERE w.type = :cartyp")
    , @NamedQuery(name = "car.findByWnaam", query = "SELECT w FROM Car w WHERE w.id = :cid")})
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    private int id;
    @OneToMany(cascade=REMOVE, mappedBy = "car")
    private CarType type;
    @OneToMany(cascade=REMOVE, mappedBy = "car")
    private Set<Reservation> reservations;
    
    
    public Car(int uid, CarType type) {
    	this.id = uid;
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
        return id;
    }

    @Override
    public String toString() {
        return "rental.Car[ id=" + id + " ]";
    }
   
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }
}
