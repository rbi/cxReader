package org.dyndns.beefochu.cxreader.ejb.ejb;

import org.dyndns.beefochu.cxreader.ejb.ejb.ReaderGateway;
import java.util.LinkedList;
import org.mockito.InOrder;
import java.security.Principal;
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.dyndns.beefochu.cxreader.ejb.domain.ReaderUser;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ReaderGatewayTest {

    private final static String TESTUSER = "testUser";
    
    private ReaderGateway gateway;
    private Principal prinicpal;
    private TypedQuery<ReaderUser> userQuery;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.gateway = new ReaderGateway();
        
        this.gateway.ctx = mock(SessionContext.class);
        this.prinicpal = mock(Principal.class);
        when(this.gateway.ctx.getCallerPrincipal()).thenReturn(this.prinicpal);
        when(this.prinicpal.getName()).thenReturn(TESTUSER);
        
        this.gateway.em = mock(EntityManager.class);
        this.userQuery = mock(TypedQuery.class);
        when(this.gateway.em.createNamedQuery(ReaderUser.FIND_BY_NAME, ReaderUser.class)).thenReturn(this.userQuery);
        when(this.userQuery.getResultList()).thenReturn(new LinkedList<ReaderUser>());
    }

    @Test
    public void testGetFeedList() {
        gateway.getFeedList();
        
        InOrder inOrder = inOrder(this.gateway.ctx, this.prinicpal, this.userQuery, this.gateway.em);

        inOrder.verify(this.gateway.ctx).getCallerPrincipal();
        inOrder.verify(this.prinicpal).getName();
        inOrder.verify(this.gateway.em).createNamedQuery(ReaderUser.FIND_BY_NAME, ReaderUser.class);
        inOrder.verify(this.userQuery).setParameter("username", TESTUSER);
        inOrder.verify(this.gateway.em).persist(any(ReaderUser.class));
    }
}
