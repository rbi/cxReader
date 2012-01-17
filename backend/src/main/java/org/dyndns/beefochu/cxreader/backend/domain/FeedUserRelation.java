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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
@NamedQueries({
		@NamedQuery(name = FeedUserRelation.FIND_FEEDS_FOR_USER, query = "Select e from FeedUserRelation e where e.user = :user"),
		@NamedQuery(name = FeedUserRelation.COUNT_FEED_SUBSCRIBERS, query = "SELECT COUNT(r) FROM FeedUserRelation r WHERE r.feed = :feed"),
		@NamedQuery(name = FeedUserRelation.FIND_FEEDS_FOR_ENTRY, query = "Select e from FeedUserRelation e where e.feed = :feed") })
public class FeedUserRelation implements Serializable {

	private static final long serialVersionUID = 6686673424345047985L;

	public static final String FIND_FEEDS_FOR_USER = "findFeedsForUser";
	public static final String COUNT_FEED_SUBSCRIBERS = "countFeedSubscribers";
	public static final String FIND_FEEDS_FOR_ENTRY = "findFeedsForEntry";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Version
	private long version;

	@ManyToOne
	private ReaderUser user;
	private Feed feed;
	@OneToMany(mappedBy = "userFeed", cascade = CascadeType.ALL)
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
	
	public void addFeedEntryUserRelation(FeedEntryUserRelation newRelation) {
		this.markedFeedEntries.add(newRelation);
		newRelation.setUserFeed(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedUserRelation other = (FeedUserRelation) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
}
