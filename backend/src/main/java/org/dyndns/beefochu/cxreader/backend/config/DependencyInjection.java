package org.dyndns.beefochu.cxreader.backend.config;

import com.sun.syndication.io.SyndFeedInput;
import javax.enterprise.inject.Produces;

public class DependencyInjection {
    
    @Produces
    public SyndFeedInput getSyndFeedInput() {
        return new SyndFeedInput();
    }
}
