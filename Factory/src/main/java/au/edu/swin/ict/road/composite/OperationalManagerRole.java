package au.edu.swin.ict.road.composite;

import au.edu.swin.ict.road.common.*;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.player.PlayerBinding;
import au.edu.swin.ict.road.composite.regulation.passthrough.PassthroughKnowledgebase;
import au.edu.swin.ict.road.composite.regulation.routing.RoutingKnowledgebase;
import au.edu.swin.ict.road.composite.regulation.sglobal.GlobalKnowledgebase;
import au.edu.swin.ict.road.composite.regulation.synchronization.SynchronizationKnowledgebase;
import au.edu.swin.ict.road.xml.bindings.*;
import au.edu.swin.ict.serendip.core.SerendipException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO documentation
 */
public class OperationalManagerRole implements IOperationalManagerRole {

    private static Logger log = Logger.getLogger(OperationalManagerRole.class.getName());
    private Composite composite;
    private ServiceNetwork smcBinding;
    private LinkedBlockingQueue<MessageWrapper> outQueue;
    private AtomicInteger buNumber = new AtomicInteger(1);

    public OperationalManagerRole(Composite composite) {
        this.composite = composite;
        this.smcBinding = composite.getSmcBinding();
        this.outQueue = new LinkedBlockingQueue<MessageWrapper>();
    }

    private String getUniqueRuIdSuffix() {
        return String.valueOf(buNumber.getAndIncrement());
    }

    public OperationalMgtOpResult addRegulationMechanism(String rmId1, String className1, String jarFileLocation1) {
        String rmId = rmId1.trim();
        String className = className1.trim();
        String jarFileLocation = jarFileLocation1.trim();
        log.info("Operational Manager : add new regulation mechanism : " + rmId);
        if (composite.containsRegulationMechanism(rmId)) {
            return new OperationalMgtOpResult(false, "A regulation mechanism is alreay there with Id " + rmId);
        }
        ClassPathHack classPathHack = new ClassPathHack();
        try {
            classPathHack.addFile(jarFileLocation);
            RegulationMechanism mechanism = new RegulationMechanism(rmId);
            mechanism.setClassName(className);
            mechanism.setJarFileLocation(jarFileLocation);
            composite.addRegulationMechanism(mechanism);
        } catch (IOException e) {
            return new OperationalMgtOpResult(false, e.getMessage());
        }
        return new OperationalMgtOpResult(true, "A regulation mechanism has been added from " + jarFileLocation);
    }

    @Override
    public OperationalMgtOpResult removeRegulationMechanism(String rmId1) {
        String rmId = rmId1.trim();
        log.info("Operational Manager : remove regulation mechanism : " + rmId);
        composite.removeRegulationMechanism(rmId);
        return new OperationalMgtOpResult(true, "A regulation mechanism has been removed. Id : " + rmId);
    }

    @Override
    public OperationalMgtOpResult updateRegulationMechanism(String rmId1, String property1, String value1) {
        String value = value1.trim();
        String property = property1.trim();
        String rmId = rmId1.trim();
        log.info("Operational Manager : update regulation mechanism : " + rmId);
        if (!composite.containsRegulationMechanism(rmId)) {
            return new OperationalMgtOpResult(false, "There is no regulation mechanism  with Id " + rmId);
        }
        RegulationMechanism regulationMechanism = composite.getRegulationMechanism(rmId);
        if ("state".equals(property)) {
            regulationMechanism.getMgtState().setState(value);
        } else if ("class".equals(property)) {
            regulationMechanism.setClassName(value);
        } else if ("jarFile".equals(property)) {
            ClassPathHack classPathHack = new ClassPathHack();
            try {
                classPathHack.addFile(value);
                regulationMechanism.setJarFileLocation(value);
            } catch (IOException e) {
                return new OperationalMgtOpResult(false, e.getMessage());
            }
        }
        return new OperationalMgtOpResult(true, "The regulation mechanism " +
                rmId + "has been updated. property : " + property + " value : " + value);
    }

    @Override
    public OperationalMgtOpResult addSynchronizationRule(String place1, String ruleContent1) {
        log.info("Operational Manager : add sync regulation rule @ " + place1);
        String place = place1.trim();
        String ruleContent = ruleContent1.trim();
        composite.getRole(place).getSynRules().addRule(ruleContent);
        return new OperationalMgtOpResult(true, "The sync regulation rule @" + place1 + "has been added.");
    }

    @Override
    public OperationalMgtOpResult removeSynchronizationRule(String place1, String ruleId1) {
        log.info("Operational Manager : remove sync regulation rule : " + ruleId1);
        String place = place1.trim();
        String ruleId = ruleId1.trim();
        composite.getRole(place).getSynRules().removeRule(ruleId);
        return new OperationalMgtOpResult(true, "The sync regulation rule" + ruleId + "has been removed.");
    }

    @Override
    public OperationalMgtOpResult updateSynchronizationRule(String place1, String ruleId1, String property1, String value1) {
        log.info("Operational Manager : update regulation rule : " + ruleId1);
        String place = place1.trim();
        String ruleId = ruleId1.trim();
        String value = value1.trim();
        String property = property1.trim();
        SynchronizationKnowledgebase knowledgebase = composite.getRole(place).getSynRules();
        if ("state".equals(property)) {
            knowledgebase.getRegulationRule(ruleId).getMgtState().setState(value);
        } else if ("replace".equals(property)) {
            String[] ruleIds = ruleId.split(",");
            for (String rule : ruleIds) {
                knowledgebase.removeRegulationRule(rule.trim());
            }
            knowledgebase.addRule(value);
        }
        return new OperationalMgtOpResult(true, "The sync regulation rule" + ruleId + "has been updated.");
    }

    @Override
    public OperationalMgtOpResult addRoutingRule(String place1, String ruleContent1) {
        log.info("Operational Manager : add routing regulation rule @ " + place1);
        String place = place1.trim();
        String ruleContent = ruleContent1.trim();
        composite.getRole(place).getRoutingRules().addRule(ruleContent);
        return new OperationalMgtOpResult(true, "The routing regulation rule @" + place1 + "has been added.");
    }

    @Override
    public OperationalMgtOpResult removeRoutingRule(String place1, String ruleId1) {
        log.info("Operational Manager : remove routing regulation rule : " + ruleId1);
        String place = place1.trim();
        String ruleId = ruleId1.trim();
        composite.getRole(place).getRoutingRules().removeRule(ruleId);
        return new OperationalMgtOpResult(true, "The routing regulation rule" + ruleId + "has been removed.");
    }

