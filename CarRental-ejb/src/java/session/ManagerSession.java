package session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;

@DeclareRoles("Manager")
@Stateless
@RolesAllowed("Manager")
public class ManagerSession extends Session implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
    @Override
    public List<CarType> getCarTypes(String company) {
        return em.createNamedQuery("CarRentalCompany.getCarTypes").setParameter("compName", company).getResultList();
        
        /*
        List<CarType> ct=new ArrayList<CarType>();
        try {
            List<CarRentalCompany> list=getRentalCompanies();
            for (CarRentalCompany r : list){
                ct.addAll(r.getAllTypes());
            }
            
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return ct;
*/
    }

    @Override
    public List<Integer> getCarIds(String company, String type) {
        return em.createNamedQuery("CarRentalCompany.getCarIds").setParameter("compName", company).setParameter("typeName", type).getResultList();
    }
        /*
        Set<Integer> out = new HashSet<Integer>();
        try {
            for(Car c: getCompanyByName(company).getCars(type)){
                out.add(c.getId());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }
*/

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        return (int) em.createNamedQuery("CarRentalCompany.getNumberOfReservations").setParameter("compName", company).setParameter("typeName", type).setParameter("id",id).getSingleResult();
    }
     /*   
        try {
            return getCompanyByName(company).getCar(id).getReservations().size();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    */
    
    @Override
    public int getNumberOfReservations(String company, String type) {
        return (int) em.createNamedQuery("CarRentalCompany.getNumberOfReservations2").setParameter("compName", company).setParameter("typeName", type).getSingleResult();
    }      
        /*
        Set<Reservation> out = new HashSet<Reservation>();
        try {
            for(Car c: getCompanyByName(company).getCars(type)){
                out.addAll(c.getReservations());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();
    }
*/  
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        return  em.createNamedQuery("car.getAvailableCarTypes").setParameter("start", start).setParameter("end", end).getResultList();
    
    }
    
    @Override
    public int getNrOfReservationsByClient(String clientName) {
        return (int) em.createNamedQuery("CarRentalCompany.getNumberOfReservationsByClient").setParameter("client", clientName).getSingleResult();
    }
        /*
        Set<Reservation> res=new HashSet<Reservation>();
        for (Iterator<Map.Entry<String, CarRentalCompany>> entries = getRentals().entrySet().iterator(); entries.hasNext(); ) {
            Map.Entry<String, CarRentalCompany> compMap= entries.next();
            CarRentalCompany compa= compMap.getValue();
            
            res.addAll(compa.getReservationsBy(clientName));
        }
        return res.size();
    }
    */
    
    @Override
    public List<String> getAllRentalCompanies() {
        return  em.createNamedQuery("CarRentalCompany.getAllRentalCompanyNames").getResultList();
    }

    

    @Override
    public List<String> bestClients() {
        return  em.createNamedQuery("CarRentalCompany.getbestClients").getResultList();
        
    }

    @Override
    public CarType mostPopular(int year) {
        return (CarType) em.createNamedQuery("car.mostPopular").setParameter("year", year).getSingleResult();
    }

    @Override
    public CarType getCheapest(Date start, Date end, String region) {
        return (CarType) em.createNamedQuery("CarRentalCompany.getCheapest").setParameter("start", start).setParameter("end", end).setParameter("region", region).getSingleResult();
    }
    
    
    
    public void loadCompanyInDb(String datafile) {
        try {
            CrcData data = loadData(datafile);
            CarRentalCompany company = new CarRentalCompany(data.name, data.regions, data.cars);
            
            em.persist(company);
            //^important
            
            Logger.getLogger(ManagerSession.class.getName()).log(Level.INFO, "Loaded {0} from file {1}", new Object[]{data.name, datafile});
        } catch (NumberFormatException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, "bad file", ex);
        } catch (IOException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static CrcData loadData(String datafile)
            throws NumberFormatException, IOException {

        CrcData out = new CrcData();
        StringTokenizer csvReader;
        int nextuid = 0;
       
        //open file from jar
        BufferedReader in = new BufferedReader(new InputStreamReader(ManagerSession.class.getClassLoader().getResourceAsStream(datafile)));
        
        try {
            while (in.ready()) {
                String line = in.readLine();
                
                if (line.startsWith("#")) {
                    // comment -> skip					
                } else if (line.startsWith("-")) {
                    csvReader = new StringTokenizer(line.substring(1), ",");
                    out.name = csvReader.nextToken();
                    out.regions = Arrays.asList(csvReader.nextToken().split(":"));
                } else {
                    csvReader = new StringTokenizer(line, ",");
                    //create new car type from first 5 fields
                    CarType type = new CarType(csvReader.nextToken(),
                            Integer.parseInt(csvReader.nextToken()),
                            Float.parseFloat(csvReader.nextToken()),
                            Double.parseDouble(csvReader.nextToken()),
                            Boolean.parseBoolean(csvReader.nextToken()));
                    //create N new cars with given type, where N is the 5th field
                    for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                        out.cars.add(new Car(nextuid++, type));
                    }        
                }
            } 
        } finally {
            in.close();
        }

        return out;
    }

  
    
    static class CrcData {
            public List<Car> cars = new LinkedList<Car>();
            public String name;
            public List<String> regions =  new LinkedList<String>();
    }
}