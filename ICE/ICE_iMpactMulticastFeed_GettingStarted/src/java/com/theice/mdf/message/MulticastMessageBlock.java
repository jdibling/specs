package com.theice.mdf.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.theice.mdf.stats.MulticastStats;

/**
 * Created by IntelliJ IDEA. User: dchen Date: Oct 1, 2007 Time: 4:53:57 PM To
 * change this template use File | Settings | File Templates.
 */
public class MulticastMessageBlock
{
   public final static short BLOCK_HEADER_LENGTH = 2 + 4 + 2 + 8;
   public final static short MAX_BLOCK_LENGTH = 1400;
   public final static short MAX_BLOCK_BODY_LENGTH = MAX_BLOCK_LENGTH - BLOCK_HEADER_LENGTH;

   private byte[] _serializedBytes;

   public short SessionNumber;
   public int SequenceNumber;
   public short NumOfMessages;
   public short BlockBodyLength;
   public long SentDateTime;
   private List<MDSequencedMessage> _mdMessages = null;
   private MulticastStats _multicastStats;

   /**
    * This method is not thread safe, however, once the
    * <code>_serializedBytes</code> is created other threads will be able to
    * see it since this object is going to be published to a Blocking Queue
    * 
    * @return this object in serialized form
    */
   public synchronized byte[] serialize()
   {
      if (_serializedBytes == null)
      {
         ByteBuffer byteBuffer = ByteBuffer.allocate(BLOCK_HEADER_LENGTH + BlockBodyLength);

         byteBuffer.putShort(SessionNumber);
         byteBuffer.putInt(SequenceNumber);
         byteBuffer.putShort((short)_mdMessages.size());
         byteBuffer.putLong(SentDateTime);

         for (int i = 0; i < _mdMessages.size(); i++)
         {
            MDMessage mdMsg = (MDMessage) _mdMessages.get(i);
            byteBuffer.put(mdMsg.serialize());
         }

         _serializedBytes = byteBuffer.array();
      }

      return _serializedBytes;
   }

   public short deserializeMessageHeaderOnly(DataInputStream inputStream) throws Exception
   {
      byte[] blockHeaderBytes = new byte[BLOCK_HEADER_LENGTH];
      inputStream.readFully(blockHeaderBytes);
      ByteBuffer headerByteBuffer = ByteBuffer.wrap(blockHeaderBytes);
      populateHeaderFields(headerByteBuffer);
      short numberOfMessagesTraversed=0;
      for(; numberOfMessagesTraversed<NumOfMessages; numberOfMessagesTraversed++)
      {
         byte[] headerBytes = new byte[3];
         inputStream.readFully(headerBytes);
         short msgBodyLength = ByteBuffer.wrap(headerBytes, 1, 2).getShort();
         
         int bytesSkipped = inputStream.skipBytes(msgBodyLength);
         if (bytesSkipped!=msgBodyLength)
         {
            StringBuffer errMsg = new StringBuffer("Error when skipping message body bytes: ");
            errMsg.append("MessageBodyLength=");
            errMsg.append(msgBodyLength);
            errMsg.append(", actual bytes skipped=");
            errMsg.append(bytesSkipped);
            throw new Exception(errMsg.toString());
         }
      }
      
      return numberOfMessagesTraversed;
   }
   public void deserialize(DataInputStream inputStream) throws IOException, InvalidRequestException,
         UnknownMessageException
   {
      // read header first
      byte[] bytes = new byte[BLOCK_HEADER_LENGTH];
      inputStream.readFully(bytes);
      ByteBuffer headerByteBuffer = ByteBuffer.wrap(bytes);
      populateHeaderFields(headerByteBuffer);
      readMDMessages(inputStream, null);
   }

   public void deserialize(ByteBuffer allBytesForMessageBlock) throws IOException, InvalidRequestException, UnknownMessageException
   {
      populateHeaderFields(allBytesForMessageBlock);
      readMDMessages(null, allBytesForMessageBlock);
   }

   public void deserialize(FileChannel inputFileChannel) throws IOException, InvalidRequestException,
         UnknownMessageException
   {
      ByteBuffer headerBuffer = ByteBuffer.allocate(4);
      inputFileChannel.read(headerBuffer);
      headerBuffer.rewind();
      int totalBodyLength = 0;
      totalBodyLength = headerBuffer.getInt();
      ByteBuffer allContentBytes = ByteBuffer.allocate(totalBodyLength);
      inputFileChannel.read(allContentBytes);
      allContentBytes.rewind();
      byte messageDelimeter = allContentBytes.get(totalBodyLength - 1);
      populateHeaderFields(allContentBytes);
      readMDMessages(null, allContentBytes);
   }

   @SuppressWarnings("unchecked")
   private void readMDMessages(DataInputStream inputStream, ByteBuffer allContentBytes) throws IOException,
         UnknownMessageException, InvalidRequestException
   {
      // read the body with the length received
      _mdMessages = new ArrayList();
      int msgReadCount = 0;
      while (msgReadCount < NumOfMessages)
      {
         MDSequencedMessage mdMsg = null;

         try
         {
            if (inputStream != null)
            {
               mdMsg = (MDSequencedMessage) RawMessageFactory.getObject(inputStream);
            }
            else
            {
               mdMsg = (MDSequencedMessage) RawMessageFactory.getObject(allContentBytes);
            }
         }
         catch (UnknownMessageException ex)
         {
            mdMsg = null;
            System.out.println("Get unknown message while reading the multicast block, ignore it and continue to read");
            System.out.println(ex.getMessage());
         }

         if (mdMsg!=null)
         {
            // set the sequence number based on the seq on the block
            // *** make sure that msgReadCount is only incremented later
            // *** per the spec, sequence number of the first message in a block is the same as that in the header
            mdMsg.setSequenceNumber(SequenceNumber+msgReadCount);
            _mdMessages.add(mdMsg);
         }

         msgReadCount++;
      }
   }

   /**
    * @param allContentBytes
    */
   private void populateHeaderFields(ByteBuffer allContentBytes)
   {
      SessionNumber = allContentBytes.getShort();
      SequenceNumber = allContentBytes.getInt();
      NumOfMessages = allContentBytes.getShort();
      SentDateTime = allContentBytes.getLong();
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer("");

      buffer.append("SessionNumber=" + this.SessionNumber);
      buffer.append("|SequenceNumber=" + this.SequenceNumber);
      buffer.append("|NumOfMessages=" + this.NumOfMessages);
      buffer.append("|BlockBodyLength=" + this.BlockBodyLength);
      buffer.append("|SentDateTime=" + this.SentDateTime);
      buffer.append("\n");

      if(_mdMessages!=null)
      {
          for (int index = 0; index < this._mdMessages.size(); index++)
          {
             buffer.append("\t" + _mdMessages.get(index).toString() + "\n");
          }
      }

      return (buffer.toString());
   }

   /**
    * @return the mdMessages
    */
   public synchronized List<MDSequencedMessage> getMdMessages()
   {
      return _mdMessages;
   }

   /**
    * @param mdMessages the mdMessages to set
    */
   public synchronized void setMdMessages(List<MDSequencedMessage> mdMessages)
   {
      _mdMessages = mdMessages;
   }
   
   public short getLength()
   {
	   return((short)(BLOCK_HEADER_LENGTH + BlockBodyLength));
   }

   /**
    * @return the multicastStats
    */
   public MulticastStats getMulticastStats()
   {
      return _multicastStats;
   }

   /**
    * @param multicastStats the multicastStats to set
    */
   public void setMulticastStats(MulticastStats multicastStats)
   {
      _multicastStats = multicastStats;
   }


}
