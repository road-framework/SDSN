<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
>
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="CO-MM.complain.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Body>
                <q0:analyze xmlns:q0="http://cos.com">
                    <content>
                        <xsl:value-of select="$CO-MM.complain.Req/soapenv:Envelope/soapenv:Body/q0:complain/args0"/>
                    </content>
                </q0:analyze>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>