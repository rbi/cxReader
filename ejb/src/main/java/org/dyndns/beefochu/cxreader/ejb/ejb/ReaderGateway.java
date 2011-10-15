package org.dyndns.beefochu.cxreader.ejb.ejb;

import java.net.URL;
import java.util.List;
import javax.ejb.Local;
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
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedUrlInvalidException;

@Stateful
@Local(Reader.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ReaderGateway implements Reader {

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;
    
    @Override
    public List<Feed> getFeedList(String username) {
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

    @Override
    public void bookmarkFeed(String username, URL feed) throws FeedUrlInvalidException, FeedAlreadyInListException {
        findOrCreateUser(username).addFeed(feed);
    }

    @Override
    public void removeBookmarkedFeed(String username, Feed feed) {
        findOrCreateUser(username).removeFeed(feed);
    }
}
