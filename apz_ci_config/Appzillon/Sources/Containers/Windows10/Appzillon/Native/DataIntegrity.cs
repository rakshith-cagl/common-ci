using Appzillon.Constants;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Native
{
   internal class DataIntegrity
    {
        internal static string GetDataIntegrityValue(string headerrequest, string bodyrequest, string CNonce)
        {
            string payloadQOPstring = "{" + "\"" + JsonKey.APPZILLON_HEADER + "\"" + ":" + headerrequest + "," + "\"" + JsonKey.APPZILLON_BODY + "\"" + ":" + bodyrequest + "}";
            string payloadhasing = GetHash256QopValue(payloadQOPstring, CNonce);
            return payloadhasing;
        }
        internal static string GetHash256QopValue(string payloadQOPstring, string CNonce)
        {
            string QopValue = HashSHA256.Instance.Getpayloadafterhasing(payloadQOPstring, CNonce);
            return QopValue;
        }
    }
}
