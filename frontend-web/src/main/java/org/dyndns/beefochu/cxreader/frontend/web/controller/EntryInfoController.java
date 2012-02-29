package org.dyndns.beefochu.cxreader.frontend.web.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.dyndns.beefochu.cxreader.backend.domain.FeedEntryUserRelation;

@Named
@SessionScoped
public class EntryInfoController implements Serializable {

	private static final long serialVersionUID = -1436282458445988278L;
	
	private FeedEntryUserRelation entry;
	private boolean rendered = false;
	
    @PostConstruct
    public void init() {
    	changeEntry(null);
    }
    
	public void changeEntry(FeedEntryUserRelation entry) {
		if(entry == null) {
			rendered = false;
			this.entry = null;//new FeedEntryUserRelation(new FeedEntry());
		} else {
			this.entry = entry;
			rendered = true;
		}
	}

	public FeedEntryUserRelation getEntry() {
		return entry;
	}

	public boolean isRendered() {
		return rendered;
	}
}
