/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;

/**
 *
 * @author Ruben Kindt r0656495
 */
// Java class to prevent duplicate code in ReservationSession and ManagerSession
public class Session {
    @PersistenceContext
    EntityManager em;
    
    protected List<CarRentalCompany> getRentalCompanies(){
        return (List<CarRentalCompany>) em.createNamedQuery("CarRentalCompany.findAll").getResultList();
        
    }
    
    protected CarRentalCompany getCompanyByName(String company){
        CarRentalCompany compa =em.find(CarRentalCompany.class, company);
        if (compa==null){
            throw new IllegalArgumentException("Company doesn't exist!: " + company);
         
        }
        return compa;
        
        /*
        // nor recommended way of doing things
        
        List<CarRentalCompany> allCompa = getRentalCompanies();
        
        for (Iterator<CarRentalCompany> entries = allCompa.iterator(); entries.hasNext(); ) {
            CarRentalCompany comp= entries.next();
            if (comp.getName().equals(company)){
                return comp;
            }
        }
        throw new IllegalArgumentException("Company doesn't exist!: " + company);
         
       */
    }
}
