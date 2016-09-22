echo Generating client stub

call wsdl2java.bat -uri http://localhost:8080/axis2/services/rosassmc_member?wsdl -o ./ -p motorist -uw
 
echo Motorist Client Generated 

pause