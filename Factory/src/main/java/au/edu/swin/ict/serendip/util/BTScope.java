package au.edu.swin.ict.serendip.util;


import au.edu.swin.ict.road.xml.bindings.CollaborationUnitType;
import au.edu.swin.ict.road.xml.bindings.ContractType;

/**
 * Defines the scope of a Behavior Term
 *
 * @author Malinda Kapuruge
 */
public class BTScope {
    private CollaborationUnitType btType = null;
    private ContractType definedInContractType = null;
    private ContractType applicableContractType = null;

    /**
     * @param btType                 the behavior term type
     * @param definedInContractType  the SR that the @bt is defined in
     * @param applicableContractType the SR that the @bt is referenced
     */
    public BTScope(CollaborationUnitType btType, ContractType definedInContractType,
                   ContractType applicableContractType) {
        super();
        this.btType = btType;
        this.definedInContractType = definedInContractType;
        this.applicableContractType = applicableContractType;
    }

    public CollaborationUnitType getBtType() {
        return btType;
    }

    public void setBtType(CollaborationUnitType btType) {
        this.btType = btType;
    }

    public ContractType getDefinedInContractType() {
        return definedInContractType;
    }

    public void setDefinedInContractType(ContractType definedInSrType) {
        this.definedInContractType = definedInSrType;
    }

    public ContractType getApplicableContractType() {
        return applicableContractType;
    }

    public void setApplicableContractType(ContractType applicableSrType) {
        this.applicableContractType = applicableSrType;
    }

}
