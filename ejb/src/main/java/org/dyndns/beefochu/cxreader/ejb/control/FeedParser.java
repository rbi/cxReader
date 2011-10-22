package org.dyndns.beefochu.cxreader.ejb.control;

import java.io.InputStream;
import org.dyndns.beefochu.cxreader.ejb.domain.Feed;

public interface FeedParser {
    public boolean isFeedParsable(InputStream feed);
    public void update(Feed feed, InputStream feedStream);
}
