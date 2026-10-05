using Appzillon.Constants;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Native
{
  public static class CommonMethods
    {
        internal static async Task<string> GetAppzlionString(string headerrequest, string bodyrequest)
        {
            StringBuilder stringToRead = new StringBuilder();
            stringToRead.AppendLine("{" + "\n" + "\"" + JsonKey.APPZILLON_HEADER + "\"" + ":" + headerrequest + ",");
            stringToRead.AppendLine("\"" + JsonKey.APPZILLON_BODY + "\"" + ":" + bodyrequest + "}");
            string appzliontring = await GetStringValue(stringToRead.ToString());
            return appzliontring;
        }



        internal static async Task<string> GetStringValue(string stringToRead)
        {
            string finalresponse = string.Empty;
            using (StringReader reader = new StringReader(stringToRead.ToString()))
            {
                finalresponse = await reader.ReadToEndAsync();

            }
            return finalresponse;

        }


        internal static async Task<string> FinalResponseJSON(string appzlionheader, string apzlionbody, string appzlionsafe, string appzlionQOP, string appzlionErrors)
        {
            string finalresponse = string.Empty;
            if (appzlionQOP == null)
            {
                StringBuilder stringToRead = new StringBuilder();
                stringToRead.AppendLine("{" + "\"" + JsonKey.APPZILLON_HEADER + "\"" + ":" + appzlionheader + ",");
                stringToRead.AppendLine("\"" + JsonKey.APPZILLON_BODY + "\"" + ":" + apzlionbody + ",");
                stringToRead.AppendLine("\"" + JsonKey.APPZILLON_SAFE + "\":" + "\"" + appzlionsafe + "\"" + "}");
                finalresponse = await GetStringValue(stringToRead.ToString());

            }
            else
            {
                if (appzlionErrors == null)
                {
                    StringBuilder stringToRead = new StringBuilder();
                    stringToRead.AppendLine("{" + "\"" + JsonKey.APPZILLON_QOP + "\":" + "\"" + appzlionQOP + "\"" + ",");
                    stringToRead.AppendLine("\"" + JsonKey.APPZILLON_SAFE + "\":" + "\"" + appzlionsafe + "\"" + ",");
                    stringToRead.AppendLine("\"" + JsonKey.APPZILLON_HEADER + "\"" + ":" + appzlionheader + ",");
                    stringToRead.AppendLine("\"" + JsonKey.APPZILLON_BODY + "\"" + ":" + apzlionbody + "}");
                    finalresponse = await GetStringValue(stringToRead.ToString());
                }
                else
                {
                    StringBuilder stringToRead = new StringBuilder();
                    stringToRead.AppendLine("{" + "\"" + JsonKey.APPZILLON_QOP + "\":" + "\"" + appzlionQOP + "\"" + ",");
                    stringToRead.AppendLine("\"" + JsonKey.APPZILLON_SAFE + "\":" + "\"" + appzlionsafe + "\"" + ",");
                    stringToRead.AppendLine("\"" + JsonKey.APPZILLON_HEADER + "\"" + ":" + appzlionheader + ",");
                    stringToRead.AppendLine("\"" + JsonKey.APPZILLON_ERROR + "\"" + ":" + appzlionErrors + ",");
                    stringToRead.AppendLine("\"" + JsonKey.APPZILLON_BODY + "\"" + ":" + apzlionbody + "}");

                    finalresponse = await GetStringValue(stringToRead.ToString());

                }
            }
            return finalresponse;
        }
    }
}
