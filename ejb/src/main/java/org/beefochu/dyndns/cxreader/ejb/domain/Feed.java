package org.beefochu.dyndns.cxreader.ejb.domain;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
@NamedQuery(name = Feed.FIND_FEEDS_FOR_USER, query = "Select e from UserFeedRelation e where e.user = :user")
public class Feed implements Serializable  {
    private static final long serialVersionUID = 1L;
    public static final String FIND_FEEDS_FOR_USER = "findFeedsForUser";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Version
    private long version;
    
    @ManyToOne
    private ReaderUser user;
    @ManyToOne()
    private FeedCommon feed;
    @OneToMany(mappedBy="userFeed", cascade= CascadeType.ALL)
    private List<FeedEntry> markedFeedEntries = new ArrayList<FeedEntry>();
    
    private String feedName;
    
    
    public Iterator<List<FeedEntryCommon>> getUnreadEntries() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<List<FeedEntryCommon>> getAllEntries() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public URL getUrl() {
        return feed.getUrl();
    }
}
