using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Security.Cryptography;
using Windows.Security.Cryptography.Core;
using Windows.Storage.Streams;

namespace Appzillon.Plugins
{
    class Crypto
    {
#region Singleton Pattern
        private static Crypto instance;
        private Crypto()
        {
        }
        public static Crypto Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Crypto();
                }
                return instance;
            }
        }
#endregion

        public void Encryptstring(JObject json)
        {
            var id = json[JsonKey.ID].ToString();
            var stringObject = json[JsonKey.STRING_TO_ENCRYPT].ToString();
            var key = json[JsonKey.KEY].ToString();
            //Validations

            //Set Key to 16 characters
            key = key.PadRight(16, '$');
            if (key.Length > 16)
            {
                key = key.Substring(0, 16);
            }
            try
            {
                string encryptedData = EncryptData(stringObject, key);
                JObject encryptedString = new JObject();
                encryptedString[JsonKey.ID] = id;
                encryptedString[JsonKey.TEXT] = encryptedData;
                Response.Success(json[JsonKey.ID].ToString(),encryptedString);
            }
            catch (Exception)
            {
                Response.Fail(id, stringObject);
            }
        }

        private string EncryptData(string dataToEncrypt, string password)
        {
            string key = password;
            char[] reverseKey = password.ToCharArray();
            Array.Reverse(reverseKey);
            byte[] bytes = new byte[] { };
            bytes = Encoding.ASCII.GetBytes(reverseKey);

            for (int i = 0; i < bytes.Length; i++)
            {
                bytes[i] = (byte)(bytes[i] >> 1);
            }

            string cipherAlgNameString = SymmetricAlgorithmNames.AesCbcPkcs7;
            IBuffer derivedKeyBuffer = sdk_DeriveKey(key);
            IBuffer reverseIvBuffer = CryptographicBuffer.CreateFromByteArray(bytes);
            IBuffer encryptedDataBuffer = EncryptedDataBuffer(derivedKeyBuffer, dataToEncrypt, cipherAlgNameString, reverseIvBuffer);
            string encryptedDataString = CryptographicBuffer.EncodeToBase64String(encryptedDataBuffer);
            return encryptedDataString;
        }

        // Encrypt a data buffer. 
        public static IBuffer EncryptedDataBuffer(IBuffer derivedKeyBuffer, string stringToEncrypt, string algNameString, IBuffer ivBuffer)
        {
            // Convert the input string, stringToEncrypt, to binary. 
            IBuffer inputDataBuffer = CryptographicBuffer.ConvertStringToBinary(stringToEncrypt, BinaryStringEncoding.Utf8);
            SymmetricKeyAlgorithmProvider algorithmProvider = SymmetricKeyAlgorithmProvider.OpenAlgorithm(algNameString);
            // Create a symmetric key.
            CryptographicKey symmetricKey = algorithmProvider.CreateSymmetricKey(derivedKeyBuffer);
            // Encrypt the input string. 
            IBuffer encryptedBuffer = CryptographicEngine.Encrypt(symmetricKey, inputDataBuffer, ivBuffer);
            return encryptedBuffer;
        }

        public static IBuffer sdk_DeriveKey(string keyParameter)
        {
            string cipherAlgNameString = SymmetricAlgorithmNames.AesCbcPkcs7;
            uint algBlockSizeInBytes = 16;
            string kdfSaltString = CustomKey(keyParameter);
            string kdfAlgNameString = KeyDerivationAlgorithmNames.Pbkdf2Sha1;
            uint kdfIterationCount = 2;
            IBuffer secret = CryptographicBuffer.ConvertStringToBinary(keyParameter, BinaryStringEncoding.Utf8);

            // Initialize the key derivation function (PBKDF2) parameters. 
            IBuffer salt = CryptographicBuffer.ConvertStringToBinary(kdfSaltString, BinaryStringEncoding.Utf8);
            KeyDerivationParameters pbkdf2Params = KeyDerivationParameters.BuildForPbkdf2(salt, kdfIterationCount);
            // Open the PBKDF2_SHA256 algorithm provider.
            KeyDerivationAlgorithmProvider algorithmProvider = KeyDerivationAlgorithmProvider.OpenAlgorithm(kdfAlgNameString);
            // Create a secret key.
            CryptographicKey secretKey = algorithmProvider.CreateKey(secret);
            IBuffer derivedKeyBuffer = CryptographicEngine.DeriveKeyMaterial(secretKey, pbkdf2Params, algBlockSizeInBytes);
            return derivedKeyBuffer;
        }

        public static string CustomKey(string key)
        {
            var firstLetter = key.Substring(0, 1);
            var lastLetter = key.Substring(key.Length - 1, 1);
            var secondLetter = key.Substring(1, 1);
            var secondLastLetter = key.Substring(key.Length - 2, 1);
            string custKey = secondLetter + firstLetter + key.Substring(2, key.Length - 4) + lastLetter + secondLastLetter;
            return custKey;
        }

        public void Decryptstring(JObject json)
        {
           
            var id = json[JsonKey.ID].ToString();
            var encryptedString = json[JsonKey.STRING_TO_DECRYPT].ToString();
            var key = json[JsonKey.KEY].ToString();
            key = key.PadRight(16, '$');
            if (key.Length > 16)
            {
                key = key.Substring(0, 16);
            }

            try
            {
                string cipherAlgNameString = SymmetricAlgorithmNames.AesCbcPkcs7;
                string decryptedData = decrypt(encryptedString, key);
                JObject decryptedString = new JObject();
                decryptedString[JsonKey.ID] = id;
                decryptedString[JsonKey.TEXT] = decryptedData;
                Response.Success(id,decryptedString);
            }

            catch (Exception)
            {
                Response.Fail(id,encryptedString);
            }
        }

        public string decrypt(string encryptedString, string password)
        {
            string key = password;
            char[] reverseKey = password.ToCharArray();
            Array.Reverse(reverseKey);
            byte[] bytes = new byte[] { };
            bytes = Encoding.ASCII.GetBytes(reverseKey);
            string cipherAlgNameString = SymmetricAlgorithmNames.AesCbcPkcs7;

            for (int i = 0; i < bytes.Length; i++)
            {
                bytes[i] = (byte)(bytes[i] >> 1);
            }

            // Derive a key
            IBuffer derivedKeyBuffer = sdk_DeriveKey(key);
            // Convert the initialization vector string to binary.
            IBuffer reverseIvBuffer = CryptographicBuffer.CreateFromByteArray(bytes);
            // Decrypt the data.
            IBuffer decryptedDataBuffer = DecryptedDataBuffer(derivedKeyBuffer, encryptedString, cipherAlgNameString, reverseIvBuffer);
            string decryptedDataString = CryptographicBuffer.ConvertBinaryToString(BinaryStringEncoding.Utf8, decryptedDataBuffer);
            return decryptedDataString;
        }

        public static IBuffer DecryptedDataBuffer(IBuffer derivedKeyBuffer, string stringToDecrypt, string algNameString, IBuffer ivBuffer)
        {
            // Convert the input string, stringToDecrypt, to binary. 
            IBuffer inputDataBuffer = CryptographicBuffer.DecodeFromBase64String(stringToDecrypt);
            // Open the algorithm provider specified by the algNameString input parameter. 
            SymmetricKeyAlgorithmProvider algorithmProvider = Windows.Security.Cryptography.Core.SymmetricKeyAlgorithmProvider.OpenAlgorithm(algNameString);
            // Create a symmetric key. 
            CryptographicKey symmetricKey = algorithmProvider.CreateSymmetricKey(derivedKeyBuffer);
            // Decrypt the input string.
            IBuffer decryptedBuffer = CryptographicEngine.Decrypt(symmetricKey, inputDataBuffer, ivBuffer);
            return decryptedBuffer;
        }
    }
}
