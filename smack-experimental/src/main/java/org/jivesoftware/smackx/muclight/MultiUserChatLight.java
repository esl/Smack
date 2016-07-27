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
package org.jivesoftware.smackx.muclight;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.IQReplyFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.muclight.element.MUCLightAffiliationsIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightChangeAffiliationsIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightConfigurationIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightCreateIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightDestroyIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightGetAffiliationsIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightGetConfigsIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightGetInfoIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightInfoIQ;
import org.jivesoftware.smackx.muclight.element.MUCLightSetConfigsIQ;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * MUCLight class.
 * 
 * @author Fernando Ramirez
 */
public class MultiUserChatLight {

    public static final String NAMESPACE = "urn:xmpp:muclight:0";

    public static final String AFFILIATIONS = "#affiliations";
    public static final String INFO = "#info";
    public static final String CONFIGURATION = "#configuration";
    public static final String CREATE = "#create";
    public static final String DESTROY = "#destroy";
    public static final String BLOCKING = "#blocking";

    private final XMPPConnection connection;
    private final EntityJid room;

    private final Set<MessageListener> messageListeners = new CopyOnWriteArraySet<MessageListener>();

    /**
     * This filter will match all stanzas send from the groupchat or from one if
     * the groupchat occupants.
     */
    private final StanzaFilter fromRoomFilter;

    /**
     * Same as {@link #fromRoomFilter} together with
     * {@link MessageTypeFilter#GROUPCHAT}.
     */
    private final StanzaFilter fromRoomGroupchatFilter;

    private final StanzaListener messageListener;

    private PacketCollector messageCollector;

