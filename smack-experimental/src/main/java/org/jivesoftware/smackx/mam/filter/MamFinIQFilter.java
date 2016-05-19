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
package org.jivesoftware.smackx.mam.filter;

import org.jivesoftware.smack.filter.FlexibleStanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.mam.element.MamQueryIQ;
import org.jivesoftware.smackx.mam.element.MamElements.MamFinExtension;
import org.jivesoftware.smackx.mam.element.MamFinIQ;

public class MamFinIQFilter extends FlexibleStanzaTypeFilter<IQ> {

    public MamFinIQFilter(MamQueryIQ mamQueryIQ) {
        super(IQ.class);
        queryId = mamQueryIQ.getQueryId();
    }

    private String queryId;

    @Override
    protected boolean acceptSpecific(IQ iq) {
        MamFinExtension mamExtension = getMamExtension(iq);

        if (mamExtension == null) {
            return false;
        }

        String resultQueryId = mamExtension.getQueryId();

        if (queryId == null && resultQueryId == null) {
            return true;
        } else if (queryId != null && queryId.equals(resultQueryId)) {
            return true;
        }

        return false;
    }

    private MamFinExtension getMamExtension(IQ iq) {
        MamFinIQ mamFinIQ;
        try {
            mamFinIQ = (MamFinIQ) iq;
        } catch (ClassCastException e) {
            return null;
        }
        return MamFinExtension.from(mamFinIQ);
    }

}
