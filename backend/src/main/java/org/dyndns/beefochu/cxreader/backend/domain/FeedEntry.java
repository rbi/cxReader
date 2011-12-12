package org.dyndns.beefochu.cxreader.backend.domain;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class FeedEntry implements Serializable  {
        private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private Feed feed;
    
    private String title;
    private String url;
    @Lob
    private String summary;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URL getUrl() {
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            //not malformed
            System.err.println(ex);
            return null;
        }
    }

    public void setUrl(URL url) {
        this.url = url.toString();
    }
}
