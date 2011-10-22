package org.dyndns.beefochu.cxreader.ejb.control;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.dyndns.beefochu.cxreader.ejb.domain.Feed;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedUrlInvalidException;

public class FeedService {

    @Inject
    EntityManager em;
    @Inject
    Instance<FeedParser> parsers;

    public Feed findOrCreate(URL url) throws FeedUrlInvalidException {
        List<Feed> feeds = findFeed(url);
        if (!feeds.isEmpty()) {
            return feeds.get(0);
        }
        
        InputStream stream = null;
        try {
            stream = url.openStream();
        } catch (IOException ex) {
            throw new FeedUrlInvalidException(ex);
        }
        
        FeedParser parser = getHandler(stream);
        if(parser == null)
            throw new FeedUrlInvalidException();
        
        Feed newFeed = new Feed();
        newFeed.setUrl(url);
        
        em.persist(newFeed);
        
        feeds = findFeed(url);
        if (!feeds.isEmpty()) {
            newFeed = feeds.get(0);
        }
        
        parser.update(newFeed, stream);
        return newFeed;
    }

    private List<Feed> findFeed(URL url) {
        TypedQuery<Feed> feedQuery = em.createNamedQuery(Feed.FIND_BY_URL, Feed.class);
        feedQuery.setParameter("url", url.toString());
        return feedQuery.getResultList();
    }

    private FeedParser getHandler(InputStream stream) {
        Iterator<FeedParser> it = parsers.iterator();
        while(it.hasNext()) {
            FeedParser next = it.next();
            if(next.isFeedParsable(stream))
                return next;
        }
        
        return null;
    }
}
