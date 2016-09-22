<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/fsdep">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="MM-FD.reimburse.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q0:reimburse xmlns:q0="http://ws.apache.org/axis2/xsd/fsdep">
                    <content>
                        Bill No 20033 for reimburse
                    </content>
                </q0:reimburse>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>