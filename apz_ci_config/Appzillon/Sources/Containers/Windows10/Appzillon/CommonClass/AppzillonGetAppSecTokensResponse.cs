using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.CommonClass
{
    public class AppzillonGetAppSecTokensResponse
    {
        public static string safeToken { get; set; }
        public static string sessionToken { get; set; }
        public static string serverNonce { get; set; }
        public static string status { get; set; }
    }
	 public class AppzlionAppMasterResponse
    {
        public static bool status { get; set; }
    }
}
