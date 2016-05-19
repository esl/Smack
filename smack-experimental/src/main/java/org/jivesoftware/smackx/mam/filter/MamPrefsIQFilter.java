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
package org.jivesoftware.smackx.mam.filter;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.mam.element.MamPrefsIQ;
import org.jivesoftware.smackx.mam.element.MamElements.AbstractMamExtension;
import org.jivesoftware.smackx.mam.element.MamElements.MamPrefsExtension;

public class MamPrefsIQFilter extends AbstractMamIQExtensionFilter {

    public MamPrefsIQFilter(MamPrefsIQ mamPrefIQ) {
        super();
    }

    @Override
    protected AbstractMamExtension getMamExtension(IQ iq) {
        return MamPrefsExtension.from(iq);
    }

}