    @Override
    public OperationalMgtOpResult updateRoutingRule(String place1, String ruleId1, String property1, String value1) {
        log.info("Operational Manager : update routing rule : " + ruleId1);
        String place = place1.trim();
        String ruleId = ruleId1.trim();
        String value = value1.trim();
        String property = property1.trim();
        RoutingKnowledgebase knowledgebase = composite.getRole(place).getRoutingRules();
        if ("state".equals(property)) {
            knowledgebase.getRegulationRule(ruleId).getMgtState().setState(value);
        } else if ("replace".equals(property)) {
            String[] ruleIds = ruleId.split(",");
            for (String rule : ruleIds) {
                knowledgebase.removeRegulationRule(rule.trim());
            }
            knowledgebase.addRule(value);
        }
        return new OperationalMgtOpResult(true, "The routing regulation rule" + ruleId + "has been updated.");
    }

    @Override
    public OperationalMgtOpResult addPassthroughRule(String place1, String ruleContent1) {
        log.info("Operational Manager : add passthrough regulation rule @ " + place1);
        String place = place1.trim();
        String ruleContent = ruleContent1.trim();
        composite.getContract(place).getContractRules().addRule(ruleContent);
        return new OperationalMgtOpResult(true, "The passthrough regulation rule @" + place1 + "has been added.");
    }

    @Override
    public OperationalMgtOpResult removePassthroughRule(String place1, String ruleId1) {
        log.info("Operational Manager : remove passthrough regulation rule : " + ruleId1);
        String place = place1.trim();
        String ruleId = ruleId1.trim();
        composite.getContract(place).getContractRules().removeRule(ruleId);
        return new OperationalMgtOpResult(true, "The passthrough regulation rule" + ruleId + "has been removed.");
    }

    @Override
    public OperationalMgtOpResult updatePassthroughRule(String place1, String ruleId1, String property1, String value1) {
        log.info("Operational Manager : update passthrough rule : " + ruleId1);
        String place = place1.trim();
        String ruleId = ruleId1.trim();
        String value = value1.trim();
        String property = property1.trim();
        PassthroughKnowledgebase knowledgebase = composite.getContract(place).getContractRules();
        if ("state".equals(property)) {
            knowledgebase.getRegulationRule(ruleId).getMgtState().setState(value);
        } else if ("replace".equals(property)) {
            String[] ruleIds = ruleId.split(",");
            for (String rule : ruleIds) {
                knowledgebase.removeRegulationRule(rule.trim());
            }
            knowledgebase.addRule(value);
        }
        return new OperationalMgtOpResult(true, "The passthrough regulation rule" + ruleId + "has been updated.");
    }

    @Override
    public OperationalMgtOpResult addGlobalRule(String ruleContent1) {
        log.info("Operational Manager : add global regulation rule " + ruleContent1);
        String ruleContent = ruleContent1.trim();
        composite.getGlobalKnowledgebase().addRule(ruleContent);
        return new OperationalMgtOpResult(true, "The global regulation rule has been added.");
    }

    @Override
    public OperationalMgtOpResult removeGlobalRule(String ruleId1) {
        log.info("Operational Manager : remove global regulation rule " + ruleId1);
        composite.getGlobalKnowledgebase().removeRegulationRule(ruleId1.trim());
        return new OperationalMgtOpResult(true, "The global regulation rule has been removed");
    }

    @Override
    public OperationalMgtOpResult updateGlobalRule(String ruleId1, String property1, String value1) {
        log.info("Operational Manager : update  global rule : " + ruleId1);
        String ruleId = ruleId1.trim();
        String value = value1.trim();
        String property = property1.trim();
        GlobalKnowledgebase knowledgebase = composite.getGlobalKnowledgebase();
        if ("state".equals(property)) {
            knowledgebase.getRegulationRule(ruleId).getMgtState().setState(value);
        } else if ("replace".equals(property)) {
            String[] ruleIds = ruleId.split(",");
            for (String rule : ruleIds) {
                knowledgebase.removeRegulationRule(rule.trim());
            }
            knowledgebase.addRule(value);
        }
        return new OperationalMgtOpResult(true, "The global regulation rule" + ruleId + "has been updated.");
    }

