using System;
using Appzillon.Constants;
using Appzillon.Native;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using Newtonsoft.Json.Linq;
using Windows.Networking.PushNotifications;
using Windows.UI.Core;
using System.Diagnostics;
using Appzillon.CommonClass;

namespace Appzillon.Native
{
    public sealed class ServerUtils
    {
     
        MainPage mainage = null;
        public ServerUtils()
        {
            
        }
        
        public  async void BindingServerRequest()
        {
             AppToken apptoken = new AppToken();
            AppMasterRequest ar = new AppMasterRequest();
            await apptoken.Getappsectoken();
            if (AppzillonGetAppSecTokensResponse.status == JsonKey.SUCCESS)
            {
                DeviceRegistration dr = new DeviceRegistration();
                bool isDone = await dr.RegisterDevice();
                if(isDone)
                {
                   
                     await ar.GetAppMasterDetails();

                    if (AppzlionAppMasterResponse.status==true)
                    {
                        string todaydate = Utils.GetTodayDate();
                        if (todaydate != IEXC.expiryDate || IEXC.expired == "N")
                        {
                            NotificatonsAppz ntf = new NotificatonsAppz();
                            await ntf.RegisterNotificationUrl();
                        }
                    }
                }
                
            }
           await ar.FirstRun();
        }
    }

   
}
