package com.xarql.chat;

import java.sql.Timestamp;

public class Message extends WebsocketPackage
{
    private static final long MESSAGE_LIFESPAN = 600000; // 10 minutes = 600000

    public Message(String content, Client client) throws IllegalArgumentException
    {
        super(content, client);
    } // Message()

    public boolean isExpired()
    {
        Timestamp maxAge = new Timestamp(System.currentTimeMillis() - MESSAGE_LIFESPAN);
        if(getCreationDate().compareTo(maxAge) > 0)
            return false;
        else
            return true;
    } // isExpired()

} // Message