package com.soapboxrace.core.api;

import com.soapboxrace.core.api.util.Secured;
import com.soapboxrace.core.bo.SocialRelationshipBO;
import com.soapboxrace.core.bo.TokenSessionBO;
import com.soapboxrace.core.dao.PersonaDAO;
import com.soapboxrace.core.dao.SocialRelationshipDAO;
import com.soapboxrace.core.exception.EngineException;
import com.soapboxrace.core.exception.EngineExceptionCode;
import com.soapboxrace.core.xmpp.OpenFireSoapBoxCli;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/addfriendrequest")
public class AddFriendRequest {
    @EJB
    private TokenSessionBO tokenSessionBO;

    @EJB
    private SocialRelationshipBO socialRelationshipBO;

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_XML)
    public Response addFriendRequest(@HeaderParam("securityToken") String securityToken,
                                     @QueryParam("displayName") String displayName) {
        Long activePersonaId = tokenSessionBO.getActivePersonaId(securityToken);
        return Response.ok().entity(socialRelationshipBO.addFriend(activePersonaId, displayName)).build();
    }
}