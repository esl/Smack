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

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.mam.packet.MamPacket;
import org.jivesoftware.smackx.mam.packet.MamQueryIQ;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.junit.Assert;
import org.junit.Test;

public class QueryArchiveTest extends MamTest {

    private String getMamSimpleQueryIQ(String stanzaId) {
        return "<iq id='" + stanzaId + "' type='set'>" + "<query xmlns='urn:xmpp:mam:1' queryid='" + queryId + "'>"
                + "<x xmlns='jabber:x:data' type='submit'>" + "<field var='FORM_TYPE' type='hidden'>" + "<value>"
                + MamPacket.NAMESPACE + "</value>" + "</field>" + "</x>" + "</query>" + "</iq>";
    }

    @Test
    public void checkMamQueryIQ() throws Exception {
        DataForm dataForm = getNewMamForm();
        MamQueryIQ mamQueryIQ = new MamQueryIQ(queryId, dataForm);
        mamQueryIQ.setType(IQ.Type.set);
        Assert.assertEquals(mamQueryIQ.toString(), getMamSimpleQueryIQ(mamQueryIQ.getStanzaId()));
    }

}
