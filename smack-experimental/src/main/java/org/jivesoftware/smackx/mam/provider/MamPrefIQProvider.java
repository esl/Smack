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
package org.jivesoftware.smackx.mam.provider;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.mam.element.MamPrefIQ;
import org.xmlpull.v1.XmlPullParser;

/**
 * MAM Preferences IQ Provider class.
 * 
 * @see <a href="http://xmpp.org/extensions/xep-0313.html">XEP-0313: Message
 *      Archive Management</a>
 *
 */
public class MamPrefIQProvider extends IQProvider<MamPrefIQ> {

    @Override
    public MamPrefIQ parse(XmlPullParser parser, int initialDepth) throws Exception {

        String iqType = parser.getAttributeValue("", "type");
        boolean isUpdate = iqType.equals("set");

        List<String> alwaysJids = null;
        List<String> neverJids = null;

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("always")) {
                    alwaysJids = iterateJids(parser, "always");
                }
                if (parser.getName().equals("never")) {
                    neverJids = iterateJids(parser, "never");
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(MamPrefIQ.ELEMENT)) {
                    done = true;
                }
            }
        }

        return new MamPrefIQ(isUpdate, alwaysJids, neverJids);
    }

    private List<String> iterateJids(XmlPullParser parser, String listType) throws Exception {
        List<String> jids = new ArrayList<>();
        boolean done = false;

        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("jid")) {
                    parser.next();
                    jids.add(parser.getText());
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(listType)) {
                    done = true;
                }
            }
        }

        return jids;
    }

}
