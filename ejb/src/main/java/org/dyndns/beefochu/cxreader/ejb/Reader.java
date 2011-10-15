package org.dyndns.beefochu.cxreader.ejb;

import java.net.URL;
import java.util.List;
import org.dyndns.beefochu.cxreader.ejb.domain.Feed;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedUrlInvalidException;

public interface Reader {

    /**
     * Returns a list with all feeds of a user.
     * 
     * @param username The name of the user
     */
    public List<Feed> getFeedList(String username);

    /**
     * Saves all changes to the database.
     */
    public void save();

    /**
     * Adds a feed to the feedlist of the user.
     * 
     * @param username The name of the user which want's to bookmark the Feed.
     * @param feed The URL for the feed the user want to bookmark.
     * 
     * @throws FeedUrlInvalidException if the URL doesn't point to a feed that
     *              is supportet by the software
     * @throws FeedAlreadyInListException Thrown when the Feed is allready bookmarked
     *              by the user
     */
    public void bookmarkFeed(String username, URL feed)
            throws FeedUrlInvalidException, FeedAlreadyInListException;

    /**
     * 
     * @param username The name of the user which want's to bookmark the Feed.
     * @param feed The feed the user want to remove from his bookmark list. Only
     *              feed retrived by #getFeedList(java.lang.String) can be used!
     */
    public void removeBookmarkedFeed(String username, Feed feed);
}
