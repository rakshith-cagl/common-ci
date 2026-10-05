using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.UI.Core;
using Windows.UI.Xaml.Controls;

namespace Appzillon.Plugins
{
    class Log
    {
        internal static int level = 0;
        internal enum DebugLevel { FATAL, ERROR, WARNING, INFO, DEBUG };
        internal static void FatalError(String msg)
        {
            if (level >= (int)DebugLevel.FATAL)
            {
                AppzillonDebug.NativeConsole("F :" + msg);
                SendLogs("F", msg);
            }
        }
        internal static void Error(string msg)
        {
            if (level >= (int)DebugLevel.ERROR)
            {
                AppzillonDebug.NativeConsole("E :" + msg);
                SendLogs("E", msg);
            }
        }
        internal static void Warning(string msg)
        {
            if (level > (int)DebugLevel.WARNING)
            {
                AppzillonDebug.NativeConsole("W :" + msg);
                SendLogs("W", msg);
            }
        }
        internal static void Debug(string msg)
        {
            if (level > (int)DebugLevel.INFO)
            {
                AppzillonDebug.NativeConsole("D :" + msg);
                SendLogs("D", msg);
            }
        }



        internal static void Info(String msg)
        {
            if (level > (int)DebugLevel.DEBUG)
            {
                AppzillonDebug.NativeConsole("I :" + msg);
                SendLogs("I", msg);
            }
        }
        private static async void SendLogs(string v, string msg)
        {
          //  await Windows.ApplicationModel.Core.CoreApplication.MainView.CoreWindow.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
           // {
                var json = new JObject();
                json["level"] = v;
                json["description"] = msg;
                Response.CallBack("sendLog", json.ToString());
           // });
        }
    }
}
