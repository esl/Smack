/**
 *
 * Copyright the original author or authors
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
 * 
 * @author Fernando Ramirez, f.e.ramirez94@gmail.com
 */
package org.jivesoftware.smackx.message.correct;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.message.correct.MessageCorrectExtension;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageCorrectExtensionTest {

    private Message initialMessage;

    private final String jidInitialMessage = "bad1";

    private final String initialMessageXml = "<message to='juliet@capulet.net/balcony' id='good1'>"
            + "<body>But soft, what light through yonder window breaks?</body>" + "</message>";

    private final CharSequence messageCorrectionXml = "<replace id='bad1' xmlns='urn:xmpp:message-correct:0'/>";

    private final CharSequence expectedXml = "<message to='juliet@capulet.net/balcony' id='good1'>"
            + "<body>But soft, what light through yonder window breaks?</body>"
            + "<replace id='bad1' xmlns='urn:xmpp:message-correct:0'/>" + "</message>";

    @Before
    public void setStanzas() throws Exception {
        initialMessage = (Message) PacketParserUtils.parseStanza(initialMessageXml);
    }

    @Test
    public void checkStanzas() throws Exception {
        MessageCorrectExtension messageCorrectExtension = new MessageCorrectExtension(jidInitialMessage);

        Assert.assertEquals(messageCorrectExtension.toXML(), messageCorrectionXml);

        initialMessage.addExtension(messageCorrectExtension);

        Assert.assertEquals(initialMessage.toXML(), expectedXml);
    }

}
