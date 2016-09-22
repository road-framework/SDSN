<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/co" xmlns:q2="http://ws.apache.org/axis2/xsd/marktow">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="SC-TC3.orderTow.Req"/>
    <xsl:param name="GC1-TC3.sendGCLocation.Req"/>
    <xsl:param name="LF-TC3.authorizeTow.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q2:tow xmlns:q2="http://ws.apache.org/axis2/xsd/marktow">
                    <pickupLocation>
                        <xsl:value-of select="$SC-TC3.orderTow.Req/soapenv:Envelope/soapenv:Body/q0:orderTow/content"/>
                    </pickupLocation>
                    <garageLocation>
                        <xsl:value-of
                                select="$GC1-TC3.sendGCLocation.Req/soapenv:Envelope/soapenv:Body/q2:sendGCLocation/content"/>
                    </garageLocation>
                </q2:tow>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>