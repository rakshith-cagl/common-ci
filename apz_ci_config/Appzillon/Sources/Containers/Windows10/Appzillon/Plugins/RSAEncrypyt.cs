using System;
using System.Diagnostics;
using System.IO;
using System.Security.Cryptography;
// use system.security.cryptographic.algoritham nuget Package
using System.Text;
using System.Threading.Tasks;
using Windows.Storage;

namespace Appzillon.Plugins
{

    /// <summary>
    ///  RSA Encryption Code : This Class is used only  for encryotion of RSA using Public key
    ///  Author : Anand Kumar
    /// </summary>
    public  class RSAEncrypyt
    {
        #region Singleton Pattern
        private static RSAEncrypyt instance;
        private RSAEncrypyt()
        {
        }
        public static RSAEncrypyt Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new RSAEncrypyt();
                }
                return instance;
            }
        }
        #endregion

        #region RSA encryption
        private readonly RSA _privateKeyRsaProvider;
        private readonly RSA _publicKeyRsaProvider;
        private readonly HashAlgorithmName _hashAlgorithmName;
        private readonly Encoding _encoding;
        public static string publickey = string.Empty;
        private string privatekey = string.Empty;
        private RSAEncrypyt rsa = null;
        internal static string PublicKeypath = string.Empty;
        public async Task<string> RSAkey(string reqstring)
        {
            try
            {
                if (publickey == string.Empty )
                {
                    string CertFileName_Public = "public.pem";
                    var path = ApplicationData.Current.LocalFolder.Path + "\\appzillonapps\\apps\\" + MainPage.CurrentAppId + "\\staticfiles";
                    Debug.WriteLine("path" + path);
                      StorageFolder folder = await StorageFolder.GetFolderFromPathAsync(path);
                 
                    StorageFile file = await folder.GetFileAsync(CertFileName_Public);
                    PublicKeypath = file.Path;
                    string txt = await FileIO.ReadTextAsync(file);
                    var res1 = txt.Trim();
                    var res2 = res1.Replace("-----BEGIN PUBLIC KEY-----", String.Empty);
                    var res3 = res2.Replace("-----END PUBLIC KEY-----", String.Empty);
                    publickey = res3.Replace("\n", String.Empty).Trim();
                                   
                  rsa = new RSAEncrypyt(RSAType.RSA, Encoding.UTF8, publickey, privatekey);
                  }
                   // Debug.WriteLine("String passed：" + reqstring);
                    string enStr = rsa.Encrypt(reqstring);
                    Debug.WriteLine("String enStr：" + enStr);
                // string decStr = rsa.Decrypt(enStr);



                return enStr;
            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex.Message);
                
                return ex.Message;
            }


        }

        public RSAEncrypyt(RSAType rsaType, Encoding encoding, string publicKey , string privateKey= null)
        {
            _encoding = encoding;
            

            //if (!string.IsNullOrEmpty(privateKey))
            //{
            //    _privateKeyRsaProvider = CreateRsaProviderFromPrivateKey(privateKey);
            //}

            if (!string.IsNullOrEmpty(publicKey))
            {
                _publicKeyRsaProvider = CreateRsaProviderFromPublicKey(publicKey);
            }

            _hashAlgorithmName = rsaType == RSAType.RSA ? HashAlgorithmName.SHA1 : HashAlgorithmName.SHA256;
        }
        public string Encrypt(string text)
        {
            if (_publicKeyRsaProvider == null)
            {
                throw new Exception("_publicKeyRsaProvider is null");
            }
            return Convert.ToBase64String(_publicKeyRsaProvider.Encrypt(Encoding.UTF8.GetBytes(text), RSAEncryptionPadding.Pkcs1));
        }
        public RSA CreateRsaProviderFromPublicKey(string publicKeyString)
        {
            // encoded OID sequence for  PKCS #1 rsaEncryption szOID_RSA_RSA = "1.2.840.113549.1.1.1"
            byte[] seqOid = { 0x30, 0x0D, 0x06, 0x09, 0x2A, 0x86, 0x48, 0x86, 0xF7, 0x0D, 0x01, 0x01, 0x01, 0x05, 0x00 };
            byte[] seq = new byte[15];

            var x509Key = Convert.FromBase64String(publicKeyString);

            // ---------  Set up stream to read the asn.1 encoded SubjectPublicKeyInfo blob  ------
            using (MemoryStream mem = new MemoryStream(x509Key))
            {
                using (BinaryReader binr = new BinaryReader(mem))  //wrap Memory Stream with BinaryReader for easy reading
                {
                    byte bt = 0;
                    ushort twobytes = 0;

                    twobytes = binr.ReadUInt16();
                    if (twobytes == 0x8130) //data read as little endian order (actual data order for Sequence is 30 81)
                        binr.ReadByte();    //advance 1 byte
                    else if (twobytes == 0x8230)
                        binr.ReadInt16();   //advance 2 bytes
                    else
                        return null;

                    seq = binr.ReadBytes(15);       //read the Sequence OID
                    if (!CompareBytearrays(seq, seqOid))    //make sure Sequence for OID is correct
                        return null;

                    twobytes = binr.ReadUInt16();
                    if (twobytes == 0x8103) //data read as little endian order (actual data order for Bit String is 03 81)
                        binr.ReadByte();    //advance 1 byte
                    else if (twobytes == 0x8203)
                        binr.ReadInt16();   //advance 2 bytes
                    else
                        return null;

                    bt = binr.ReadByte();
                    if (bt != 0x00)     //expect null byte next
                        return null;

                    twobytes = binr.ReadUInt16();
                    if (twobytes == 0x8130) //data read as little endian order (actual data order for Sequence is 30 81)
                        binr.ReadByte();    //advance 1 byte
                    else if (twobytes == 0x8230)
                        binr.ReadInt16();   //advance 2 bytes
                    else
                        return null;

                    twobytes = binr.ReadUInt16();
                    byte lowbyte = 0x00;
                    byte highbyte = 0x00;

                    if (twobytes == 0x8102) //data read as little endian order (actual data order for Integer is 02 81)
                        lowbyte = binr.ReadByte();  // read next bytes which is bytes in modulus
                    else if (twobytes == 0x8202)
                    {
                        highbyte = binr.ReadByte(); //advance 2 bytes
                        lowbyte = binr.ReadByte();
                    }
                    else
                        return null;
                    byte[] modint = { lowbyte, highbyte, 0x00, 0x00 };   //reverse byte order since asn.1 key uses big endian order
                    int modsize = BitConverter.ToInt32(modint, 0);

                    int firstbyte = binr.PeekChar();
                    if (firstbyte == 0x00)
                    {   //if first byte (highest order) of modulus is zero, don't include it
                        binr.ReadByte();    //skip this null byte
                        modsize -= 1;   //reduce modulus buffer size by 1
                    }

                    byte[] modulus = binr.ReadBytes(modsize);   //read the modulus bytes
                    string mod = Convert.ToBase64String(modulus);
                    if (binr.ReadByte() != 0x02)            //expect an Integer for the exponent data
                        return null;
                    int expbytes = (int)binr.ReadByte();        // should only need one byte for actual exponent data (for all useful values)
                    byte[] exponent = binr.ReadBytes(expbytes);
                     string expo = Convert.ToBase64String(exponent);
                    // ------- create RSACryptoServiceProvider instance and initialize with public key -----
                    var rsa = RSA.Create();
                    RSAParameters rsaKeyInfo = new RSAParameters
                    {
                        Modulus = modulus,
                        Exponent = exponent
                    };
                    rsa.ImportParameters(rsaKeyInfo);

                    return rsa;
                }

            }
        }
        private bool CompareBytearrays(byte[] a, byte[] b)
        {
            if (a.Length != b.Length)
                return false;
            int i = 0;
            foreach (byte c in a)
            {
                if (c != b[i])
                    return false;
                i++;
            }
            return true;
        }
        public enum RSAType
        {

            RSA = 0,

            RSA2
        }
        
    }
    #endregion

     //this code is NotImplemented for decryption Rsa decryption using private key.
    #region  RSA Decrption Using Private Key
    /// <summary>
    ///  RSA Decryption Code : This Class is not in used for decryption
    ///  Author : Anand Kumar
    /// </summary>
    //public string Sign(string data)
    //{
    //    byte[] dataBytes = _encoding.GetBytes(data);

    //    var signatureBytes = _privateKeyRsaProvider.SignData(dataBytes, _hashAlgorithmName, RSASignaturePadding.Pkcs1);

    //    return Convert.ToBase64String(signatureBytes);
    //}


    //public bool Verify(string data, string sign)
    //{
    //    byte[] dataBytes = _encoding.GetBytes(data);
    //    byte[] signBytes = Convert.FromBase64String(sign);

    //    var verify = _publicKeyRsaProvider.VerifyData(dataBytes, signBytes, _hashAlgorithmName, RSASignaturePadding.Pkcs1);

    //    return verify;
    //}



    //public string Decrypt(string cipherText)
    //{
    //    if (_privateKeyRsaProvider == null)
    //    {
    //        throw new Exception("_privateKeyRsaProvider is null");
    //    }
    //    return Encoding.UTF8.GetString(_privateKeyRsaProvider.Decrypt(Convert.FromBase64String(cipherText), RSAEncryptionPadding.Pkcs1));
    //}

    //public RSA CreateRsaProviderFromPrivateKey(string privateKey)
    //{
    //    var privateKeyBits = Convert.FromBase64String(privateKey);

    //    var rsa = RSA.Create();
    //    var rsaParameters = new RSAParameters();

    //    using (BinaryReader binr = new BinaryReader(new MemoryStream(privateKeyBits)))
    //    {
    //        byte bt = 0;
    //        ushort twobytes = 0;
    //        twobytes = binr.ReadUInt16();
    //        if (twobytes == 0x8130)
    //            binr.ReadByte();
    //        else if (twobytes == 0x8230)
    //            binr.ReadInt16();
    //        else
    //            throw new Exception("Unexpected value read binr.ReadUInt16()");

    //        twobytes = binr.ReadUInt16();
    //        if (twobytes != 0x0102)
    //            throw new Exception("Unexpected version");

    //        bt = binr.ReadByte();
    //        if (bt != 0x00)
    //            throw new Exception("Unexpected value read binr.ReadByte()");

    //        rsaParameters.Modulus = binr.ReadBytes(GetIntegerSize(binr));
    //        rsaParameters.Exponent = binr.ReadBytes(GetIntegerSize(binr));
    //        rsaParameters.D = binr.ReadBytes(GetIntegerSize(binr));
    //        rsaParameters.P = binr.ReadBytes(GetIntegerSize(binr));
    //        rsaParameters.Q = binr.ReadBytes(GetIntegerSize(binr));
    //        rsaParameters.DP = binr.ReadBytes(GetIntegerSize(binr));
    //        rsaParameters.DQ = binr.ReadBytes(GetIntegerSize(binr));
    //        rsaParameters.InverseQ = binr.ReadBytes(GetIntegerSize(binr));
    //    }

    //    rsa.ImportParameters(rsaParameters);
    //    return rsa;
    //}
    //private int GetIntegerSize(BinaryReader binr)
    //{
    //    byte bt = 0;
    //    int count = 0;
    //    bt = binr.ReadByte();
    //    if (bt != 0x02)
    //        return 0;
    //    bt = binr.ReadByte();

    //    if (bt == 0x81)
    //        count = binr.ReadByte();
    //    else
    //    if (bt == 0x82)
    //    {
    //        var highbyte = binr.ReadByte();
    //        var lowbyte = binr.ReadByte();
    //        byte[] modint = { lowbyte, highbyte, 0x00, 0x00 };
    //        count = BitConverter.ToInt32(modint, 0);
    //    }
    //    else
    //    {
    //        count = bt;
    //    }

    //    while (binr.ReadByte() == 0x00)
    //    {
    //        count -= 1;
    //    }
    //    binr.BaseStream.Seek(-1, SeekOrigin.Current);
    //    return count;
    //}

    #endregion
}
