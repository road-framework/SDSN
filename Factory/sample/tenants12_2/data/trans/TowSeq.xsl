<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/co">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="CO-TTS.orderTow.Req"/>
    <xsl:param name="GREX-TTS.sendGRLocation.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">

            <soapenv:Body>
                <q2:tow xmlns:q2="http://ws.apache.org/axis2/xsd/tts">
                    <pickupLocation>
                        <xsl:value-of select="$CO-TTS.orderTow.Req/soapenv:Envelope/soapenv:Body/q0:orderTow/content"/>
                    </pickupLocation>
                    <garageLocation>
                        <xsl:value-of
                                select="$GREX-TTS.sendGRLocation.Req/soapenv:Envelope/soapenv:Body/q2:sendGRLocation/content"/>
                    </garageLocation>
                </q2:tow>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>