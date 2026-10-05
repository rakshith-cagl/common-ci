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
    class Storage
    {
#region Singleton Pattern
        private static Storage instance;
        public Storage()
        {
        }
        public static Storage Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Storage();
                }
                return instance;
            }
        }
#endregion

        public async void ExecuteQuery(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                string cmd = obj[JsonKey.EXECUTE_QUERY].ToString();
                string dbPath = obj[JsonKey.DATABASE_NAME].ToString();
                string result = await ExecuteQueryOnDatabaseAsync(cmd, dbPath);
                if (result != "fail")
                {
                    //Success
                    JObject j = new JObject();
                    j[JsonKey.ID] = id;
                    j[JsonKey.SQL_RESULT] = result;
                    Response.Success(id,j);
                }
                else
                {
                    //Failure
                    JObject j = new JObject();
                    j[JsonKey.ID] = id;
                    Response.Fail(id,ErrorCode.SQL_QUERY_FAIL);
                }
            }
            catch (Exception ex)
            {
               Log.Error("Execute Query Failed " + ex.Message);
            }
        }
        public async void CreateLocalDatabase()
        {
            string dbPath = "APPSDB";
            string notes = "CREATE TABLE IF NOT EXISTS Notes(id varchar PRIMARY KEY,notes varchar)";//"CREATE TABLE Customer2(id int, name varchar)";//
            string tb_asmi_offline_data = "create table IF NOT EXISTS tb_asmi_offline_data(" +
                "refno integer primary key autoincrement," +
                "userid varchar," +
                "screenid varchar," +
                "interfaceid varchar," +
                "interfacepayload varchar," +
                "screenpayload varchar," +
                "screenresponse varchar," +
                "status varchar," +
                "appjsonstr varchar," +
                "appreqbody varchar," +
                "starttime varchar," +
                "endtime varchar)";
            string tb_asmi_template_data = "CREATE TABLE IF NOT EXISTS TB_ASMI_TEMPLATE_DATA(" +
                "TEMPLATEREFNO INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TEMPLATENAME VARCHAR," +
                "SCREENID VARCHAR," +
                "SCREENPAYLOAD VARCHAR)";

            string q1 = await ExecuteQueryOnDatabaseAsync(notes, dbPath);
            string q2 = await ExecuteQueryOnDatabaseAsync(tb_asmi_offline_data, dbPath);
            string q3 = await ExecuteQueryOnDatabaseAsync(tb_asmi_template_data, dbPath);
            if (q1 == "success" && q2 == "success" && q3 == "success")
            {
                Log.Debug("Loacal Database created for App ID =" + MainPage.CurrentAppId);
            }
            else
            {
                Log.Debug("Loacal Database Status\n" +
                    "Notes =" + q1 + "\ntb_asmi_offline_data =" + q2 + "\ntb_asmi_template_data =" + q3);
            }
        }

        internal async void storeNotification(string message,double timeStamp,char readFlag)
        {
            string path = "appzillonapps\\apps\\" + MainPage.CurrentAppId + "\\sqlite\\APPSDB";
            dynamic dataBase = await Windows.Storage.ApplicationData.Current.LocalFolder.CreateFileAsync(path,
                     Windows.Storage.CreationCollisionOption.OpenIfExists);
            using (Database db = new Database(dataBase))
            {
                try
                {
                    await ExecuteAsync("create  table IF NOT EXISTS tb_notifications(id integer primary key autoincrement,message varchar(500),timeStamp varchar(8),readFlag varchar(1))",db);
                    using (Database data = new Database(dataBase))
                    {
                        await ExecuteAsync("INSERT INTO tb_notifications(message,timeStamp,readFlag) VALUES(\"" + message + "\"," + timeStamp + ",\"" + readFlag + "\")", data);
                    }
                }
                catch (Exception e)
                {
                    Log.Debug(e.Message);
                }
            }

        }

        private async Task<string> ExecuteQueryOnDatabaseAsync(string cmd, string dbPath)
        {
            string result = "fail";
            string path = "appzillonapps\\apps\\" + MainPage.CurrentAppId + "\\sqlite\\" + dbPath;
            try
            {

                dynamic dataBase = await Windows.Storage.ApplicationData.Current.LocalFolder.CreateFileAsync(path,
                      Windows.Storage.CreationCollisionOption.OpenIfExists);
                Database db = new Database(dataBase);

                if (cmd.Substring(0, 6).ToUpper() == "SELECT")
                {
                    result = await SelectAsync(cmd, db);
                    Log.Debug("Query Selected Successfully");
                }
                else
                {
                    bool flag = await ExecuteAsync(cmd, db);
                   Log.Debug("Execute async =" + flag);
                    if (flag)
                    {
                        result = "pass";
                    }
                }
            }
            catch (Exception ex)
            {
                //Log.Debug("Databse Path = " + path + "\nQuery = " + cmd + "\nSqlite Error =" + ex.Message);
                Log.Debug("Sqlite Error =" + ex.Message);
            }
            return result;
        }
        private async Task<string> SelectAsync(string cmd, Database db)
        {
            string result = "[";
            try
            {
                await db.OpenAsync();
                Statement st = await db.PrepareStatementAsync(cmd);
                st.EnableColumnsProperty();
                int colnumber = st.ColumnCount;

                while (await st.StepAsync())
                {
                    JObject json = new JObject();
                    for (int i = 0; i < colnumber; i++)
                        json[st.GetColumnName(i)] = st.GetTextAt(i);

                    result += json.ToString() + ",";
                }
                char[] comma = { ',' };
                result = result.TrimEnd(comma);
                result += "]";
            }
            catch (Exception ex)
            {
                result += "]";
                //Log.Debug("Databse Path = " + db.Path + "\nQuery = " + cmd + "\nSqlite Error =" + ex.Message);
                Log.Debug("Sqlite Error =" + ex.Message);
            }
            db.Dispose();
            return result;
        }
       internal async Task<bool> ExecuteAsync(string cmd, Database db)
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
                //Log.Debug("Databse Path = " + db.Path + "\nQuery = " + cmd + "\nSqlite Error =" + ex.Message);
                Log.Debug("Sqlite Error =" + ex.Message);
            }
            finally
            {
                db.Dispose();
            }
            return flag;
        }
    }
}
