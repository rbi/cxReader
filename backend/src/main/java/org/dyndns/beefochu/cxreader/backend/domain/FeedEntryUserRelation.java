package org.dyndns.beefochu.cxreader.backend.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

@Entity
@NamedQuery(name = FeedEntryUserRelation.FIND_ENTRIES_NEWER_THAN,
	query = "Select r from FeedEntryUserRelation r " +
			"where r.userFeed = :feedRelation " +
			"and r.feedEntry.lastUpdate > :date " +
			"order by r.feedEntry.lastUpdate desc ")
public class FeedEntryUserRelation implements Serializable {

	private static final long serialVersionUID = 7497761210708403786L;
	public static final String FIND_ENTRIES_NEWER_THAN = "findEntriesNewerThan";
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Version
    private long version;
    @ManyToOne
    private FeedUserRelation userFeed;
    @ManyToOne
    private FeedEntry feedEntry;
    private boolean entryRead;
    private boolean bookmarked;
    
    protected FeedEntryUserRelation() {
    	
    }
    
    public FeedEntryUserRelation(FeedEntry feedEntry) {
    	this.feedEntry = feedEntry;
    }

	public long getId() {
		return this.id;
	}
    
    public FeedEntry getFeedEntry() {
        return this.feedEntry;
    }
    
    void setUserFeed(FeedUserRelation userFeed) {
    	this.userFeed = userFeed;
    }

	public boolean isRead() {
		return this.entryRead;
	}

	public void setRead(boolean read) {
		this.entryRead = read;
	}
}
