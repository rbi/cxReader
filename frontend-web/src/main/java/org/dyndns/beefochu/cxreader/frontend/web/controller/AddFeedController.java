package org.dyndns.beefochu.cxreader.frontend.web.controller;

import java.net.URL;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.dyndns.beefochu.cxreader.backend.Reader;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.backend.exceptions.FeedUrlInvalidException;

@Named
@RequestScoped
public class AddFeedController {
	private URL url;

	@Inject
	Reader reader;
	@Inject
	FacesContext context;
	@Inject
	FeedListController feedList;
	
	public void add() {
		try {
			reader.bookmarkFeed(url);
		} catch (FeedUrlInvalidException e) {
			context.addMessage(
					"addFeedDialog:newFeedForm:url",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"No valid feed url",
							"The data at the URL provided could not be understood as feed."));
		} catch (FeedAlreadyInListException e) {
			context.addMessage("addFeedDialog:newFeedForm:url", new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Already bookmarked",
					"This feed is already in the list of bookmarked feeds."));
		}
		this.url = null;
		feedList.refreshBookmarkList();
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
}
