using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Foundation;
using Windows.Security.Cryptography;
using Windows.Security.Cryptography.Core;
using Windows.Storage;
using Windows.Storage.Streams;

namespace Appzillon.Plugins
{
    class CryptoFile
    {
#region Singleton Pattern
        private static CryptoFile instance;
        private CryptoFile()
        {
        }
        public static CryptoFile Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new CryptoFile();
                }
                return instance;
            }
        }
#endregion

        private string id = "";
        public async void Encryptfile(JObject json)
        {
            id = json[JsonKey.ID].ToString();
            string key = json[JsonKey.KEY].ToString();
            string srcFilePath = json[JsonKey.SRC_FILEPATH].ToString();
            string destFilePath = json[JsonKey.DEST_FILEPATH].ToString();

            key = key.PadRight(16, '$');
            if (key.Length > 16)
            {
                key = key.Substring(0, 16);
            }
            srcFilePath = Filepathresolver.PathResolver(srcFilePath);
            try
            {
                destFilePath = destFilePath.Replace('/', '\\');
            }
            catch (Exception)
            { }
            if (destFilePath[0] == '/')
            {               
                destFilePath = @"appzillonapps\apps\" + MainPage.CurrentAppId + destFilePath;
            }
            else
            {
                destFilePath = @"appzillonapps\apps\" + MainPage.CurrentAppId +@"\"+ destFilePath;
            }
            try
            {
                StorageFolder localFolder = ApplicationData.Current.LocalFolder;
                StorageFile file = await StorageFile.GetFileFromPathAsync(srcFilePath);
                IRandomAccessStream stream = await file.OpenAsync(FileAccessMode.Read);

                IInputStream inputStream = stream.GetInputStreamAt(0);
                ulong size = stream.Size;
                //Read data from the input stream.
                DataReader reader = new DataReader(inputStream);
                uint loadReader = await reader.LoadAsync((uint)size);
                IBuffer binary = reader.ReadBuffer((uint)size);

                string encryptedData = EncryptData(binary, key);

                StorageFile encryptedFile = await localFolder.CreateFileAsync(destFilePath, CreationCollisionOption.ReplaceExisting);
                IAsyncAction fileWrite = FileIO.WriteTextAsync(encryptedFile, encryptedData);

                JObject success = new JObject();
                success[JsonKey.SUCCESS_MESSAGE] = "File Encrypted Successfully";
                success[JsonKey.FILE_PATH] = encryptedFile.Path;
                Response.Success(id,success);
                Log.Debug("File Encryption Success");
            }
            catch (Exception e)
            {
                Response.Fail(id,ErrorCode.FILE_ENCRYPTION_FAIL);
                Log.Error(e.Message);
            }
        }

        private string EncryptData(IBuffer binary, string key)
        {
            string password = key;
            char[] reverseKey = password.ToCharArray();
            Array.Reverse(reverseKey);
            byte[] bytes = new byte[] { };
            bytes = Encoding.ASCII.GetBytes(reverseKey);

            for (int i = 0; i < bytes.Length; i++)
            {
                bytes[i] = (byte)(bytes[i] >> 1);
            }

            string cipherAlgNameString = SymmetricAlgorithmNames.AesCbcPkcs7;
            IBuffer derivedKeyBuffer = DeriveKey(key);
            IBuffer reverseIvBuffer = CryptographicBuffer.CreateFromByteArray(bytes);
            IBuffer encryptedDataBuffer = EncryptedDataBuffer(derivedKeyBuffer, binary, cipherAlgNameString, reverseIvBuffer);
            string encryptedDataString = CryptographicBuffer.EncodeToBase64String(encryptedDataBuffer);
            return encryptedDataString;
        }

        private IBuffer DeriveKey(string keyParameter)
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

        private string CustomKey(string key)
        {
            var firstLetter = key.Substring(0, 1);
            var lastLetter = key.Substring(key.Length - 1, 1);
            var secondLetter = key.Substring(1, 1);
            var secondLastLetter = key.Substring(key.Length - 2, 1);
            string custKey = secondLetter + firstLetter + key.Substring(2, key.Length - 4) + lastLetter + secondLastLetter;
            return custKey;
        }

        // Encrypt a data buffer. 
        private IBuffer EncryptedDataBuffer(IBuffer derivedKeyBuffer, IBuffer stringToEncrypt, string algNameString, IBuffer ivBuffer)
        {
            // Convert the input string, stringToEncrypt, to binary. 
            //IBuffer inputDataBuffer = CryptographicBuffer.ConvertStringToBinary(stringToEncrypt, BinaryStringEncoding.Utf8);
            SymmetricKeyAlgorithmProvider algorithmProvider = SymmetricKeyAlgorithmProvider.OpenAlgorithm(algNameString);
            // Create a symmetric key.
            CryptographicKey symmetricKey = algorithmProvider.CreateSymmetricKey(derivedKeyBuffer);
            // Encrypt the input string. 
            IBuffer encryptedBuffer = CryptographicEngine.Encrypt(symmetricKey, stringToEncrypt, ivBuffer);
            return encryptedBuffer;
        }

        public async void DecryptFile(JObject json)
        {
            string key = json[JsonKey.KEY].ToString();
            string srcFilePath = json[JsonKey.SRC_FILEPATH].ToString();
            string destFilePath = json[JsonKey.DEST_FILEPATH].ToString();

            key = key.PadRight(16, '$');
            if (key.Length > 16)
            {
                key = key.Substring(0, 16);
            }
            srcFilePath = Filepathresolver.PathResolver(srcFilePath);
            try
            {
                destFilePath = destFilePath.Replace("/", @"\");
            }
            catch (Exception)
            { }
            if (destFilePath[0] == '/')
            {
              
                destFilePath = @"appzillonapps\apps\" + MainPage.CurrentAppId + destFilePath;
            }
            else
            {
                destFilePath = @"appzillonapps\apps\" + MainPage.CurrentAppId + @"\" + destFilePath;
            }
            try
            {
                StorageFolder localFolder = ApplicationData.Current.LocalFolder;
                StorageFile file = await StorageFile.GetFileFromPathAsync(srcFilePath);
                string base64String = await FileIO.ReadTextAsync(file);
                IBuffer decryptedData = Decrypt(base64String, key);

                StorageFile decryptedFile = await localFolder.CreateFileAsync(destFilePath, CreationCollisionOption.ReplaceExisting);
                await FileIO.WriteBufferAsync(decryptedFile, decryptedData);

                JObject success = new JObject();
                success[JsonKey.FILE_PATH] = decryptedFile.Path;
                success[JsonKey.SUCCESS_MESSAGE] = "File Decrypted Successfully";
                Response.Success(json[JsonKey.ID].GetString(), success);
                Log.Debug("File Encryption Success");
            }
            catch (Exception e)
            {
                Response.Fail(json[JsonKey.ID].GetString(), ErrorCode.FILE_DECRYPTION_FAIL);
                Log.Error("File Encryption Success");

            }
        }

        private IBuffer Decrypt(string base64String, string key)
        {
            string password = key;
            char[] reverseKey = password.ToCharArray();
            Array.Reverse(reverseKey);
            byte[] bytes = new byte[] { };
            bytes = Encoding.ASCII.GetBytes(reverseKey);

            for (int i = 0; i < bytes.Length; i++)
            {
                bytes[i] = (byte)(bytes[i] >> 1);
            }

            string cipherAlgNameString = SymmetricAlgorithmNames.AesCbcPkcs7;

            IBuffer derivedKeyBuffer = DeriveKey(key);
            IBuffer reverseIvBuffer = CryptographicBuffer.CreateFromByteArray(bytes);
            IBuffer decryptedDataBuffer = DecryptDataBuffer(derivedKeyBuffer, base64String, cipherAlgNameString, reverseIvBuffer);
            //string decryptedDataString = CryptographicBuffer.ConvertBinaryToString(BinaryStringEncoding.Utf8, decryptedDataBuffer);
            return decryptedDataBuffer;
        }

        private IBuffer DecryptDataBuffer(IBuffer derivedKeyBuffer, string stringToDecrypt, string algNameString, IBuffer ivBuffer)
        {
            // Convert the input string, stringToDecrypt, to binary. 
            IBuffer inputDataBuffer = CryptographicBuffer.DecodeFromBase64String(stringToDecrypt);
            // Open the algorithm provider specified by the algNameString input parameter. 
            SymmetricKeyAlgorithmProvider algorithmProvider = SymmetricKeyAlgorithmProvider.OpenAlgorithm(algNameString);
            // Create a symmetric key. 
            CryptographicKey symmetricKey = algorithmProvider.CreateSymmetricKey(derivedKeyBuffer);
            // Decrypt the input string.
            IBuffer decryptedBuffer = CryptographicEngine.Decrypt(symmetricKey, inputDataBuffer, ivBuffer);
            return decryptedBuffer;
        }
    }
}
