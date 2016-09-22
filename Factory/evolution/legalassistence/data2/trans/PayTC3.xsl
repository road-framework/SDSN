<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="SC-TC3.payTow.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q1:payTC xmlns:q1="http://ws.apache.org/axis2/xsd/co">
                    <content>tow done
                        <!--<xsl:value-of-->
                        <!--select="$CO-TTP.payTow.Req/soapenv:Envelope/soapenv:Body/q1:payTow/content"/>-->
                    </content>
                </q1:payTC>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>