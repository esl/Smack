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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
    private MamManager mamManager;

    @Before
    public void setup() {
        // mock connection
        connection = mock(XMPPConnection.class);

        // test query id
        queryId = "testid";

        // MamManager instance
        mamManager = MamManager.getInstanceFor(connection);
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
        return "<iq id='" + stanzaId + "' type='set'>" + "<query xmlns='urn:xmpp:mam:1' queryid='testid'>"
                + "<x xmlns='jabber:x:data' type='submit'>" + "<field var='FORM_TYPE' type='hidden'>"
                + "<value>urn:xmpp:mam:1</value>" + "</field>" + "</x>" + "</query>" + "</iq>";
    }

    private String getMamXMemberWith(List<String> fieldsNames, List<String> fieldsValues) {
        String xml = "<x xmlns='jabber:x:data' type='submit'>" + "<field var='FORM_TYPE' type='hidden'>"
                + "<value>urn:xmpp:mam:1</value>" + "</field>";

        for (int i = 0; i < fieldsNames.size() && i < fieldsValues.size(); i++) {
            xml += "<field var='" + fieldsNames.get(i) + "'>" + "<value>" + fieldsValues.get(i) + "</value>"
                    + "</field>";
        }

        xml += "</x>";
        return xml;
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
        Method methodAddStartDateFilter = MamManager.class.getDeclaredMethod("addStart", Date.class, DataForm.class);
        methodAddStartDateFilter.setAccessible(true);

        Date date = new Date();
        DataForm dataForm = getNewMamForm();
        methodAddStartDateFilter.invoke(mamManager, date, dataForm);

        List<String> fields = new ArrayList<>();
        fields.add("start");
        List<String> values = new ArrayList<>();
        values.add(XmppDateTime.formatXEP0082Date(date));
        
        Assert.assertEquals(dataForm.toXML().toString(), getMamXMemberWith(fields, values));
    }

    @Test
    public void checkEndDateFilter() throws Exception {
        Method methodAddEndDateFilter = MamManager.class.getDeclaredMethod("addEnd", Date.class, DataForm.class);
        methodAddEndDateFilter.setAccessible(true);

        Date date = new Date();
        DataForm dataForm = getNewMamForm();
        methodAddEndDateFilter.invoke(mamManager, date, dataForm);

        List<String> fields = new ArrayList<>();
        fields.add("end");
        List<String> values = new ArrayList<>();
        values.add(XmppDateTime.formatXEP0082Date(date));

        Assert.assertEquals(dataForm.toXML().toString(), getMamXMemberWith(fields, values));
    }

    @Test
    public void checkWithJidFilter() throws Exception {
        Method methodAddJidFilter = MamManager.class.getDeclaredMethod("addWithJid", String.class, DataForm.class);
        methodAddJidFilter.setAccessible(true);

        String jid = "test@jid.com";
        DataForm dataForm = getNewMamForm();
        methodAddJidFilter.invoke(mamManager, jid, dataForm);

        List<String> fields = new ArrayList<>();
        fields.add("with");
        List<String> values = new ArrayList<>();
        values.add(jid);
        
        Assert.assertEquals(dataForm.toXML().toString(), getMamXMemberWith(fields, values));
    }

    @Test
    public void checkMultipleFilters() throws Exception {
        Method methodAddStartDateFilter = MamManager.class.getDeclaredMethod("addStart", Date.class, DataForm.class);
        methodAddStartDateFilter.setAccessible(true);
        Method methodAddEndDateFilter = MamManager.class.getDeclaredMethod("addEnd", Date.class, DataForm.class);
        methodAddEndDateFilter.setAccessible(true);
        Method methodAddJidFilter = MamManager.class.getDeclaredMethod("addWithJid", String.class, DataForm.class);
        methodAddJidFilter.setAccessible(true);

        DataForm dataForm = getNewMamForm();
        Date date = new Date();
        String dateString = XmppDateTime.formatXEP0082Date(date);
        String jid = "test@jid.com";

        methodAddStartDateFilter.invoke(mamManager, date, dataForm);
        methodAddEndDateFilter.invoke(mamManager, date, dataForm);
        methodAddJidFilter.invoke(mamManager, jid, dataForm);
        String dataFormResult = dataForm.toXML().toString();

        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        
        fields.add("start");
        values.add(dateString);
        Assert.assertNotEquals(dataFormResult, getMamXMemberWith(fields, values));

        fields.add("end");
        values.add(dateString);
        Assert.assertNotEquals(dataFormResult, getMamXMemberWith(fields, values));

        fields.clear();
        values.clear();
        
        fields.add("start");
        values.add(dateString);
        fields.add("with");
        values.add(jid);
        Assert.assertNotEquals(dataFormResult, getMamXMemberWith(fields, values));

        fields.clear();
        values.clear();
        
        fields.add("end");
        values.add(dateString);
        fields.add("with");
        values.add(jid);
        fields.add("start");
        values.add(dateString);
        Assert.assertNotEquals(dataFormResult, getMamXMemberWith(fields, values));
        
        fields.clear();
        values.clear();
        
        fields.add("start");
        values.add(dateString);
        fields.add("end");
        values.add(dateString);
        fields.add("with");
        values.add(jid);
        Assert.assertEquals(dataFormResult, getMamXMemberWith(fields, values));
    }

}
