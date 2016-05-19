/**
 *
 * Copyright © 2016 Florian Schmaus and Fernando Ramirez
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
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jivesoftware.smackx.mam.element.MamPrefIQ;
import org.jivesoftware.smackx.mam.element.MamQueryIQ;
import org.jivesoftware.smackx.mam.element.MamElements.MamFinExtension;
import org.jivesoftware.smackx.mam.element.MamElements.MamPrefsExtension;
import org.jivesoftware.smackx.mam.element.MamElements.MamResultExtension;
import org.jivesoftware.smackx.mam.element.MamFinIQ;
import org.jivesoftware.smackx.mam.filter.MamIQFinFilter;
import org.jivesoftware.smackx.mam.filter.MamMessageResultFilter;
import org.jivesoftware.smackx.mam.filter.MamPrefsResultFilter;
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

    /**
     * Get the singleton instance of MamManager.
     * 
     * @param connection
     * @return the instance of MamManager
     */
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
        ServiceDiscoveryManager.getInstanceFor(connection).addFeature(MamElements.NAMESPACE);
    }

    /**
     * Query archive with a maximum amount of results.
     * 
     * @param max
     * @return the MAM query result
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamQueryResult queryArchive(Integer max) throws NoResponseException, XMPPErrorException,
            NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(max, null, null, null, null);
    }

    /**
     * Query archive with a JID (only messages from/to the JID).
     * 
     * @param withJid
     * @return the MAM query result
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamQueryResult queryArchive(String withJid) throws NoResponseException, XMPPErrorException,
            NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, null, null, withJid, null);
    }

    /**
     * Query archive filtering by start and/or end date. If start == null, the
     * value of 'start' will be equal to the date/time of the earliest message
     * stored in the archive. If end == null, the value of 'end' will be equal
     * to the date/time of the most recent message stored in the archive.
     * 
     * @param start
     * @param end
     * @return the MAM query result
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamQueryResult queryArchive(Date start, Date end) throws NoResponseException, XMPPErrorException,
            NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, start, end, null, null);
    }

    /**
     * Query Archive adding filters with additional fields.
     * 
     * @param additionalFields
     * @return the MAM query result
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamQueryResult queryArchive(List<AdditionalField> additionalFields) throws NoResponseException,
            XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, null, null, null, additionalFields);
    }

    /**
     * Query archive filtering by start date. The value of 'end' will be equal
     * to the date/time of the most recent message stored in the archive.
     * 
     * @param start
     * @return the MAM query result
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamQueryResult queryArchiveWithStartDate(Date start) throws NoResponseException, XMPPErrorException,
            NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, start, null, null, null);
    }

    /**
     * Query archive filtering by end date. The value of 'start' will be equal
     * to the date/time of the earliest message stored in the archive.
     * 
     * @param end
     * @return the MAM query result
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamQueryResult queryArchiveWithEndDate(Date end) throws NoResponseException, XMPPErrorException,
            NotConnectedException, InterruptedException, NotLoggedInException {
        return queryArchive(null, null, end, null, null);
    }

    /**
     * Query archive applying filters: max count, start date, end date, from/to
     * JID and with additional fields.
     * 
     * @param max
     * @param start
     * @param end
     * @param withJid
     * @param additionalFields
     * @return the MAM query result
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamQueryResult queryArchive(Integer max, Date start, Date end, String withJid,
            List<AdditionalField> additionalFields) throws NoResponseException, XMPPErrorException,
            NotConnectedException, InterruptedException, NotLoggedInException {
        DataForm dataForm = null;
        String queryId = UUID.randomUUID().toString();

        if (start != null || end != null || withJid != null || additionalFields != null) {
            dataForm = getNewMamForm();
            addStart(start, dataForm);
            addEnd(end, dataForm);
            addWithJid(withJid, dataForm);
            addAdditionalFields(additionalFields, dataForm);
        }

        MamQueryIQ mamQueryIQ = prepareMamQueryIQSet(dataForm, queryId);

        addResultsLimit(max, mamQueryIQ);

        return queryArchive(mamQueryIQ, 0);
    }

    private void addAdditionalFields(List<AdditionalField> additionalFields, DataForm dataForm) {
        if (additionalFields != null) {
            for (AdditionalField additionalField : additionalFields) {
                FormField formField = new FormField(additionalField.variable);
                formField.setType(additionalField.type);
                formField.addValue(additionalField.value);
                dataForm.addField(formField);
            }
        }
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

    /**
     * Returns a page of the archive.
     * 
     * @param dataForm
     * @param rsmSet
     * @return the MAM query result
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamQueryResult page(DataForm dataForm, RSMSet rsmSet) throws NoResponseException, XMPPErrorException,
            NotConnectedException, InterruptedException, NotLoggedInException {
        MamQueryIQ mamQueryIQ = new MamQueryIQ(UUID.randomUUID().toString(), dataForm);
        preparePageQuery(mamQueryIQ, rsmSet);
        return queryArchive(mamQueryIQ, 0);
    }

    /**
     * Returns the next page of the archive.
     * 
     * @param mamQueryResult
     *            is the previous query result
     * @param count
     *            is the amount of messages that a page contains
     * @return the MAM query result
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamQueryResult pageNext(MamQueryResult mamQueryResult, int count) throws NoResponseException,
            XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        RSMSet previousResultRsmSet = mamQueryResult.mamFin.getRSMSet();
        RSMSet requestRsmSet = new RSMSet(count, previousResultRsmSet.getLast(), RSMSet.PageDirection.after);
        return page(mamQueryResult.form, requestRsmSet);
    }

    private void preparePageQuery(MamQueryIQ mamQueryIQ, RSMSet rsmSet) {
        mamQueryIQ.setType(IQ.Type.set);
        mamQueryIQ.addExtension(rsmSet);
    }

    /**
     * Get the form fields supported by the server.
     * 
     * @return the MAM query result.
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamQueryResult retrieveFormFields() throws NoResponseException, XMPPErrorException, NotConnectedException,
            InterruptedException, NotLoggedInException {
        String queryId = UUID.randomUUID().toString();
        MamQueryIQ mamQueryIQ = prepareMamQueryIQGet(queryId);
        return queryArchive(mamQueryIQ, 0);
    }

    private MamQueryIQ prepareMamQueryIQSet(DataForm dataForm, String queryId) {
        MamQueryIQ mamQueryIQ = new MamQueryIQ(queryId, dataForm);
        mamQueryIQ.setType(IQ.Type.set);
        return mamQueryIQ;
    }

    private MamQueryIQ prepareMamQueryIQGet(String queryId) {
        MamQueryIQ mamQueryIQ = new MamQueryIQ(queryId, null);
        mamQueryIQ.setType(IQ.Type.get);
        return mamQueryIQ;
    }

    private MamQueryResult queryArchive(MamQueryIQ mamQueryIq, long extraTimeout) throws NoResponseException,
            XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        if (extraTimeout < 0) {
            throw new IllegalArgumentException("extra timeout must be zero or positive");
        }

        final XMPPConnection connection = getAuthenticatedConnectionOrThrow();
        MamFinExtension mamFinExtension = null;

        PacketCollector finIQCollector = connection.createPacketCollector(new MamIQFinFilter(mamQueryIq));

        PacketCollector.Configuration resultCollectorConfiguration = PacketCollector.newConfiguration()
                .setStanzaFilter(new MamMessageResultFilter(mamQueryIq)).setCollectorToReset(finIQCollector);

        PacketCollector resultCollector = connection.createPacketCollector(resultCollectorConfiguration);

        try {
            connection.createPacketCollectorAndSend(mamQueryIq).nextResultOrThrow();
            IQ iq = finIQCollector.nextResultOrThrow(connection.getPacketReplyTimeout() + extraTimeout);
            MamFinIQ mamFinIQ = MamFinIQ.from(iq);
            mamFinExtension = MamFinExtension.from(mamFinIQ);
        } finally {
            resultCollector.cancel();
            finIQCollector.cancel();
        }

        List<Forwarded> forwardedMessages = new ArrayList<>(resultCollector.getCollectedCount());

        for (Message resultMessage = resultCollector
                .pollResult(); resultMessage != null; resultMessage = resultCollector.pollResult()) {
            // XEP-313 § 4.2
            MamResultExtension mamResultExtension = MamResultExtension.from(resultMessage);
            forwardedMessages.add(mamResultExtension.getForwarded());
        }

        return new MamQueryResult(forwardedMessages, mamFinExtension, DataForm.from(mamQueryIq));
    }

    /**
     * MAM query result class.
     *
     */
    public final static class MamQueryResult {
        public final List<Forwarded> forwardedMessages;
        public final MamFinExtension mamFin;
        private final DataForm form;

        private MamQueryResult(List<Forwarded> forwardedMessages, MamFinExtension mamFin, DataForm form) {
            this.forwardedMessages = forwardedMessages;
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
        return ServiceDiscoveryManager.getInstanceFor(connection()).serverSupportsFeature(MamElements.NAMESPACE);
    }

    private DataForm getNewMamForm() {
        FormField field = new FormField(FormField.FORM_TYPE);
        field.setType(FormField.Type.hidden);
        field.addValue(MamElements.NAMESPACE);
        DataForm form = new DataForm(DataForm.Type.submit);
        form.addField(field);
        return form;
    }

    /**
     * Additional field class.
     *
     */
    public final static class AdditionalField {
        public final FormField.Type type;
        public final String variable;
        public final String value;

        public AdditionalField(FormField.Type type, String variable, String value) {
            this.type = type;
            this.variable = variable;
            this.value = value;
        }
    }

    /**
     * Get the preferences stored in the server.
     * 
     * @return the MAM preferences result
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamPrefsResult retrieveArchivingPreferences() throws NoResponseException, XMPPErrorException,
            NotConnectedException, InterruptedException, NotLoggedInException {
        MamPrefIQ mamPrefIQ = prepareRetrievePreferencesStanza();
        return queryMamPrefs(mamPrefIQ, 0);
    }

    private MamPrefIQ prepareRetrievePreferencesStanza() {
        MamPrefIQ mamPrefIQ = new MamPrefIQ(false, null, null);
        mamPrefIQ.setType(IQ.Type.get);
        return mamPrefIQ;
    }

    /**
     * Update the preferences in the server.
     * 
     * @param alwaysJids
     *            is the list of JIDs that should always have messages to/from
     *            archived in the user's store
     * @param neverJids
     *            is the list of JIDs that should never have messages to/from
     *            archived in the user's store
     * @return the MAM preferences result
     * @throws NoResponseException
     * @throws XMPPErrorException
     * @throws NotConnectedException
     * @throws InterruptedException
     * @throws NotLoggedInException
     */
    public MamPrefsResult updateArchivingPreferences(List<String> alwaysJids, List<String> neverJids)
            throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException,
            NotLoggedInException {
        MamPrefIQ mamPrefIQ = prepareUpdatePreferencesStanza(alwaysJids, neverJids);
        return queryMamPrefs(mamPrefIQ, 0);
    }

    private MamPrefIQ prepareUpdatePreferencesStanza(List<String> alwaysJids, List<String> neverJids) {
        MamPrefIQ mamPrefIQ = new MamPrefIQ(true, alwaysJids, neverJids);
        mamPrefIQ.setType(IQ.Type.set);
        return mamPrefIQ;
    }

    /**
     * MAM preferences result class.
     *
     */
    public final static class MamPrefsResult {
        public final MamPrefsExtension mamPrefs;
        public final DataForm form;

        private MamPrefsResult(MamPrefsExtension mamPrefs, DataForm form) {
            this.mamPrefs = mamPrefs;
            this.form = form;
        }
    }

    private MamPrefsResult queryMamPrefs(MamPrefIQ mamPrefIQ, long extraTimeout) throws NoResponseException,
            XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
        if (extraTimeout < 0) {
            throw new IllegalArgumentException("extra timeout must be zero or positive");
        }

        final XMPPConnection connection = getAuthenticatedConnectionOrThrow();
        MamPrefsExtension mamPrefsExtension = null;

        PacketCollector prefsIQCollector = connection.createPacketCollector(new MamPrefsResultFilter(mamPrefIQ));

        try {
            connection.createPacketCollectorAndSend(mamPrefIQ).nextResultOrThrow();
            IQ mamPrefsIQ = prefsIQCollector.nextResultOrThrow(connection.getPacketReplyTimeout() + extraTimeout);
            mamPrefsExtension = MamPrefsExtension.from(mamPrefsIQ);
        } finally {
            prefsIQCollector.cancel();
        }

        return new MamPrefsResult(mamPrefsExtension, DataForm.from(mamPrefIQ));
    }

}
