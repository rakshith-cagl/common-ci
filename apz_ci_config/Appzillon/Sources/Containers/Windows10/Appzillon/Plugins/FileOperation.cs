using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.IO.Compression;
using System.Linq;
using System.Net.Http;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Text;
using System.Threading.Tasks;
using Windows.Storage;
using Windows.System;

namespace Appzillon.Plugins
{
    class FileOperation
    {

        #region Upload File
        public async static void UploadFile(JObject json)
        {
            string id = json[JsonKey.ID].ToString();
            try
            {

                string filePath = json[JsonKey.FILE_PATH].GetString();
                string destination = json[JsonKey.DESTINATION].GetString() ?? "";
                string hasSession = json["sessionReq"].GetString() ?? "";

                string fileOverride = json["overwrite"].GetString().GetDefault("Y", "N");
                JObject appzillonHeader = JObject.Parse(json[JsonKey.APPZILLON_HEADER].ToString());
                string url = AppSettings.AppProperties.GetValue("serverUrl").ToString() + "/upload";
                var requestjs = new JObject();
                requestjs[JsonKey.APPZILLON_HEADER] = appzillonHeader;
                requestjs[JsonKey.APPZILLON_BODY] = new JObject();
                string path = Filepathresolver.PathResolver(filePath);
                StorageFile filetoblob = await StorageFile.GetFileFromPathAsync(path);
                var inputStream = await filetoblob.OpenSequentialReadAsync();
                var readStream = inputStream.AsStreamForRead();
                byte[] buffer = new byte[readStream.Length];
                await readStream.ReadAsync(buffer, 0, buffer.Length);
                inputStream.Dispose();
                JObject details = new JObject();
                details["fileName"] = filetoblob.Name;
                details["fileSize"] = buffer.Length;
                details["fileNo"] = 1;
                details["fileType"] = filetoblob.ContentType;
                JArray jarray = new JArray();
                jarray.Add(details);
                requestjs[JsonKey.APPZILLON_BODY]["fileDetails"] = jarray;
                requestjs[JsonKey.APPZILLON_BODY]["overWrite"] = fileOverride;
                requestjs[JsonKey.APPZILLON_BODY]["destination"] = destination;
                MultipartFormDataContent form = new MultipartFormDataContent();
                HttpContent httpcontent = new ByteArrayContent(buffer);
                buffer = null;
                form.Add(httpcontent, filetoblob.Name, filetoblob.Name);
                HttpContent httpcontentjs = new StringContent(requestjs.ToString(), Encoding.UTF8);
                form.Add(httpcontentjs, "appzillonRequest");
                HttpResponseMessage response;
                using (HttpClient httpclient = new HttpClient())
                {
                    response = await httpclient.PostAsync(url, form);
                }
                try
                {
                    JObject resp = JObject.Parse(response.Content.ReadAsStringAsync().Result);
                    // Log.Debug("WebResponse \n" + resp.ToString());
                    Log.Debug("WebResponse recieved");
                    bool status = (bool)resp[JsonKey.APPZILLON_HEADER][JsonKey.STATUS];
                    if (!status)
                    {
                        Response.Fail(id, ErrorCode.SERVER_ERROR);
                        return;
                    }
                    string fileStatus;
                    if (hasSession == "Y")
                    {
                        fileStatus = JsonKey.APPZILLON_UPLOAD_FILE_RESPONSE;
                        JObject server = new JObject();
                        server[JsonKey.SESSION_ID] = resp[JsonKey.APPZILLON_HEADER][JsonKey.SESSION_ID].ToString();
                        server[JsonKey.REQUEST_KEY] = resp[JsonKey.APPZILLON_HEADER][JsonKey.REQUEST_KEY].ToString();
                    }
                    else
                    {
                        fileStatus = JsonKey.APPZILLON_WS_RESPONSE;
                    }
                    if (resp[JsonKey.APPZILLON_BODY][fileStatus].ToString().IndexOf(JsonKey.SUCCESS) >= 0)
                    {
                        JObject param = new JObject();
                        param[JsonKey.SUCCESS_MESSAGE] = "File uploded suceessfully";
                        Response.Success(id, param);
                    }
                    else
                    {
                        Response.Fail(id, "UPLOAD FAIL");
                    }
                }
                catch (Exception e)
                {
                    Response.Fail(id, ErrorCode.SERVER_ERROR);
                    Log.Error(e.Message);
                }

            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.UPLOARDING_FILE);
                Log.Error(e.Message);
            }
        }
        #endregion

