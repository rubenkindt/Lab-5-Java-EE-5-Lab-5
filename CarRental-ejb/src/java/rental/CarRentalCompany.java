/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rental;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import static javax.persistence.CascadeType.REMOVE;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 *
 * @author Ruben Kindt R065649
 */

//  usefull site https://docs.oracle.com/cd/E19159-01/819-3669/bnbtg/index.html
@Entity
@NamedQueries({
 @NamedQuery(name = "CarRentalCompany.findAll", query = "SELECT DISTINCT c FROM CarRentalCompany c")
    ,@NamedQuery(name = "CarRentalCompany.getCarTypes", 
            query = "SELECT DISTINCT c.carTypes FROM CarRentalCompany c WHERE c.name LIKE :compName")
    ,@NamedQuery(name = "CarRentalCompany.getCarIds", 
            query = "SELECT DISTINCT ca.id FROM CarRentalCompany co join co.cars ca JOIN ca.type ty WHERE co.name LIKE :compName AND ty.name LIKE :typeName")
    ,@NamedQuery(name = "CarRentalCompany.getNumberOfReservations", 
            query = "SELECT count(ca.reservations) FROM CarRentalCompany co JOIN co.cars ca JOIN ca.type ty JOIN ca.reservations res WHERE co.name LIKE :compName AND ty.name LIKE :typeName AND res.carId = :id")
    ,@NamedQuery(name = "CarRentalCompany.getNumberOfReservations2", 
            query = "SELECT count(co.cars.reservations) FROM CarRentalCompany co JOIN co.cars ca JOIN ca.type ty WHERE co.name LIKE :compName AND ty.name LIKE :typeName")
    ,@NamedQuery(name = "CarRentalCompany.getNumberOfReservationsByClient", 
            query = "SELECT count(co.cars.reservations) FROM CarRentalCompany co JOIN co.cars ca JOIN ca.reservations res WHERE res.carRenter LIKE :client")
    ,@NamedQuery(name = "CarRentalCompany.getAllRentalCompanyNames", 
            query = "SELECT DISTINCT co.name FROM CarRentalCompany co")
    
    //source https://www.w3resource.com/sql/aggregate-functions/max-count.php see SQL MAX() and COUNT() with HAVING
    ,@NamedQuery(name = "CarRentalCompany.getbestClients", 
            query = "SELECT res.carRenter from CarRentalCompany co JOIN co.cars ca JOIN ca.reservations res GROUP BY res.carRenter HAVING COUNT(res.carRenter))=(SELECT MAX(mycount) FROM (SELECT res.carRenter, COUNT(res.carRenter mycount FROM CarRentalCompany co JOIN co.cars ca JOIN ca.reservations res GROUP BY res.carRenter))")
    
    ,@NamedQuery(name = "CarRentalCompany.getCheapest", 
            query = "SELECT TOP 1 co.cars.type.name from CarRentalCompany co JOIN co.cars ca JOIN ca.reservations res join ca.type ty WHERE co.regions=:region and (ca.reservations=NULL OR (res.startDate NOT BETWEEN :start AND :stop AND res.endDate NOT BETWEEN :start AND :stop) ORDER BY ty.rentalPricePerDay DESC) )")
    
    ,@NamedQuery(name = "CarRentalCompany.mostPopular", 
            query = "SELECT TOP 1 ca.type FROM CarRentalCompany co JOIN co.cars ca JOIN ca.reservations res WHERE co.name LIKE :company AND YEAR(res.startDate)=:year ) GROUP BY ca.type ORDER BY count(*)")
   
        
    })
public class CarRentalCompany implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String name;
    @OneToMany(cascade=REMOVE)
    private List<Car> cars;
    @OneToMany(cascade=REMOVE)
    private Set<CarType> carTypes = new HashSet<CarType>();
    private List<String> regions;
    
    public CarRentalCompany (){
        
    }
    
    public CarRentalCompany(String name, List<String> regions, List<Car> cars) {
        setName(name);
        this.cars = cars;
        setRegions(regions);
        for (Car car : cars) {
            carTypes.add(car.getType());
        }
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
        for (Car car : cars) {
            carTypes.add(car.getType());
        }
    }

    /********
     * NAME *
     ********/
    
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    /***********
     * Regions *
     **********/
    private void setRegions(List<String> regions) {
        this.regions = regions;
    }
    
    public List<String> getRegions() {
        return this.regions;
    }
    
    public Collection<CarType> getAllTypes() {
        return carTypes;
    }

    public CarType getType(String carTypeName) {
        for(CarType type:carTypes){
            if(type.getName().equals(carTypeName))
                return type;
        }
        throw new IllegalArgumentException("<" + carTypeName + "> No cartype of name " + carTypeName);
    }

    public boolean isAvailable(String carTypeName, Date start, Date end) {
        return getAvailableCarTypes(start, end).contains(getType(carTypeName));
    }

    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCarTypes = new HashSet<CarType>();
        for (Car car : cars) {
            if (car.isAvailable(start, end)) {
                availableCarTypes.add(car.getType());
            }
        }
        return availableCarTypes;
    }

    /*********
     * CARS *
     *********/
    
    public Car getCar(int uid) {
        for (Car car : cars) {
            if (car.getId() == uid) {
                return car;
            }
        }
        throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
    }

    public Set<Car> getCars(CarType type) {
        Set<Car> out = new HashSet<Car>();
        for (Car car : cars) {
            if (car.getType().equals(type)) {
                out.add(car);
            }
        }
        return out;
    }
    
     public Set<Car> getCars(String type) {
        Set<Car> out = new HashSet<Car>();
        for (Car car : cars) {
            if (type.equals(car.getType().getName())) {
                out.add(car);
            }
        }
        return out;
    }

    private List<Car> getAvailableCars(String carType, Date start, Date end) {
        List<Car> availableCars = new LinkedList<Car>();
        for (Car car : cars) {
            if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    /****************
     * RESERVATIONS *
     ****************/
    
    public Quote createQuote(ReservationConstraints constraints, String guest)  throws ReservationException {

        if (!this.regions.contains(constraints.getRegion()) || !isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate())) {
            throw new ReservationException("<" + name
                    + "> No cars available to satisfy the given constraints.");
        }
		
        CarType type = getType(constraints.getCarType());

        double price = calculateRentalPrice(type.getRentalPricePerDay(), constraints.getStartDate(), constraints.getEndDate());

        return new Quote(guest, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
    }

   // Implementation can be subject to different pricing strategies
    private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
        return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
                / (1000 * 60 * 60 * 24D));
    }

    public Reservation confirmQuote(Quote quote) throws ReservationException {
        List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
        if (availableCars.isEmpty()) {
            throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
                    + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
        }
        Car car = availableCars.get((int) (Math.random() * availableCars.size()));

        Reservation res = new Reservation(quote, car.getId());
        car.addReservation(res);
        return res;
    }

    public void cancelReservation(Reservation res) {
        getCar(res.getCarId()).removeReservation(res);
    }
    
    public Set<Reservation> getReservationsBy(String renter) {
        Set<Reservation> out = new HashSet<Reservation>();
        for(Car c : cars) {
            for(Reservation r : c.getReservations()) {
                if(r.getCarRenter().equals(renter))
                    out.add(r);
            }
        }
        return out;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CarRentalCompany)) {
            return false;
        }
        CarRentalCompany other = (CarRentalCompany) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rental.CarRentalCompany[ id=" + name + " ]";
    }
    
}
