<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/grex">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="GREX-PS.orderParts.Res"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Body>
                <q0:doRepair xmlns:q0="http://ws.apache.org/axis2/xsd/grex">
                    <content>
                        <xsl:value-of
                                select="$GREX-PS.orderParts.Res/soapenv:Envelope/soapenv:Body/q0:orderPartsResponse/return"/>
                    </content>
                </q0:doRepair>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>