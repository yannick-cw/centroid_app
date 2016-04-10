package testREST;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import testREST.Enums.MessageType;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoogleMessageConnector {
    //this is the project server id
    private static final String SENDER_ID = "AIzaSyC58NddmbaLi99O-EfNGFOcysYXs3WNIqI";
    private final String MESSAGE_TYPE = "type";


    public String pushMessage (String token, Map messages, MessageType messageType) throws IOException {

        Sender _sender = new Sender(SENDER_ID);

        double _random = Math.random();
        Message _message = new Message.Builder()
                //with same key user gets only the latest update, when he goes online
                .collapseKey(String.valueOf(_random))
                .timeToLive(30)
                .delayWhileIdle(true)
                .setData(messages)
                .addData(MESSAGE_TYPE, messageType.toString())
                .build();
        Result _result = _sender.send(_message, token, 1);

        //the logger shows the result of the message sent (push)
        Logger.getLogger(GoogleMessageConnector.class.getName()).log(Level.INFO,_result.getErrorCodeName());
        Logger.getLogger(GoogleMessageConnector.class.getName()).log(Level.INFO,"type: " + messageType + " message: " + _message.toString());
        return _result.getErrorCodeName();
    }
}
