<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="PayGR.doneMsg"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Body>
                <q0:requestAssistResponse xmlns:q0="http://ws.apache.org/axis2">
                    <q0:return>ComplainResolved</q0:return>
                </q0:requestAssistResponse>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>