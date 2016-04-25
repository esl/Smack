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
package org.jivesoftware.smackx.mam;

import static org.mockito.Mockito.mock;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.mam.packet.MamPacket;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.junit.Before;

public class MamTest {

    protected XMPPConnection connection;
    protected String queryId;
    protected MamManager mamManager;

    @Before
    public void setup() {
        // mock connection
        connection = mock(XMPPConnection.class);

        // test query id
        queryId = "testid";

        // MamManager instance
        mamManager = MamManager.getInstanceFor(connection);
    }

    protected DataForm getNewMamForm() {
        FormField field = new FormField(FormField.FORM_TYPE);
        field.setType(FormField.Type.hidden);
        field.addValue(MamPacket.NAMESPACE);
        DataForm form = new DataForm(DataForm.Type.submit);
        form.addField(field);
        return form;
    }

}
