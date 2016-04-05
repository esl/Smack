package org.jivesoftware.smackx.message.correction;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageCorrectionTest {

	private Message initialMessage;
	
	private final String jidInitialMessage = "bad1";
   
	private final String initialMessageXml = 
					"<message to='juliet@capulet.net/balcony' id='good1'>"
					+ "<body>But soft, what light through yonder window breaks?</body>"
					+ "</message>";
	
	private final CharSequence messageCorrectionXml = "<replace id='bad1' xmlns='urn:xmpp:message-correct:0'/>";
	
	private final CharSequence expectedXml = 
			"<message to='juliet@capulet.net/balcony' id='good1'>"
			+ "<body>But soft, what light through yonder window breaks?</body>"
			+ "<replace id='bad1' xmlns='urn:xmpp:message-correct:0'/>"
			+ "</message>";    
	

	@Before
	public void setStanzas() throws Exception{
        initialMessage = (Message) PacketParserUtils.parseStanza(initialMessageXml);
	}

	@Test
	public void checkStanzas() throws Exception {
		MessageCorrection messageCorrection = new MessageCorrection(jidInitialMessage);
				
		Assert.assertEquals(messageCorrection.toXML(), messageCorrectionXml);
		
		initialMessage.addExtension(messageCorrection);
		
		Assert.assertEquals(initialMessage.toXML(), expectedXml);
	}
	
}
