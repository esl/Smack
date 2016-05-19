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
package org.jivesoftware.smackx.mam.provider;

import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.mam.element.MamElements.MamFinExtension;
import org.jivesoftware.smackx.mam.element.MamFinIQ;
import org.xmlpull.v1.XmlPullParser;

/**
 * MAM Fin IQ Provider class.
 * 
 * @see <a href="http://xmpp.org/extensions/xep-0313.html">XEP-0313: Message
 *      Archive Management</a>
 *
 */
public class MamFinIQProvider extends IQProvider<MamFinIQ> {

    @Override
    public MamFinIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        MamFinExtension mamFinExtension = new MamFinExtensionProvider().parse(parser);
        return new MamFinIQ(mamFinExtension);
    }

}
