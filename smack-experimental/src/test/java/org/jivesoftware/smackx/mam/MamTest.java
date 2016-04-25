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
