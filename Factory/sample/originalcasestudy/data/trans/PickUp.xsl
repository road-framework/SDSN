<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/ttp">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="MM-TC1.pickUp.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q0:pickUp>
                    <pickupLocation>
                        <xsl:value-of
                                select="$MM-TC1.pickUp.Req/soapenv:Envelope/soapenv:Body/q0:pickUp/content"/>
                    </pickupLocation>
                </q0:pickUp>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>