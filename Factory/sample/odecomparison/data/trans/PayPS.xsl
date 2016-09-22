<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="PS-CO.payParts.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">

            <soapenv:Body>
                <q1:payPS xmlns:q1="http://ws.apache.org/axis2/xsd/co">
                    <content>
                        <xsl:value-of
                                select="$PS-CO.payParts.Req/soapenv:Envelope/soapenv:Body/q1:payParts/content"/>
                    </content>
                </q1:payPS>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>