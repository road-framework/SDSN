<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
>
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="GC2-TC1.sendGCLocation.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q1:deliver xmlns:q1="http://ws.apache.org/axis2/xsd/ttp">
                    <garageLocation>kandy gampola
                        <!--<xsl:value-of-->
                        <!--select="$GRIN-TTP.sendGRLocation.Req/soapenv:Envelope/soapenv:Body/q0:sendGRLocation/content"/>-->
                    </garageLocation>
                </q1:deliver>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>