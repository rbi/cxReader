package org.dyndns.beefochu.cxreader.backend.ejb;

import java.net.URL;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dyndns.beefochu.cxreader.backend.Reader;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntryUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;
import org.dyndns.beefochu.cxreader.backend.services.FeedService;
import org.dyndns.beefochu.cxreader.backend.services.UserService;

@Stateless
@Local(Reader.class)
//@DeclareRoles("users")
//@RolesAllowed("users")
public class ReaderFacade implements Reader {

    @PersistenceContext
    EntityManager em;

    @Resource
    SessionContext ctx;
    
    @Inject
    FeedService feedService;
    @Inject
    UserService userService;
    
    @Override
    public List<FeedUserRelation> getFeedList() {
        return userService.getFeedList(getUserName());
    }

    @Override
    public FeedUserRelation bookmarkFeed(URL feedUrl) throws FeedUrlInvalidException, FeedAlreadyInListException {
        Feed feed = feedService.findOrCreate(feedUrl);
        return userService.addFeed(getUserName(), feed);
    }

    @Override
    public void removeBookmarkedFeed(FeedUserRelation feed) {
        userService.removeBookmarkedFeed(getUserName(), feed);
    }

    @Override
    public List<FeedEntryUserRelation> getEntries(FeedUserRelation feed, int offset, int count, boolean unreadOnly) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setReadStatus(FeedEntryUserRelation entry, boolean read) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private String getUserName() {
    	return ctx.getCallerPrincipal().getName();
    }
}
