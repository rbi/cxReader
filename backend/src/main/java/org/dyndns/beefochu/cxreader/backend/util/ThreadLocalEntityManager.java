package org.dyndns.beefochu.cxreader.backend.util;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ThreadLocalEntityManager {
 
    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    
    public static EntityManager em() {
        return lookUp().entityManager;
    }
    
    /**
     * This method is a hack for unit tests, to set a EntityManager mock used
     * by the entity beans. Don't use this in non-test code!
     
    public static void setEntityManagerForTests(EntityManager manager) {
        //TODO
    }*/
        
    private static ThreadLocalEntityManager lookUp() {
        try {
            return (ThreadLocalEntityManager)new InitialContext().lookup("java:global/ThreadLocalEntityManager");
        } catch (NamingException ex) {
            //TODO implement me!
            ex.printStackTrace();
        }
        return null;
    }
}
