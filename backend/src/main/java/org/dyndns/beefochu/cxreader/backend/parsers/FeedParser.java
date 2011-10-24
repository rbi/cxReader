package org.dyndns.beefochu.cxreader.backend.parsers;

import java.io.InputStream;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;

public interface FeedParser {
    public boolean isFeedParsable(InputStream feed);
    public void update(Feed feed, InputStream feedStream);
}
