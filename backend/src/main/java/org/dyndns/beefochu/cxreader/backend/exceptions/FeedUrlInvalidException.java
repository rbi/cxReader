package org.dyndns.beefochu.cxreader.backend.exceptions;

import java.io.IOException;

public class FeedUrlInvalidException extends Exception {
    private static final long serialVersionUID = 2L;

    public FeedUrlInvalidException() {
        
    }
    
    public FeedUrlInvalidException(IOException ex) {
        super(ex);
    }
    
}
