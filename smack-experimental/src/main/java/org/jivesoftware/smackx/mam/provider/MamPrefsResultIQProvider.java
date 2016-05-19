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
import org.jivesoftware.smackx.mam.element.MamPrefsResultIQ;
import org.jivesoftware.smackx.mam.element.MamElements.MamPrefsExtension;
import org.xmlpull.v1.XmlPullParser;

public class MamPrefsResultIQProvider extends IQProvider<MamPrefsResultIQ> {

    @Override
    public MamPrefsResultIQ parse(XmlPullParser parser, int initialDepth) throws Exception {
        MamPrefsExtension mamPrefsExtension = new MamPrefsResultProvider().parse(parser);
        return new MamPrefsResultIQ(mamPrefsExtension);
    }

}
