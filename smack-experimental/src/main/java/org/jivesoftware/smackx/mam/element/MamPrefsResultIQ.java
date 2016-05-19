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
package org.jivesoftware.smackx.mam.element;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.mam.element.MamElements.MamPrefsExtension;

/**
 * MAM Preferences result IQ class.
 * 
 * @see <a href="http://xmpp.org/extensions/xep-0313.html">XEP-0313: Message
 *      Archive Management</a>
 *
 */
public class MamPrefsResultIQ extends IQ {

    /**
     * the preferences element.
     */
    public static final String ELEMENT = "prefs";

    /**
     * the IQ NAMESPACE.
     */
    public static final String NAMESPACE = MamElements.NAMESPACE;

    /**
     * The preferences extension element.
     */
    private MamPrefsExtension mamPrefsExtension;

    /**
     * MAM preferences result IQ constructor.
     * 
     * @param mamPrefsExtension
     */
    public MamPrefsResultIQ(MamPrefsExtension mamPrefsExtension) {
        super(ELEMENT, NAMESPACE);
        this.mamPrefsExtension = mamPrefsExtension;
        this.addExtension(mamPrefsExtension);
    }

    /**
     * Get the list of always store info JIDs.
     * 
     * @return the always list
     */
    public MamPrefsExtension getMamPrefsExtension() {
        return mamPrefsExtension;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return xml;
    }

    public static MamPrefsResultIQ from(IQ iq) {
        return (MamPrefsResultIQ) iq;
    }

}
