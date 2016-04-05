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
package org.jivesoftware.smackx.mam.element;

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
public class MamElements {

    public static final String NAMESPACE = "urn:xmpp:mam:1";

    /**
     * Abstract MAM extension class.
     *
     */
    public static abstract class AbstractMamExtension implements ExtensionElement {
        public String queryId;

        protected AbstractMamExtension(String queryId) {
            this.queryId = queryId;
        }

        protected AbstractMamExtension() {
        }

        /**
         * Get query id.
         * 
         * @return the query id
         */
        public final String getQueryId() {
            return queryId;
        }

        public final String getNamespace() {
            return NAMESPACE;
        }

    }

    /**
     * MAM fin extension class.
     *
     */
    public static class MamFinExtension extends AbstractMamExtension {

        /**
         * fin element.
         */
        public static final String ELEMENT = "fin";

        /**
         * RSM set.
         */
        private final RSMSet rsmSet;

        /**
         * if is complete.
         */
        private final boolean complete;

        /**
         * if is stable.
         */
        private final boolean stable;

        /**
         * MAM fin extension constructor.
         * 
         * @param queryId
         * @param rsmSet
         * @param complete
         * @param stable
         */
        public MamFinExtension(String queryId, RSMSet rsmSet, boolean complete, boolean stable) {
            super(queryId);
            if (rsmSet == null) {
                throw new IllegalArgumentException("rsmSet must not be null");
            }
            this.rsmSet = rsmSet;
            this.complete = complete;
            this.stable = stable;
        }

        /**
         * Get RSM set.
         * 
         * @return the RSM set
         */
        public RSMSet getRSMSet() {
            return rsmSet;
        }

        /**
         * Return if it is complete.
         * 
         * @return true if it is complete
         */
        public boolean isComplete() {
            return complete;
        }

        /**
         * Return if it is stable.
         * 
         * @return true if it is stable
         */
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

    /**
     * MAM result extension class.
     *
     */
    public static class MamResultExtension extends AbstractMamExtension {

        /**
         * result element.
         */
        public static final String ELEMENT = "result";

        /**
         * id of the result.
         */
        private final String id;

        /**
         * the forwarded element.
         */
        private final Forwarded forwarded;

        /**
         * MAM result extension constructor.
         * 
         * @param queryId
         * @param id
         * @param forwarded
         */
        public MamResultExtension(String queryId, String id, Forwarded forwarded) {
            super(queryId);
            if (StringUtils.isEmpty(id)) {
                throw new IllegalArgumentException("id must not be null or empty");
            }
            if (forwarded == null) {
                throw new IllegalArgumentException("forwarded must no be null");
            }
            this.id = id;
            this.forwarded = forwarded;
        }

        /**
         * Get the id.
         * 
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * Get the forwarded element.
         * 
         * @return the forwarded element
         */
        public Forwarded getForwarded() {
            return forwarded;
        }

        @Override
        public String getElementName() {
            return ELEMENT;
        }

        @Override
        public CharSequence toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement(this);
            xml.optAttribute("queryId", getQueryId());
            xml.optAttribute("id", getId());
            xml.rightAngleBracket();

            xml.element(getForwarded());

            xml.closeElement(this);
            return xml;
        }

        public static MamResultExtension from(Message message) {
            return message.getExtension(ELEMENT, NAMESPACE);
        }

    }

    /**
     * MAM preferences extension class.
     *
     */
    public static class MamPrefsExtension extends AbstractMamExtension {

        /**
         * preferences element.
         */
        public static final String ELEMENT = "prefs";

        /**
         * the 'default' attribute.
         */
        private final String defaultField;

        /**
         * the list of JIDs inside 'always' element.
         */
        private final List<String> alwaysJids;

        /**
         * the list of JIDs inside 'never' element.
         */
        private final List<String> neverJids;

        /**
         * MAM preferences extension class.
         * 
         * @param defaultField
         * @param alwaysJids
         * @param neverJids
         */
        public MamPrefsExtension(String defaultField, List<String> alwaysJids, List<String> neverJids) {
            super();
            this.defaultField = defaultField;
            this.alwaysJids = alwaysJids;
            this.neverJids = neverJids;
        }

        /**
         * Get the 'default' attribute value.
         * 
         * @return the 'default' attribute value
         */
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

        /**
         * Always element class.
         *
         */
        public static class AlwaysElement implements Element {

            /**
             * list of JIDs.
             */
            private List<String> alwaysJids;

            /**
             * Always element constructor.
             * 
             * @param alwaysJids
             */
            AlwaysElement(List<String> alwaysJids) {
                this.alwaysJids = alwaysJids;
            }

            @Override
            public CharSequence toXML() {
                XmlStringBuilder xml = new XmlStringBuilder();
                xml.openElement("always");

                for (String jid : alwaysJids) {
                    xml.element("jid", jid);
                }

                xml.closeElement("always");
                return xml;
            }
        }

        /**
         * Never element class.
         *
         */
        public static class NeverElement implements Element {

            /**
             * list of JIDs
             */
            private List<String> neverJids;

            /**
             * Never element constructor.
             * 
             * @param neverJids
             */
            public NeverElement(List<String> neverJids) {
                this.neverJids = neverJids;
            }

            @Override
            public CharSequence toXML() {
                XmlStringBuilder xml = new XmlStringBuilder();
                xml.openElement("never");

                for (String jid : neverJids) {
                    xml.element("jid", jid);
                }

                xml.closeElement("never");
                return xml;
            }
        }

    }

}
