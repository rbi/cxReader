package org.beefochu.dyndns.cxreader.ejb.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class FeedEntry implements Serializable  {
        private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Version
    private long version;
    @ManyToOne
    private Feed userFeed;
    @ManyToOne
    private FeedEntryCommon entry;
    
    private boolean unread;
    private boolean bookmarked;
}
