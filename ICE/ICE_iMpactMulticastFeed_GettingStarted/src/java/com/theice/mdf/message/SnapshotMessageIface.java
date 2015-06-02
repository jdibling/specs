package com.theice.mdf.message;

/**
 * Snapshot message interface
 * 
 * A snapshot message always has a sequence number and contains the number of book entries
 * Also, all snapshot messages will have a LastMessageSequence number representing the instance at which
 * 		the snapshot was taken. This number is a link to the Live/Incremental channel
 * 
 * @author Adam Athimuthu
 */
public interface SnapshotMessageIface extends HasSequenceNumber
{
	public int getNumOfBookEntries();
	public int getLastMessageSequenceNumber();
}
