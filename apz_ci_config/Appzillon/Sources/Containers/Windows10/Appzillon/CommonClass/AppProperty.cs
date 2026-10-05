using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.CommonClass
{
  public static  class AppProperty
    {
        public static string expiryDate { get; set; }
        public static string idleTimeOut { get; set; }
        public static string facebookAppId { get; set; }
        public static string linkedinClientId { get; set; }
        public static string linkedinSecretKey { get; set; }
        public static string twitterClientId { get; set; }
        public static string twitterSecretKey { get; set; }
        public static string appId { get; set; }
        public static string firstPage { get; set; }
        public static string serverToken { get; set; }
        public static string decryptredserverToken { get; set; } // Added to fetch decrypted servertoken  
        public static string defaultAuthorization { get; set; }
        public static string otpReqd { get; set; }
        public static string trackLocation { get; set; }
        public static string serverUrl { get; set; }
        public static string auditLogReqd { get; set; }
        public static int noOfLogLines { get; set; }
        public static string logLevel { get; set; }
        public static string authenticationType { get; set; }
        public static string sendLog { get; set; }
        public static string useProcessJson { get; set; }
        public static bool enableMockServer { get; set; }
        public static string enableAnimation { get; set; }
        public static string sslPinning { get; set; }
        public static string trustAllCertificates { get; set; }
        public static string payloadEncryption { get; set; }
        public static string dataIntegrity { get; set; }
        public static string enableServer { get; set; }
        public static string appVersion { get; set; }
    }
}
