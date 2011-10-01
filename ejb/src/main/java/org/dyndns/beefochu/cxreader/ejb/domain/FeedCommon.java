package org.dyndns.beefochu.cxreader.ejb.domain;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedUrlInvalidException;

@Entity
@NamedQuery(name = FeedCommon.FIND_BY_URL, query = "Select e from FeedCommon e where e.url = :url")
public class FeedCommon implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String FIND_BY_URL = "findFeedCommonByUrl";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique=true, nullable=false)
    private String url;
    @OneToMany(mappedBy="feed", cascade= CascadeType.ALL)
    private List<FeedEntryCommon> entries;
    
    private String name;
    
    protected FeedCommon() {
    
    }
    
    public FeedCommon(URL url) throws FeedUrlInvalidException {
        //TODO init feed
        this.url = url.toString();
    }
    
    public String getName() {
        return this.name;
    }

    public URL getUrl() {
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            //TODO not malformed
        }
        return null;
    }
}
