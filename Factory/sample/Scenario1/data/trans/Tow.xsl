<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="CO-TT.orderTow.Req"/>
    <xsl:param name="GR-TT.sendGRLocation.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">

            <soapenv:Body>
                <q0:tow xmlns:q0="http://ws.apache.org/axis2">
                    <pickupLocation>
                        <xsl:value-of select="$CO-TT.orderTow.Req/soapenv:Envelope/soapenv:Body/q0:orderTow/content"/>
                    </pickupLocation>
                    <garageLocation>
                        <xsl:value-of
                                select="$GR-TT.sendGRLocation.Req/soapenv:Envelope/soapenv:Body/q0:sendGRLocation/content"/>
                    </garageLocation>
                </q0:tow>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>