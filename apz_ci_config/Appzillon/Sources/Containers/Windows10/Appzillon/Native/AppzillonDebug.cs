using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Native
{
    class AppzillonDebug
    {
        public static void JavascriptConsole(string msg)
        {
            Debug.WriteLine("[JavaScript] " + msg);
        }
        public static void NativeConsole(string msg)
        {
            Debug.WriteLine("[Native] " + msg);
        }
    }
}
