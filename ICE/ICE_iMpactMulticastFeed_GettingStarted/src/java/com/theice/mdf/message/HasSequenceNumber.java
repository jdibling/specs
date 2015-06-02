/*
 * This material may not be reproduced or redistributed in whole
 * or in part without the express prior written consent of
 * IntercontinentalExchange, Inc.
 *
 * Copyright IntercontinentalExchange, Inc. 2006, All Rights Reserved.
 */
package com.theice.mdf.message;

/**
 * @author qwang
 * @version     %I%, %G%
 * Created: Sep 28, 2007 11:12:44 PM
 *
 *
 */
public interface HasSequenceNumber
{
   public void setSequenceNumber(int marketSequenceNumber);
   public int getSequenceNumber();
}
