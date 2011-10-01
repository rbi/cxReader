package org.dyndns.beefochu.cxreader.ejb.util;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ThreadLocalEntityManager {
 
    @PersistenceContext
    private EntityManager entityManager;
    
    public static EntityManager em() {
        return lookUp().entityManager;
    }
        
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
