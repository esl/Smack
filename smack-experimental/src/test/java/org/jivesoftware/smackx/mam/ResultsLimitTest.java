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

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jivesoftware.smackx.mam.element.MamQueryIQ;
import org.jivesoftware.smackx.mam.element.MamElements.MamFinExtension;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.junit.Assert;
import org.junit.Test;

public class ResultsLimitTest extends MamTest {

    private String getResultsLimitStanza(String stanzaId, Integer limitNumber) {
        return "<iq id='" + stanzaId + "' type='set'>" + "<query xmlns='urn:xmpp:mam:1' queryid='" + queryId + "'>"
                + "<x xmlns='jabber:x:data' type='submit'>" + "<field var='FORM_TYPE' type='hidden'>" + "<value>"
                + MamElements.NAMESPACE + "</value>" + "</field>" + "</x>"
                + "<set xmlns='http://jabber.org/protocol/rsm'>" + "<max>" + String.valueOf(limitNumber) + "</max>"
                + "</set>" + "</query>" + "</iq>";
    }

    private String getIQLimitedResultsExample() {
        return "<iq type='result' id='u29303'>" + "<fin xmlns='urn:xmpp:mam:1' complete='true'>"
                + "<set xmlns='http://jabber.org/protocol/rsm'>" + "<first index='0'>23452-4534-1</first>"
                + "<last>390-2342-22</last>" + "<count>16</count>" + "</set>" + "</fin>" + "</iq>";
    }

    @Test
    public void checkResultsLimit() throws Exception {
        Method methodAddResultsLimit = MamManager.class.getDeclaredMethod("addResultsLimit", Integer.class,
                MamQueryIQ.class);
        methodAddResultsLimit.setAccessible(true);

        DataForm dataForm = getNewMamForm();
        MamQueryIQ mamQueryIQ = new MamQueryIQ(queryId, dataForm);
        mamQueryIQ.setType(IQ.Type.set);

        methodAddResultsLimit.invoke(mamManager, 10, mamQueryIQ);
        Assert.assertEquals(mamQueryIQ.toString(), getResultsLimitStanza(mamQueryIQ.getStanzaId(), 10));
    }

    @Test
    public void checkQueryLimitedResults() throws Exception {
        IQ iq = (IQ) PacketParserUtils.parseStanza(getIQLimitedResultsExample());

        MamFinExtension mamFinExtension = MamFinExtension.from(iq);

        Assert.assertEquals(mamFinExtension.getRSMSet().getCount(), 16);

    }

}
