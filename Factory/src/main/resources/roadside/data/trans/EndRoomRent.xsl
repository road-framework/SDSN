<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/hc">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="SC-HC.endRoomRent.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q0:endRoomRent xmlns:q0="http://ws.apache.org/axis2/xsd/hc">
                    <content>
                        <xsl:value-of
                                select="$SC-HC.endRoomRent.Req/soapenv:Envelope/soapenv:Body/q0:endRoomRent/content"/>
                    </content>
                </q0:endRoomRent>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>