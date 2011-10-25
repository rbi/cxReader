package org.dyndns.beefochu.cxreader.backend.parsers;

import java.io.InputStream;
import com.sun.syndication.io.FeedException;
import java.io.Reader;
import com.sun.syndication.io.SyndFeedInput;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RomeParserTest {

    private RomeParser parser;
    private InputStream dummyInputStream;

    @Before
    public void setUp() {
        this.parser = new RomeParser();
        this.parser.parserFactory = mock(SyndFeedInput.class);
        this.dummyInputStream = new DummyInputStream();
    }

    @Test
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

    private static class DummyInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
