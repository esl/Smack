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

import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.mam.element.MamPrefIQ;
import org.jivesoftware.smackx.mam.provider.MamPrefIQProvider;
import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;

public class MamPrefIQProviderTest extends MamTest {

    String exampleMamPrefIQ1 = "<iq type='set' id='juliet3'>" + "<prefs xmlns='urn:xmpp:mam:1' default='roster'>"
            + "<always>" + "<jid>romeo@montague.lit</jid>" + "</always>" + "<never>"
            + "<jid>montague@montague.lit</jid>" + "</never>" + "</prefs>" + "</iq>";

    String exampleMamPrefIQ2 = "<iq type='set' id='juliet3'>" + "<prefs xmlns='urn:xmpp:mam:1' default='roster'>"
            + "<always>" + "<jid>romeo@montague.lit</jid>" + "<jid>montague@montague.lit</jid>" + "</always>"
            + "<never>" + "</never>" + "</prefs>" + "</iq>";

    String exampleMamPrefIQ3 = "<iq type='get' id='juliet3'>" + "<prefs xmlns='urn:xmpp:mam:1'>" + "</prefs>" + "</iq>";

    @Test
    public void checkMamPrefIQProvider() throws Exception {
        XmlPullParser parser1 = PacketParserUtils.getParserFor(exampleMamPrefIQ1);
        MamPrefIQ mamPrefIQ1 = new MamPrefIQProvider().parse(parser1);

        Assert.assertTrue(mamPrefIQ1.isUpdatePrefs());
        Assert.assertEquals(mamPrefIQ1.getAlwaysJids().get(0), "romeo@montague.lit");
        Assert.assertEquals(mamPrefIQ1.getNeverJids().get(0), "montague@montague.lit");

        XmlPullParser parser2 = PacketParserUtils.getParserFor(exampleMamPrefIQ2);
        MamPrefIQ mamPrefIQ2 = new MamPrefIQProvider().parse(parser2);
        Assert.assertTrue(mamPrefIQ2.isUpdatePrefs());
        Assert.assertEquals(mamPrefIQ2.getAlwaysJids().get(0), "romeo@montague.lit");
        Assert.assertEquals(mamPrefIQ2.getAlwaysJids().get(1), "montague@montague.lit");
        Assert.assertTrue(mamPrefIQ2.getNeverJids().isEmpty());

        XmlPullParser parser3 = PacketParserUtils.getParserFor(exampleMamPrefIQ3);
        MamPrefIQ mamPrefIQ3 = new MamPrefIQProvider().parse(parser3);
        Assert.assertFalse(mamPrefIQ3.isUpdatePrefs());
    }

}
