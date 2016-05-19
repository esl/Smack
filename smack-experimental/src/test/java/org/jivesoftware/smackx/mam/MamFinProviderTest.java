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
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.mam.element.MamFinIQ;
import org.jivesoftware.smackx.mam.element.MamElements.MamFinExtension;
import org.jivesoftware.smackx.mam.provider.MamFinExtensionProvider;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;

public class MamFinProviderTest extends MamTest {

    String exmapleMamFinXml = "<fin xmlns='urn:xmpp:mam:1' complete='true'>"
            + "<set xmlns='http://jabber.org/protocol/rsm'>" + "<first index='0'>28482-98726-73623</first>"
            + "<last>09af3-cc343-b409f</last>" + "<count>20</count>" + "</set>" + "</fin>";

    private String getIQLimitedResultsExample() {
        return "<iq type='result' id='u29303'>" + "<fin xmlns='urn:xmpp:mam:1' complete='true'>"
                + "<set xmlns='http://jabber.org/protocol/rsm'>" + "<first index='0'>23452-4534-1</first>"
                + "<last>390-2342-22</last>" + "<count>16</count>" + "</set>" + "</fin>" + "</iq>";
    }

    @Test
    public void checkMamFinProvider() throws Exception {
        XmlPullParser parser = PacketParserUtils.getParserFor(exmapleMamFinXml);
        MamFinExtension mamFinExtension = new MamFinExtensionProvider().parse(parser);

        Assert.assertTrue(mamFinExtension.isComplete());
        Assert.assertFalse(mamFinExtension.isStable());
        Assert.assertNull(mamFinExtension.getQueryId());

        RSMSet rsmSet = mamFinExtension.getRSMSet();
        Assert.assertEquals(rsmSet.getLast(), "09af3-cc343-b409f");
        Assert.assertEquals(rsmSet.getCount(), 20);
        Assert.assertEquals(rsmSet.getFirst(), "28482-98726-73623");
        Assert.assertEquals(rsmSet.getFirstIndex(), 0);
    }

    @Test
    public void checkQueryLimitedResults() throws Exception {
        IQ iq = (IQ) PacketParserUtils.parseStanza(getIQLimitedResultsExample());

        MamFinIQ mamFinIQ = MamFinIQ.from(iq);
        Assert.assertEquals(mamFinIQ.getType(), Type.result);

        MamFinExtension mamFinExtension = MamFinExtension.from(iq);
        Assert.assertTrue(mamFinExtension.isComplete());
        Assert.assertEquals(mamFinExtension.getRSMSet().getCount(), 16);
        Assert.assertEquals(mamFinExtension.getRSMSet().getFirst(), "23452-4534-1");
        Assert.assertEquals(mamFinExtension.getRSMSet().getFirstIndex(), 0);
        Assert.assertEquals(mamFinExtension.getRSMSet().getLast(), "390-2342-22");
    }

}
