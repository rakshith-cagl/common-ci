using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Encodings;
using Org.BouncyCastle.Crypto.Engines;
using Org.BouncyCastle.OpenSsl;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Plugins
{
    /// <summary>
    ///  RSA Decrypt using Public key
    ///  Author : Anand Kumar
    /// </summary>
    public class RSADecrypyt
    {
        #region Singleton Pattern
        private static RSADecrypyt instance;
        private RSADecrypyt()
        {
        }
        public static RSADecrypyt Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new RSADecrypyt();
                }
                return instance;
            }
        }
        #endregion

        #region RSA decryption
        private async Task <ICipherParameters> ReadAsymmetricKeyParameter(string pemFilename)
        {
            var fileStream =  File.OpenText(pemFilename);
            var pemReader = new PemReader(fileStream);
            var KeyParameter =  (ICipherParameters)pemReader.ReadObject();
            return KeyParameter;
        }

        public async Task <string> decrypt(string ciphertext)
        {
            try
            {

                byte[] cipherTextBytes = Convert.FromBase64String(ciphertext);
                string pemFilename = RSAEncrypyt.PublicKeypath;
                ICipherParameters keys = await ReadAsymmetricKeyParameter(pemFilename);
                // Pure mathematical RSA implementation
                // RsaEngine eng = new RsaEngine();

                // PKCS1 v1.5 paddings
                Pkcs1Encoding eng = new Pkcs1Encoding(new RsaEngine());

                // PKCS1 OAEP paddings
                //OaepEncoding eng = new OaepEncoding(new RsaEngine());
                eng.Init(false, keys);

                int length = cipherTextBytes.Length;
                int blockSize = eng.GetInputBlockSize();
                List<byte> plainTextBytes = new List<byte>();
                for (int chunkPosition = 0;
                    chunkPosition < length;
                    chunkPosition += blockSize)
                {
                    int chunkSize = Math.Min(blockSize, length - chunkPosition);
                    plainTextBytes.AddRange(eng.ProcessBlock(
                        cipherTextBytes, chunkPosition, chunkSize
                    ));
                }
                string decryptedstring = Encoding.UTF8.GetString(plainTextBytes.ToArray());
                return decryptedstring;
            }
            catch (Exception ex)
            {
                return ex.Message.ToString();
            }
        }
        #endregion
    }
}
