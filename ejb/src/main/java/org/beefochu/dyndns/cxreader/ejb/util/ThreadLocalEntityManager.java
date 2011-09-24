package org.beefochu.dyndns.cxreader.ejb.util;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ThreadLocalEntityManager {
 
    @PersistenceContext
    private EntityManager entityManager;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void commitTransaction() {
    
    }
    
    public static EntityManager em() {
        return lookUp().entityManager;
    }
    
    public static void save() {
        lookUp().commitTransaction();
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
