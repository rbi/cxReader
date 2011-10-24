package org.dyndns.beefochu.cxreader.backend.ejb;

import java.net.URL;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.dyndns.beefochu.cxreader.backend.Reader;
import org.dyndns.beefochu.cxreader.backend.control.FeedService;
import org.dyndns.beefochu.cxreader.backend.control.UserService;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntryUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;

@Stateless
@Local(Reader.class)
public class ReaderFacade implements Reader {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    FeedService feedService;
    @Inject
    UserService userService;
    
    @Override
    public List<FeedUserRelation> getFeedList(String username) {
        return userService.getFeedList(username);
    }

    @Override
    public FeedUserRelation bookmarkFeed(String username, URL feedUrl) throws FeedUrlInvalidException, FeedAlreadyInListException {
        Feed feed = feedService.findOrCreate(feedUrl);
        return userService.addFeed(username, feed);
    }

    @Override
    public void removeBookmarkedFeed(String username, FeedUserRelation feed) {
        userService.removeBookmarkedFeed(username, feed);
    }

    @Override
    public List<FeedEntryUserRelation> getEntries(String username, FeedUserRelation feed, int offset, int count, boolean unreadOnly) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setReadStatus(String username, FeedEntryUserRelation entry, boolean read) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
