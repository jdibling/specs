package com.theice.mdf.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Tunneling Message Block servers as a wrapper to a MulticastMessageBlock
 * The header information includes the Group and Port number that the server is currently
 * multicasting the blocks on
 * 
 * @author Adam Athimuthu  
 */
public class TunnelingMessageBlock
{
   public final static short BLOCK_HEADER_LENGTH=2+30+4;

   private byte[] _serializedBytes;

   public short BlockBodyLength;
   public char GroupAddress[]=new char[30];
   public int Port;
   
   private MulticastMessageBlock _multicastMessageBlock;

   /**
    * This method is not thread safe, however, once the
    * <code>_serializedBytes</code> is created other threads will be able to
    * see it since this object is going to be published to a Blocking Queue
    * 
    * @return this object in serialized form
    */
   public byte[] serialize()
   {
      if (_serializedBytes == null)
      {
         byte[] multicastMessageBlockByteArray = _multicastMessageBlock.serialize();
         serializeWithGivenMessageBlockByteArray(multicastMessageBlockByteArray);
      }

      return _serializedBytes;
   }

   public void serializeWithGivenMessageBlockByteArray(byte[] multicastMessageBlockByteArray)
   {
    	BlockBodyLength = (short) multicastMessageBlockByteArray.length;
      ByteBuffer byteBuffer = ByteBuffer.allocate(BLOCK_HEADER_LENGTH + BlockBodyLength);

      byteBuffer.putShort(BlockBodyLength);

      for(int i=0; i<GroupAddress.length;i++)
      {
         byteBuffer.put( (byte)GroupAddress[i]);
      }

      byteBuffer.putInt(Port);

      byteBuffer.put(multicastMessageBlockByteArray);

      _serializedBytes = byteBuffer.array();
   }
  
   public void populateBody(ByteBuffer byteBuffer) throws IOException, InvalidRequestException,
         UnknownMessageException
   {
      _multicastMessageBlock = new MulticastMessageBlock();
      _multicastMessageBlock.deserialize(byteBuffer);
   }   

   /**
    * @param allContentBytes
    */
   public void populateHeaderFields(ByteBuffer allContentBytes)
   {
	   BlockBodyLength=allContentBytes.getShort();
	   
	   for(int i=0;i<GroupAddress.length;i++)
	   {
		   GroupAddress[i]=(char) allContentBytes.get();
	   }
	      
	   Port = allContentBytes.getInt();
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer("");

      buffer.append("BodyLength=" + this.BlockBodyLength);
      buffer.append("|GroupNumber=" + MessageUtil.toString(GroupAddress));
      buffer.append("|Port=" + this.Port);
      buffer.append("|BlockBodyLength=" + this.BlockBodyLength);
      buffer.append("\n");

      if (_multicastMessageBlock!=null)
      {
         buffer.append("\t" + _multicastMessageBlock.toString() + "\n");
      }

      return (buffer.toString());
   }
   

   /**
    * @return the MulticastMessageBlock
    */
   public synchronized MulticastMessageBlock getMulticastMessageBlock()
   {
      return _multicastMessageBlock;
   }

   /**
    * @param multicastMessageBlock to set
    */
   public synchronized void setMulticastMessageBlock(MulticastMessageBlock multicastMessageBlock)
   {
	   _multicastMessageBlock = multicastMessageBlock;
   }

   /**
    * @return the serializedBytes
    */
   public byte[] getSerializedBytes()
   {
      return _serializedBytes;
   }

   /**
    * @param serializedBytes the serializedBytes to set
    */
   public void setSerializedBytes(byte[] serializedBytes)
   {
      _serializedBytes = serializedBytes;
   }

}

