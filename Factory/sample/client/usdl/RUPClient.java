package usdl;

import org.apache.log4j.Logger;
import usdl.Usdl_rupStub.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RUPClient {
    private static Logger log = Logger.getLogger(RUPClient.class.getName());
    private String epr = null;
    private Usdl_rupStub stub = null;
    private List<String> userList = new ArrayList<String>();

    public RUPClient(String epr) throws RemoteException {
        this.epr = epr;
        this.stub = new Usdl_rupStub(epr);
        this.stub._getServiceClient().getOptions().setSoapVersionURI(org.apache.axiom.soap.SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        this.stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(400000);
        //Add sample users
        this.addSampleUsers();
    }


    /**
     * Add few sample users
     *
     * @throws RemoteException
     */
    public void addSampleUsers() throws RemoteException {
        this.userList.add("spuser_1");
        this.userList.add("spuser_2");
        this.userList.add("sc2user_1");
        this.userList.add("sc2user_2");
        this.userList.add("sc2user_3");
        this.userList.add(USDLUtil.anonUserName);//and another single anon user

        Facts fs = new Facts();
        for (String uid : this.userList) {
            Fact f = this.createUserFact(uid, "false");
            fs.addFact(f);
        }
        this.stub.updateRegisteredUsersFacts(fs);
    }

    public List<String> getSampleUsers() {
        //return this.userList;
        List<String> myUserVect = new ArrayList<String>();
        try {
            Facts facts = this.stub.getAllRegisteredUsersFacts();
            Fact[] factArr = facts.getFact();
            for (Fact f : factArr) {
                myUserVect.add(f.getIdentifier().getIdentifierValue());
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return myUserVect;
    }

    public void printAllUsers() throws RemoteException {
        Facts facts = this.stub.getAllRegisteredUsersFacts();
        Fact[] factArr = facts.getFact();
        for (Fact f : factArr) {
            log.debug(f.getIdentifier().getIdentifierKey() + " = " + f.getIdentifier().getIdentifierValue());
        }
    }

    private Fact createUserFact(String userId, String isBlocked) {
        Fact f = new Fact();
        f.setName("RegisteredUsers");
        f.setSource("External");
        FactIdentifier fi = new FactIdentifier();
        fi.setIdentifierKey("UserId");
        fi.setIdentifierValue(userId);
        f.setIdentifier(fi);
        FactAttributes fAttribs = new FactAttributes();
        FactAttribute fAttrib = new FactAttribute();
        fAttrib.setAttributeKey("Block");
        fAttrib.setAttributeValue(isBlocked);
        fAttribs.addAttribute(fAttrib);
        f.setAttributes(fAttribs);

        return f;
    }

    public void blockUser(String userId) throws RemoteException {
        if (USDLUtil.TESTING) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            USDLUtil.isSp1Blocked = true;
        }


        Facts fs = new Facts();
        Fact f = this.createUserFact(userId, "true");//true=blocked

        fs.addFact(f);
        this.stub.updateRegisteredUsersFacts(fs);//Is this correct? Do I need to replace?
    }

    public void unBlockUser(String userId) throws RemoteException {
        if (USDLUtil.TESTING) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            USDLUtil.isSp1Blocked = false;
        }
        Facts fs = new Facts();
        Fact f = this.createUserFact(userId, "false");//true=blocked

        fs.addFact(f);
        this.stub.updateRegisteredUsersFacts(fs);//Is this correct? Do I need to replace?
    }

    public static void main(String[] args) throws RemoteException {
        String epr = "http://localhost:7070/axis2/services/usdl_rup";
        RUPClient rClient = new RUPClient(epr);

        log.debug("Testing user addition");
        rClient.printAllUsers();

    }
}
