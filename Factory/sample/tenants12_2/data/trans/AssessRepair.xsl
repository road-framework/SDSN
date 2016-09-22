<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/grex">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="GREX-TTS.sendGRLocation.Res"/>
    <xsl:param name="GREX-TTP.sendGRLocation.Res"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Body>
                <q0:assessRepair>
                    <content>check vehicle
                        <!--<xsl:value-of-->
                        <!--select="$GREX-TTP.sendGRLocation.Res/soapenv:Envelope/soapenv:Body/q0:sendGRLocationResponse/return"/>-->
                    </content>
                </q0:assessRepair>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>