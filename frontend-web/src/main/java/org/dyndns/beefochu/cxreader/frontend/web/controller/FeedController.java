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
public class FeedController implements Serializable {

	private static final long serialVersionUID = -1216869584943556445L;
	
	@Inject
    Reader reader;
    private List<FeedUserRelation> bookmarks;

    public FeedController() {
    }

    @PostConstruct
    public void init() {
    	this.bookmarks = reader.getFeedList();
    }

    public List<FeedUserRelation> getBookmarks() {
        return bookmarks;
    }
}
