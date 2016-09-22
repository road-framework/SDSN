<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/vc">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="SC-VC.rentVehicle.Req"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Header/>
            <soapenv:Body>
                <q0:rentVehicle xmlns:q0="http://ws.apache.org/axis2/xsd/vc">
                    <content>
                        <xsl:value-of
                                select="$SC-VC.rentVehicle.Req/soapenv:Envelope/soapenv:Body/q0:rentVehicle/content"/>
                    </content>
                </q0:rentVehicle>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>