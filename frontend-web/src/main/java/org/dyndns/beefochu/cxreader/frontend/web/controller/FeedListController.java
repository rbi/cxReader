package org.dyndns.beefochu.cxreader.frontend.web.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.dyndns.beefochu.cxreader.backend.Reader;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;

@Named
@SessionScoped
public class FeedListController implements Serializable {

	private static final long serialVersionUID = -1216869584943556445L;

	@Inject
	Reader reader;
	
	@Inject
	FeedEntryListController entryList;

	private List<FeedUserRelation> bookmarks;

	public FeedListController() {
	}

	@PostConstruct
	public void init() {
		refreshBookmarkList();
	}

	public List<FeedUserRelation> getBookmarks() {
		return bookmarks;
	}

	public void delete(FeedUserRelation bookmark) {
		entryList.changeFeed(null);
		reader.removeBookmarkedFeed(bookmark);
		refreshBookmarkList();
	}

	public void refreshBookmarkList() {
		this.bookmarks = reader.getFeedList();
	}
}
