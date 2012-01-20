package org.dyndns.beefochu.cxreader.backend.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class FeedEntry implements Serializable {

	private static final long serialVersionUID = -4375666728969510036L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@ManyToOne
	private Feed feed;

	private String title;
	private String url;
	@Lob
	private String summary;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;

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
			// not malformed
			System.err.println(ex);
			return null;
		}
	}

	public void setUrl(URL url) {
		this.url = url.toString();
	}

	/**
	 * Returns the last time this feed was updated in UTC.
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public long getId() {
		return this.id;
	}
}
