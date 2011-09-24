package org.beefochu.dyndns.cxreader.ejb.ejb;

import java.util.List;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.beefochu.dyndns.cxreader.ejb.Reader;
import org.beefochu.dyndns.cxreader.ejb.domain.Feed;
import org.beefochu.dyndns.cxreader.ejb.domain.ReaderUser;

@Stateful
@Local(Reader.class)
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ReaderGateway implements Reader {

    @PersistenceContext
    EntityManager em;
    @Inject
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
}
