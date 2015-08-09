package usbong.android.questionloader;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * Operations on char primitives and Character objects.
 *
 * This class tries to handle <code>null</code> input gracefully.
 * An exception will not be thrown for a <code>null</code> input.
 * Each method documents its behaviour in more detail.
 * 
 * @author Stephen Colebourne
 * @since 2.1
 * @version $Id: CharUtils.java 437554 2006-08-28 06:21:41Z bayard $
 */
public class ParseToUnicode {

  //--------------------------------------------------------------------------
  /**
   * Converts the string to the unicode format '\u0020'.
   * 
   * This format is the Java source code format.
   *
   * <pre>
   *   CharUtils.unicodeEscaped(' ') = "\u0020"
   *   CharUtils.unicodeEscaped('A') = "\u0041"
   * </pre>
   * 
   * @param ch  the character to convert
   * @return the escaped unicode string
   */
  public static String unicodeEscaped(char ch) {
      if (ch < 0x10) {
          return "\\u000" + Integer.toHexString(ch);
      } else if (ch < 0x100) {
          return "\\u00" + Integer.toHexString(ch);
      } else if (ch < 0x1000) {
          return "\\u0" + Integer.toHexString(ch);
      }
      return "\\u" + Integer.toHexString(ch);
  }
  
  /**
   * Converts the string to the unicode format '\u0020'.
   * 
   * This format is the Java source code format.
   * 
   * If <code>null</code> is passed in, <code>null</code> will be returned.
   *
   * <pre>
   *   CharUtils.unicodeEscaped(null) = null
   *   CharUtils.unicodeEscaped(' ')  = "\u0020"
   *   CharUtils.unicodeEscaped('A')  = "\u0041"
   * </pre>
   * 
   * @param ch  the character to convert, may be null
   * @return the escaped unicode string, null if null input
   */
  public static String unicodeEscaped(Character ch) {
      if (ch == null) {
          return null;
      }
      return unicodeEscaped(ch.charValue());
  }
  


}
