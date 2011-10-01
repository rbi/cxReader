package org.dyndns.beefochu.cxreader.ejb.domain;

import javax.persistence.TypedQuery;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedUrlInvalidException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import static org.dyndns.beefochu.cxreader.ejb.util.ThreadLocalEntityManager.*;

@Entity
@NamedQuery(name = ReaderUser.FIND_BY_NAME, query = "Select e from ReaderUser e where e.name = :username")
public class ReaderUser implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String FIND_BY_NAME = "findReaderUserByName";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique = true, nullable = false)
    private String name;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Feed> feeds = new ArrayList<Feed>();

    protected ReaderUser() {
    }

    public ReaderUser(String name) {
        this.name = name;
    }

    /**
     * Returns a read only list with all feeds of the user.
     */
    public List<Feed> getFeedList() {
        return Collections.unmodifiableList(feeds);
    }

    /**
     * Adds a feed to the list with feeds of the user.
     * 
     * @param feedUrl The url of the feed to add.
     * @return The Feed object for the new feed.
     * @throws FeedUrlInvalidException If the url doesn't point to a valid url.
     * @throws FeedAlreadyInListException If the user has this feed already in his list.
     */
    public Feed addFeed(URL feedUrl) throws FeedUrlInvalidException, FeedAlreadyInListException {
        for(Feed feed: feeds)
           if(feed.getUrl().equals(feedUrl))
               throw new FeedAlreadyInListException();
        
        FeedCommon feedCommon;
        
        TypedQuery<FeedCommon> feedQuery = em().createNamedQuery(FeedCommon.FIND_BY_URL, FeedCommon.class);
        feedQuery.setParameter("url", feedUrl);
        List<FeedCommon> feeds = feedQuery.getResultList();
        
        if(!feeds.isEmpty())
            feedCommon = feeds.get(0);
        else
            feedCommon = new FeedCommon(feedUrl);
        
        em().persist(feedCommon);
        Feed feed = new Feed(this, feedCommon);
        
        em().persist(feed);
        
        return feed;
    }

    /**
     * Removes the feed to the list with feeds of the user.
     */
    public void removeFeed(Feed feed) {
        feeds.remove(feed);
    }
}
