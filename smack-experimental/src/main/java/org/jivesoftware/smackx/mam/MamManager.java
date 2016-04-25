/**
 *
 * Copyright © 2015 Florian Schmaus and Fernando Ramirez
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
package org.jivesoftware.smackx.mam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.mam.filter.MamMessageFinFilter;
import org.jivesoftware.smackx.mam.filter.MamMessageResultFilter;
import org.jivesoftware.smackx.mam.packet.MamPacket;
import org.jivesoftware.smackx.mam.packet.MamPacket.MamFinExtension;
import org.jivesoftware.smackx.mam.packet.MamQueryIQ;
import org.jivesoftware.smackx.mam.packet.MamPacket.MamResultExtension;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.util.XmppDateTime;

/**
 * Message Archive Management Manager class.
 * 
 * @see <a href="http://xmpp.org/extensions/xep-0313.html">XEP-0313: Message
 *      Archive Management</a>
 *
 */
public final class MamManager extends Manager {

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(XMPPConnection connection) {
                getInstanceFor(connection);
            }
        });
    }

    private static final Map<XMPPConnection, MamManager> INSTANCES = new WeakHashMap<>();

    public static synchronized MamManager getInstanceFor(XMPPConnection connection) {
        MamManager mamManager = INSTANCES.get(connection);

        if (mamManager == null) {
            mamManager = new MamManager(connection);
            INSTANCES.put(connection, mamManager);
        }

        return mamManager;
    }

    private MamManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(MamPacket.NAMESPACE);
    }

    public MamQueryResult queryArchive(Integer max)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return queryArchive(max, null, null, null);
    }

    public MamQueryResult queryArchive(String withJid)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return queryArchive(null, null, null, withJid);
    }

    public MamQueryResult queryArchive(Date start, Date end)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return queryArchive(null, start, end, null);
    }

    public MamQueryResult queryArchiveWithStartDate(Date start)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return queryArchive(null, start, null, null);
    }

    public MamQueryResult queryArchiveWithEndDate(Date end)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return queryArchive(null, null, end, null);
    }

    public MamQueryResult queryArchive(Integer max, Date start, Date end, String withJid)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        DataForm dataForm = null;
        String queryId = UUID.randomUUID().toString();

        if (start != null || end != null || withJid != null) {
            dataForm = getNewMamForm();
            addStart(start, dataForm);
            addEnd(end, dataForm);
            addWithJid(withJid, dataForm);
        }

        MamQueryIQ mamQueryIQ = new MamQueryIQ(queryId, dataForm);
        mamQueryIQ.setType(IQ.Type.set);

        addResultsLimit(max, mamQueryIQ);

        return queryArchive(mamQueryIQ, 0);
    }

    private void addResultsLimit(Integer max, MamQueryIQ mamQueryIQ) {
        if (max != null) {
            RSMSet rsmSet = new RSMSet(max);
            mamQueryIQ.addExtension(rsmSet);
        }
    }

    private void addWithJid(String withJid, DataForm dataForm) {
        if (withJid != null) {
            FormField formField = new FormField("with");
            formField.addValue(withJid);
            dataForm.addField(formField);
        }
    }

    private void addEnd(Date end, DataForm dataForm) {
        if (end != null) {
            FormField formField = new FormField("end");
            formField.addValue(XmppDateTime.formatXEP0082Date(end));
            dataForm.addField(formField);
        }
    }

    private void addStart(Date start, DataForm dataForm) {
        if (start != null) {
            FormField formField = new FormField("start");
            formField.addValue(XmppDateTime.formatXEP0082Date(start));
            dataForm.addField(formField);
        }
    }

    public MamQueryResult pageNext(MamQueryResult mamQueryResult, int count)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        RSMSet previousResultRsmSet = mamQueryResult.mamFin.getRSMSet();
        RSMSet requestRsmSet = new RSMSet(count, previousResultRsmSet.getLast(), RSMSet.PageDirection.after);
        return page(mamQueryResult, requestRsmSet);
    }

    public MamQueryResult page(MamQueryResult mamQueryResult, RSMSet rsmSet)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        MamQueryIQ mamQueryIQ = new MamQueryIQ(UUID.randomUUID().toString(), mamQueryResult.form);
        mamQueryIQ.setType(IQ.Type.set);
        mamQueryIQ.addExtension(rsmSet);
        return queryArchive(mamQueryIQ, 0);
    }

    private MamQueryResult queryArchive(MamQueryIQ mamQueryIq, long extraTimeout)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        if (extraTimeout < 0) {
            throw new IllegalArgumentException("extra timeout must be zero or positive");
        }

        final XMPPConnection connection = connection();
        MamFinExtension mamFinExtension = null;

        PacketCollector finMessageCollector = connection.createPacketCollector(new MamMessageFinFilter(mamQueryIq));

        PacketCollector.Configuration resultCollectorConfiguration = PacketCollector.newConfiguration()
                .setStanzaFilter(new MamMessageResultFilter(mamQueryIq)).setCollectorToReset(finMessageCollector);

        PacketCollector resultCollector = connection.createPacketCollector(resultCollectorConfiguration);

        try {
            connection.createPacketCollectorAndSend(mamQueryIq).nextResultOrThrow();
            Message mamFinMessage = finMessageCollector
                    .nextResultOrThrow(connection.getPacketReplyTimeout() + extraTimeout);
            mamFinExtension = MamFinExtension.from(mamFinMessage);
        } finally {
            resultCollector.cancel();
            finMessageCollector.cancel();
        }

        List<Forwarded> messages = new ArrayList<>(resultCollector.getCollectedCount());

        for (Message resultMessage = resultCollector.pollResult(); resultMessage != null;) {
            // XEP-313 § 4.2
            MamResultExtension mamResultExtension = MamResultExtension.from(resultMessage);
            messages.add(mamResultExtension.getForwarded());
        }

        return new MamQueryResult(messages, mamFinExtension, DataForm.from(mamQueryIq));
    }

    public final static class MamQueryResult {
        public final List<Forwarded> messages;
        public final MamFinExtension mamFin;
        private final DataForm form;

        private MamQueryResult(List<Forwarded> messages, MamFinExtension mamFin, DataForm form) {
            this.messages = messages;
            this.mamFin = mamFin;
            this.form = form;
        }
    }

    /**
     * Returns true if Message Archive Management is supported by the server.
     * 
     * @return true if Message ARchive Management is supported by the server.
     * @throws NotConnectedException
     * @throws XMPPErrorException
     * @throws NoResponseException
     * @throws InterruptedException
     */
    public boolean isSupportedByServer()
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).serverSupportsFeature(MamPacket.NAMESPACE);
    }

    private static DataForm getNewMamForm() {
        FormField field = new FormField(FormField.FORM_TYPE);
        field.setType(FormField.Type.hidden);
        field.addValue(MamPacket.NAMESPACE);
        DataForm form = new DataForm(DataForm.Type.submit);
        form.addField(field);
        return form;
    }

}
