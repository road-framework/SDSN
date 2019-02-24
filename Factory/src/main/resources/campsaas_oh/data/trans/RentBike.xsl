<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/br">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="SC-BR.rentBike.Req"/>
    <xsl:param name="CM-BR.sendBikeRequirements.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q0:rentBike xmlns:q0="http://ws.apache.org/axis2/xsd/br">
                    <content>
                        <xsl:value-of
                                select="$SC-BR.rentBike.Req/soapenv:Envelope/soapenv:Body/q0:rentBike/content"/>
                    </content>
                </q0:rentBike>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>