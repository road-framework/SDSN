<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/co">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="CO-GRIN.payRepair.Req"/>
    <xsl:param name="CO-GREX.payRepair.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Body>
                <q0:payGR>
                    <content>repair done
                        <!--<xsl:value-of-->
                        <!--select="$CO-GRIN.payRepair.Req/soapenv:Envelope/soapenv:Body/q0:payRepair/content"/>-->
                    </content>
                </q0:payGR>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>