<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/ep1">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="SC-EP1.rentEquipment.req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q0:deliver xmlns:q0="http://ws.apache.org/axis2/xsd/ep1">
                    <q0:equipmentRequirements>
                        tent brand x ep1
                    </q0:equipmentRequirements>
                </q0:deliver>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>