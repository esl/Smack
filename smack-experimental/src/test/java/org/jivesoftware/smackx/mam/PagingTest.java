package org.jivesoftware.smackx.mam;

import java.lang.reflect.Method;
import java.util.UUID;

import org.jivesoftware.smackx.mam.packet.MamQueryIQ;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.junit.Test;

import org.junit.Assert;

public class PagingTest extends MamTest {

    private String getPagingStanza(String stanzaId, String queryId, int max) {
        return "<iq id='" + stanzaId + "' type='set'>" + "<query xmlns='urn:xmpp:mam:1' queryid='" + queryId + "'>"
                + "<x xmlns='jabber:x:data' type='submit'>" + "<field var='FORM_TYPE' type='hidden'>"
                + "<value>urn:xmpp:mam:1</value>" + "</field>" + "</x>" + "<set xmlns='http://jabber.org/protocol/rsm'>"
                + "<max>"+ String.valueOf(max) +"</max>" + "</set>" + "</query>" + "</iq>";
    }

    @Test
    public void checkPageQueryStanza() throws Exception {
        Method methodPreparePageQuery = MamManager.class.getDeclaredMethod("preparePageQuery", MamQueryIQ.class,
                RSMSet.class);
        methodPreparePageQuery.setAccessible(true);

        DataForm dataForm = getNewMamForm();
        int max = 10;
        RSMSet rsmSet = new RSMSet(max);
        MamQueryIQ mamQueryIQ = new MamQueryIQ(queryId, dataForm);

        methodPreparePageQuery.invoke(mamManager, mamQueryIQ, rsmSet);

        Assert.assertEquals(mamQueryIQ.toString(), getPagingStanza(mamQueryIQ.getStanzaId(), queryId, max));
    }

}
