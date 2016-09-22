package au.edu.swin.ict.serendip.epc.test;

import au.edu.swin.ict.serendip.epc.EPMLBehavior;

import java.io.IOException;

public class TestEPMLWrite {
    public static void main(String[] args) {
        try {
            EPMLBehavior.createEmptyBehaviors("sample/xml/epml/", new String[]{
                    "a", "b", "c"});
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
