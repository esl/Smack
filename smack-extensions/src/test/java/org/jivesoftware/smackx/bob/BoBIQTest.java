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
package org.jivesoftware.smackx.bob;

import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.bob.element.BoBIQ;
import org.jivesoftware.smackx.bob.provider.BoBIQProvider;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;

public class BoBIQTest {

    String sampleBoBIQRequest = "<iq to='ladymacbeth@shakespeare.lit/castle' id='sarasa' type='get'>"
            + "<data xmlns='urn:xmpp:bob' cid='sha1+8f35fef110ffc5df08d579a50083ff9308fb6242@bob.xmpp.org'/>" + "</iq>";

    String sampleBoBIQResponse = "<iq to='doctor@shakespeare.lit/pda' id='sarasa' type='result'>"
            + "<data xmlns='urn:xmpp:bob' cid='sha1+8f35fef110ffc5df08d579a50083ff9308fb6242@bob.xmpp.org' "
            + "max-age='86400' type='image/png'>" + "iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAABGdBTUEAALGP"
            + "CxhBQAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9YGARc5KB0XV+IA"
            + "AAAddEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIFRoZSBHSU1Q72QlbgAAAF1J"
            + "REFUGNO9zL0NglAAxPEfdLTs4BZM4DIO4C7OwQg2JoQ9LE1exdlYvBBeZ7jq"
            + "ch9q1uH4TLzw4d6+ErXMMcXuHWxId3KOETnnXXV6MJpcq2MLaI97CER3N0vr4MkhoXe0rZigAAAABJRU5ErkJggg==" + "</data>"
            + "</iq>";

    @Test
    public void checkBoBIQRequest() throws Exception {
        XmlPullParser parser = PacketParserUtils.getParserFor(sampleBoBIQRequest);
        BoBIQ bobIQ = new BoBIQProvider().parse(parser);
        bobIQ.setStanzaId("sarasa");
        bobIQ.setTo(JidCreate.from("ladymacbeth@shakespeare.lit/castle"));
        bobIQ.setType(Type.get);

        BoBHash bobHash = new BoBHash("8f35fef110ffc5df08d579a50083ff9308fb6242", "sha1");

        BoBIQ createdBoBIQ = new BoBIQ(bobHash);
        createdBoBIQ.setStanzaId("sarasa");
        createdBoBIQ.setTo(JidCreate.from("ladymacbeth@shakespeare.lit/castle"));
        createdBoBIQ.setType(Type.get);

        Assert.assertEquals(sampleBoBIQRequest, createdBoBIQ.toXML().toString());
    }

    @Test
    public void checkBoBIQResponse() throws Exception {
        XmlPullParser parser = PacketParserUtils.getParserFor(sampleBoBIQResponse);
        BoBIQ bobIQ = new BoBIQProvider().parse(parser);
        bobIQ.setStanzaId("sarasa");
        bobIQ.setTo(JidCreate.from("doctor@shakespeare.lit/pda"));
        bobIQ.setType(Type.result);

        String data = "iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAABGdBTUEAALGPC"
                + "xhBQAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9YGARc5KB0XV+IA"
                + "AAAddEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIFRoZSBHSU1Q72QlbgAAAF1J"
                + "REFUGNO9zL0NglAAxPEfdLTs4BZM4DIO4C7OwQg2JoQ9LE1exdlYvBBeZ7jq"
                + "ch9q1uH4TLzw4d6+ErXMMcXuHWxId3KOETnnXXV6MJpcq2MLaI97CER3N0vr4MkhoXe0rZigAAAABJRU5ErkJggg==";

        BoBHash bobHash = new BoBHash("8f35fef110ffc5df08d579a50083ff9308fb6242", "sha1");
        BoBData bobData = new BoBData(86400, "image/png", data.getBytes());

        BoBIQ createdBoBIQ = new BoBIQ(bobHash, bobData);
        createdBoBIQ.setStanzaId("sarasa");
        createdBoBIQ.setTo(JidCreate.from("doctor@shakespeare.lit/pda"));
        createdBoBIQ.setType(Type.result);

        Assert.assertEquals(bobIQ.toXML().toString(), createdBoBIQ.toXML().toString());
    }

}