package org.dyndns.beefochu.cxreader.backend.parsers;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.inject.Inject;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.exceptions.ParsingException;

public class RomeParser implements FeedParser {
    
    @Inject
    SyndFeedInput parserFactory;
    
    @Override
    public boolean isFeedParsable(InputStream feed) throws IOException {
        try {
            parserFactory.build(new InputStreamReader(feed));
        } catch (IllegalArgumentException ex) {
            return false;
        } catch (FeedException ex) {
            return false;
        }
        return true;
    }

    @Override
    public void update(Feed feed, InputStream feedStream) throws IOException, ParsingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
