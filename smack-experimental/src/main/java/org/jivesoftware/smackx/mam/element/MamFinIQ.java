/**
 *
 * Copyright © 2016 Fernando Ramirez
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

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.mam.element.MamElements.MamFinExtension;
import org.jivesoftware.smackx.rsm.packet.RSMSet;

/**
 * MAM fin IQ extension class.
 *
 */
public class MamFinIQ extends IQ {

    /**
     * fin element.
     */
    public static final String ELEMENT = "fin";

    /**
     * the IQ NAMESPACE.
     */
    public static final String NAMESPACE = MamElements.NAMESPACE;

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
     * the query id.
     */
    private final String queryId;

    /**
     * MamFinIQ constructor.
     * 
     * @param mamFinExtension
     */
    public MamFinIQ(MamFinExtension mamFinExtension) {
        super(ELEMENT, NAMESPACE);
        if (mamFinExtension.getRSMSet() == null) {
            throw new IllegalArgumentException("rsmSet must not be null");
        }
        this.rsmSet = mamFinExtension.getRSMSet();
        this.complete = mamFinExtension.isComplete();
        this.stable = mamFinExtension.isStable();
        this.queryId = mamFinExtension.getQueryId();
        this.addExtension(mamFinExtension);
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

    /**
     * Get query id.
     * 
     * @return the query id
     */
    public final String getQueryId() {
        return queryId;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return xml;
    }

    public static MamFinIQ from(IQ iq) {
        return (MamFinIQ) iq;
    }

}
