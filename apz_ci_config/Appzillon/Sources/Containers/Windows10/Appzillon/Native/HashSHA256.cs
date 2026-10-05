using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Text;
using System.Threading.Tasks;
using Windows.Security.Cryptography;
using Windows.Security.Cryptography.Core;
using Windows.Storage.Streams;
using Newtonsoft.Json.Linq;
using Appzillon.Constants;
using Appzillon.Plugins;
using Appzillon.CommonClass;
using System.Diagnostics;
using System.Security.Cryptography;

namespace Appzillon.Native
{
        #region 
    // Author:Anand Kumar
    /// <summary>
    /// Used for dataintegrity check  
    /// </summary>
    public sealed class HashSHA256
    {
        #region Singleton Pattern
        private static HashSHA256 instance;
        private HashSHA256()
        {
        }
        public static HashSHA256 Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new HashSHA256();
                }
                return instance;
            }
        }
        #endregion
        internal  string Getpayloadafterhasing(string req, string cnonce)
        {
            string salt = AppzillonGetAppSecTokensResponse.serverNonce + AppProperty.decryptredserverToken;
            string hashedCNONCE = GethashPayload(cnonce, salt);
            // Debug.WriteLine("HashedCNOnce", hashedCNONCE);
            byte[] data = new byte[200];
            data = Encoding.UTF8.GetBytes(req);
            string base64Hashedtext = Convert.ToBase64String(data);
            Debug.WriteLine("base64text" + base64Hashedtext);


            string haspayload = GethashPayload(base64Hashedtext, hashedCNONCE);

            return haspayload;
        }

        private static string GethashPayload(string texttohash, string hasedCNONCE)
        {
            // Use ShaQOP for data Integrity
            string haspin = HashSHA256.sha256QOP(texttohash + hasedCNONCE);
            return haspin;
        }


        private static char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };
        private static String sha256QOP(String data)
        {
            byte[] ByteData = new byte[200];

            ByteData = Encoding.ASCII.GetBytes(data);

            SHA256 sha = SHA256.Create();

            //Hash değerini hesaplayalım.
            byte[] HashData = sha.ComputeHash(ByteData);

            //convert byte array to hex format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < HashData.Length; i++)
            {
                int c = ((HashData[i]) >> 4) & 0xf;
                sb.Append(hex[c]);
                c = (HashData[i] & 0xf);
                sb.Append(hex[c]);
            }

            return sb.ToString();
        }

        #endregion
        public static void GetOTP(JObject j)
        {
            try
            {
                string userID = (string)j[JsonKey.USER_ID];
                string pin = (string)j["pwd"];
                string serverToken = j["serverToken"].GetString();
                string udid = Utils.GetDeviceID();
                string salt = userID + serverToken;
                if (serverToken == null)
                {
                    salt = j["salt"].GetString();
                }
                string hashPin = HashSHA256.sha256(pin + salt);
                var datetime = j["date"].ToString();
                string hash = HashSHA256.hashValue("", "", "", userID, hashPin, datetime);
                JObject json = new JObject();
                json["text"] = hash;
                Response.Success(j[JsonKey.ID].ToString(), json);
            }
            catch (Exception ex)
            {
                Log.Error("Error While Genrating OTP\n" + ex.Message);
                Response.Fail(j[JsonKey.ID].ToString(), "");
            }
        }

        public static string hashValue(string pimie, string pimsi, string puid, string puname, string pInpin, string pDate)
        {
            string uname = puname;
            string inpin = pInpin;
            string abc;
            string imie = pimie;
            string imsi = pimsi;
            string inconcatstr;
            string dtstr = pDate.ToString();
            string day = dtstr.Substring(0, 3);
            string hr = dtstr.Substring(16, 2);
            string min = dtstr.Substring(19, 2);
            string sec = dtstr.Substring(22, 2);
            string yr = dtstr.Substring(13, 2);
            string dd = dtstr.Substring(5, 2);
            string mm = dtstr.Substring(8, 2);

            inconcatstr = hr + min + day + yr + dd + mm + sec + uname + imie + imsi;

            try
            {
                // Sha-256 value of concatenated String
                // Sha-256 value of 4-digit pin repeated twice to get 8-digits
                // XOR Sha-256 values of concatenated String and PIN
                string hshxor = xorHex(HashSHA256.sha256(inconcatstr).ToString(), HashSHA256.sha256(inpin).ToString());

                // First Byte of XOR'ed Sha-256 value of concatenated String and PIN
                string fbyte = hshxor.Substring(0, 16);


                // Last Byte of XOR'ed Sha-256 value of concatenated String and PIN

                string lbyte = hshxor.Substring(48);
                // XOR First And Last Bytes
                string fblbxor = xorHex(fbyte, lbyte);

                // XOR Above with Pin Hash
                string hshxor2 = xorHex(fblbxor, HashSHA256.sha256(inpin).ToString());

                // Round 2 -> First Byte of XOR'ed Sha-256 value of concatenated String and PIN
                string fbyte2 = hshxor2.Substring(0, 8);
                // Round 2 -> Last Byte of XOR'ed Sha-256 value of concatenated String and PIN


                string lbyte2 = hshxor2.Substring(8);
                // Round 2 -> XOR First And Last Bytes
                string fblbxor2 = xorHex(fbyte2, lbyte2);


                string finalstr = xorHex(fblbxor2, HashSHA256.sha256(inpin).ToString());
                abc = Convert.ToBase64String(Encoding.UTF8.GetBytes(finalstr));

            }
            catch (Exception e)
            {
                return e.Message;
            }
            return abc;
        }

        // XOR Truth Table from two Hex Strings 	   
        public static string xorHex(String a, String b)
        {
            char[] chars = new char[a.Length];
            for (int i = 0; i < chars.Length; i++)
            {

                chars[i] = toHex(fromHex(a.Substring(i, 1)[0]) ^ fromHex(b.Substring(i, 1)[0]));

            }
            return new String(chars);
        }


        private static int fromHex(char c)
        {
            try
            {

                if (c >= '0' && c <= '9')
                {
                    return c - '0';
                }
                if (c >= 'A' && c <= 'F')
                {
                    return c - 'A' + 10;
                }
                if (c >= 'a' && c <= 'f')
                {
                    return c - 'a' + 10;
                }
            }
            catch (Exception e)
            {
                throw e;
            }
            return 'A'; // Only for testing 

        }

        //char wise int to Hex
        private static char toHex(int nybble)
        {
            try
            {
                if (nybble < 0 || nybble > 15)
                {

                }
            }
            catch (Exception e)
            {
                throw e;
            }
            return "0123456789ABCDEF".Substring(nybble, 1)[0];
        }


        public static String sha256(String input)
        {

            byte[] value = new byte[200];

            value = Encoding.UTF8.GetBytes(input);
            Debug.WriteLine("bytevalue" + value);

            //value= Encoding.Unicode.GetBytes(input);
            String strAlgName = HashAlgorithmNames.Sha256;



            //String strMsg = "This is a message to be hashed.";
            String result = HashMsg(strAlgName, value);
            //result = Encoding.UTF8.GetBytes(strEncodedHash);

            return result;

        }





        public static string HashMsg(String strAlgName, [ReadOnlyArray()] byte[] strMsg)
        {
            // Convert the message string to binary data.
            IBuffer buffUtf8Msg = CryptographicBuffer.CreateFromByteArray(strMsg);

            // Create a HashAlgorithmProvider object.
            HashAlgorithmProvider objAlgProv = HashAlgorithmProvider.OpenAlgorithm(strAlgName);

            // Demonstrate how to retrieve the name of the hashing algorithm.
            string strAlgNameUsed = objAlgProv.AlgorithmName;

            // Hash the message.
            IBuffer buffHash = objAlgProv.HashData(buffUtf8Msg);


            // Verify that the hash length equals the length specified for the algorithm.
            if (buffHash.Length != objAlgProv.HashLength)
            {
                throw new Exception("There was an error creating the hash");
            }

            // Convert the hash to a string (for display).
            string strHashBase64 = CryptographicBuffer.EncodeToBase64String(buffHash);

            string abcd;

            abcd = CryptographicBuffer.EncodeToHexString(buffHash);
            // Return the encoded string
            return abcd;
        }



    }
}
