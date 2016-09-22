<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/grex">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="AssessRepair.doneMsg"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Body>
                <q1:orderParts xmlns:q1="http://ws.apache.org/axis2/xsd/ps">
                    <content>
                        <xsl:value-of
                                select="$AssessRepair.doneMsg/soapenv:Envelope/soapenv:Body/q0:assessRepairResponse/q0:return"/>
                    </content>
                </q1:orderParts>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>