DEF:AddFeatureRentingVehicle {
   addRole id="RoadsideTaxiChain" name="RoadsideTaxiChain";
   addPlayerBinding pbId="RoadsideTaxiChain" id="RoadsideTaxiChain" endpoint="http://localhost:8082/axis2/services/RoadsideTaxiChain";
   addTaskDef id="RoadsideTaxiChain" tId="RentVehicle" usingMsgs="SupportCentre-RoadsideTaxiChain.rentVehicle.Req" resultingMsgs="SupportCentre-RoadsideTaxiChain.rentVehicle.Res.rentVehicleRes";
   addTaskDef id="RoadsideTaxiChain" tId="EndLease" usingMsgs="SupportCentre-RoadsideTaxiChain.endVehicleLease.Req" resultingMsgs="SupportCentre-RoadsideTaxiChain.endVehicleLease.Res.endVehicleLeaseRes";
   setOutMsgOnTask id="RoadsideTaxiChain" tId="RentVehicle" deliveryType="push" returnType="String" parameters="String.location" name="rentVehicle";
   setInMsgOnTask id="RoadsideTaxiChain" tId="RentVehicle" returnType="String" parameters="String.location" name="rentVehicle";
   setOutMsgOnTask id="RoadsideTaxiChain" tId="EndLease" deliveryType="push" returnType="String" parameters="String.bookingNo" name="endVehicleLease";
   setInMsgOnTask id="RoadsideTaxiChain" tId="EndLease" returnType="String" parameters="String.bookingNo" name="endVehicleLease";
   updateTaskDef id="SupportCentre" tId="AnalyseAssistRequest" property="resultingMsgs" value="SupportCentre-GarageChain.orderRepair.Req.orderRepairReq,SupportCentre-TowChain.orderTow.Req.orderTowReq,SupportCentre-RoadsideTaxiChain.rentVehicle.Req.rentVehicleReq,SupportCentre-RoadsideLawFirm.provideLegalAdvice.Req.provideLegalAdviceReq,SupportCentre-Member.requestAssist.Res.analyseAssistRequestRes";
   updateTaskDef id="SupportCentre" tId="AckRepairing" property="resultingMsgs" value="SupportCentre-Member.notifyRepairStatus.Req.notifyRepairStatusReq3,SupportCentre-RoadsideTaxiChain.endVehicleLease.Req.endVehicleLeaseReq";
   addContract cId="SupportCentre-RoadsideTaxiChain" ruleFile="SupportCentre-RoadsideTaxiChain.drl" type="permissive" state="Incipient" rAId="SupportCentre" rBId="RoadsideTaxiChain";
   addTerm  tmId="rentVehicle" cId="SupportCentre-RoadsideTaxiChain"  name="rentVehicle" direction="AtoB" messageType="push";
   addOperationToTerm tmId="rentVehicle" cId="SupportCentre-RoadsideTaxiChain" returnType="String" parameters="String.location" name="rentVehicle";
   addTerm  tmId="endVehicleLease" cId="SupportCentre-RoadsideTaxiChain" name="endVehicleLease" direction="AtoB" messageType="push";
   addOperationToTerm tmId="endVehicleLease" cId="SupportCentre-RoadsideTaxiChain" returnType="String" parameters="String.bookingNo" name="endVehicleLease";   
   addBehavior bId="RentingVehicle";
   addTaskRef tId="RoadsideTaxiChain.RentVehicle" bId="RentingVehicle" preEP="eRentVehicleReqd" postEP="eVehicleRented";
   addTaskRef tId="RoadsideTaxiChain.EndLease" bId="RentingVehicle" preEP="eEndVehicleLease" postEP="eVehicleLeaseEnded";
   updateProcessDef pdId="Tenant2" property="CoT" value="eNotifyMM * eVehicleLeaseEnded";
   addBehaviorRefToProcessDef pdId="Tenant2" bId="RentingVehicle";
 }
