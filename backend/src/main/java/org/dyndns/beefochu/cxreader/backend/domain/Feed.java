package org.dyndns.beefochu.cxreader.backend.domain;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQuery(name = Feed.FIND_BY_URL, query = "Select e from Feed e where e.url = :url")
public class Feed implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String FIND_BY_URL = "findFeedCommonByUrl";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique = true, nullable = false)
    private String url;
    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
    private List<FeedEntry> entries;
    private String name;

    public Feed() {
    }

    public Feed(URL url) {
        this.url = url.toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setUrl(URL url) {
        this.url = url.toString();
    }

    public URL getUrl() {
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            //not malformed
        }
        return null;
    }

    /**
     * Two feeds objects are equal if they have the same url.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Feed other = (Feed) obj;
        if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.url.hashCode();
    }
}
