package com.theice.mdf.client.gui.panel;

import javax.swing.AbstractListModel;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class TradeHistoryListModel extends AbstractListModel 
{
	String[] _tradeHistory=new String[0];
	
	/**
	 * Returns the length of the list.
	 * 
	 * @return the length of the list
	 */
	public int getSize()
	{
		return(_tradeHistory.length);
	}
	
	public synchronized void setHistory(String[] history)
	{
		_tradeHistory=history;
		
		if(_tradeHistory.length>0)
		{
			fireIntervalAdded(this, 0, _tradeHistory.length-1);
		}
		else
		{
			fireIntervalAdded(this, 0, 0);
		}
	}

	/**
	 * Returns the value at the specified index.
	 * 
	 * @param index, the requested index
	 * @return the value at <code>index</code>
	 */
	public synchronized Object getElementAt(int index)
	{
		int size=_tradeHistory.length;
		
		if(index<0)
		{
			return(null);
		}
		
		if(index>=size)
		{
			return(null);
		}
		
		return(_tradeHistory[index]);
	}
	
//	public synchronized void add(String element)
//	{
//		int size=_tradeHistory.size();
//		((LinkedList) _tradeHistory).addLast(element);
//		fireIntervalAdded(this, size, size);
//	}
//
//	public void synchronized addFirst(String element)
//	{
//		((LinkedList) _tradeHistory).addFirst(element);
//		fireIntervalAdded(this, 0, 0);
//	}
//
//	public void removeLast()
//	{
//		int size=_tradeHistory.size();
//		
//		if(size<=0)
//		{
//			return;
//		}
//		
//		((LinkedList) _tradeHistory).removeLast();
//		fireIntervalRemoved(this, size-1, size-1);
//	}
//
//	public synchronized void removeAll()
//	{
//		int size=_tradeHistory.size();
//		
//		if(size<=0)
//		{
//			return;
//		}
//		
//		((LinkedList) _tradeHistory).clear();
//		fireIntervalRemoved(this, 0, size-1);
//	}
	
}

