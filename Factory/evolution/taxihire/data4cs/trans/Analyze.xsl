<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/co">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="SC-MM.assist.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q0:analyze xmlns:q0="http://ws.apache.org/axis2/xsd/co">
                    <memId>
                        <xsl:value-of
                                select="$SC-MM.assist.Req/soapenv:Envelope/soapenv:Body/q0:assist/q0:memId"/>
                    </memId>
                    <complainDetails>
                        <xsl:value-of
                                select="$SC-MM.assist.Req/soapenv:Envelope/soapenv:Body/q0:assist/q0:complain"/>
                    </complainDetails>
                </q0:analyze>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>