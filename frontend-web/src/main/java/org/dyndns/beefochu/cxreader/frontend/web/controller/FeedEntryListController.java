package org.dyndns.beefochu.cxreader.frontend.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.dyndns.beefochu.cxreader.backend.Reader;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntryUserRelation;
import org.dyndns.beefochu.cxreader.backend.domain.FeedUserRelation;

@Named
@SessionScoped
public class FeedEntryListController implements Serializable {
	private static final long serialVersionUID = 3530319847365542259L;
	
	@Inject
    Reader reader;
	
	@Inject
	EntryInfoController entryInfo;
	
	private List<FeedEntryUserRelation> entries;
	
    @PostConstruct
    public void init() {
    	changeFeed(null);
    }
	
	public void changeFeed(FeedUserRelation current) {
		if(current == null) {
			this.entries = new ArrayList<FeedEntryUserRelation>(0);
		} else {
			//TODO pagination
			this.entries = reader.getEntries(current, new Date(0));
		}
		entryInfo.changeEntry(null);
	}

	public List<FeedEntryUserRelation> getEntries() {
		return entries;
	}
}