        #region Download File
        public static async void DownloadFile(JObject json)
        {
            string id = json[JsonKey.ID].ToString();
            string serverUrl = AppSettings.AppProperties["serverUrl"].ToString();

            try
            {
                // string data = json[JsonKey.DATA_STRING].ToString();
                bool hasSession = json["sessionReq"].ToString() == "Y" ? true : false;
                JObject appzillonHeader = JObject.Parse(json["appzillonHeader"].ToString());
                JObject request = new JObject();
                JObject body = new JObject();
                body["filePath"] = json[JsonKey.FILE_PATH].ToString();
                body["fileName"] = json[JsonKey.FILE_NAME].ToString();
                body[JsonKey.BASE64] = "Y";
                request["appzillonHeader"] = appzillonHeader;
                request["appzillonBody"] = new JObject();
                if (hasSession)
                {
                    request["appzillonBody"]["appzillonFilePushServiceRequest"] = body;
                }
                else
                {
                    request["appzillonBody"]["appzillonFilePushServiceWSRequest"] = body;
                }
                string destDir = json[JsonKey.DESTINATION_PATH].ToString() ?? "downloads";
                bool isBase64 = json[JsonKey.BASE64].ToString() == "Y" ? true : false;
                string responseString = await GenericUtils.SendRequestAsync(serverUrl, request.ToString());
                JObject resp = JObject.Parse(responseString);
                bool status = (bool)resp[JsonKey.APPZILLON_HEADER][JsonKey.STATUS];
                if (!status)
                {
                    Response.Fail(id, ErrorCode.SERVER_ERROR);
                    return;
                }
                string respType;
                if (hasSession)
                {
                    JObject server = new JObject();
                    server[JsonKey.SESSION_ID] = resp[JsonKey.APPZILLON_HEADER][JsonKey.SESSION_ID].ToString();
                    server[JsonKey.REQUEST_KEY] = resp[JsonKey.APPZILLON_HEADER][JsonKey.REQUEST_KEY].ToString();
                    respType = "appzillonFilePushServiceResponse";
                }
                else
                {
                    respType = "appzillonFilePushServiceWSResponse";
                }
                JObject fileObj = JObject.Parse(resp[JsonKey.APPZILLON_BODY][respType].ToString());
                string fileData = fileObj[JsonKey.FILE].ToString();
                string fileType = fileObj[JsonKey.FILE_TYPE].ToString();
                string fileName = fileObj[JsonKey.FILE_NAME].ToString();
                if (isBase64)
                {
                    JObject param = new JObject();
                    param[JsonKey.BASE64] = fileData;
                    param[JsonKey.TEXT] = param[JsonKey.BASE64];
                    param[JsonKey.SUCCESS_MESSAGE] = "File downloaded successfully";
                    Response.Success(id, param);
                    return;
                }

                var filebuffer = Windows.Security.Cryptography.CryptographicBuffer.DecodeFromBase64String(fileData);

                var localFolder = Windows.Storage.ApplicationData.Current.LocalFolder;
                try
                {
                    var newFolder = await localFolder.CreateFolderAsync(("apps/" + MainPage.CurrentAppId + "/" + destDir).GetPath(), Windows.Storage.CreationCollisionOption.OpenIfExists);
                    var newFile = await newFolder.CreateFileAsync(fileName, Windows.Storage.CreationCollisionOption.ReplaceExisting);
                    var fileWrite = Windows.Storage.FileIO.WriteBufferAsync(newFile, filebuffer);
                    JObject param = new JObject();
                    param[JsonKey.SUCCESS_MESSAGE] = "File downloaded successfully";
                    param[JsonKey.FILE_PATH] = destDir + "/" + fileName;
                    param[JsonKey.TEXT] = param[JsonKey.FILE_PATH];
                    Response.Success(id, param);
                }
                catch (Exception e)
                {
                    Response.Fail(id, ErrorCode.FILE_COULD_NOT_BE_CREATED);
                    Log.Error(e.Message);
                }
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.DOWNLOADING_FILE);
                Log.Error(e.Message);
            }
        }

        internal static async void fileLaunch(JObject reqObject)
        {
            string fp = Filepathresolver.PathResolver(reqObject[JsonKey.FILE_PATH].GetString());
            try
            {
                StorageFile file = await StorageFile.GetFileFromPathAsync(fp);
                bool x = await Launcher.LaunchFileAsync(file);
                if (x)
                {
                    Response.Success(reqObject[JsonKey.ID].GetString(),false);
                }
                else
                    Response.Fail(reqObject[JsonKey.ID].GetString(), ErrorCode.FILE_GET_FAIL);
            }
            catch (Exception e)
            {
                Response.Fail(reqObject[JsonKey.ID].GetString(), ErrorCode.FILE_GET_FAIL);
                Log.Error(e.Message);
            }
        }

        #endregion

        #region File Create
        public static async void FileCreate(JObject obj)
        {
            string id = obj["id"].ToString();
            try
            {
                string filepath = obj[JsonKey.FILE_PATH].ToString();
                string filename = obj[JsonKey.FILE_NAME].ToString();
                var content = obj[JsonKey.FILE_CONTENT].ToString();
                string fp = Filepathresolver.PathResolver(filepath);
                string nfp = "";
                if (!fp.EndsWith("\\"))
                {
                    nfp = fp + "\\" + filename;
                }
                else
                {
                    nfp = fp + filename;
                }
                DirectoryInfo finfo = new DirectoryInfo(fp);
                if (!finfo.Exists)
                {
                    finfo.Create();
                }
                FileInfo fi = new FileInfo(nfp);
                if (!fi.Exists)
                {
                    fi.Create().Dispose();
                }
                StorageFile fil = await StorageFile.GetFileFromPathAsync(fi.FullName);
                await FileIO.WriteTextAsync(fil, content);
                Response.Success(id, false);

            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.FILE_COULD_NOT_BE_CREATED);
                Log.Error(e.Message);
            }
        }
        #endregion

        #region File Delete
        public static async void FileDelete(JObject obj)
        {
            string id = obj["id"].ToString();
            try
            {
                string filepath = obj[JsonKey.FILE_PATH].ToString();
               // string filename = obj[JsonKey.FILE_NAME].ToString();
                string dir = Filepathresolver.PathResolver(filepath);
                StorageFile fil = await StorageFile.GetFileFromPathAsync(dir);
                await fil.DeleteAsync();
                Response.Success(id, false);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.FILE_DELETE);
                Log.Error(e.Message);
            }
        }
        #endregion

        #region File content
        public static async void FileContent(JObject obj)
        {
            string id = obj["id"].ToString();
            try
            {
                string filepath = obj[JsonKey.FILE_PATH].ToString();
                string fp = Filepathresolver.PathResolver(filepath);
                
                //text
                StorageFile file = await StorageFile.GetFileFromPathAsync(fp);
                string res = await Windows.Storage.FileIO.ReadTextAsync(file);

                /*
                 * 
                //buffer                                                                        //==============>> this part is coded and commented intentionally (might be useful for future purposes)
                var buff = await Windows.Storage.FileIO.ReadBufferAsync(file);
                //base64
                var base64 = Windows.Security.Cryptography.CryptographicBuffer.EncodeToBase64String(buff);
                *
                */

                JObject jasoom = new JObject();
                jasoom[JsonKey.CONTENT] = res;
                Response.Success(id, jasoom);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.FILE_READ);
                Log.Error(e.Message);
            }
        }
        #endregion

        #region Base64Code
        public static void GetBase64Code(JObject obj)
        {
            string id = obj["id"].ToString();

            try
            {
                string filepath = obj[JsonKey.FILE_PATH].ToString();
                string newfilepath = Filepathresolver.PathResolver(filepath);
                string result = null;
                byte[] bytearray = File.ReadAllBytes(newfilepath);
                Windows.Storage.Streams.IBuffer buffer = bytearray.AsBuffer();
                result = Windows.Security.Cryptography.CryptographicBuffer.EncodeToBase64String(buffer);

                if (result != null)
                {
                    JObject jn = new JObject();
                    jn[JsonKey.TEXT] = result;
                    Response.Success(id, jn);
                }
                else
                {
                    Log.Error("file has no content");
                }

            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.FILE_ENCODING);
                Log.Error(e.Message);
            }
        }
        #endregion

        #region zip & unzip
        public static async void Zipit(JObject zip)
        {
            StorageFolder loc = ApplicationData.Current.LocalFolder;
            StorageFolder tempzip = await loc.CreateFolderAsync("tempzip", CreationCollisionOption.GenerateUniqueName);
            string id = zip[JsonKey.ID].ToString();
            string filepath = zip[JsonKey.SRC_FILEPATH].ToString();
            string dest = zip[JsonKey.DEST_FILEPATH].ToString();
            string nfp = Filepathresolver.PathResolver(dest);
            string ovr = "N";
            try
            {
                ovr = zip["overwrite"].ToString();
            }
            catch (Exception)
            {
                Log.Info("Overwrite Key Does Not Exist In The Json");
            }
            string newfilepath = Filepathresolver.PathResolver(filepath);
            try
            {

                DirectoryInfo temp = null;
                FileInfo fi = new FileInfo(nfp + "\\some.zip");
                if (!Directory.Exists(nfp))
                {
                    temp = Directory.CreateDirectory(nfp);

                }
                if (fi.Exists)
                {
                    if (ovr == "Y")
                    {
                        await (await StorageFile.GetFileFromPathAsync(fi.FullName)).DeleteAsync();

                    }
                    else
                    {
                        JObject body = new JObject();
                        Response.Fail(id, ErrorCode.FILE_ALREADY_EXISTS);
                        Log.Error("Zip File With Same Name Already Exists And Could Not Be Modified");
                    }
                }
                try
                {
                    FileInfo file = new FileInfo(newfilepath);
                    if (file.Exists)
                    {
                        StorageFile filetozip = await StorageFile.GetFileFromPathAsync(file.FullName);
                        await filetozip.CopyAsync(tempzip, file.Name);
                    }
                    else
                    {
                        DirectoryInfo folder = new DirectoryInfo(newfilepath);
                        if (folder.Exists)
                        {
                            StorageFolder src = await StorageFolder.GetFolderFromPathAsync(folder.FullName);
                            await CopyFilestolocal(src, loc, "tempzip");
                        }
                    }
                }
                catch (Exception)
                { }
                DirectoryInfo source = new DirectoryInfo(newfilepath);
                ZipFile.CreateFromDirectory(tempzip.Path, fi.FullName);
                await tempzip.DeleteAsync();
                var js = new JObject();
                js[JsonKey.FILE_PATH] = fi.FullName;
                Response.Success(id, js);
            }
            catch (Exception e)
            {
                JObject body = new JObject();
                Response.Fail(id, ErrorCode.ZIP_FAIL);
                Log.Error(e.Message);
            }
        }
        public static void Unzip(JObject unzip)
        {
            string id = unzip[JsonKey.ID].ToString();
            string filepath = unzip[JsonKey.SRC_FILEPATH].ToString();
            string dest = unzip[JsonKey.DEST_FILEPATH].ToString();
            string nfp = Filepathresolver.PathResolver(dest);
            string newfilepath = Filepathresolver.PathResolver(filepath);
            try
            {

                DirectoryInfo destpath = new DirectoryInfo(nfp);
                if (!Directory.Exists(nfp))
                {
                    Directory.CreateDirectory(nfp);
                }
                DirectoryInfo source = new DirectoryInfo(newfilepath);
                ZipFile.ExtractToDirectory(source.FullName, destpath.FullName);
                var js = new JObject();
                js[JsonKey.FILE_PATH] = destpath.FullName;
                Response.Success(id, js);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.UNZIP_FAIL);
                Log.Error(e.Message);
            }
        }
        #endregion

        #region FileSize
        internal static void GetFileSize(JObject obj)
        {
            string id = obj["id"].ToString();
            string filepath = obj["filePath"].ToString();
            string newfilepath = Filepathresolver.PathResolver(filepath);
            try
            {
                FileInfo fil = new FileInfo(newfilepath);
                JObject js = new JObject();
                js["fileSize"] = (fil.Length) / 1024;
                js[JsonKey.TEXT] = js["fileSize"];
                Response.Success(id, js);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.UNABLE_TO_GET_FILE_SIZE);
                Log.Error(e.Message);
            }

        }
        #endregion

        #region Base64ToPdf
        internal static async void Base64ToPdf(JObject obj, MainPage lobject)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                string base64 = obj[JsonKey.BASE64].ToString();
                string fileName = obj[JsonKey.FILE_NAME].ToString();
                string filePath = obj[JsonKey.FILE_PATH].ToString();
                string extension = obj[JsonKey.FILE_EXT].ToString();
                StorageFolder local = ApplicationData.Current.LocalFolder;
                StorageFile dest;
                if (string.IsNullOrEmpty(filePath))
                {
                    dest = await local.CreateFileAsync(fileName + "." + extension, CreationCollisionOption.ReplaceExisting);

                }
                else
                {
                    if (!filePath.EndsWith("/"))
                    {
                        filePath = filePath + "/" + fileName + ".pdf";
                    }
                    else
                    {
                        filePath = filePath + fileName + ".pdf";
                    }
                    if (filePath.StartsWith("/"))
                    {
                        filePath = "appzillonapps/apps/" + MainPage.CurrentAppId + filePath;
                    }
                    else
                    {
                        filePath = "appzillonapps/apps/" + MainPage.CurrentAppId +"/"+ filePath;
                    }
                    filePath = filePath.Replace("/", "\\");
                    dest = await local.CreateFileAsync(filePath, CreationCollisionOption.ReplaceExisting);
                }
                var byteArray = Convert.FromBase64String(base64);
                var buffer = Windows.Security.Cryptography.CryptographicBuffer.CreateFromByteArray(byteArray);
                await FileIO.WriteBufferAsync(dest, buffer);
                obj[JsonKey.FILE_PATH] = dest.Path;
                Pdf.PdfViewer(obj, lobject);
            }
            catch (Exception e)
            {
                Response.Fail(id,ErrorCode.BASE64_TO_FILE_FAIL);
                Log.Error(e.Message);
            }
        }
        #endregion

        #region Base64ToFile
        internal static async void Base64ToFile(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            try
            {
                string base64 = obj[JsonKey.BASE64].ToString();
                string fileName = obj[JsonKey.FILE_NAME].ToString();
                string filePath = obj[JsonKey.FILE_PATH].ToString();
                StorageFolder local = ApplicationData.Current.LocalFolder;
                StorageFile dest;
                if (string.IsNullOrEmpty(filePath))
                {
                    dest = await local.CreateFileAsync(fileName, CreationCollisionOption.ReplaceExisting);

                }
                else
                {
                   
                    if(filePath.EndsWith("/"))
                        filePath = filePath + fileName;
                    else
                        filePath = filePath +"/"+ fileName;
                    filePath = Filepathresolver.PathResolver(filePath);
                    FileInfo fi = new FileInfo(filePath);
                    if (!fi.Exists)
                    {
                        fi.Create().Dispose();
                    }
                    dest = await StorageFile.GetFileFromPathAsync(filePath);
                }
                var byteArray = Convert.FromBase64String(base64);
                var buffer = Windows.Security.Cryptography.CryptographicBuffer.CreateFromByteArray(byteArray);
                await FileIO.WriteBufferAsync(dest, buffer);
                JObject js = new JObject();
                js[JsonKey.FILE_PATH] = dest.Path;
                Response.Success(id, js);
            }
            catch (Exception e)
            {
                Response.Fail(id, ErrorCode.BASE64_TO_PDF_FAIL);
                Log.Error("base64 to file, "+e.Message);
            }
        }
        #endregion

        public async static Task CopyFilestolocal(StorageFolder src, StorageFolder destination, string destname)
        {
            StorageFolder destFolder = await destination.CreateFolderAsync(destname, CreationCollisionOption.OpenIfExists);
            try
            {
                foreach (string dirPath in Directory.GetDirectories(src.Path, "*", SearchOption.AllDirectories))
                {
                    string lastPart = dirPath.Split('\\').Last();
                    Directory.CreateDirectory(dirPath.Replace(src.Path, destFolder.Path));
                }
            }
            catch (Exception ex)
            {
                Log.Debug("----Error----->" + ex.Message);
            }

            foreach (string newPath in Directory.GetFiles(src.Path, "*.*", SearchOption.AllDirectories))
            {
                try
                {
                    File.Copy(newPath, newPath.Replace(src.Path, destFolder.Path), true);
                }
                catch (Exception e)
                {
                    Log.Warning(e.Message);
                }
            }
        }
    }
}
