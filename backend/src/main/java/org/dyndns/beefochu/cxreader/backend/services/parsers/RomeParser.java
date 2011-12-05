package org.dyndns.beefochu.cxreader.backend.services.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntry;
import org.dyndns.beefochu.cxreader.backend.exceptions.ParsingException;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

public class RomeParser implements FeedParser {
	
	public RomeParser() {
		
	}

    @Inject
    SyndFeedInput parserFactory;

    @Override
    public boolean isFeedParsable(InputStream feed) throws IOException {
        try {
            parseFeed(feed);
        } catch (ParsingException ex) {
            return false;
        }
        return true;
    }

    @Override
    public Feed parse(InputStream feedStream) throws IOException, ParsingException {
        return transform(parseFeed(feedStream));
    }

    private SyndFeed parseFeed(InputStream feedStream) throws ParsingException {
        SyndFeed syndFeed = null;
        try {
            syndFeed = parserFactory.build(new InputStreamReader(feedStream));
        } catch (IllegalArgumentException ex) {
            throw new ParsingException(ex);
        } catch (FeedException ex) {
            throw new ParsingException(ex);
        }
        return syndFeed;
    }

    @SuppressWarnings("unchecked")
    private Feed transform(SyndFeed syndFeed) {
        Feed feed = new Feed();
        feed.setName(syndFeed.getTitle());
        
        for(SyndEntry entry : (List<SyndEntry>)syndFeed.getEntries())
            feed.getEntries().add(transform(entry));

        return feed;
    }

    private FeedEntry transform(SyndEntry syndEntry) {
        FeedEntry entry = new FeedEntry();
        
        entry.setTitle(syndEntry.getTitle());
        
        SyndContent description = syndEntry.getDescription();
        if(description != null)
            entry.setSummary(description.getValue());
        
        try {
            entry.setUrl(new URL(syndEntry.getLink()));
        } catch (MalformedURLException ex) {
            //do nothing, url will be null;
        }
        return entry;
    }
}
