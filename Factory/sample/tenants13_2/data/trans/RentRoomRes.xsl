<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2/xsd/hc">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="RentRoom.doneMsg"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Body>
                <q0:rentRoomResponse>
                    <return>
                        <content>
                            <xsl:value-of
                                    select="$RentRoom.doneMsg/soapenv:Envelope/soapenv:Body/q0:rentRoomResponse/q0:return"/>
                        </content>
                    </return>
                </q0:rentRoomResponse>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>