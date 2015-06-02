package com.theice.mdf.client.multicast;

import java.util.List;

import com.theice.mdf.message.*;
import com.theice.mdf.message.pricelevel.*;

/**
 * @author qwang
 * @version %I%, %G% Created: Feb 6, 2008 1:23:32 PM
 * 
 * 
 */
public class SimplePLTOBMulticastClient extends SimpleMulticastClient
{
   private int _marketID;
   private char _side;

   private SimplePLTOBMulticastClient()
   {
      super();
   }

   public SimplePLTOBMulticastClient(String ipAddress, int port)
   {
      super(ipAddress, port);
   }

   /**
    * multicast client
    * 
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception
   {
      SimplePLTOBMulticastClient client = new SimplePLTOBMulticastClient();
      client.setMarketID(Integer.valueOf(System.getProperty("pl.marketId")));
      client.setSide(System.getProperty("pl.side").charAt(0));
      runClient(client, args);
   }

   /**
    * @param messageBlock
    */
   protected void printMessage(MulticastMessageBlock messageBlock)
   {
      int topOfBookPosition = 1;
      List<MDSequencedMessage> mdMessages = messageBlock.getMdMessages();
      if (mdMessages != null)
      {
         for (MDSequencedMessage mdSequencedMessage : mdMessages)
         {
            if (mdSequencedMessage instanceof MDSequencedMessageWithMarketID)
            {
               MDSequencedMessageWithMarketID mdSequencedMessageWithMarketID = (MDSequencedMessageWithMarketID) mdSequencedMessage;
               if (_marketID == mdSequencedMessageWithMarketID.getMarketID())
               {
                  if (mdSequencedMessageWithMarketID instanceof ChangePriceLevelMessage)
                  {
                     ChangePriceLevelMessage changePriceLevelMessage = (ChangePriceLevelMessage) mdSequencedMessageWithMarketID;
                     if (_side == changePriceLevelMessage.getSide() && topOfBookPosition == changePriceLevelMessage.getPriceLevelPosition())
                     {
                        System.out.println(changePriceLevelMessage.getPrice() + ":" + changePriceLevelMessage.getQuantity() + " CHANGE");
                     }
                  }
                  else if (mdSequencedMessageWithMarketID instanceof AddPriceLevelMessage)
                  {
                     AddPriceLevelMessage addPriceLevelMessage = (AddPriceLevelMessage) mdSequencedMessageWithMarketID;
                     if (_side == addPriceLevelMessage.getSide() && topOfBookPosition == addPriceLevelMessage.getPriceLevelPosition())
                     {
                        System.out.println(addPriceLevelMessage.getPrice() + ":" + addPriceLevelMessage.getQuantity() + " ADD");
                     }
                  }
                  else if (mdSequencedMessageWithMarketID instanceof DeletePriceLevelMessage)
                  {
                     DeletePriceLevelMessage deletePriceLevelMessage = (DeletePriceLevelMessage) mdSequencedMessageWithMarketID;
                     if (_side == deletePriceLevelMessage.getSide() && topOfBookPosition == deletePriceLevelMessage.getPriceLevelPosition())
                     {
                        System.out.println("TOP OF BOOK DELETED");
                     }
                  }
               }
            }
         }
      }      
   }

   /**
    * @return the marketID
    */
   public int getMarketID()
   {
      return _marketID;
   }

   /**
    * @param marketID the marketID to set
    */
   public void setMarketID(int marketID)
   {
      _marketID = marketID;
   }

   /**
    * @return the side
    */
   public char getSide()
   {
      return _side;
   }

   /**
    * @param side the side to set
    */
   public void setSide(char side)
   {
      _side = side;
   }
}