    MultiUserChatLight(XMPPConnection connection, EntityJid room) {
        this.connection = connection;
        this.room = room;

        fromRoomFilter = FromMatchesFilter.create(room);
        fromRoomGroupchatFilter = new AndFilter(fromRoomFilter, MessageTypeFilter.GROUPCHAT);

        messageListener = new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws NotConnectedException {
                Message message = (Message) packet;
                for (MessageListener listener : messageListeners) {
                    listener.processMessage(message);
                }
            }
        };

    }

    /**
     * Returns the JID of the room.
     *
     * @return the MUCLight room JID.
     */
    public EntityJid getRoom() {
        return room;
    }

    /**
     * Sends a message to the chat room.
     *
     * @param text
     *            the text of the message to send.
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public void sendMessage(String text) throws NotConnectedException, InterruptedException {
        Message message = createMessage();
        message.setBody(text);
        connection.sendStanza(message);
    }

    /**
     * Returns a new Chat for sending private messages to a given room occupant.
     * The Chat's occupant address is the room's JID (i.e.
     * roomName@service/nick). The server service will change the 'from' address
     * to the sender's room JID and delivering the message to the intended
     * recipient's full JID.
     *
     * @param occupant
     *            occupant unique room JID (e.g.
     *            'darkcave@macbeth.shakespeare.lit/Paul').
     * @param listener
     *            the listener is a message listener that will handle messages
     *            for the newly created chat.
     * @return new Chat for sending private messages to a given room occupant.
     */
    public Chat createPrivateChat(EntityJid occupant, ChatMessageListener listener) {
        return ChatManager.getInstanceFor(connection).createChat(occupant, listener);
    }

    /**
     * Creates a new Message to send to the chat room.
     *
     * @return a new Message addressed to the chat room.
     */
    public Message createMessage() {
        return new Message(room, Message.Type.groupchat);
    }

    /**
     * Sends a Message to the chat room.
     *
     * @param message
     *            the message.
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public void sendMessage(Message message) throws NotConnectedException, InterruptedException {
        message.setTo(room);
        message.setType(Message.Type.groupchat);
        connection.sendStanza(message);
    }

    /**
     * Polls for and returns the next message.
     *
     * @return the next message if one is immediately available
     */
    public Message pollMessage() {
        return messageCollector.pollResult();
    }

    /**
     * Returns the next available message in the chat. The method call will
     * block (not return) until a message is available.
     *
     * @return the next message.
     * @throws InterruptedException
     */
    public Message nextMessage() throws InterruptedException {
        return messageCollector.nextResult();
    }

    /**
     * Returns the next available message in the chat.
     *
     * @param timeout
     *            the maximum amount of time to wait for the next message.
     * @return the next message, or null if the timeout elapses without a
     *         message becoming available.
     * @throws InterruptedException
     */
    public Message nextMessage(long timeout) throws InterruptedException {
        return messageCollector.nextResult(timeout);
    }

    /**
     * Adds a stanza(/packet) listener that will be notified of any new messages
     * in the group chat. Only "group chat" messages addressed to this group
     * chat will be delivered to the listener.
     *
     * @param listener
     *            a stanza(/packet) listener.
     * @return true if the listener was not already added.
     */
    public boolean addMessageListener(MessageListener listener) {
        return messageListeners.add(listener);
    }

    /**
     * Removes a stanza(/packet) listener that was being notified of any new
     * messages in the MUCLight. Only "group chat" messages addressed to this
     * MUCLight were being delivered to the listener.
     *
     * @param listener
     *            a stanza(/packet) listener.
     * @return true if the listener was removed, otherwise the listener was not
     *         added previously.
     */
    public boolean removeMessageListener(MessageListener listener) {
        return messageListeners.remove(listener);
    }

    /**
     * Remove the connection callbacks used by this MUC Light from the
     * connection.
     */
    private void removeConnectionCallbacks() {
        connection.removeSyncStanzaListener(messageListener);
        if (messageCollector != null) {
            messageCollector.cancel();
            messageCollector = null;
        }
    }

    @Override
    public String toString() {
        return "MUC Light: " + room + "(" + connection.getUser() + ")";
    }

    /**
     * Create new MUCLight.
     * 
     * @param roomName
     * @param subject
     * @param customConfigs
     * @param occupants
     * @return true if it was successfully created
     * @throws NotConnectedException
     * @throws XmppStringprepException
     * @throws InterruptedException
     */
    public boolean create(String roomName, String subject, HashMap<String, String> customConfigs, List<Jid> occupants)
            throws NotConnectedException, XmppStringprepException, InterruptedException {
        MUCLightCreateIQ createMUCLightIQ = new MUCLightCreateIQ(room, roomName, occupants);

        connection.addSyncStanzaListener(messageListener, fromRoomGroupchatFilter);
        messageCollector = connection.createPacketCollector(fromRoomGroupchatFilter);

        StanzaFilter responseFilter = new AndFilter(FromMatchesFilter.createBare(room.asDomainBareJid()),
                new StanzaTypeFilter(IQ.class));

        try {
            IQ responseIq = connection.createPacketCollectorAndSend(responseFilter, createMUCLightIQ)
                    .nextResultOrThrow();
            return responseIq.getType().equals(IQ.Type.result);
        } catch (NoResponseException | XMPPErrorException e) {
            removeConnectionCallbacks();
            return false;
        } catch (InterruptedException e) {
            removeConnectionCallbacks();
            return false;
        }
    }

    /**
     * Create new MUCLight.
     * 
     * @param roomName
     * @param occupants
     * @return true if it was successfully created
     * @throws NotConnectedException
     * @throws XmppStringprepException
     * @throws InterruptedException
     */
    public boolean create(String roomName, List<Jid> occupants)
            throws NotConnectedException, XmppStringprepException, InterruptedException {
        return create(roomName, null, null, occupants);
    }

    /**
     * Leave the MUCLight.
     * 
     * @return true if it was successfully left.
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NoResponseException
     * @throws XMPPErrorException
     */
    public boolean leave() throws NotConnectedException, InterruptedException, NoResponseException, XMPPErrorException {
        HashMap<Jid, MUCLightAffiliation> affiliations = new HashMap<>();
        affiliations.put(connection.getUser(), MUCLightAffiliation.none);

        MUCLightChangeAffiliationsIQ changeAffiliationsIQ = new MUCLightChangeAffiliationsIQ(room, affiliations);
        StanzaFilter responseFilter = new AndFilter(FromMatchesFilter.createBare(room.asDomainBareJid()),
                new StanzaTypeFilter(IQ.class));
        IQ responseIq = connection.createPacketCollectorAndSend(responseFilter, changeAffiliationsIQ)
                .nextResultOrThrow();
        boolean roomLeft = responseIq.getType().equals(IQ.Type.result);

        if (roomLeft) {
            removeConnectionCallbacks();
        }

        return roomLeft;
    }

    /**
     * Get the MUC Light info.
     * 
     * @param version
     * @return the room info
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public MUCLightRoomInfo getFullInfo(String version)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightGetInfoIQ mucLightGetInfoIQ = new MUCLightGetInfoIQ(room, version);
        StanzaFilter responseFilter = new IQReplyFilter(mucLightGetInfoIQ, connection);

        IQ responseIq = connection.createPacketCollectorAndSend(responseFilter, mucLightGetInfoIQ).nextResultOrThrow();
        MUCLightInfoIQ mucLightInfoResponseIQ = (MUCLightInfoIQ) responseIq;

        return new MUCLightRoomInfo(mucLightInfoResponseIQ.getVersion(), room,
                mucLightInfoResponseIQ.getConfiguration(), mucLightInfoResponseIQ.getOccupants());
    }

    /**
     * Get the MUC Light info.
     * 
     * @return the room info
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public MUCLightRoomInfo getFullInfo()
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getFullInfo(null);
    }

    /**
     * Get the MUC Light configuration.
     * 
     * @param version
     * @return the room configuration
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public MUCLightRoomConfiguration getConfiguration(String version)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightGetConfigsIQ mucLightGetConfigsIQ = new MUCLightGetConfigsIQ(room, version);
        StanzaFilter responseFilter = new IQReplyFilter(mucLightGetConfigsIQ, connection);

        IQ responseIq = connection.createPacketCollectorAndSend(responseFilter, mucLightGetConfigsIQ)
                .nextResultOrThrow();
        MUCLightConfigurationIQ mucLightConfigurationIQ = (MUCLightConfigurationIQ) responseIq;

        return mucLightConfigurationIQ.getConfiguration();
    }

    /**
     * Get the MUC Light configuration.
     * 
     * @return the room configuration
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public MUCLightRoomConfiguration getConfiguration()
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getConfiguration(null);
    }

    /**
     * Get the MUC Light affiliations.
     * 
     * @param version
     * @return the room affiliations
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public HashMap<Jid, MUCLightAffiliation> getAffiliations(String version)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightGetAffiliationsIQ mucLightGetAffiliationsIQ = new MUCLightGetAffiliationsIQ(room, version);
        StanzaFilter responseFilter = new IQReplyFilter(mucLightGetAffiliationsIQ, connection);

        IQ responseIq = connection.createPacketCollectorAndSend(responseFilter, mucLightGetAffiliationsIQ)
                .nextResultOrThrow();
        MUCLightAffiliationsIQ mucLightAffiliationsIQ = (MUCLightAffiliationsIQ) responseIq;

        return mucLightAffiliationsIQ.getAffiliations();
    }

    /**
     * Get the MUC Light affiliations.
     * 
     * @return the room affiliations
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public HashMap<Jid, MUCLightAffiliation> getAffiliations()
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return getAffiliations(null);
    }

    /**
     * Change the MUC Light affiliations.
     * 
     * @param affiliations
     * @return true if it was successfully done.
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public boolean changeAffiliations(HashMap<Jid, MUCLightAffiliation> affiliations)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightChangeAffiliationsIQ changeAffiliationsIQ = new MUCLightChangeAffiliationsIQ(room, affiliations);
        StanzaFilter responseFilter = new IQReplyFilter(changeAffiliationsIQ, connection);
        IQ responseIq = connection.createPacketCollectorAndSend(responseFilter, changeAffiliationsIQ)
                .nextResultOrThrow();
        return responseIq.getType().equals(IQ.Type.result);
    }

    /**
     * Destroy the MUC Light. Only will work if it is requested by the owner.
     * 
     * @return true if it was successfully destroyed.
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public boolean destroy()
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightDestroyIQ mucLightDestroyIQ = new MUCLightDestroyIQ(room);
        StanzaFilter responseFilter = new IQReplyFilter(mucLightDestroyIQ, connection);
        IQ responseIq = connection.createPacketCollectorAndSend(responseFilter, mucLightDestroyIQ).nextResultOrThrow();
        boolean roomDestroyed = responseIq.getType().equals(IQ.Type.result);

        if (roomDestroyed) {
            removeConnectionCallbacks();
        }

        return roomDestroyed;
    }

    /**
     * Change the subject of the MUC Light.
     * 
     * @param subject
     * @return true if the subject was successfully changed
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public boolean changeSubject(String subject)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightSetConfigsIQ mucLightSetConfigIQ = new MUCLightSetConfigsIQ(room, null, subject, null);
        StanzaFilter responseFilter = new IQReplyFilter(mucLightSetConfigIQ, connection);
        IQ responseIq = connection.createPacketCollectorAndSend(responseFilter, mucLightSetConfigIQ)
                .nextResultOrThrow();
        return responseIq.getType().equals(IQ.Type.result);
    }

    /**
     * Change the name of the room.
     * 
     * @param roomName
     * @return true if the room name was successfully changed
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public boolean changeRoomName(String roomName)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightSetConfigsIQ mucLightSetConfigIQ = new MUCLightSetConfigsIQ(room, roomName, null);
        StanzaFilter responseFilter = new IQReplyFilter(mucLightSetConfigIQ, connection);
        IQ responseIq = connection.createPacketCollectorAndSend(responseFilter, mucLightSetConfigIQ)
                .nextResultOrThrow();
        return responseIq.getType().equals(IQ.Type.result);
    }

    /**
     * Set the room configurations.
     * 
     * @param customConfigs
     * @return true if the configurations were successfully set
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public boolean setRoomConfigs(HashMap<String, String> customConfigs)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return setRoomConfigs(null, customConfigs);
    }

    /**
     * Set the room configurations.
     * 
     * @param roomName
     * @param customConfigs
     * @return true if the configurations were successfully set
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     */
    public boolean setRoomConfigs(String roomName, HashMap<String, String> customConfigs)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MUCLightSetConfigsIQ mucLightSetConfigIQ = new MUCLightSetConfigsIQ(room, roomName, customConfigs);
        StanzaFilter responseFilter = new IQReplyFilter(mucLightSetConfigIQ, connection);
        IQ responseIq = connection.createPacketCollectorAndSend(responseFilter, mucLightSetConfigIQ)
                .nextResultOrThrow();
        return responseIq.getType().equals(IQ.Type.result);
    }

}
