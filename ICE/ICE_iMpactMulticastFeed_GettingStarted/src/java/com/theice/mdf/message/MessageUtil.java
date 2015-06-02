package com.theice.mdf.message;


/**
 * <code>MessageUtil</code> provides message utility related methods.
 *
 * @author David Chen
 * @version %I%, %G%
 * @since 12/12/2006
 */


public class MessageUtil
{
   /**
    * Convert the array of characters in market data message to string.
    * According to the spec, characters are null padded
    *
    * @param rawChars
    * @return
    */

   public static String toString(char[] rawChars)
   {
      int length = 0;
      for (int i=0; i < rawChars.length; i++)
      {
         if (rawChars[i]=='\0')
         {
            break;
         }

         length++;
      }

      return String.valueOf(rawChars, 0, length);
   }

   /**
    * Create an array of characters based on the given string. According to
    * the message spec, string is null padded up to its length of the field.
    *
    * @param value
    * @param charArrayLength
    * @return
    */
   public static char[] toRawChars(String value, int charArrayLength)
   {
      if (value==null)
      {
         value = "";
      }

      char[] chars = null;
      char[] origChars = value.toCharArray();

      if (origChars.length==charArrayLength)
      {
         // string passed in has the same length as expected,
         // just return the array
         chars = origChars;
      }
      else
      {
         // need to create a new array with the expected length
         // and copy characters from the original array to it
         // since alpha field is padded with null character according
         // the spec, by default an array is initialized with null
         // character, so we don't need the extra padding
         chars = new char[charArrayLength];
         for (int i=0; i<charArrayLength && i<origChars.length; i++)
         {
            chars[i] = origChars[i];
         }
      }

      return chars;
   }

   /**
    * Convert a float value to long using the decimal length
    *
    * @param fVal
    * @param decimalLength
    * @return
    */
   public static long toLong(float fVal, int decimalLength)
   {
      return Math.round(fVal * Math.pow(10, decimalLength));
   }
   
   public static long toLong(double dVal, int decimalLength)
   {
      return Math.round(dVal * Math.pow(10, decimalLength));
   }


}
