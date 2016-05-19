/**
 *
 * Copyright © 2016 Florian Schmaus and Fernando Ramirez
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
package org.jivesoftware.smackx.mam.element;

import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.mam.element.MamElements.MamPrefsExtension.AlwaysElement;
import org.jivesoftware.smackx.mam.element.MamElements.MamPrefsExtension.NeverElement;

/**
 * MAM Preferences IQ class.
 * 
 * @see <a href="http://xmpp.org/extensions/xep-0313.html">XEP-0313: Message
 *      Archive Management</a>
 *
 */
public class MamPrefsIQ extends IQ {

    /**
     * the preferences element.
     */
    public static final String ELEMENT = "prefs";

    /**
     * the IQ NAMESPACE.
     */
    public static final String NAMESPACE = MamElements.NAMESPACE;

    /**
     * true if it is a request for update preferences.
     */
    public boolean updatePrefs;

    /**
     * list of always.
     */
    public List<String> alwaysJids;

    /**
     * list of never.
     */
    public List<String> neverJids;

    /**
     * MAM preferences IQ constructor.
     * 
     * @param updatePrefs
     * @param alwaysJids
     * @param neverJids
     */
    public MamPrefsIQ(boolean updatePrefs, List<String> alwaysJids, List<String> neverJids) {
        super(ELEMENT, NAMESPACE);
        this.updatePrefs = updatePrefs;
        this.alwaysJids = alwaysJids;
        this.neverJids = neverJids;
    }

    /**
     * True if it is a request for update preferences.
     * 
     * @return the update preferences boolean
     */
    public boolean isUpdatePrefs() {
        return updatePrefs;
    }

    /**
     * Get the list of always store info JIDs.
     * 
     * @return the always list
     */
    public List<String> getAlwaysJids() {
        return alwaysJids;
    }

    /**
     * Get the list of never store info JIDs.
     * 
     * @return the never list
     */
    public List<String> getNeverJids() {
        return neverJids;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {

        if (updatePrefs) {
            xml.attribute("default", "roster");
            xml.rightAngleBracket();

            if (alwaysJids != null) {
                AlwaysElement alwaysElement = new AlwaysElement(alwaysJids);
                xml.element(alwaysElement);
            }

            if (neverJids != null) {
                NeverElement neverElement = new NeverElement(neverJids);
                xml.element(neverElement);
            }
        } else {
            xml.rightAngleBracket();
        }

        return xml;
    }

}
