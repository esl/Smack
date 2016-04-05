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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smackx.mam.MamManager.AdditionalField;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jivesoftware.smackx.mam.element.MamQueryIQ;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.junit.Test;

import org.junit.Assert;

public class RetrieveFormFieldsTest extends MamTest {

    private String getRetrieveFormFieldStanza(String stanzaId, String queryId) {
        return "<iq id='" + stanzaId + "' type='get'>" + "<query xmlns='" + MamElements.NAMESPACE + "' queryid='"
                + queryId + "'></query>" + "</iq>";
    }

    private String getAdditionalFieldsStanza() {
        return "<x xmlns='jabber:x:data' type='submit'>" + "<field var='FORM_TYPE' type='hidden'>" + "<value>"
                + MamElements.NAMESPACE + "</value>" + "</field>"
                + "<field var='urn:example:xmpp:free-text-search' type='text-single'>" + "<value>Hi</value>"
                + "</field>" + "<field var='urn:example:xmpp:stanza-content' type='jid-single'>" + "<value>Hi2</value>"
                + "</field>" + "</x>";
    }

    @Test
    public void checkRetrieveFormFieldsStanza() throws Exception {
        Method methodPrepareMamQueryIQGet = MamManager.class.getDeclaredMethod("prepareMamQueryIQGet", String.class);
        methodPrepareMamQueryIQGet.setAccessible(true);
        MamQueryIQ mamQueryIQ = (MamQueryIQ) methodPrepareMamQueryIQGet.invoke(mamManager, queryId);
        Assert.assertEquals(mamQueryIQ.toString(), getRetrieveFormFieldStanza(mamQueryIQ.getStanzaId(), queryId));
    }

    @Test
    public void checkAddAdditionalFieldsStanza() throws Exception {
        Method methodAddAdditionalFields = MamManager.class.getDeclaredMethod("addAdditionalFields", List.class,
                DataForm.class);
        methodAddAdditionalFields.setAccessible(true);

        DataForm dataForm = getNewMamForm();

        List<AdditionalField> additionalFields = new ArrayList<>();

        AdditionalField field1 = new MamManager.AdditionalField(FormField.Type.text_single,
                "urn:example:xmpp:free-text-search", "Hi");
        AdditionalField field2 = new MamManager.AdditionalField(FormField.Type.jid_single,
                "urn:example:xmpp:stanza-content", "Hi2");

        additionalFields.add(field1);
        additionalFields.add(field2);

        methodAddAdditionalFields.invoke(mamManager, additionalFields, dataForm);

        String dataFormResult = dataForm.toXML().toString();

        Assert.assertEquals(dataFormResult, getAdditionalFieldsStanza());
    }

}
