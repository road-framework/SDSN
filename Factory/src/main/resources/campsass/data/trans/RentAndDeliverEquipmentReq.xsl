<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/ep2">
    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q0:rentAndDeliverEquipment xmlns:q0="http://ws.apache.org/axis2/xsd/ep2">
                    <q0:content>
                        tent and cooking ep2
                    </q0:content>
                </q0:rentAndDeliverEquipment>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>