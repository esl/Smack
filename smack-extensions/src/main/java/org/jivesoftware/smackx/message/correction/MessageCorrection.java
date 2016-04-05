package org.jivesoftware.smackx.message.correction;

import org.jivesoftware.smack.packet.ExtensionElement;

public class MessageCorrection implements ExtensionElement{

    public static final String ELEMENT = "replace";
    public static final String NAMESPACE = "urn:xmpp:message-correct:0";
    private static final String JID_TAG = "id";

    private String jidPreviousMessage;

    public MessageCorrection(String jidPreviousMessage) {
        this.setJidPreviousMessage(jidPreviousMessage);
    }
	
	@Override
	public String getElementName() {
		return ELEMENT;
	}

	@Override
	public CharSequence toXML() {
		return "<" + ELEMENT + " " + JID_TAG + "=" + getJidPreviousMessage() + " xmlns='" + NAMESPACE + ">";
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	public String getJidPreviousMessage() {
		return jidPreviousMessage;
	}

	public void setJidPreviousMessage(String jidPreviousMessage) {
		this.jidPreviousMessage = jidPreviousMessage;
	}

}
