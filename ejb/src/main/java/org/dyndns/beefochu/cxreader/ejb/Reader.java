package org.dyndns.beefochu.cxreader.ejb;

import java.net.URL;
import java.util.List;
import org.dyndns.beefochu.cxreader.ejb.domain.FeedEntryUserRelation;
import org.dyndns.beefochu.cxreader.ejb.domain.FeedUserRelation;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedAlreadyInListException;
import org.dyndns.beefochu.cxreader.ejb.exceptions.FeedUrlInvalidException;

public interface Reader {

    /**
     * Returns a list with all feeds of a user.
     * 
     * @param username The name of the user
     */
    public List<FeedUserRelation> getFeedList(String username);

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
    public FeedUserRelation bookmarkFeed(String username, URL feed)
            throws FeedUrlInvalidException, FeedAlreadyInListException;

    /**
     * 
     * @param username The name of the user which want's to bookmark the Feed.
     * @param feed The feed the user want to remove from his bookmark list. Only
     *              feeds retrived by #getFeedList(java.lang.String) can be used!
     */
    public void removeBookmarkedFeed(String username, FeedUserRelation feed);

    /**
     * Returns entries of a feed. The entries are ordered by date.
     * 
     * @param offset The number of the first entry in the list.
     * @param count The number of entries that should be transfered. If -1 than
     *          all entries starting from offset are returned.
     * @param unreadOnly When true this methode returns only feed entries that aren't
     *              read by the user. When false, all entries are returned.
     * @return 
     */
    public List<FeedEntryUserRelation> getEntries(String username,
            FeedUserRelation feed, int offset, int count, boolean unreadOnly);

    /**
     *  Mark an feed entry as read or not read for a specific user.
     * 
     * @param read When true the entry get's marked as read. When not it get's
     *              marked as unread.
     */
    public void setReadStatus(String username, FeedEntryUserRelation entry, boolean read);
}
