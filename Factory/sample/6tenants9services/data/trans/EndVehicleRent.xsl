<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/vc">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="CO-VC.endVehicleRent.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Body>
                <q0:endVehicleRent xmlns:q0="http://ws.apache.org/axis2/xsd/vc">
                    <content>
                        <xsl:value-of
                                select="$CO-VC.endVehicleRent.Req/soapenv:Envelope/soapenv:Body/q0:endVehicleRent/content"/>
                    </content>
                </q0:endVehicleRent>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>