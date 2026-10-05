using System;
using System.Text;
using System.Security.Cryptography;
using Appzillon.Plugins;

namespace Appzillon.Native
{
    /// <summary>
    /// AES Encryption  and decryption method
    /// Author: Anand Kumar
    /// </summary>
       internal class AesBase64Wrapper
    {
        #region Singleton Pattern
        private static AesBase64Wrapper instance;
        private static byte[] IV = new byte[16];//string.Empty;
        private static string PASSWORD = string.Empty;
        private static string SALT = string.Empty;
        public AesBase64Wrapper()
        {

        }
        /// <summary>
        /// Call this cuntructor before encryption and decryption
        /// </summary>
        /// <param name="key"></param>
        public AesBase64Wrapper(string key)
        {
            PASSWORD = key;
            IV = AESCommon.Instance.getIV(key);
            SALT = AESCommon.Instance.getSalt(key);
        }
        public static AesBase64Wrapper Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new AesBase64Wrapper();
                }
                return instance;
            }
        }
        #endregion

        #region Encryption and Decryption 
        /// <summary>
        /// Method for encryption of AES using Key, IV & Salt
        /// </summary>
        /// <param name="raw"></param>
        /// <param name="key"></param>
        /// <returns></returns>

        public string EncryptAndEncode(string raw, string key)
        {

            using (var csp = Aes.Create())
            {

                ICryptoTransform e = GetCryptoTransform(csp, true);
                byte[] inputBuffer = Encoding.UTF8.GetBytes(raw);
                byte[] output = e.TransformFinalBlock(inputBuffer, 0, inputBuffer.Length);
                string encrypted = Convert.ToBase64String(output);
                return encrypted;
            }
        }
        /// <summary>
        /// Decryption method for AES Using Key, IV & Salt
        /// </summary>
        /// <param name="encrypted"></param>
        /// <param name="key"></param>
        /// <returns></returns>
        public string DecodeAndDecrypt(string encrypted, string key)
        {
            using (var csp = Aes.Create())
            {
                var d = GetCryptoTransform(csp, false);
                byte[] output = Convert.FromBase64String(encrypted);
                byte[] decryptedOutput = d.TransformFinalBlock(output, 0, output.Length);
                string decypted = Encoding.UTF8.GetString(decryptedOutput);
                return decypted;
            }

        }
        /// <summary>
        /// Padding Declration With password and Saltm and Padding mode:
        /// PaddingMode.PKCS7;
        /// CipherMode.CBC;
        /// </summary>
        /// <param name="csp"></param>
        /// <param name="encrypting"></param>
        /// <returns></returns>
        private static ICryptoTransform GetCryptoTransform(Aes csp, bool encrypting)
        {
            csp.Mode = CipherMode.CBC;
            csp.Padding = PaddingMode.PKCS7;
            var spec = new Rfc2898DeriveBytes(Encoding.UTF8.GetBytes(PASSWORD), Encoding.UTF8.GetBytes(SALT), 2);
            byte[] key = spec.GetBytes(16);


            csp.IV = IV;
            csp.Key = key;
            if (encrypting)
            {
                return csp.CreateEncryptor();
            }
            return csp.CreateDecryptor();
        }
        #endregion
    }
}



