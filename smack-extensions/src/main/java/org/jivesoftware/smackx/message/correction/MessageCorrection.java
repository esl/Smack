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
package org.jivesoftware.smackx.message.correction;

import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MessageCorrection implements ExtensionElement {

    public static final String ELEMENT = "replace";
    public static final String NAMESPACE = "urn:xmpp:message-correct:0";
    private static final String JID_TAG = "id";

    private String jidInitialMessage;

    public MessageCorrection(String jidInitialMessage) {
        this.setJidInitialMessage(jidInitialMessage);
    }

    public String getJidInitialMessage() {
        return jidInitialMessage;
    }

    public void setJidInitialMessage(String jidInitialMessage) {
        this.jidInitialMessage = jidInitialMessage;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public CharSequence toXML() {
        return "<" + ELEMENT + " " + JID_TAG + "='" + getJidInitialMessage() + "' xmlns='" + NAMESPACE + "'/>";
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    public static class Provider extends ExtensionElementProvider<MessageCorrection> {

        @Override
        public MessageCorrection parse(XmlPullParser parser, int initialDepth)
                throws XmlPullParserException, IOException, SmackException {
            String jidMessageToReplace;

            try {
                jidMessageToReplace = parser.getAttributeValue("", JID_TAG);
            } catch (Exception ex) {
                jidMessageToReplace = "";
            }

            return new MessageCorrection(jidMessageToReplace);
        }
    }

}
