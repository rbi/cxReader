package org.dyndns.beefochu.cxreader.backend.services;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.ReaderUser;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;

public class UserService {

    @PersistenceContext
    EntityManager em;

    public List<FeedUserRelation> getFeedList(String username) {
        ReaderUser user = findOrCreateUser(username);
        return user.getFeeds();
    }

    public FeedUserRelation addFeed(String username, Feed feed) throws FeedAlreadyInListException {
        ReaderUser user = findOrCreateUser(username);
        List<FeedUserRelation> feeds = user.getFeeds();

        for (FeedUserRelation feedRelation : feeds) {
            if (feedRelation.getFeed().equals(feed)) {
                throw new FeedAlreadyInListException();
            }
        }

        FeedUserRelation relation = new FeedUserRelation(user, feed);
        em.persist(relation);
        feeds.add(relation);
        return relation;
    }

    public void removeBookmarkedFeed(String username, FeedUserRelation feed) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private ReaderUser findOrCreateUser(String username) {
        List<ReaderUser> readerUsers = findUser(username);
        if (!readerUsers.isEmpty()) {
            return readerUsers.get(0);
        }

        ReaderUser newUser = new ReaderUser(username);
        em.persist(newUser);
        
        readerUsers = findUser(username);
        if (!readerUsers.isEmpty()) {
            return readerUsers.get(0);
        }
        
        return null;
    }

    private List<ReaderUser> findUser(String username) {
        TypedQuery<ReaderUser> userQuery = em.createNamedQuery(ReaderUser.FIND_BY_NAME, ReaderUser.class);
        userQuery.setParameter("username", username);
        return userQuery.getResultList();
    }
}
