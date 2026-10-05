using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using SQLiteWinRT;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Appzillon.Plugins
{
    class WhiteList
    {
        #region Singleton Pattern
        private static WhiteList instance;
        private WhiteList()
        {
        }
        public static WhiteList Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new WhiteList();
                }
                return instance;
            }
        }
        #endregion

        internal async void UpdateWhiteList(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            string cmd = obj[JsonKey.EXECUTE_QUERY].ToString();
            string dbPath = obj[JsonKey.DATABASE_NAME].ToString();
            string path = "appzillonapps\\apps\\" + MainPage.CurrentAppId + "\\" + dbPath;
            dynamic dataBase = await Windows.Storage.ApplicationData.Current.LocalFolder.CreateFileAsync(path,
                     Windows.Storage.CreationCollisionOption.OpenIfExists);
            Database db = new Database(dataBase);
            bool flag = await ExecuteAsync(cmd, db);
            if (flag)
            {
                JObject j = new JObject();
                j[JsonKey.SUCCESS_MESSAGE] = JsonKey.SUCCESS;
                Response.Success(id, j);
            }
            else
            {
                Response.Fail(id, ErrorCode.WHITELIST_UPDATE_FAIL);
            }
            //}
        }
        private async Task<bool> ExecuteAsync(string cmd, Database db)
        {
            bool flag = false;
            try
            {
                await db.OpenAsync();
                await db.ExecuteStatementAsync(cmd);
                flag = true;

            }
            catch (Exception ex)
            {
                flag = false;
               Log.Debug("Sqlite Error =" + ex.Message);
            }
            finally
            {
                db.Dispose();
            }
            return flag;
        }

        internal void UpdateLocal(JObject reqObject)
        {
           //
        }

        internal async void ValidateUrl(JObject obj)
        {
            //  throw new NotImplementedException();
            string id = obj[JsonKey.ID].ToString();
            string url = obj[JsonKey.URL].ToString();
            string cmd = "SELECT" + url + "FROM DEPARTMENT";
            string dbPath = obj[JsonKey.DATABASE_NAME].ToString();
            string path = "appzillonapps\\apps\\" + MainPage.CurrentAppId + "\\" + dbPath;
            dynamic dataBase = await Windows.Storage.ApplicationData.Current.LocalFolder.CreateFileAsync(path,
                     Windows.Storage.CreationCollisionOption.OpenIfExists);
            Database db = new Database(dataBase);
            await db.OpenAsync();
            Statement st = await db.PrepareStatementAsync(cmd);
            st.EnableColumnsProperty();
            int colnumber = st.ColumnCount;
            if (colnumber > 0)
            {
                Response.Success(id,false);
            }
            else
            {
                Response.Fail(id, ErrorCode.VALIDATE_URL_FAIL);
                Log.Error("URL VALIDATION FAILED");
            }
        }

    }
}
