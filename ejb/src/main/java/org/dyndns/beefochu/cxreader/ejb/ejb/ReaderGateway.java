package org.dyndns.beefochu.cxreader.ejb.ejb;

import java.util.List;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import org.dyndns.beefochu.cxreader.ejb.Reader;
import org.dyndns.beefochu.cxreader.ejb.domain.Feed;
import org.dyndns.beefochu.cxreader.ejb.domain.ReaderUser;
import org.dyndns.beefochu.cxreader.ejb.util.Roles;

@Stateful
@Local(Reader.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@DeclareRoles(Roles.USER)
@RolesAllowed(Roles.USER)
public class ReaderGateway implements Reader {

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;
    @Resource
    SessionContext ctx;
    
    @Override
    public List<Feed> getFeedList() {
        String username = ctx.getCallerPrincipal().getName();

        ReaderUser user = findOrCreateUser(username);
        return user.getFeedList();
    }

    private ReaderUser findOrCreateUser(String username) {
        TypedQuery<ReaderUser> userQuery = em.createNamedQuery(ReaderUser.FIND_BY_NAME, ReaderUser.class);
        userQuery.setParameter("username", username);
        List<ReaderUser> readerUsers = userQuery.getResultList();
        if(!readerUsers.isEmpty())
            return readerUsers.get(0);
        
        ReaderUser newUser = new ReaderUser(username);
        em.persist(newUser);
        return newUser;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save() {
        //all work is done by the annotation
    }
}
