package org.dyndns.beefochu.cxreader.backend.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;
import org.dyndns.beefochu.cxreader.backend.services.parsers.FeedParser;

public class FeedService {

    /**
     * The maximal size that is allowed for a feed (currently 1MB).
     */
    public static final int MAX_FEED_SIZE = 1048576;
    
    @PersistenceContext
    EntityManager em;
    @Inject
    Instance<FeedParser> parsers;

    public Feed findOrCreate(URL url) throws FeedUrlInvalidException {
        List<Feed> feeds = findFeed(url);
        if (!feeds.isEmpty()) {
            return feeds.get(0);
        }

        InputStream stream = null;
        FeedParser parser = null;
        try {
            stream = readStream(url);
            
            parser = getHandler(stream);
            if (parser == null) {
                stream.close();
                throw new FeedUrlInvalidException();
            }
            stream.reset();
        } catch (IOException ex) {
            throw new FeedUrlInvalidException(ex);
        }

        Feed newFeed = null;

        try {
            newFeed = parser.parse(stream);
        } catch (Exception ex) {
            throw new FeedUrlInvalidException(ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                throw new FeedUrlInvalidException(ex);
            }
        }

        newFeed.setUrl(url);
        em.persist(newFeed);
        feeds = findFeed(url);

        if (!feeds.isEmpty()) {
            newFeed = feeds.get(0);
        }
        
        return newFeed;
    }
    
    /**
     * Removes a feed and all it's entries from the database if no user
     * has bookmarked it anymore.
     * 
     * @param feed The feed that should be removed when it's orphan. Must be
     * 		attached!
     */
	public void removeIfOrphan(Feed feed) {
        TypedQuery<Long> feedQuery = em.createNamedQuery(FeedUserRelation.COUNT_FEED_SUBSCRIBERS, Long.class);
        feedQuery.setParameter("feed", feed);

        if(feedQuery.getSingleResult() != 0)
        	return;
        
        em.remove(feed);
	}

    private List<Feed> findFeed(URL url) {
        TypedQuery<Feed> feedQuery = em.createNamedQuery(Feed.FIND_BY_URL, Feed.class);
        feedQuery.setParameter("url", url.toString());

        return feedQuery.getResultList();
    }

    private FeedParser getHandler(InputStream stream) throws IOException {
        Iterator<FeedParser> it = parsers.iterator();
        while (it.hasNext()) {
            stream.reset();
            FeedParser next = it.next();
            if (next.isFeedParsable(stream)) {
                return next;
            }
        }

        return null;
    }

    /**
     * Reads in the contents at an url and returnes an ByteArrayInputStream that
     * contains the contents.
     * 
     * @throws IOException when the file at url is bigger than FeedService.MAX_FEED_SIZE,
     *      the url is invalid or the read failed.
     */
    private InputStream readStream(URL url) throws IOException {
        int READ_AT_ONCE = 1024;

        byte[] buffer = new byte[MAX_FEED_SIZE];
        InputStream in = url.openStream();
        int bytesRead = 0;
        
        int readCount = READ_AT_ONCE;
        while(readCount > 0 && (readCount = in.read(buffer, bytesRead, readCount))!= -1) {
        	bytesRead += readCount;
            readCount = MAX_FEED_SIZE - bytesRead;
            if (READ_AT_ONCE < readCount) {
                readCount = READ_AT_ONCE;
            }
        }
        if (in.read(new byte[1], 0, 1) != -1) {
            in.close();
            throw new IOException("Feed to large!");
        }

        in.close();
        return new ByteArrayInputStream(buffer, 0, bytesRead);
    }
}
