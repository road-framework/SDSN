<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="SC-GC2.orderRepair.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">

            <soapenv:Body>
                <q0:placeRepairOrder xmlns:q0="http://ws.apache.org/axis2">
                    <content>
                        <xsl:value-of
                                select="$SC-GC2.orderRepair.Req/soapenv:Envelope/soapenv:Body/q0:orderRepair/content"/>
                    </content>
                </q0:placeRepairOrder>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>