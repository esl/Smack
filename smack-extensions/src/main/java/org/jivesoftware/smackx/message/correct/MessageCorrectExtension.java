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
package org.jivesoftware.smackx.message.correct;

import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * An Extension that implements XEP-0308: Last Message Correction
 * 
 * This extension is expected to be added to message stanzas. Please refer to
 * the XEP for more implementation guidelines.
 * 
 * @author Fernando Ramirez, f.e.ramirez94@gmail.com
 */
public class MessageCorrectExtension implements ExtensionElement {

    /**
     * The XML element name of a 'message correct' extension.
     */
    public static final String ELEMENT = "replace";

    /**
     * The namespace that qualifies the XML element of a 'message correct'
     * extension.
     */
    public static final String NAMESPACE = "urn:xmpp:message-correct:0";

    /**
     * The id tag of a 'message correct' extension.
     */
    private static final String JID_TAG = "id";

    /**
     * The jid of the message to correct.
     */
    private String jidInitialMessage;

    public MessageCorrectExtension(String jidInitialMessage) {
        this.setJidInitialMessage(jidInitialMessage);
    }

    public String getJidInitialMessage() {
        return jidInitialMessage;
    }

    public void setJidInitialMessage(String jidInitialMessage) {
        this.jidInitialMessage = jidInitialMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jivesoftware.smack.packet.PacketExtension#getElementName()
     */
    @Override
    public String getElementName() {
        return ELEMENT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jivesoftware.smack.packet.PacketExtension#toXML()
     */
    @Override
    public XmlStringBuilder toXML() {
        XmlStringBuilder xml = new XmlStringBuilder(this, getNamespace());
        
        xml.attribute(JID_TAG, getJidInitialMessage());
        xml.attribute("xmlns", getNamespace());
        xml.append('/');
        xml.rightAngleBracket();
        
        return xml;
        
//        final StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append('<').append(getElementName()).append(' ').append(JID_TAG).append('=').append('\'')
//                .append(getJidInitialMessage()).append('\'').append(" xmlns='").append(getNamespace()).append("'/>");
//        return stringBuilder.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jivesoftware.smack.packet.PacketExtension#getNamespace()
     */
    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    /**
     * A ExtensionElementProvider for the MessageCorrectExtension. 
     * As MessageCorrection elements have only the JID of the message to replace.
     * 
     * @author Fernando Ramirez, f.e.ramirez94@gmail.com
     */
    public static class Provider extends ExtensionElementProvider<MessageCorrectExtension> {

        @Override
        public MessageCorrectExtension parse(XmlPullParser parser, int initialDepth)
                throws XmlPullParserException, IOException, SmackException {
            String jidMessageToReplace;
            jidMessageToReplace = parser.getAttributeValue("", JID_TAG);
            return new MessageCorrectExtension(jidMessageToReplace);
        }
    }

}
