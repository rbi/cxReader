package org.dyndns.beefochu.cxreader.backend.domain;

import java.io.Serializable;
import java.util.ArrayList;
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
@NamedQuery(name = FeedUserRelation.FIND_FEEDS_FOR_USER, query = "Select e from FeedUserRelation e where e.user = :user")
public class FeedUserRelation implements Serializable  {
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
    private Feed feed;
    @OneToMany(mappedBy="userFeed", cascade= CascadeType.ALL)
    private List<FeedEntryUserRelation> markedFeedEntries = new ArrayList<FeedEntryUserRelation>();
    
    private String feedName;
    
    protected FeedUserRelation() {
    
    }
    
    public FeedUserRelation(ReaderUser user, Feed feed) {
        this.user = user;
        this.feed = feed;
    }
    
    public Feed getFeed() {
        return this.feed;
    }
}
