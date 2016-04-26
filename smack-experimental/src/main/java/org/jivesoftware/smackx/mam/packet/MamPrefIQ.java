/**
 *
 * Copyright © 2015 Florian Schmaus and Fernando Ramirez
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
package org.jivesoftware.smackx.mam.packet;

import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.mam.packet.MamPacket.MamPrefsExtension.AlwaysElement;
import org.jivesoftware.smackx.mam.packet.MamPacket.MamPrefsExtension.NeverElement;

/**
 * MAM Preferences IQ class.
 * 
 * @see <a href="http://xmpp.org/extensions/xep-0313.html">XEP-0313: Message
 *      Archive Management</a>
 *
 */
public class MamPrefIQ extends IQ {

    public static final String ELEMENT = "prefs";
    public static final String NAMESPACE = MamPacket.NAMESPACE;

    private boolean updatePrefs;
    private List<String> alwaysJids;
    private List<String> neverJids;

    public MamPrefIQ(boolean updatePrefs, List<String> alwaysJids, List<String> neverJids) {
        super(ELEMENT, NAMESPACE);
        this.updatePrefs = updatePrefs;
        this.alwaysJids = alwaysJids;
        this.neverJids = neverJids;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {

        if (updatePrefs) {
            if (alwaysJids != null) {
                AlwaysElement alwaysElement = new AlwaysElement(alwaysJids);
                xml.element(alwaysElement);
            }

            if (neverJids != null) {
                NeverElement neverElement = new NeverElement(neverJids);
                xml.element(neverElement);
            }
        }

        return null;
    }
}
