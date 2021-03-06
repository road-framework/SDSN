<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                xmlns:q0="http://ws.apache.org/axis2">
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="GetServiceCapability.doneMsg"/>
    <xsl:template match="/">
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
            <soapenv:Body>
                <q0:GetServiceCapabilityResponse
                        xmlns:q0="http://ws.apache.org/axis2" xmlns:q1="http://jaxws.core/">
                    <return>
                        <actions>
                            <name>
                                <xsl:value-of
                                        select="$GetServiceCapability.doneMsg/soapenv:Envelope/soapenv:Body/q1:GetServiceResponse/return/capabilities/actions/name"/>
                            </name>
                            <raasRef>
                                <xsl:value-of
                                        select="$GetServiceCapability.doneMsg/soapenv:Envelope/soapenv:Body/q1:GetServiceResponse/return/capabilities/actions/raasRef"/>
                            </raasRef>
                        </actions>
                        <name>
                            <xsl:value-of
                                    select="$GetServiceCapability.doneMsg/soapenv:Envelope/soapenv:Body/q1:GetServiceResponse/return/capabilities/name"/>
                        </name>
                        <raasRef>
                            <xsl:value-of
                                    select="$GetServiceCapability.doneMsg/soapenv:Envelope/soapenv:Body/q1:GetServiceResponse/return/capabilities/raasRef"/>
                        </raasRef>
                    </return>
                </q0:GetServiceCapabilityResponse>
            </soapenv:Body>
        </soapenv:Envelope>
    </xsl:template>
</xsl:stylesheet>