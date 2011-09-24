package org.beefochu.dyndns.cxreader.ejb.domain;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class FeedCommon implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique=true, nullable=false)
    private URL url;
    @OneToMany(mappedBy="feed", cascade= CascadeType.ALL)
    private List<FeedEntryCommon> entries;
    
    private String name;
    
    public String getName() {
        return this.name;
    }

    public URL getUrl() {
        return url;
    }
}
