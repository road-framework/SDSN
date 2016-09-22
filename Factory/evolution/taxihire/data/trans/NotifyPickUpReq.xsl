<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/ttp">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="PickUp.doneMsg"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q1:notifyPickUp xmlns:q1="http://ws.apache.org/axis2/xsd/co">
                    <return>
                        <xsl:value-of
                                select="$PickUp.doneMsg/soapenv:Envelope/soapenv:Body/q0:pickUpResponse/q0:return"/>
                    </return>
                </q1:notifyPickUp>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>