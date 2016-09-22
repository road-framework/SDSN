echo Generating client stubs for USDL

call wsdl2java.bat -uri http://localhost:8080/axis2/services/usdl_sc1?wsdl -o  ./ -p usdl -uw
echo C1 Generated 
 
call wsdl2java.bat -uri http://localhost:8080/axis2/services/usdl_sc2?wsdl -o  ./ -p usdl -uw
echo C2 Generated
 
call wsdl2java.bat -uri http://localhost:8080/axis2/services/usdl_sp1?wsdl -o  ./ -p usdl -uw
echo SP Generated
 
call wsdl2java.bat -uri http://localhost:8080/axis2/services/usdl_rup?wsdl -o  ./ -p usdl -uw
echo RUP Generated
 
call wsdl2java.bat -uri http://localhost:8080/axis2/services/usdl_mon?wsdl -o  ./ -p usdl -uw
echo MON Generated
pause