using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.CommonClass
{
  public class RSAJson
    {
        public string id { get; set; }
        public string stringToEncrypt { get; set; }
        public string key { get; set; }
        public string encryptionId { get; set; }
        public object callBack { get; set; }
        public string command { get; set; }
    }
}
