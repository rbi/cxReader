package org.beefochu.dyndns.cxreader.ejb.domain;

import org.beefochu.dyndns.cxreader.ejb.exceptions.FeedAlreadyInListException;
import org.beefochu.dyndns.cxreader.ejb.exceptions.FeedUrlInvalidException;
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
import org.beefochu.dyndns.cxreader.ejb.Reader;
import static org.beefochu.dyndns.cxreader.ejb.util.ThreadLocalEntityManager.*;

@Entity
@NamedQuery(name = ReaderUser.FIND_BY_NAME, query = "Select e from ReaderUser e where e.name = :username")
public class ReaderUser implements Reader, Serializable {

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
    @Override
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
        
        //Search FeedCommon
        //Create FeedCommon if not found
        //Add Feed with FeedCommon instance
        //Persist Feed
        //Save
        //Return Feed
        return null;
    }

    /**
     * Removes the feed to the list with feeds of the user.
     */
    public void removeFeed(Feed feed) {
        feeds.remove(feed);
    }
}
