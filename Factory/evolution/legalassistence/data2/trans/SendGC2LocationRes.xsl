<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q1="http://ws.apache.org/axis2/xsd/tts" xmlns:ax21="http://ws.apache.org/axis2/xsd">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="Tow.doneMsg"/>
    <xsl:param name="Deliver.doneMsg"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q0:sendGCLocationResponse xmlns:q0="http://ws.apache.org/axis2/xsd/grex">
                    <return>
                        <xsl:value-of
                                select="$Tow.doneMsg/soapenv:Envelope/soapenv:Body/q1:towResponse/q1:return/ax21:sendGCLocationResponse"/>
                    </return>
                </q0:sendGCLocationResponse>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>