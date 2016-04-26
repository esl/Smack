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

import org.jivesoftware.smackx.mam.packet.MamPacket;
import org.jivesoftware.smackx.mam.packet.MamPrefIQ;
import org.junit.Test;

import org.junit.Assert;

public class PreferencesTest extends MamTest {

    private String getRetrievePrefsStanzaExample(String stanzaId) {
        return "<iq id='" + stanzaId + "' type='get'>" + "<prefs xmlns='" + MamPacket.NAMESPACE + "'></prefs>"
                + "</iq>";
    }

    private String getUpdatePrefsStanzaExample(String stanzaId) {
        return "<iq id='" + stanzaId + "' type='set'>" + "<prefs xmlns='" + MamPacket.NAMESPACE + "' default='roster'>"
                + "<always>" + "<jid>romeo@montague.lit</jid>" + "<jid>other@montague.lit</jid>" + "</always>"
                + "<never>" + "<jid>montague@montague.lit</jid>" + "</never>" + "</prefs>" + "</iq>";
    }

    @Test
    public void checkRetrievePrefsStanza() throws Exception {
        Method prepareRetrievePreferencesStanza = MamManager.class
                .getDeclaredMethod("prepareRetrievePreferencesStanza");
        prepareRetrievePreferencesStanza.setAccessible(true);

        MamPrefIQ mamPrefIQ = (MamPrefIQ) prepareRetrievePreferencesStanza.invoke(mamManager);
        Assert.assertEquals(mamPrefIQ.toString(), getRetrievePrefsStanzaExample(mamPrefIQ.getStanzaId()));
    }

    @Test
    public void checkUpdatePrefsStanza() throws Exception {
        Method prepareUpdatePreferencesStanza = MamManager.class.getDeclaredMethod("prepareUpdatePreferencesStanza",
                List.class, List.class);
        prepareUpdatePreferencesStanza.setAccessible(true);

        List<String> alwaysJids = new ArrayList<>();
        alwaysJids.add("romeo@montague.lit");
        alwaysJids.add("other@montague.lit");

        List<String> neverJids = new ArrayList<>();
        neverJids.add("montague@montague.lit");

        MamPrefIQ mamPrefIQ = (MamPrefIQ) prepareUpdatePreferencesStanza.invoke(mamManager, alwaysJids, neverJids);
        Assert.assertEquals(mamPrefIQ.toString(), getUpdatePrefsStanzaExample(mamPrefIQ.getStanzaId()));
    }

}
