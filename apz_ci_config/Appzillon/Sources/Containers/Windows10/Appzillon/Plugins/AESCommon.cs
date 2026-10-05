using Appzillon.Native;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Plugins
{
    /// <summary>
    /// Author Anand Kumar
    /// Common class for AES IV, Salt and Padding
    /// </summary>
   public class AESCommon

    {
        #region Singleton Pattern
        private static AESCommon instance;
        public AESCommon()
        {
        }
        public static AESCommon Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new AESCommon();
                }
                return instance;
            }
        }
        #endregion

        #region Algorithum for RandomNumber Genration
        internal long GetRandomNumber()
        {
            Random rnd = new Random();

            byte[] buf = new byte[8];
            rnd.NextBytes(buf);
            long longRand = BitConverter.ToInt64(buf, 0);

            long result = (Math.Abs(longRand % (2000000000000000 - 1000000000000000)) + 1000000000000000);

            long random_seed = (long)rnd.Next(1000, 5000);
            random_seed = random_seed * result + rnd.Next(1000, 5000);

            long randomnumber = ((long)(random_seed / 655) % 10000000000000001);
            Debug.WriteLine("randomnumber" + randomnumber);
            Debug.WriteLine("KeyLength" + randomnumber.ToString().Length);
            return randomnumber;
        }
        #endregion

        #region  Methods to use IV
        /// <summary>
        /// genrating of vector 
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        internal byte[] getIV(string key)
        {
            byte[] iv = new byte[16];
            Arrays.Fill(iv, (byte)0);
            string reversestring = ReverseString(key);
            byte[] keyBytes = new byte[16];                 
            keyBytes = Encoding.UTF8.GetBytes(reversestring);                     
            byte[] rawIV = new byte[keyBytes.Length];
            for (int i = 0; i < keyBytes.Length; i++)
            {
                rawIV[i] = (byte)(keyBytes[i] >> 1);
            }
            for (int i = 0; i < iv.Length; i++)
            {
                iv[i] = rawIV[i];
            }
            return iv;
        }
        
        internal static class Arrays
        {
         
            public static void Fill<T>(T[] array, T value)
            {
                for (int i = 0; i < array.Length; i++)
                {
                    array[i] = value;
                }
            }

          
        }
        public static string ReverseString(string str)

        {

            char[] chars = str.ToCharArray();

            for (int i = 0, j = str.Length - 1; i < j; i++, j--)

            {

                char c = chars[i];

                chars[i] = chars[j];

                chars[j] = c;

            }

            return new string(chars);

        }
        #endregion

        #region Algorithm to genrate Salt
        internal string getSalt(String key)
        {
            String originalString = key;

            char[] c = originalString.ToCharArray();

            // Replace with a "swap" function, if desired:
            char temp = c[0];
            c[0] = c[1];
            c[1] = temp;

            temp = c[c.Length - 1];
            c[c.Length - 1] = c[c.Length - 2];
            c[c.Length - 2] = temp;
            string swappedString = new string(c);
            //string halfstring
            //byte[] salt = Encoding.UTF8.GetBytes(swappedString);
            return swappedString;
        }
        #endregion

        #region Padding Algorithum
        private static string paddingMask = "$$$$$$$$$$$$$$$$";
        internal  string GetPaddingKey(string key)
        {
            if (key.Length <= 16)
            {
                key += paddingMask.Substring(0, 16 - key.Length);
            }
            else
            {
                key = key.Substring(0, 16);
            }

            return key;
        }
        #endregion

        #region Request Encryption and Decryption Method
        internal async Task<string> GetAESEncryptedValue(string request, string key)
        {
            string encryptedvalue = AesBase64Wrapper.Instance.EncryptAndEncode(request, key);
            return encryptedvalue;
        }
        internal  async Task<string> GetAESDecryptedValue(string response, string key)
        {
            string decryptedvalue = AesBase64Wrapper.Instance.DecodeAndDecrypt(response, key);
            return decryptedvalue;
        }
        #endregion
    }
}
