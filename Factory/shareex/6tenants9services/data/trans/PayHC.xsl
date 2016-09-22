<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="SC-HC.payRoomRent.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q1:payHC xmlns:q1="http://ws.apache.org/axis2/xsd/co">
                    <content>
                        <xsl:value-of
                                select="$SC-HC.payRoomRent.Req/soapenv:Envelope/soapenv:Body/q1:payRoomRent/content"/>
                    </content>
                </q1:payHC>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>