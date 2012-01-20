package org.dyndns.beefochu.cxreader.backend.ejb;

import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.dyndns.beefochu.cxreader.backend.Reader;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntryUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;
import org.dyndns.beefochu.cxreader.backend.services.FeedService;
import org.dyndns.beefochu.cxreader.backend.services.FeedService.FindOrCreateReturn;
import org.dyndns.beefochu.cxreader.backend.services.UserService;

@Named
@Stateless
@Local(Reader.class)
// @DeclareRoles("users")
// @RolesAllowed("users")
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
	public FeedUserRelation bookmarkFeed(URL feedUrl)
			throws FeedUrlInvalidException, FeedAlreadyInListException {
		FindOrCreateReturn findOrCreateReturn = feedService.findOrCreate(feedUrl);
		FeedUserRelation feedRelation = userService.addFeed(getUserName(),
				findOrCreateReturn.feed);
		
		if (findOrCreateReturn.isNew) {
			userService.createFeedEntryUserRelations(findOrCreateReturn.feed,
					findOrCreateReturn.feed.getEntries());
		}
		
		return feedRelation;
	}

	@Override
	public void removeBookmarkedFeed(FeedUserRelation feedUserRelation) {
		Feed feed = userService.removeBookmarkedFeed(getUserName(),
				feedUserRelation);
		if (feed != null)
			feedService.removeIfOrphan(feed);
	}

	@Override
	public List<FeedEntryUserRelation> getEntries(FeedUserRelation feed,
			int offset, int count, boolean unreadOnly) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<FeedEntryUserRelation> getEntries(FeedUserRelation feed,
			Date newerThan) {
		TypedQuery<FeedEntryUserRelation> query = em.createNamedQuery(
				FeedEntryUserRelation.FIND_ENTRIES_NEWER_THAN,
				FeedEntryUserRelation.class);
		query.setParameter("feedRelation", feed);
		query.setParameter("date", newerThan);
		return query.getResultList();
	}
	
	@Override
	public void setReadStatus(FeedEntryUserRelation entry, boolean read) {
		em.find(FeedEntryUserRelation.class, entry.getId()).setRead(read);
	}
	
	private String getUserName() {
		return ctx.getCallerPrincipal().getName();
	}
}
