package org.dyndns.beefochu.cxreader.frontend.web;

import javax.naming.NamingException;

import net.sourceforge.jwebunit.htmlunit.HtmlUnitTestingEngineImpl;
import net.sourceforge.jwebunit.junit.WebTester;

import org.dyndns.beefochu.cxreader.frontend.web.testutil.LoginHelper;
import org.dyndns.beefochu.cxreader.frontend.web.testutil.TestWar;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;

@RunWith(Arquillian.class)
public class IndexIT {

    private static enum IDHelper {
        TABEL_FEED_LIST("feedList");

        private String id;
        
        private IDHelper(String id) {
            this.id = id;
        }
        
        public String getId() {
            return id;
        }
    }
    
    private WebTester browser;
    
    @SuppressWarnings("rawtypes")
	@Deployment
    public static Archive deploy() {
        return TestWar.getTestWar();
    }

    @Before
    public void setUp() throws NamingException {
    	LoginHelper.login();
        browser = new WebTester();
        browser.setBaseUrl(TestWar.getUrl());
        browser.beginAt("/");
        ((HtmlUnitTestingEngineImpl) browser.getTestingEngine()).getWebClient().setAjaxController(new NicelyResynchronizingAjaxController());
    }

    @Test
    public void feedBookmarksListTest() {
        browser.assertTableRowCountEquals(IDHelper.TABEL_FEED_LIST.getId(), 2);
//        browser.assertTextInTable(IDHelper.TABLE_FEED_BOOKMARK.getId(),
//                FeedBookmarkBeanMock.FEED1);
//        browser.assertTextInTable(IDHelper.TABLE_FEED_BOOKMARK.getId(),
//                FeedBookmarkBeanMock.FEED2);
    }
}
