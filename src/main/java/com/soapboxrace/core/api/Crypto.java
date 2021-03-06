/*
 * This file is part of the Soapbox Race World core source code.
 * If you use any of this code for third-party purposes, please provide attribution.
 * Copyright (c) 2020.
 */

package com.soapboxrace.core.api;

import com.soapboxrace.core.api.util.Secured;
import com.soapboxrace.core.api.util.UUIDGen;
import com.soapboxrace.core.bo.TokenSessionBO;
import com.soapboxrace.jaxb.http.UdpRelayCryptoTicket;
import com.soapboxrace.udp.UDPClient;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.nio.ByteBuffer;
import java.util.Base64;

@Path("/crypto")
public class Crypto {

    @EJB
    private TokenSessionBO tokenBO;

    @EJB
    private UDPClient udpClient;

    @GET
    @Secured
    @Path("/relaycryptoticket/{personaId}")
    @Produces(MediaType.APPLICATION_XML)
    public UdpRelayCryptoTicket relayCryptoTicket(@HeaderParam("securityToken") String securityToken, @PathParam(
            "personaId") Long personaId) {
        byte[] randomUUIDBytes = UUIDGen.getRandomUUIDBytes();
        String ticketIV = Base64.getEncoder().encodeToString(randomUUIDBytes);
        udpClient.sendRaceUdpKey(randomUUIDBytes);
        UdpRelayCryptoTicket udpRelayCryptoTicket = new UdpRelayCryptoTicket();
        String activeRelayCryptoTicket = tokenBO.getActiveRelayCryptoTicket(securityToken);
        udpRelayCryptoTicket.setCryptoTicket(activeRelayCryptoTicket);
        udpRelayCryptoTicket.setSessionKey("AAAAAAAAAAAAAAAAAAAAAA==");
        udpRelayCryptoTicket.setTicketIv(ticketIV);
        return udpRelayCryptoTicket;
    }

    @GET
    @Path("/cryptoticket")
    @Produces(MediaType.APPLICATION_XML)
    public String cryptoticket() {
        byte[] randomUUIDBytes = UUIDGen.getRandomUUIDBytes();
        String ticketIV = Base64.getEncoder().encodeToString(randomUUIDBytes);
        udpClient.sendFreeroamUdpKey(randomUUIDBytes);
        byte[] helloPacket = {10, 11, 12, 13};
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        byteBuffer.put(helloPacket);
        byte[] cryptoTicketBytes = byteBuffer.array();
        String cryptoTicketBase64 = Base64.getEncoder().encodeToString(cryptoTicketBytes);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<ClientServerCryptoTicket>\n");
        stringBuilder.append("<CryptoTicket>");
        stringBuilder.append(cryptoTicketBase64);
        stringBuilder.append("</CryptoTicket>\n");
        stringBuilder.append("<SessionKey>AAAAAAAAAAAAAAAAAAAAAA==</SessionKey>\n");
        stringBuilder.append("<TicketIv>");
        stringBuilder.append(ticketIV);
        stringBuilder.append("</TicketIv>\n");
        stringBuilder.append("</ClientServerCryptoTicket>");
        return stringBuilder.toString();
    }
}
