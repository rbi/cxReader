package org.dyndns.beefochu.cxreader.backend.parsers;

import java.net.URL;
import com.sun.syndication.feed.synd.SyndContent;
import org.dyndns.beefochu.cxreader.backend.domain.FeedEntry;
import java.util.List;
import java.util.LinkedList;
import com.sun.syndication.feed.synd.SyndEntry;
import org.dyndns.beefochu.cxreader.backend.domain.Feed;
import org.dyndns.beefochu.cxreader.backend.exceptions.ParsingException;
import com.sun.syndication.feed.synd.SyndFeed;
import java.io.InputStream;
import java.io.Reader;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RomeParserTest {

    private RomeParser parser;
    private InputStream dummyInputStream;
    private SyndFeed syndFeed;

    @Before
    public void setUp() throws IllegalArgumentException, FeedException {
        this.parser = new RomeParser();
        this.parser.parserFactory = mock(SyndFeedInput.class);
        this.dummyInputStream = new DummyInputStream();
        
        mockBuild();
    }

    private void mockBuild() throws IllegalArgumentException, FeedException {
        this.syndFeed = mock(SyndFeed.class);
        when(this.parser.parserFactory.build(any(Reader.class))).thenReturn(this.syndFeed);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void isFeedParsableNotParsableTest() throws IOException, IllegalArgumentException, FeedException {
        when(this.parser.parserFactory.build(any(Reader.class)))
                .thenThrow(IllegalArgumentException.class).thenThrow(FeedException.class);
        
        assertFalse(parser.isFeedParsable(this.dummyInputStream));
        assertFalse(parser.isFeedParsable(this.dummyInputStream));
    }

    @Test
    public void isFeedParsableIsParsableTest() throws IOException {
        assertTrue(parser.isFeedParsable(this.dummyInputStream));
    }
    
    @Test(expected=ParsingException.class)
    @SuppressWarnings("unchecked")
    public void parseNotParsableTest() throws IllegalArgumentException, FeedException, IOException, ParsingException {
        when(this.parser.parserFactory.build(any(Reader.class)))
                .thenThrow(IllegalArgumentException.class).thenThrow(FeedException.class);
        parser.parse(dummyInputStream);
    }
    
    @Test
    public void parseParsableTest() throws IOException, ParsingException {
        SyndEntry entry1 = mock(SyndEntry.class);
        SyndContent summary1 = mock(SyndContent.class);
        SyndEntry entry2 = mock(SyndEntry.class);
        SyndContent summary2 = mock(SyndContent.class);
        List<SyndEntry> entries = new LinkedList<SyndEntry>();
        entries.add(entry1);
        entries.add(entry2);
        
        when(this.syndFeed.getTitle()).thenReturn("Test Feed");
        when(this.syndFeed.getEntries()).thenReturn(entries);
        
        when(entry1.getTitle()).thenReturn("Title 1");
        when(entry1.getLink()).thenReturn("invalid url");
        when(summary1.getValue()).thenReturn("test summary of feed 1");
        when(entry1.getDescription()).thenReturn(summary1);
        
        when(entry2.getTitle()).thenReturn("Another Title");
        when(entry2.getLink()).thenReturn("http://feed.de/url2");
        when(summary2.getValue()).thenReturn("an summary for feed entry 2");
        when(entry2.getDescription()).thenReturn(summary2);
        
        Feed feed = parser.parse(dummyInputStream);
        assertEquals("Test Feed", feed.getName());
        
        List<FeedEntry> feedEntries = feed.getEntries();
        assertNotNull(feedEntries);
        assertEquals(2, feedEntries.size());
        
        FeedEntry returnedEntry1 = feedEntries.get(0);
        assertEquals("Title 1", returnedEntry1.getTitle());
        assertNull(returnedEntry1.getUrl());
        assertEquals("test summary of feed 1", returnedEntry1.getSummary());
        
        FeedEntry returnedEntry2 = feedEntries.get(1);
        assertEquals("Another Title", returnedEntry2.getTitle());
        assertEquals(new URL("http://feed.de/url2"), returnedEntry2.getUrl());
        assertEquals("an summary for feed entry 2", returnedEntry2.getSummary());
        
    }

    private static class DummyInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