    @Override
    public OperationalMgtOpResult addRegulationUnit(String ruId1) {
        log.info("Operational Manager : add regulation unit : " + ruId1);
        String ruId = ruId1.trim();
        CollaborationUnitType collaborationUnitType =
                new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId);
        collaborationUnitType.setRegulationDesign(new RegulationDesignType());
        composite.addRegulationUnitState(new RegulationUnitState(ruId, RegulationUnitState.STATE_PASSIVE));
        return new OperationalMgtOpResult(true, "The regulation unit " + ruId1 + "has been added.");
    }

    @Override
    public OperationalMgtOpResult removeRegulationUnit(String ruId1) {
        log.info("Operational Manager : remove regulation unit : " + ruId1);
        String ruId = ruId1.trim();
        CollaborationUnitType tobeRemoved =
                new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId);
        if (tobeRemoved != null) {
            tobeRemoved.setRegulationDesign(null);
            composite.removeRegulationUnitState(ruId);
            composite.undeployRegulationDesignOfCollaboration(ruId, tobeRemoved.getRegulationDesign()); // update reg tables
        }
        return new OperationalMgtOpResult(true, "The regulation unit " + ruId1 + "has been removed.");
    }

    @Override
    public OperationalMgtOpResult updateRegulationUnit(String ruId1, String property1, String value1) {
        log.info("Operational Manager : remove regulation unit : " + ruId1);
        String ruId = ruId1.trim();
        String value = value1.trim();
        String property = property1.trim();
        CollaborationUnitType toBeUpdated = new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId);
        if (toBeUpdated != null) {
            if ("state".equals(property)) {
                toBeUpdated.setState(value);
                composite.getRegulationUnitState(ruId).setState(value);
            }
        }
        return new OperationalMgtOpResult(true, "The regulation unit " + ruId1 + "has been removed.");
    }

    @Override
    public OperationalMgtOpResult addInterProcessRegulationUnit(String ruId1) {
        log.info("Operational Manager : addInterProcessRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        ServiceNetwork smcCur = composite.getSmcBinding();
        InterProcessRegulationUnitType unitType = new InterProcessRegulationUnitType();
        unitType.setId(ruId);
        smcCur.getInterProcessRegulationUnits().getInterProcessRegulationUnit().add(unitType);
        composite.addRegulationUnitState(new RegulationUnitState(ruId, RegulationUnitState.STATE_PASSIVE));
        return new OperationalMgtOpResult(true, "The InterProcessRegulationUnit " + ruId1 + "has been added.");
    }

    public OperationalMgtOpResult addInterCollaborationRegulationUnit(String ruId1) {
        log.info("Operational Manager : addInterCollaborationRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        ServiceNetwork smcCur = composite.getSmcBinding();
        InterCollaborationRegulationUnitType unitType = new InterCollaborationRegulationUnitType();
        unitType.setId(ruId);
        smcCur.getInterCollaborationRegulationUnits().getInterCollaborationRegulationUnit().add(unitType);
        composite.addRegulationUnitState(new RegulationUnitState(ruId, RegulationUnitState.STATE_PASSIVE));
        return new OperationalMgtOpResult(true, "The InterCollaborationRegulationUnit " + ruId1 + "has been added.");
    }

    public OperationalMgtOpResult addInterVSNRegulation(String ruId1) {
        log.info("Operational Manager : addInterVSNRegulation: " + ruId1);
        String ruId = ruId1.trim();
        ServiceNetwork smcCur = composite.getSmcBinding();
        InterVSNRegulationType unitType = new InterVSNRegulationType();
        smcCur.setInterVSNRegulation(unitType);
        composite.addRegulationUnitState(new RegulationUnitState(ruId, RegulationUnitState.STATE_PASSIVE));
        return new OperationalMgtOpResult(true, "The InterVSNRegulation " + ruId1 + "has been added.");
    }

    @Override
    public OperationalMgtOpResult addSynchronizationRulesToRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add  sync rules to the regulation unit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        CollaborationUnitType unitType = new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (unitType != null) {
            RegulationDesignType designType = unitType.getRegulationDesign();
            if (designType != null) {
                RegulationRuleRef ruleRef = designType.getSynchronization();
                if (ruleRef == null) {
                    ruleRef = new RegulationRuleRef();
                    designType.setSynchronization(ruleRef);
                }
                String[] ruleIdList = ruleIds.split(",");
                for (String rule : ruleIdList) {
                    String[] pars = rule.trim().split(":");
                    RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                    ruleIdType.setId(pars[0].trim());
                    ruleIdType.setPlace(pars[1].trim());
                    ruleRef.getRuleRef().add(ruleIdType);
                    added.add(ruleIdType);
                }
            }
        }
        composite.deploySyncRulesOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The sync rules to the regulation unit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addSynchronizationRulesToInterProcessRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add  sync rules to the InterProcessRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterProcessRegulationUnitType unitType = new SMCDataExtractor(smcBinding).getInterProcessRegulationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (unitType != null) {
            RegulationRuleRef ruleRef = unitType.getSynchronization();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                unitType.setSynchronization(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }

        }
        composite.deploySyncRulesOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The sync rules to the InterProcessRegulationUnit" + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addSynchronizationRulesToInterCollaborationRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add  sync rules to the InterCollaborationRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterCollaborationRegulationUnitType unitType = new SMCDataExtractor(smcBinding).getInterCollaborationRegulationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (unitType != null) {
            RegulationRuleRef ruleRef = unitType.getSynchronization();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                unitType.setSynchronization(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }

        }
        composite.deploySyncRulesOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The sync rules to the InterCollaborationRegulationUnit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addSynchronizationRulesToInterVSNRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add  sync rules to the InterVSNRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterVSNRegulationType unitType = smcBinding.getInterVSNRegulation();
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (unitType != null) {
            RegulationRuleRef ruleRef = unitType.getSynchronization();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                unitType.setSynchronization(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }

        }
        composite.deploySyncRulesOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The sync rules to the InterVSNRegulationUnit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult removeSynchronizationRulesFromRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : remove  sync rules from the regulation unit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        CollaborationUnitType unitType = new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId);

        String[] ruleIdList = ruleIds.split(",");
        List<String> ruleIdKeys = new ArrayList<String>();
        for (String rule : ruleIdList) {
            String[] pars = rule.trim().split(":");
            ruleIdKeys.add(pars[0] + "." + pars[1]);
        }
        if (unitType != null) {
            RegulationDesignType designType = unitType.getRegulationDesign();
            if (designType != null) {
                List<RegulationRuleIdType> removed = new ArrayList<RegulationRuleIdType>();
                RegulationRuleRef ruleRef = designType.getSynchronization();
                if (ruleRef != null) {
                    for (RegulationRuleIdType ruleIdType : ruleRef.getRuleRef()) {
                        if (ruleIdKeys.contains(ruleIdType.getId() + "." + ruleIdType.getPlace())) {
                            removed.add(ruleIdType);
                        }
                    }
                }
                designType.getSynchronization().getRuleRef().removeAll(removed);
                composite.undeploySyncRulesOfRegulationUnit(ruId, removed);
            }
        }
        return new OperationalMgtOpResult(true, "The sync rules to the regulation unit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addRoutingRulesToRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add routing rules to the regulation unit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        CollaborationUnitType unitType = new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (unitType != null) {
            RegulationDesignType designType = unitType.getRegulationDesign();
            if (designType != null) {
                RegulationRuleRef ruleRef = designType.getRouting();
                if (ruleRef == null) {
                    ruleRef = new RegulationRuleRef();
                    designType.setRouting(ruleRef);
                }
                String[] ruleIdList = ruleIds.split(",");
                for (String rule : ruleIdList) {
                    String[] pars = rule.trim().split(":");
                    RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                    ruleIdType.setId(pars[0].trim());
                    ruleIdType.setPlace(pars[1].trim());
                    ruleRef.getRuleRef().add(ruleIdType);
                    added.add(ruleIdType);
                }
            }
        }
        composite.deployRoutingRuleOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The routing rules to the regulation unit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addRoutingRulesToInterProcessRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add routing rules to the InterProcessRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterProcessRegulationUnitType unitType = new SMCDataExtractor(smcBinding).getInterProcessRegulationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (unitType != null) {
            RegulationRuleRef ruleRef = unitType.getRouting();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                unitType.setRouting(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }
        }
        composite.deployRoutingRuleOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The routing rules to the InterProcessRegulationUnit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addRoutingRulesToInterCollaborationRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add routing rules to the InterCollaborationRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterCollaborationRegulationUnitType unitType =
                new SMCDataExtractor(smcBinding).getInterCollaborationRegulationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (unitType != null) {
            RegulationRuleRef ruleRef = unitType.getRouting();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                unitType.setRouting(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }
        }
        composite.deployRoutingRuleOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The routing rules to the InterCollaborationRegulationUnit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addRoutingRulesToInterVSNRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add routing rules to the InterVSNRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterVSNRegulationType unitType = smcBinding.getInterVSNRegulation();
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (unitType != null) {
            RegulationRuleRef ruleRef = unitType.getRouting();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                unitType.setRouting(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }
        }
        composite.deployRoutingRuleOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The routing rules to the InterVSNRegulationUnit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult removeRoutingRulesFromRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : remove routing rules from the regulation unit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        CollaborationUnitType unitType = new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId);
        String[] ruleIdList = ruleIds.split(",");
        List<String> ruleIdKeys = new ArrayList<String>();
        for (String rule : ruleIdList) {
            String[] pars = rule.trim().split(":");
            ruleIdKeys.add(pars[0] + "." + pars[1]);
        }
        if (unitType != null) {
            RegulationDesignType designType = unitType.getRegulationDesign();
            if (designType != null) {
                List<RegulationRuleIdType> removed = new ArrayList<RegulationRuleIdType>();
                RegulationRuleRef ruleRef = designType.getRouting();
                if (ruleRef != null) {
                    for (RegulationRuleIdType ruleIdType : ruleRef.getRuleRef()) {
                        if (ruleIdKeys.contains(ruleIdType.getId() + "." + ruleIdType.getPlace())) {
                            removed.add(ruleIdType);
                        }
                    }
                }
                designType.getRouting().getRuleRef().removeAll(removed);
                composite.undeployRoutingRulesOfRegulationUnit(ruId, removed);
            }
        }
        return new OperationalMgtOpResult(true, "The routing rules to the regulation unit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addPassthroughRulesToRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add passthrough rules to the regulation unit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        CollaborationUnitType unitType = new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (unitType != null) {
            RegulationDesignType designType = unitType.getRegulationDesign();
            if (designType != null) {
                RegulationRuleRef ruleRef = designType.getPassthrough();
                if (ruleRef == null) {
                    ruleRef = new RegulationRuleRef();
                    designType.setPassthrough(ruleRef);
                }
                String[] ruleIdList = ruleIds.split(",");
                for (String rule : ruleIdList) {
                    String[] pars = rule.trim().split(":");
                    RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                    ruleIdType.setId(pars[0].trim());
                    ruleIdType.setPlace(pars[1].trim());
                    ruleRef.getRuleRef().add(ruleIdType);
                    added.add(ruleIdType);
                }
            }
        }
        composite.deployPassthroughRulesOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The passthrough rules to the regulation unit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addPassthroughRulesToInterProcessRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add passthrough rules to the InterProcessRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterProcessRegulationUnitType designType = new SMCDataExtractor(smcBinding).getInterProcessRegulationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (designType != null) {
            RegulationRuleRef ruleRef = designType.getPassthrough();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                designType.setPassthrough(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }
        }
        composite.deployPassthroughRulesOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The passthrough rules to the InterProcessRegulationUnit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addPassthroughRulesToInterCollaborationRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add passthrough rules to the InterCollaborationRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterCollaborationRegulationUnitType designType =
                new SMCDataExtractor(smcBinding).getInterCollaborationRegulationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (designType != null) {
            RegulationRuleRef ruleRef = designType.getPassthrough();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                designType.setPassthrough(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }
        }
        composite.deployPassthroughRulesOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The passthrough rules to the InterCollaborationRegulationUnit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addPassthroughRulesToInterVSNRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add passthrough rules to the InterVSNRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterVSNRegulationType designType = smcBinding.getInterVSNRegulation();
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (designType != null) {
            RegulationRuleRef ruleRef = designType.getPassthrough();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                designType.setPassthrough(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }
        }
        composite.deployPassthroughRulesOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The passthrough rules to the InterVSNRegulationUnit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult removePassthroughRulesFromRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : remove passthrough rules from the regulation unit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        CollaborationUnitType unitType = new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId);
        String[] ruleIdList = ruleIds.split(",");
        List<String> ruleIdKeys = new ArrayList<String>();
        for (String rule : ruleIdList) {
            String[] pars = rule.trim().split(":");
            ruleIdKeys.add(pars[0] + "." + pars[1]);
        }
        if (unitType != null) {
            RegulationDesignType designType = unitType.getRegulationDesign();
            if (designType != null) {
                List<RegulationRuleIdType> removed = new ArrayList<RegulationRuleIdType>();
                RegulationRuleRef ruleRef = designType.getPassthrough();
                if (ruleRef != null) {
                    for (RegulationRuleIdType ruleIdType : ruleRef.getRuleRef()) {
                        if (ruleIdKeys.contains(ruleIdType.getId() + "." + ruleIdType.getPlace())) {
                            removed.add(ruleIdType);
                        }
                    }
                }
                designType.getPassthrough().getRuleRef().removeAll(removed);
                composite.undeployPassthroughOfRegulationUnit(ruId, removed);
            }
        }
        return new OperationalMgtOpResult(true, "The passthrough rules to the regulation unit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addGlobalRulesToRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add global rules to the regulation unit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        CollaborationUnitType unitType = new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (unitType != null) {
            RegulationDesignType designType = unitType.getRegulationDesign();
            if (designType != null) {
                RegulationRuleRef ruleRef = designType.getGlobal();
                if (ruleRef == null) {
                    ruleRef = new RegulationRuleRef();
                    designType.setGlobal(ruleRef);
                }
                String[] ruleIdList = ruleIds.split(",");
                for (String rule : ruleIdList) {
                    String[] pars = rule.trim().split(":");
                    RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                    ruleIdType.setId(pars[0].trim());
//                ruleIdType.setPlace(pars[1].trim());
                    ruleRef.getRuleRef().add(ruleIdType);
                    added.add(ruleIdType);
                }
            }
        }
        composite.deployGlobalRuleOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The global rules to the regulation unit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addGlobalRulesToInterProcessRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add global rules to the InterProcessRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterProcessRegulationUnitType designType =
                new SMCDataExtractor(smcBinding).getInterProcessRegulationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (designType != null) {
            RegulationRuleRef ruleRef = designType.getGlobal();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                designType.setGlobal(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
//                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }
        }
        composite.deployGlobalRuleOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The global rules to the InterProcessRegulationUnit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addGlobalRulesToInterCollaborationRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add global rules to the InterCollaborationRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterCollaborationRegulationUnitType designType =
                new SMCDataExtractor(smcBinding).getInterCollaborationRegulationUnitTypeById(ruId);
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (designType != null) {
            RegulationRuleRef ruleRef = designType.getGlobal();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                designType.setGlobal(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
//                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }
        }
        composite.deployGlobalRuleOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The global rules to the InterCollaborationRegulationUnit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addGlobalRulesToInterVSNRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : add global rules to the VSNRegulationUnit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        InterVSNRegulationType designType =
                smcBinding.getInterVSNRegulation();
        List<RegulationRuleIdType> added = new ArrayList<RegulationRuleIdType>();
        if (designType != null) {
            RegulationRuleRef ruleRef = designType.getGlobal();
            if (ruleRef == null) {
                ruleRef = new RegulationRuleRef();
                designType.setGlobal(ruleRef);
            }
            String[] ruleIdList = ruleIds.split(",");
            for (String rule : ruleIdList) {
                String[] pars = rule.trim().split(":");
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(pars[0].trim());
//                ruleIdType.setPlace(pars[1].trim());
                ruleRef.getRuleRef().add(ruleIdType);
                added.add(ruleIdType);
            }
        }
        composite.deployGlobalRuleOfRegulationUnit(ruId, added);
        return new OperationalMgtOpResult(true, "The global rules to the VSNRegulationUnit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult removeGlobalRulesFromRegulationUnit(String ruId1, String ruleIds1) {
        log.info("Operational Manager : remove global rules from the regulation unit : " + ruId1);
        String ruId = ruId1.trim();
        String ruleIds = ruleIds1.trim();
        CollaborationUnitType unitType = new SMCDataExtractor(smcBinding).getCollaborationUnitTypeById(ruId);
        String[] ruleIdList = ruleIds.split(",");
        List<String> ruleIdKeys = new ArrayList<String>();
        for (String rule : ruleIdList) {
            String[] pars = rule.trim().split(":");
            ruleIdKeys.add(pars[0] + "." + pars[1]);
        }
        if (unitType != null) {
            RegulationDesignType designType = unitType.getRegulationDesign();
            if (designType != null) {
                List<RegulationRuleIdType> removed = new ArrayList<RegulationRuleIdType>();
                RegulationRuleRef ruleRef = designType.getGlobal();
                if (ruleRef != null) {
                    for (RegulationRuleIdType ruleIdType : ruleRef.getRuleRef()) {
                        if (ruleIdKeys.contains(ruleIdType.getId() + "." + ruleIdType.getPlace())) {
                            removed.add(ruleIdType);
                        }
                    }
                }
                designType.getGlobal().getRuleRef().removeAll(removed);
                composite.undeployGlobalRulesOfRegulationUnit(ruId, removed);
            }
        }
        return new OperationalMgtOpResult(true, "The global rules to the regulation unit " + ruId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addRegulationUnitsToProcessRegulationPolicy(String vsnId1, String processId1, String ruIds1) {
        log.info("Operational Manager : add regulation units " + ruIds1 + " to the process : " + processId1);
        String vsnId = vsnId1.trim();
        String processId = processId1.trim();
        String ruIds = ruIds1.trim();
        String[] ruIdArray = ruIds.split(",");
        List<String> stringList = new ArrayList<String>(ruIdArray.length);
        for (String s : ruIdArray) {
            stringList.add(s.trim());
        }
        new SMCDataExtractor(smcBinding).getInterCollaborationRegulationUnitsOfProcess(vsnId, processId).addAll(stringList);
        composite.deployProcessRegulationPolicy(vsnId, processId, stringList, ManagementState.STATE_PASSIVE);
        return new OperationalMgtOpResult(true, "The regulation units to the process " + processId + "have been added.");
    }

    @Override
    public OperationalMgtOpResult removeRegulationUnitsFromProcessRegulationPolicy(String vsnId1, String processId1, String ruIds1) {
        log.info("Operational Manager : remove regulation units " + ruIds1 + " from the process : " + processId1);
        String vsnId = vsnId1.trim();
        String processId = processId1.trim();
        String ruIds = ruIds1.trim();
        String[] ruIdArray = ruIds.split(",");
        List<String> stringList = new ArrayList<String>(ruIdArray.length);
        for (String s : ruIdArray) {
            stringList.add(s.trim());
        }
        new SMCDataExtractor(smcBinding).getInterCollaborationRegulationUnitsOfProcess(vsnId, processId).removeAll(stringList);
        composite.undeployProcessRegulationPolicy(vsnId, processId, stringList);
        return new OperationalMgtOpResult(true, "The regulation units from the process " + processId + "have been removed.");
    }

    @Override
    public OperationalMgtOpResult updateRegulationUnitOfProcessRegulationPolicy(String vsnId1, String processId1,
                                                                                String ruId1, String property1, String value1) {
        log.info("Operational Manager : update regulation units " + ruId1 + " of the process : " + processId1 + " property : " + property1 + " value : " + value1);
        String vsnId = vsnId1.trim();
        String processId = processId1.trim();
        String ruId = ruId1.trim();
        String value = value1.trim();
        String property = property1.trim();
        if ("state".equals(property)) {
            if ("active".equals(value)) {
                composite.activateRegulationUnitForProcess(vsnId, processId, ruId);
            } else if ("passive".equals(value)) {
                composite.passivateRegulationUnitForProcess(vsnId, processId, ruId);
            }
        }
        return new OperationalMgtOpResult(true, "The regulation unit of the process " +
                processId + "have been updated. property : " + property + " value : " + value);
    }

    public OperationalMgtOpResult updateRegulationUnitsOfProcessRegulationPolicy(String vsnId1, String processId1,
                                                                                 String ruIds1, String property1, String value1) {
        log.info("Operational Manager : update regulation units " + ruIds1 + " of the process : " + property1 + " value : " + value1);
        String ruIds = ruIds1.trim();
        String[] ruIdArray = ruIds.split(",");

        for (String ruId : ruIdArray) {
            updateRegulationUnitOfProcessRegulationPolicy(vsnId1, processId1, ruId, property1, value1);
        }
        return new OperationalMgtOpResult(true, "The regulation unit of the process " +
                processId1 + "have been updated. property : " + property1 + " value : " + value1);
    }

    @Override
    public OperationalMgtOpResult addRegulationUnitsToVSNRegulationPolicy(String vsnId1, String ruIds1) {
        return addRegulationUnitsToVSNRegulationPolicy(vsnId1, ruIds1, ManagementState.STATE_PASSIVE);
    }

    private OperationalMgtOpResult addRegulationUnitsToVSNRegulationPolicy(String vsnId1, String ruIds1, String state) {
        log.info("Operational Manager : add regulation units " + ruIds1 + " to the process : " + vsnId1);
        String vsnId = vsnId1.trim();
        String ruIds = ruIds1.trim();
        String[] ruIdArray = ruIds.split(",");
        List<String> stringList = new ArrayList<String>(ruIdArray.length);
        for (String s : ruIdArray) {
            stringList.add(s.trim());
        }
        new SMCDataExtractor(smcBinding).getInterProcessRegulationUnitsOfVSN(vsnId).addAll(stringList);
        composite.deployVSNRegulationPolicy(vsnId, stringList, state);
        return new OperationalMgtOpResult(true, "The regulation units to the vsn " + vsnId1 + "have been added.");
    }

    @Override
    public OperationalMgtOpResult addParameterizedRegulationUnitToVSNRegulationPolicy(String vsnId1, String ruId1, String parameters) {
        log.info("Operational Manager : add parameterized regulation unit " + ruId1 + " to the VSN : " + vsnId1);
        String vsnId = vsnId1.trim();
        String ruId = ruId1.trim();
        SMCDataExtractor extractor = new SMCDataExtractor(smcBinding);

        InterProcessRegulationUnitType unitType = null;
        // Remove current regulation unit instance
        for (String s : extractor.getInterProcessRegulationUnitsOfVSN(vsnId)) {
            if (s.startsWith(ruId + "__")) {
                unitType = extractor.getInterProcessRegulationUnitTypeById(s);
                //TODO  passivate and register a listner
                removeRegulationUnitsFromVSNRegulationPolicy(vsnId1, s);
                removeRegulationUnit(s);
                break;
            }
        }
        String suffix = "__" + getUniqueRuIdSuffix();
        String newruId = ruId + suffix;
        addRegulationUnit(newruId);
        InterProcessRegulationUnitType newUnit = extractor.getInterProcessRegulationUnitTypeById(newruId);
        List<RuleParameter> ruleParameters = RuleParameterFactory.createRuleParameters(parameters.trim());
        RegulationRuleRef ruleRef = newUnit.getRouting();
        if (ruleRef == null) {
            ruleRef = new RegulationRuleRef();
            newUnit.setRouting(ruleRef);
        }
        if (unitType != null) {
            RegulationRuleRef routing = unitType.getRouting();
            if (routing != null) {
                List<RegulationRuleIdType> tobeAdded = new ArrayList<RegulationRuleIdType>();
                List<RegulationRuleIdType> ruleIdTypes = routing.getRuleRef();
                for (RegulationRuleIdType idType : ruleIdTypes) {
                    if (isParamerizedRule(ruleParameters, idType.getId())) {
                        removeRoutingRule(idType.getPlace(), idType.getId());
                    } else {
                        tobeAdded.add(idType); // Coping to be normal rule ids;
                    }
                }
                for (RuleParameter ruleParameter : ruleParameters) {
                    String ruleFileInstance = composite.getRulesDir() + ruleParameter.getRuleName() + suffix + ".drl";
                    RuleParameterFactory.instantiateParametrizedRule(ruleFileInstance, ruleParameter, suffix, composite.getRulesDir());
                    RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                    ruleIdType.setId(ruleParameter.getRuleName() + suffix);
                    ruleIdType.setPlace(ruleParameter.getPlace());
                    tobeAdded.add(ruleIdType);
                    addRoutingRule(ruleParameter.getPlace(), ruleParameter.getRuleName() + suffix + ".drl");
                }
                ruleRef.getRuleRef().addAll(tobeAdded);
                composite.deployRoutingRuleOfRegulationUnit(newruId, tobeAdded);
            }
        } else {
            List<RegulationRuleIdType> tobeAdded = new ArrayList<RegulationRuleIdType>();
            for (RuleParameter ruleParameter : ruleParameters) {
                String ruleFileInstance = composite.getRulesDir() + ruleParameter.getRuleName() + suffix + ".drl";
                RuleParameterFactory.instantiateParametrizedRule(ruleFileInstance, ruleParameter, suffix, composite.getRulesDir());
                RegulationRuleIdType ruleIdType = new RegulationRuleIdType();
                ruleIdType.setId(ruleParameter.getRuleName() + suffix);
                ruleIdType.setPlace(ruleParameter.getPlace());
                tobeAdded.add(ruleIdType);
                addRoutingRule(ruleParameter.getPlace(), ruleParameter.getRuleName() + suffix + ".drl");
            }
            ruleRef.getRuleRef().addAll(tobeAdded);
            composite.deployRoutingRuleOfRegulationUnit(newruId, tobeAdded);
        }
        addRegulationUnitsToVSNRegulationPolicy(vsnId, newruId, ManagementState.STATE_ACTIVE);
//        updateRegulationUnitOfProcessRegulationPolicy()
        return new OperationalMgtOpResult(true, "The regulation units to the vsn " + vsnId1 + "have been added.");
    }

    private boolean isParamerizedRule(List<RuleParameter> parameters, String ruleId) {
        for (RuleParameter ruleParameter : parameters) {
            if (ruleId.startsWith(ruleParameter.getRuleName() + "__")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public OperationalMgtOpResult removeRegulationUnitsFromVSNRegulationPolicy(String vsnId1, String ruIds1) {
        log.info("Operational Manager : remove regulation units " + ruIds1 + " from the process : " + vsnId1);
        String vsnId = vsnId1.trim();
        String ruIds = ruIds1.trim();
        SMCDataExtractor extractor = new SMCDataExtractor(smcBinding);
        String[] ruIdArray = ruIds.split(",");
        List<String> stringList = new ArrayList<String>(ruIdArray.length);
        for (String s : ruIdArray) {
            stringList.add(s.trim());
        }
        extractor.getInterProcessRegulationUnitsOfVSN(vsnId).removeAll(stringList);
        composite.undeployVSNRegulationPolicy(vsnId, stringList);
        return new OperationalMgtOpResult(true, "The regulation units from the vsn " + vsnId1 + "have been removed.");
    }

    @Override
    public OperationalMgtOpResult updateRegulationUnitOfVSNRegulationPolicy(String vsnId1, String ruId1, String property1, String value1) {
        log.info("Operational Manager : update regulation units " + ruId1 + " of the VSN : " + vsnId1 + " property : " + property1 + " value : " + value1);
        String vsnId = vsnId1.trim();
        String ruId = ruId1.trim();
        String value = value1.trim();
        String property = property1.trim();
        if ("state".equals(property)) {
            if ("active".equals(value)) {
                composite.activateRegulationUnitForVSN(vsnId, ruId);
            } else if ("passive".equals(value)) {
                composite.passivateRegulationUnitForVSN(vsnId, ruId);
            }
        }
        return new OperationalMgtOpResult(true, "The regulation unit of the vsn " +
                vsnId1 + "have been updated. property : " + property + " value : " + value);
    }

    public OperationalMgtOpResult updateRegulationUnitsOfVSNRegulationPolicy(String vsnId1, String ruIds1, String property1, String value1) {
        log.info("Operational Manager : update regulation units " + ruIds1 + " of the vsn : " + vsnId1 + "  with property" + property1 + " value : " + value1);
        String ruIds = ruIds1.trim();
        String[] ruIdArray = ruIds.split(",");

        for (String ruId : ruIdArray) {
            updateRegulationUnitOfVSNRegulationPolicy(vsnId1, ruId, property1, value1);
        }
        return new OperationalMgtOpResult(true, "The regulation unit of the vsn " +
                value1 + "have been updated. property : " + property1 + " value : " + value1);

    }

    @Override
    public OperationalMgtOpResult addRegulationUnitsToServiceNetworkRegulationPolicy(String ruIds1) {
        log.info("Operational Manager : add regulation units " + ruIds1 + " to the service network ");
//        String ruIds = ruIds1.trim();
        InterVSNRegulationType regPolicy = smcBinding.getInterVSNRegulation();
        if (regPolicy == null) {
            regPolicy = new InterVSNRegulationType();
            smcBinding.setInterVSNRegulation(regPolicy);
        }
//        String[] ruIdArray = ruIds.split(",");
//        List<String> stringList = new ArrayList<String>(ruIdArray.length);
//        for (String s : ruIdArray) {
//            stringList.add(s.trim());
//        }
        composite.deployNetworkLevelPolicies(regPolicy, ManagementState.STATE_PASSIVE);
        return new OperationalMgtOpResult(true, "The regulation units " + ruIds1 + "have been added.");
    }

    @Override
    public OperationalMgtOpResult removeRegulationUnitsFromServiceNetworkRegulationPolicy(String ruIds1) {
        log.info("Operational Manager : remove regulation units " + ruIds1 + " from the service network");
        String ruIds = ruIds1.trim();
        smcBinding.setInterVSNRegulation(null);
        String[] ruIdArray = ruIds.split(",");
        List<String> stringList = new ArrayList<String>(ruIdArray.length);
        for (String s : ruIdArray) {
            stringList.add(s.trim());
        }
        composite.undeployNetworkLevelPolicies(stringList);
        return new OperationalMgtOpResult(true, "The regulation units " + ruIds1 + "have been removed.");
    }

    @Override
    public OperationalMgtOpResult updateRegulationUnitsOfServiceNetworkRegulationPolicy(String ruIds, String property, String value) {
        return null;
    }

    @Override
    public OperationalMgtOpResult updateRegulationUnitOfServiceNetworkRegulationPolicy(String ruIds, String property, String value) {
        return null;
    }

    @Override
    public OperationalMgtOpResult addServiceNetworkEvent(String eventId1, String places, Classifier classifier) {
        log.info("Operational Manager : add an event " + eventId1);
        try {
            EventRecord eventRecord = new EventRecord(eventId1.trim(), classifier, ManagementState.STATE_PASSIVE);
            eventRecord.setPlace(places);
            composite.getSerendipEngine().getEventCloud().addEvent(eventRecord);
        } catch (SerendipException e) {
            return new OperationalMgtOpResult(false, e.getMessage());
        }
        return new OperationalMgtOpResult(true, "The event " + eventId1 + "have been added.");
    }

    @Override
    public OperationalMgtOpResult removeServiceNetworkEvent(String eventId1, Classifier classifier) {
        log.info("Operational Manager : remove an event " + eventId1);
        composite.getSerendipEngine().getEventCloud().expireEvent(eventId1, classifier);
        return new OperationalMgtOpResult(true, "The event " + eventId1 + "have been removed.");
    }

    @Override
    public OperationalMgtOpResult updateServiceNetworkEvent(String eventId1, Classifier classifier, String property1, String value1) {
        log.info("Operational Manager : update an event " + eventId1 + " property : " + property1 + " value : " + value1);
        String eventId = eventId1.trim();
        String value = value1.trim();
        String property = property1.trim();
        EventRecord eventRecord =
                composite.getSerendipEngine().getEventCloud().getEventReocrd(eventId, classifier);
        if ("state".equals(property)) {
            eventRecord.getMgtState().setState(value);
        }
        return new OperationalMgtOpResult(true, "The event " + eventId1 + "have been updated.");
    }

    @Override
    public OperationalMgtOpResult addServiceNetworkStateImplementation(String stateType1, String className1, String jarFileLocation1) {
        String stateType = stateType1.trim();
        String className = className1.trim();
        String jarFileLocation = jarFileLocation1.trim();
        log.info("Operational Manager : add new SN state implementation : " + stateType);
        if (composite.containsSNStateImplementation(stateType)) {
            return new OperationalMgtOpResult(false, "An SN state implementation is already there with the type " + stateType);
        }
        ClassPathHack classPathHack = new ClassPathHack();
        try {
            classPathHack.addFile(jarFileLocation);
            SNStateImplementation snStateImplementation = new SNStateImplementation(stateType);
            snStateImplementation.setClassName(className);
            snStateImplementation.setJarFileLocation(jarFileLocation);
            composite.addSNStateImplementation(snStateImplementation);
        } catch (IOException e) {
            return new OperationalMgtOpResult(false, e.getMessage());
        }
        return new OperationalMgtOpResult(true, "An SN state implementation has been added from " + jarFileLocation);
    }

    @Override
    public OperationalMgtOpResult removeServiceNetworkStateImplementation(String stateType1) {
        String stateType = stateType1.trim();
        log.info("Operational Manager : remove SN state implementation : " + stateType);
        composite.removeSNStateImplementation(stateType);
        return new OperationalMgtOpResult(true, "An SN state implementation has been removed. Id : " + stateType);
    }

    @Override
    public OperationalMgtOpResult updateServiceNetworkStateImplementation(String stateType1, String property1, String value1) {
        String value = value1.trim();
        String property = property1.trim();
        String stateType = stateType1.trim();
        log.info("Operational Manager : update SN state implementation : " + stateType);
        if (!composite.containsSNStateImplementation(stateType)) {
            return new OperationalMgtOpResult(false, "There is no SN state implementation with type " + stateType);
        }
        SNStateImplementation snStateImplementation = composite.getSNStateImplementation(stateType);
        if ("state".equals(property)) {
            snStateImplementation.getMgtState().setState(value);
        } else if ("class".equals(property)) {
            snStateImplementation.setClassName(value);
        } else if ("jarFile".equals(property)) {
            ClassPathHack classPathHack = new ClassPathHack();
            try {
                classPathHack.addFile(value);
                snStateImplementation.setJarFileLocation(value);
            } catch (IOException e) {
                return new OperationalMgtOpResult(false, e.getMessage());
            }
        }
        return new OperationalMgtOpResult(true, "The SN state implementation " +
                stateType + "has been updated. property : " + property + " value : " + value);
    }

    @Override
    public OperationalMgtOpResult addServiceNetworkState(String stateId1, String stateType1, String scope1, Classifier classifier) {
        log.info("Operational Manager : add SN state  : " + stateId1);
        String stateId = stateId1.trim();
        String stateType = stateType1.trim();
        String scope = scope1.trim();
        if (!composite.containsSNStateImplementation(stateType)) {
            return new OperationalMgtOpResult(false, "There is no SN state implementation with type " + stateType);
        }
        SNStateImplementation snStateImplementation = composite.getSNStateImplementation(stateType);
        try {
            Class aClass = Class.forName(snStateImplementation.getClassName());
            Object stateObject = aClass.newInstance();
            StateRecord stateRecord = new StateRecord(stateId, stateObject);
            if ("instance".equals(scope)) {
                composite.getServiceNetworkState().
                        getVsnState(classifier.getVsnId()).getProcessState(classifier.getProcessId()).
                        getProcessInstanceState(classifier.getProcessInsId()).putInCache(stateId, stateRecord);
            } else if ("process".equals(scope)) {
                composite.getServiceNetworkState().getVsnState(classifier.getVsnId()).
                        getProcessState(classifier.getProcessId()).putInCache(stateId, stateRecord);
            } else if ("vsn".equals(scope)) {
                composite.getServiceNetworkState().
                        getVsnState(classifier.getVsnId()).putInCache(stateId, stateRecord);
            } else if ("sn".equals(scope)) {
                composite.getServiceNetworkState().putInCache(stateId, stateRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new OperationalMgtOpResult(false, e.getMessage());
        }
        return new OperationalMgtOpResult(true, "An SN state has been added. Id : " + stateId);
    }

    @Override
    public OperationalMgtOpResult removeServiceNetworkState(String stateId1, String scope1, Classifier classifier) {
        log.info("Operational Manager : remove SN state  : " + stateId1);
        String stateId = stateId1.trim();
        String scope = scope1.trim();
        try {
            if ("instance".equals(scope)) {
                composite.getServiceNetworkState().
                        getVsnState(classifier.getVsnId()).getProcessState(classifier.getProcessId()).
                        getProcessInstanceState(classifier.getProcessInsId()).removeFromCache(stateId);
            } else if ("process".equals(scope)) {
                composite.getServiceNetworkState().getVsnState(classifier.getVsnId()).
                        getProcessState(classifier.getProcessId()).removeFromCache(stateId);
            } else if ("vsn".equals(scope)) {
                composite.getServiceNetworkState().
                        getVsnState(classifier.getVsnId()).removeFromCache(stateId);
            } else if ("sn".equals(scope)) {
                composite.getServiceNetworkState().removeFromCache(stateId);
            }
        } catch (Exception e) {
            return new OperationalMgtOpResult(false, e.getMessage());
        }
        return new OperationalMgtOpResult(true, "An SN state has been removed. Id : " + stateId);
    }

    @Override
    public OperationalMgtOpResult updateServiceNetworkState(String stateId1, String scope1, Classifier classifier, String property1, String value1) {
        log.info("Operational Manager : update SN state  : " + stateId1);
        String value = value1.trim();
        String property = property1.trim();
        String stateId = stateId1.trim();
        String scope = scope1.trim();
        StateRecord stateRecord = null;
        try {
            if ("instance".equals(scope)) {
                stateRecord = composite.getServiceNetworkState().
                        getVsnState(classifier.getVsnId()).getProcessState(classifier.getProcessId()).
                        getProcessInstanceState(classifier.getProcessInsId()).retrieveFromCache(stateId);
            } else if ("process".equals(scope)) {
                stateRecord = composite.getServiceNetworkState().getVsnState(classifier.getVsnId()).
                        getProcessState(classifier.getProcessId()).retrieveFromCache(stateId);
            } else if ("vsn".equals(scope)) {
                stateRecord = composite.getServiceNetworkState().
                        getVsnState(classifier.getVsnId()).retrieveFromCache(stateId);
            } else if ("sn".equals(scope)) {
                stateRecord = composite.getServiceNetworkState().retrieveFromCache(stateId);
            }
            if (stateRecord != null) {
                if ("state".equals(property)) {
                    stateRecord.getMgtState().setState(value);
                } else {
                    Object stateObject = stateRecord.getStateInstance();
                    Class aClass = Class.forName(stateObject.getClass().getName());
                    //TODO make upper case first letter
                    Method m = aClass.getMethod("set" + property, String.class);
                    m.invoke(stateObject, value);
                }
            }
        } catch (Exception e) {
            return new OperationalMgtOpResult(false, e.getMessage());
        }
        return new OperationalMgtOpResult(true, "An SN state has been updated. Id : " + stateId);
    }

    @Override
    public OperationalMgtOpResult setOperationalManagerBinding(String epr) {
        if (log.isDebugEnabled()) {
            log.info("Operational Manager: Set OperationalManagerBinding with epr : " + epr);
        }
        String pbId = composite.getSmcBinding().getOperationalManagerBinding();
        if (pbId == null) {
            pbId = "OperationalManager" + "_SB";
        }
        addServiceBinding(pbId, null, epr);
        composite.getSmcBinding().setOperationalManagerBinding(pbId);
        return new OperationalMgtOpResult(true, "Set OperationalManagerBinding  with epr : " + epr);
    }

    public OMElement getNextManagementMessageBlocking() {
        try {
            log.info("retrieving next message for operational manager");
            return (OMElement) outQueue.take().getMessage();
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public OMElement getNextManagementMessage(long timeout) {
        try {
            if (log.isInfoEnabled()) {
                log.info("retrieving next message for operational manager with a timeout");
            }
            return (OMElement) outQueue.poll(timeout, TimeUnit.MILLISECONDS).getMessage();
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public OrganiserMgtOpResult subscribeToManagementMessages(String epPattern, String notificationOperation) {
        //TODO
        return null;
    }

    @Override
    public OrganiserMgtOpResult enactOperationalManagementPolicy(String policyId, String policyFile) {
        log.info("Got a management policyto admit \n" + policyFile);

        try {
            long start = System.nanoTime();
            composite.getPolicyEnactmentEngine().enactManagementPolicy(policyId, policyFile);
            long stop = System.nanoTime();
            return new OrganiserMgtOpResult(true, "Management policy applied in ." + (stop - start) + "nanoseconds");
        } catch (Exception e) {
            e.printStackTrace();
            return new OrganiserMgtOpResult(false, "Management policy enactment has failed." + e.getMessage());
        }
    }

    @Override
    public OrganiserMgtOpResult enactOperationalManagementPolicyRemote(String policyId, OMElement policyFile) {
        log.info("Got a management policyto admit \n" + policyId);

        try {
            OMText binaryNode = (OMText) (policyFile).getFirstOMChild();
            DataHandler actualDH = (DataHandler) binaryNode.getDataHandler();
            long start = System.nanoTime();
            composite.getPolicyEnactmentEngine().enactManagementPolicy(policyId, actualDH);
            long stop = System.nanoTime();
            return new OrganiserMgtOpResult(true, "Management policy applied in ." + (stop - start) + "nanoseconds");
        } catch (Exception e) {
            e.printStackTrace();
            return new OrganiserMgtOpResult(false, "Management policy enactment has failed." + e.getMessage());
        }
    }

//    @Override
//    public void sendToOperationalManager(MessageWrapper message) {
//        outQueue.add(message);
//        if (log.isDebugEnabled()) {
//            log.debug("Added a management message the operational roles outQueue");
//        }
//    }

    private OrganiserMgtOpResult addServiceBinding(String pbId, String rid, String endpoint) {
        if (log.isDebugEnabled()) {
            log.info("Organiser: addServiceBinding, pbID: " + pbId);
        }

        PlayerBindingType playerBindingType = new PlayerBindingType();
        playerBindingType.setId(pbId);
        if (rid != null) {
            PlayerBindingType.Roles roles = new PlayerBindingType.Roles();
            roles.getRoleID().add(rid);
            playerBindingType.setRoles(roles);
        }
        playerBindingType.setEndpoint(endpoint);

        PlayerBinding playerBinding = new PlayerBinding(playerBindingType);
        composite.getPlayerBindingMap().put(pbId, playerBinding);
        composite.updateRoleBindings(playerBinding, true);
        return new OrganiserMgtOpResult(true, "New PlayerBinding "
                + playerBinding.getId() + " added successfully");
    }
}
