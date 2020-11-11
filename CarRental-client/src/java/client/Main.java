package client;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.ManagerSessionRemote;
import session.ReservationSessionRemote;

public class Main extends AbstractTestManagement<ReservationSessionRemote, ManagerSessionRemote> {

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        InitialContext context = new InitialContext();
        
        ManagerSessionRemote manager = (ManagerSessionRemote)context.lookup(ManagerSessionRemote.class.getName());    
        manager.loadCompanyInDb("hertz.csv");
        manager.loadCompanyInDb("dockx.csv");
        
        
        new Main("trips").run();
    }

    
    
    @Override
    protected ReservationSessionRemote getNewReservationSession(String name) throws Exception {
        InitialContext context = new InitialContext();
        ReservationSessionRemote session2 = (ReservationSessionRemote)context.lookup(ReservationSessionRemote.class.getName());    
        session2.setRenterName(name);
        return session2; 
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name) throws Exception {
        InitialContext context = new InitialContext();
        
        ManagerSessionRemote session2 = (ManagerSessionRemote)context.lookup(ManagerSessionRemote.class.getName());    
        
        return session2; 
    }

    @Override
    protected void getAvailableCarTypes(ReservationSessionRemote session, Date start, Date end) throws Exception {
        
        session.getAvailableCarTypes(start, end);
    }

    @Override
    protected void createQuote(ReservationSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        if (session.getRenterName().equals(name)){
            ReservationConstraints con =new ReservationConstraints(start,end,carType,region);
            
            session.createQuote(  name, con);
        }
        else{
            throw new Exception("Name does not match: "+session.getRenterName()+" Vs: "+name);
        }
    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSessionRemote session, String name) throws Exception {
        if (session.getRenterName().equals(name)){
            return session.confirmQuotes();
        }
        else{
            throw new Exception("Name does not match: "+session.getRenterName()+" Vs: "+name);
        }
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName){
        return ms.getNrOfReservationsByClient(clientName);
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) {
        Set<String> convertedToSet = new HashSet<String>();
        List<String> list=ms.bestClients();
        for (String str : list) 
            convertedToSet.add(str); 
        return convertedToSet;
    }

    @Override
    protected String getCheapestCarType(ReservationSessionRemote session, Date start, Date end, String region) throws Exception {
        return session.getCheapest(start, end, region);
        
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.mostPopular(carRentalCompanyName,year);
    }

    @Override
    protected int getNumberOfReservationsByCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservations(carRentalName, carType);
    }
}