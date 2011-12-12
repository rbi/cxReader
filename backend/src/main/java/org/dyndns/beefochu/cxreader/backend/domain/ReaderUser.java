package org.dyndns.beefochu.cxreader.backend.domain;

import java.io.Serializable;
import java.util.ArrayList;
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
@NamedQuery(name = ReaderUser.FIND_BY_NAME, query = "Select e from ReaderUser e where e.name = :username")
public class ReaderUser implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String FIND_BY_NAME = "findReaderUserByName";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique = true, nullable = false)
    private String name;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FeedUserRelation> feeds = new ArrayList<FeedUserRelation>();

    public ReaderUser() {
    }

    public ReaderUser(String name) {
        this.name = name;
    }

    public List<FeedUserRelation> getFeeds() {
        return this.feeds;
    }

	public String getName() {
		return name;
	}
}
