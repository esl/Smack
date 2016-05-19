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

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.mam.element.MamPrefsIQ;
import org.jivesoftware.smackx.mam.element.MamElements.MamPrefsExtension;
import org.xmlpull.v1.XmlPullParser;

public class MamPrefsResultProvider extends ExtensionElementProvider<MamPrefsExtension> {

    @Override
    public MamPrefsExtension parse(XmlPullParser parser, int initialDepth) throws Exception {
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
                if (parser.getName().equals(MamPrefsIQ.ELEMENT)) {
                    done = true;
                }
            }
        }

        return new MamPrefsExtension(alwaysJids, neverJids);
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
