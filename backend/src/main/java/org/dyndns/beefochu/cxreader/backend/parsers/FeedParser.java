package org.dyndns.beefochu.cxreader.backend.parsers;

import java.io.IOException;
import java.io.InputStream;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.exceptions.ParsingException;

public interface FeedParser {
    public boolean isFeedParsable(InputStream feed) throws IOException;
    public void update(Feed feed, InputStream feedStream) throws IOException, ParsingException;
}
