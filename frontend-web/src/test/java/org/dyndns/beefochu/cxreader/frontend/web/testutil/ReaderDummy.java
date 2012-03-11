package org.dyndns.beefochu.cxreader.frontend.web.testutil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.dyndns.beefochu.cxreader.backend.Reader;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntryUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.ReaderUser;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;

@Stateless
public class ReaderDummy implements Reader {

	@SuppressWarnings("serial")
	@Override
	public List<FeedUserRelation> getFeedList() {
		return new ArrayList<FeedUserRelation>(2) {
			{
				ReaderUser user = new ReaderUser("test");

				try {
					Feed feed1 = new Feed(new URL("http://testFeed1"));
					feed1.setName("TestFeed1");
					Feed feed2 = new Feed(new URL("http://testFeed2"));
					feed2.setName("FeedWithÜmlaut");
					
					add(new FeedUserRelation(user, feed1));
					add(new FeedUserRelation(user, feed2));
				} catch (MalformedURLException e) {
					// not malformed
				}
			}
		};

	}

	@Override
	public FeedUserRelation bookmarkFeed(URL feed)
			throws FeedUrlInvalidException, FeedAlreadyInListException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeBookmarkedFeed(FeedUserRelation feed) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<FeedEntryUserRelation> getEntries(FeedUserRelation feed,
			int offset, int count, boolean unreadOnly) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FeedEntryUserRelation> getEntries(FeedUserRelation feed,
			Date newerThan) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReadStatus(FeedEntryUserRelation entry, boolean read) {
		// TODO Auto-generated method stub

	}

}
