<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q1="http://ws.apache.org/axis2/xsd/tx">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="PayTX.doneMsg"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q0:sendTaxiInvoiceResponse xmlns:q0="http://ws.apache.org/axis2/xsd/tx">
                    <return>
                        <xsl:value-of
                                select="$PayTX.doneMsg/soapenv:Envelope/soapenv:Body/q1:payTXResponse/q1:return"/>
                    </return>
                </q0:sendTaxiInvoiceResponse>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>