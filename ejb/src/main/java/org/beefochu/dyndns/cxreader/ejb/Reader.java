package org.beefochu.dyndns.cxreader.ejb;

import java.util.List;
import org.beefochu.dyndns.cxreader.ejb.domain.Feed;

public interface Reader {

    /**
     * Returns a list with all feeds of the currently logged in user.
     */
    public List<Feed> getFeedList();
}
