/**
 *
 * Copyright 2016 Fernando Ramirez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.mam;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.util.Date;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.mam.packet.MamPacket;
import org.jivesoftware.smackx.mam.packet.MamQueryIQ;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jxmpp.util.XmppDateTime;

public class QueryArchiveTest {

    private XMPPConnection connection;
    private String queryId;
    
    @Before
    public void setup() {
        // mock connection
        connection = mock(XMPPConnection.class);
        
        // test query id
        queryId = "testid";
    }

    private DataForm getNewMamForm() {
        FormField field = new FormField(FormField.FORM_TYPE);
        field.setType(FormField.Type.hidden);
        field.addValue(MamPacket.NAMESPACE);
        DataForm form = new DataForm(DataForm.Type.submit);
        form.addField(field);
        return form;
    }

    private String getMamSimpleQueryIQ(String stanzaId) {
        return "<iq id='" + stanzaId + "' type='set'>"
                + "<query xmlns='urn:xmpp:mam:1' queryid='testid'>"
                    + "<x xmlns='jabber:x:data' type='submit'>" 
                        + "<field var='FORM_TYPE' type='hidden'>"
                            + "<value>urn:xmpp:mam:1</value>" 
                        + "</field>" 
                    + "</x>" 
                + "</query>" 
              + "</iq>";
    }
    
    private String getMamXMemberWithStartDate(String startDate) {
        return  "<x xmlns='jabber:x:data' type='submit'>"
                    + "<field var='FORM_TYPE' type='hidden'>"
                        + "<value>urn:xmpp:mam:1</value>"
                    + "</field>"
                    + "<field var='start'>"
                        + "<value>" + startDate + "</value>"
                    + "</field>"
                +"</x>";
    }

    @Test
    public void checkMamQueryIQ() throws Exception {
        
        DataForm dataForm = getNewMamForm();

        MamQueryIQ mamQueryIQ = new MamQueryIQ(queryId, dataForm);
        mamQueryIQ.setType(IQ.Type.set);

        Assert.assertEquals(mamQueryIQ.toString(), getMamSimpleQueryIQ(mamQueryIQ.getStanzaId()));
    }

    @Test
    public void checkStartDateFilter() throws Exception {
        MamManager mamManager = MamManager.getInstanceFor(connection);
        Method method = MamManager.class.getDeclaredMethod("addStart", Date.class, DataForm.class);
        method.setAccessible(true);
        
        Date date = new Date();
        
        DataForm dataForm = getNewMamForm();
        method.invoke(mamManager, date, dataForm);
        
        Assert.assertEquals(dataForm.toXML().toString(), getMamXMemberWithStartDate(XmppDateTime.formatXEP0082Date(date)));
    }
    
}
