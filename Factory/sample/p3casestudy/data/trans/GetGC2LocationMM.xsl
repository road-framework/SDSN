<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/grex">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="MM-GC2.getGCLocation.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Body>
                <q0:getGarageLocation xmlns:q0="http://ws.apache.org/axis2/xsd/grex">
                    <content>
                        <xsl:value-of
                                select="$MM-GC2.getGCLocation.Req/soapenv:Envelope/soapenv:Body/q0:getGCLocation/content"/>
                    </content>
                </q0:getGarageLocation>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>