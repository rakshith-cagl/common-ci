using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Plugins
{
    class ServerCall
    {
        public ServerCall()
        {
        }
        public async void Server_await(JObject obj)
        {
            try { await SendRequestAsync(obj); }
            catch (Exception)
            { }
        }
        public async Task<bool> SendRequestAsync(JObject obj)
        {
            string data = obj["reqFull"].ToString();
            string url = obj["url"].ToString();
            string method = obj["method"].ToString();
            JObject res = new JObject();
            using (var client = new HttpClient())
            {
                try
                {
                    HttpContent content = new StringContent(data, Encoding.UTF8, "application/json");
                    HttpResponseMessage response = null;
                    if (method == "POST")
                    {
                        response = await client.PostAsync(url, content);
                      
                    }
                    else if (method == "GET")
                    {
                        response = await client.GetAsync(url, HttpCompletionOption.ResponseContentRead);
                    }
                    else if (method == "PUT")
                    {
                        response = await client.PutAsync(url, content);
                    }
                    try
                    {
                        var responseString = await response.Content.ReadAsStringAsync();
                        Log.Debug("Response String\n" + responseString);
                        res = JObject.Parse(responseString);
                        obj["result"] = res;
                        Response.Success(obj[JsonKey.ID].ToString(), obj);
                    }
                    catch (Exception)
                    {
                        obj["result"] = res;
                        Response.Success(obj[JsonKey.ID].ToString(), obj);
                    }
                   
                }
                catch (Exception ex)
                {
                    obj["result"] = ex.Message;
                    Response.Fail(obj);
                    try
                    {
                        if (obj["reqFull"]["appzillonHeader"]["interfaceId"].ToString() != "appzillonErrorLogging")
                            Log.FatalError(ex.Message);
                    } 
                    catch (Exception) {
                        Log.FatalError(ex.Message);
                    }
                }
                return true;
            }
        }
    }
}
