package testREST;

import testREST.Enums.InviteReply;
import testREST.Enums.TransportationModes;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/android")
public class AndroidConnector {
    private NumberHandler numberHandler;
    private GpsHandler gpsHandler;
    private InviteHandler inviteHandler;

    public AndroidConnector() {
        numberHandler = new NumberHandler();
        gpsHandler = new GpsHandler();
        inviteHandler = new InviteHandler();
    }

    @Path("/registerNumber/{ownNumber}/{ownToken}")
    @POST
    public String registerNumber(@PathParam("ownNumber") String ownNumber,
                                 @PathParam("ownToken") String ownToken) {
        //add number to db
        Logger.getLogger(AndroidConnector.class.getName()).log(Level.INFO, ownToken);
        return Boolean.toString(numberHandler.addNumber(ownNumber, ownToken));
    }

    @Path("/checkNumbers/{ownNumber}/{contactNumbers}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String checkNumbers(@PathParam("contactNumbers") String contactNumbers,
                               @PathParam("ownNumber") String ownNumber) {
        //compare numbers with DB
        return numberHandler.checkNumbers(ownNumber, contactNumbers);
    }

    @Path("/inviteFriends/{ownNumber}/{friendNumbers}/{ownTransportationMode}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String inviteFriends(@PathParam("friendNumbers") String friendNumbers,
                                @PathParam("ownNumber") String ownNumber,
                                @PathParam("ownTransportationMode") String ownTransportationMode) {
        //invite
        return inviteHandler.inviteFriends(ownNumber, friendNumbers, TransportationModes.valueOf(ownTransportationMode));
    }


    @Path("/currentGPS/{ownNumber}/{longitude}/{latitude}")
    @POST
    public String currentGps(@PathParam("latitude") String latitude,
                             @PathParam("longitude") String longitude,
                             @PathParam("ownNumber") String ownNumber) {
        return Boolean.toString(gpsHandler.saveGPS(ownNumber, latitude, longitude));
    }

    @Path("/responseToInvite/{ownNumber}/{inviteId}/{inviteReply}/{transportationMode}")
    @POST
    public String responseToInvite(@PathParam("ownNumber") String ownNumber,
                                   @PathParam("inviteId") String inviteId,
                                   @PathParam("inviteReply") String inviteReply,
                                   @PathParam("transportationMode") String transportationMode) {
        Logger.getLogger(AndroidConnector.class.getName()).log(Level.INFO,"RESPONSE TO INVITE: "
                + ownNumber + " " + inviteId + " " + InviteReply.valueOf(inviteReply) + " " + TransportationModes.valueOf(transportationMode));
        return String.valueOf(inviteHandler.responseToInvite(ownNumber, inviteId, InviteReply.valueOf(inviteReply),
                TransportationModes.valueOf(transportationMode)));
    }

    @Path("/addPlace/{inviteId}/{place}")
    @POST
    public String currentGps(@PathParam("inviteId") String inviteId,
                             @PathParam("place") String place) {
        return Boolean.toString(inviteHandler.addPlaceToInvite(inviteId, place));
    }

    @Path("/updateInvite/{inviteId}/")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String updateInvite(@PathParam("inviteId") String inviteId) {
        return inviteHandler.getInviteString(inviteId);
    }

    @Path("/updateAllInvites/{ownNumber}/{inviteIds}/")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String updateInvite(@PathParam("ownNumber") String ownNumber,
            @PathParam("inviteIds") String inviteIds) {
        return inviteHandler.getMissingInvites(ownNumber, inviteIds);
    }

    @Path("/draengel/{ownNumber}/{friendNumber}/")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String draengel(@PathParam("ownNumber") String ownNumber,
                               @PathParam("friendNumber") String friendNumber) {

        return inviteHandler.draengeln(ownNumber, friendNumber);
    }
}
