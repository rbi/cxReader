package org.dyndns.beefochu.cxreader.backend.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntry;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntryUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.ReaderUser;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;

public class UserService {

	@PersistenceContext
	EntityManager em;

	public List<FeedUserRelation> getFeedList(String username) {
		ReaderUser user = findOrCreateUser(username);
		return user.getFeeds();
	}

	public FeedUserRelation addFeed(String username, Feed feed)
			throws FeedAlreadyInListException {
		ReaderUser user = findOrCreateUser(username);
		List<FeedUserRelation> feeds = user.getFeeds();

		for (FeedUserRelation feedRelation : feeds) {
			if (feedRelation.getFeed().equals(feed)) {
				throw new FeedAlreadyInListException();
			}
		}

		FeedUserRelation relation = new FeedUserRelation(user, feed);
		em.persist(relation);
		feeds.add(relation);
		return relation;
	}

	/**
	 * Removes a feed bookmark and the all FeedEntryUserRelations for this user
	 * and feed.
	 * 
	 * @param username
	 * @param feed
	 *            The bookmark to remove
	 * @return The Feed object of the feed bookmark that was removed or null if
	 *         the feed was not bookmarked by this user
	 */
	public Feed removeBookmarkedFeed(String username, FeedUserRelation feed) {
		List<FeedUserRelation> feeds = getFeedList(username);

		FeedUserRelation attachedFeed = null;

		for (FeedUserRelation curr : feeds) {
			if (feed.equals(curr)) {
				attachedFeed = curr;
				break;
			}
		}

		if (attachedFeed == null)
			return null;

		feeds.remove(attachedFeed);
		em.remove(attachedFeed);

		return attachedFeed.getFeed();
	}

	/**
	 * Creates FeedEntryUserRelation objects for all FeedEntry passed to this
	 * method via the newEntry object.
	 * 
	 * @param feed the feed these entries belong to.
	 * @param newEntries
	 */
	public void createFeedEntryUserRelations(Feed feed,
			List<FeedEntry> newEntries) {
		TypedQuery<FeedUserRelation> query = em.createNamedQuery(
				FeedUserRelation.FIND_FEEDS_FOR_ENTRY, FeedUserRelation.class);
		query.setParameter("feed", feed);
		List<FeedUserRelation> relations = query.getResultList();

		for(FeedUserRelation rel : relations) {
			for(FeedEntry newEntry : newEntries) {
				rel.addFeedEntryUserRelation(new FeedEntryUserRelation(newEntry));
			}
		}
	}

	private ReaderUser findOrCreateUser(String username) {
		List<ReaderUser> readerUsers = findUser(username);
		if (!readerUsers.isEmpty()) {
			return readerUsers.get(0);
		}

		ReaderUser newUser = new ReaderUser(username);
		em.persist(newUser);

		readerUsers = findUser(username);
		if (!readerUsers.isEmpty()) {
			return readerUsers.get(0);
		}

		return null;
	}

	private List<ReaderUser> findUser(String username) {
		TypedQuery<ReaderUser> userQuery = em.createNamedQuery(
				ReaderUser.FIND_BY_NAME, ReaderUser.class);
		userQuery.setParameter("username", username);
		return userQuery.getResultList();
	}
}
