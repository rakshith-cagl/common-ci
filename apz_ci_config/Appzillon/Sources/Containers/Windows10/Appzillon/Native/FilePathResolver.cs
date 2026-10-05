using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Storage;

namespace Appzillon.Native
{
    class Filepathresolver
    {
        public static string PathResolver(string f_path)
        {
            StorageFolder localFolder = ApplicationData.Current.LocalFolder;
            string path = f_path;
            try
            {
                path = f_path.Replace('/', '\\');
            }
            catch (Exception)
            { }
            if (path.LastIndexOf("ms-appdata:", StringComparison.CurrentCultureIgnoreCase) != -1)
            {
                string newfilepath = path.Replace("ms-appdata:\\\\\\local", localFolder.Path);
                return newfilepath;
            }
            else if (path.IndexOf("..\\") != -1)
            {
                string newfilepath = "";
                string inputfilepath = path;
                int index = inputfilepath.IndexOf("..\\");
                var count = 0;
                while (index != -1)
                {
                    count++;
                    inputfilepath = inputfilepath.Substring(index + 2);
                    index = inputfilepath.IndexOf("..\\");
                }

                if ((count) > 0)
                {
                    if (count == 3)
                        newfilepath = localFolder.Path + inputfilepath;
                    if ((count) == 2)
                        newfilepath = localFolder.Path + "\\appzillonapps\\apps" + inputfilepath;
                    if ((count) == 1)
                    {
                        if (inputfilepath.StartsWith("\\"))
                            newfilepath = localFolder.Path + inputfilepath;//---------------<<<<<appid
                        else
                            newfilepath = localFolder.Path + "\\" + inputfilepath;
                    }
                }
                return newfilepath;
            }
            else if (path.StartsWith("apps"))
            {
                string newfilepath = "";
                newfilepath = localFolder.Path + "\\appzillonapps\\"+ path;
                return newfilepath;
            }
            else
            {
                if (f_path[0] == '\\')
                {
                    string fullp = localFolder.Path + "\\appzillonapps\\apps\\" + MainPage.CurrentAppId + path;
                    return fullp;
                }
                else
                {
                    string full = localFolder.Path + "\\appzillonapps\\apps\\" + MainPage.CurrentAppId + "\\" + path;
                    return full;
                }
            }
        }
    }
}
