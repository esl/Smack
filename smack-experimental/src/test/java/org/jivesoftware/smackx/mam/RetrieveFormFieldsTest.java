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

import org.jivesoftware.smackx.mam.packet.MamPacket;
import org.jivesoftware.smackx.mam.packet.MamQueryIQ;
import org.junit.Test;

import org.junit.Assert;

public class RetrieveFormFieldsTest extends MamTest {

    private String getRetrieveFormFieldStanza(String stanzaId, String queryId) {
        return "<iq id='" + stanzaId + "' type='get'>" + "<query xmlns='" + MamPacket.NAMESPACE + "' queryid='"
                + queryId + "'></query>" + "</iq>";
    }

    @Test
    public void checkRetrieveFormFieldsStanza() throws Exception {
        Method methodPrepareMamQueryIQGet = MamManager.class.getDeclaredMethod("prepareMamQueryIQGet", String.class);
        methodPrepareMamQueryIQGet.setAccessible(true);
        MamQueryIQ mamQueryIQ = (MamQueryIQ) methodPrepareMamQueryIQGet.invoke(mamManager, queryId);
        Assert.assertEquals(mamQueryIQ.toString(), getRetrieveFormFieldStanza(mamQueryIQ.getStanzaId(), queryId));
    }

}
