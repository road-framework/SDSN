<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/tx1" xmlns:ax21="http://ws.apache.org/axis2/xsd">
    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q1:bookTaxi xmlns:q1="http://ws.apache.org/axis2/xsd/tx1">
                    <q1:content>
                        book taxi amsterdam
                    </q1:content>
                </q1:bookTaxi>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>