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
package org.jivesoftware.smackx.mam.packet;

import java.util.List;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.rsm.packet.RSMSet;

/**
 * MAM packet class.
 * 
 * @see <a href="http://xmpp.org/extensions/xep-0313.html">XEP-0313: Message
 *      Archive Management</a>
 *
 */
public class MamPacket {

    public static final String NAMESPACE = "urn:xmpp:mam:1";

    public static abstract class AbstractMamExtension implements ExtensionElement {
        public String queryId;

        protected AbstractMamExtension(String queryId) {
            this.queryId = queryId;
        }

        protected AbstractMamExtension() {
        }

        public final String getQueryId() {
            return queryId;
        }

        public final String getNamespace() {
            return NAMESPACE;
        }

    }

    public static class MamFinExtension extends AbstractMamExtension {

        public static final String ELEMENT = "fin";

        private final RSMSet rsmSet;
        private final boolean complete;
        private final boolean stable;

        public MamFinExtension(String queryId, RSMSet rsmSet, boolean complete, boolean stable) {
            super(queryId);
            if (rsmSet == null) {
                throw new IllegalArgumentException("rsmSet must not be null");
            }
            this.rsmSet = rsmSet;
            this.complete = complete;
            this.stable = stable;
        }

        public RSMSet getRSMSet() {
            return rsmSet;
        }

        public boolean isComplete() {
            return complete;
        }

        public boolean isStable() {
            return stable;
        }

        @Override
        public String getElementName() {
            return ELEMENT;
        }

        @Override
        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement(this);
            xml.optAttribute("queryid", queryId);
            xml.optBooleanAttribute("complete", complete);
            xml.optBooleanAttribute("stable", stable);
            if (rsmSet == null) {
                xml.closeEmptyElement();
            } else {
                xml.rightAngleBracket();
                xml.element(rsmSet);
                xml.closeElement(this);
            }
            return xml;
        }

        public static MamFinExtension from(Message message) {
            return message.getExtension(ELEMENT, NAMESPACE);
        }
    }

    public static class MamResultExtension extends AbstractMamExtension {

        public static final String ELEMENT = "result";

        private final String id;
        private final Forwarded forwarded;

        public MamResultExtension(String queryId, String id, Forwarded forwarded) {
            super(queryId);
            if (StringUtils.isNotEmpty(id)) {
                throw new IllegalArgumentException("id must not be null or empty");
            }
            if (forwarded == null) {
                throw new IllegalArgumentException("forwarded must no be null");
            }
            this.id = id;
            this.forwarded = forwarded;
        }

        public String getId() {
            return id;
        }

        public Forwarded getForwarded() {
            return forwarded;
        }

        @Override
        public String getElementName() {
            return ELEMENT;
        }

        @Override
        public CharSequence toXML() {
            // TODO Auto-generated method stub
            return null;
        }

        public static MamResultExtension from(Message message) {
            return message.getExtension(ELEMENT, NAMESPACE);
        }

    }

    public static class MamPrefsExtension extends AbstractMamExtension {

        public static final String ELEMENT = "prefs";

        private final String defaultField;
        private final List<String> alwaysJids;
        private final List<String> neverJids;

        public MamPrefsExtension(String defaultField, List<String> alwaysJids, List<String> neverJids) {
            super();
            this.defaultField = defaultField;
            this.alwaysJids = alwaysJids;
            this.neverJids = neverJids;
        }

        public String getDefault() {
            return defaultField;
        }

        @Override
        public String getElementName() {
            return ELEMENT;
        }

        @Override
        public CharSequence toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement(this);
            xml.optAttribute("default", defaultField);
            xml.rightAngleBracket();

            AlwaysElement alwaysElement = new AlwaysElement(alwaysJids);
            xml.element(alwaysElement);

            NeverElement neverElement = new NeverElement(neverJids);
            xml.element(neverElement);

            xml.closeElement(this);
            return xml;
        }

        public static MamPrefsExtension from(Message message) {
            return message.getExtension(ELEMENT, NAMESPACE);
        }

        public static class AlwaysElement implements Element {

            private List<String> alwaysJids;

            private AlwaysElement(List<String> alwaysJids) {
                this.alwaysJids = alwaysJids;
            }

            @Override
            public CharSequence toXML() {
                XmlStringBuilder xml = new XmlStringBuilder();
                xml.halfOpenElement("always");

                for (String jid : alwaysJids) {
                    xml.element("jid", jid);
                }

                xml.closeElement("always");
                return xml;
            }
        }

        public static class NeverElement implements Element {

            private List<String> neverJids;

            private NeverElement(List<String> neverJids) {
                this.neverJids = neverJids;
            }

            @Override
            public CharSequence toXML() {
                XmlStringBuilder xml = new XmlStringBuilder();
                xml.halfOpenElement("never");

                for (String jid : neverJids) {
                    xml.element("jid", jid);
                }

                xml.closeElement("never");
                return xml;
            }
        }

    }

}
