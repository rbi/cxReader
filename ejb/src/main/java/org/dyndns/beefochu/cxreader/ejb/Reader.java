package org.dyndns.beefochu.cxreader.ejb;

import java.util.List;
import org.dyndns.beefochu.cxreader.ejb.domain.Feed;

public interface Reader {

    /**
     * Returns a list with all feeds of the currently logged in user.
     */
    public List<Feed> getFeedList(String username);
    
    /**
     * Saves all changes to the database.
     */
    public void save();
}
