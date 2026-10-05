using System;
using Windows.ApplicationModel.Contacts;
using System.Linq;
using Newtonsoft.Json.Linq;
using System.Threading.Tasks;
using Appzillon.Native;
using Appzillon.Constants;
using Windows.System.Profile;

namespace Appzillon.Plugins
{
    class Contacts
    {

#region Singleton Pattern
        private static Contacts instance;
        private Contacts()
        {
        }
        public static Contacts Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new Contacts();
                }
                return instance;
            }
        }
#endregion

        public async void ContactsPlugin(JObject o)
        {
            string id = o[JsonKey.ID].ToString();
            var con = o[JsonKey.DETAILS];

            bool add = await AddContactPlugin(o);
            if (add)
            {
                string result = "Contact Added Successfully";
                JObject json = new JObject();
                json[JsonKey.SUCCESS_MESSAGE] = result;
                Response.Success(id,json);
            }

            else
            {
               // string result = "Cannot add contact";
                Response.Fail(id,ErrorCode.CANNOT_ADD_CONTACT);
                Log.Error("Could Not Add Contact");
            }
        }


        public async Task<bool> AddContactPlugin(JObject o)
        {
            var con = o[JsonKey.DETAILS];
            JObject contact = JObject.Parse(con.ToString());

            contact[JsonKey.PHONE_HOME] = contact[JsonKey.PHONE_HOME] == null ? "" : contact[JsonKey.PHONE_HOME];
            contact[JsonKey.PHONE_WORK] = contact[JsonKey.PHONE_WORK] == null ? "" : contact[JsonKey.PHONE_WORK];
            contact[JsonKey.PHONE_MOBILE] = contact[JsonKey.PHONE_MOBILE] == null ? "" : contact[JsonKey.PHONE_MOBILE];

            var result = false;

            try
            {
                ContactStore store = await ContactManager.RequestStoreAsync(ContactStoreAccessType.AppContactsReadWrite);
                var lists = await store.FindContactListsAsync();
                ContactList list = lists.FirstOrDefault((x) => x.DisplayName == "myList");
                if (list == null)
                {
                    list = await store.CreateContactListAsync("myList");
                    list.OtherAppReadAccess = ContactListOtherAppReadAccess.Full;
                    list.OtherAppWriteAccess = ContactListOtherAppWriteAccess.SystemOnly;
                    await list.SaveAsync();
                }

                Contact newContact = new Contact();
                newContact.FirstName = contact[JsonKey.FIRST_NAME].ToString();
                newContact.LastName = contact[JsonKey.LAST_NAME].ToString();
                newContact.Phones.Add(new ContactPhone()
                {
                    Kind = ContactPhoneKind.Home,
                    Number = contact[JsonKey.PHONE_HOME].ToString()
                });
                newContact.Phones.Add(new ContactPhone()
                {
                    Kind = ContactPhoneKind.Work,
                    Number = contact[JsonKey.PHONE_WORK].ToString()
                });
                newContact.Phones.Add(new ContactPhone()
                {
                    Kind = ContactPhoneKind.Mobile,
                    Number = contact[JsonKey.PHONE_MOBILE].ToString()
                });
                newContact.Emails.Add(new ContactEmail() { Address = contact[JsonKey.MAIL].ToString() });

                newContact.Addresses.Add(new ContactAddress() { StreetAddress = contact[JsonKey.ADDRESS].ToString() });
                newContact.Websites.Add(new ContactWebsite() { RawValue = contact[JsonKey.WEBSITE].ToString() });

                await list.SaveContactAsync(newContact);
                result = true;
            }
            catch (Exception)
            {
                result = false;
            }
            return result;
        }


        public async void ContactsSearchPlugin(JObject o)
        {
            string id = o[JsonKey.ID].ToString();
            string result = await SearchContact(o);
            if (result != string.Empty)
            {
                JObject js = JObject.Parse(result);
                Response.Success(id, js);
            }
            else
            {
                // jsonstring = "{\"successMessage\":\" Contact not found \"}";
                // lobject.InvokeScript(failureCallback, result);
                Response.Fail(id, ErrorCode.CONTACT_NOT_FOUND);
                Log.Error("Contact Not Found");
            }
        }


        public async Task<string> SearchContact(JObject o)
        {
            var con = o[JsonKey.SEARCH_CRITERIA];
            JObject contact = JObject.Parse(con.ToString());

            //bool found = false;
            string firstName = contact[JsonKey.FIRST_NAME].ToString();
            string lastName = contact[JsonKey.LAST_NAME].ToString();
            string phWork = contact[JsonKey.PHONE_WORK].ToString();
            string phMobile = contact[JsonKey.PHONE_MOBILE].ToString();
            string phHome = contact[JsonKey.PHONE_HOME].ToString();

            Contact searchContact = new Contact();
            ContactStore store = await ContactManager.RequestStoreAsync(ContactStoreAccessType.AppContactsReadWrite);
            var lists = await store.FindContactsAsync();

            var contacts = from Contact scon in lists
                           where scon.FirstName.Contains(firstName)
                                   && scon.LastName.Contains(lastName)
                                   && ((phMobile != "" ? (scon.Phones.Count() > 0 ?
                                                          scon.Phones.ElementAt(2).Number.Contains(phMobile)
                                                           || scon.Phones.ElementAt(1).Number.Contains(phMobile)
                                                           || scon.Phones.ElementAt(0).Number.Contains(phMobile)
                                                           : scon.Phones.Count() > 1 ?
                                                               scon.Phones.ElementAt(1).Number.Contains(phMobile)
                                                               || scon.Phones.ElementAt(0).Number.Contains(phMobile)
                                                               : scon.Phones.Count() > 0 ?
                                                                   scon.Phones.ElementAt(0).Number.Contains(phMobile) : false)
                                                                   : true)

                                   && (phHome != "" ? (scon.Phones.Count() > 1 ?
                                                          scon.Phones.ElementAt(2).Number.Contains(phHome)
                                                           || scon.Phones.ElementAt(1).Number.Contains(phHome)
                                                           || scon.Phones.ElementAt(0).Number.Contains(phHome)
                                                           : scon.Phones.Count() > 1 ?
                                                               scon.Phones.ElementAt(1).Number.Contains(phHome)
                                                               || scon.Phones.ElementAt(0).Number.Contains(phHome)
                                                               : scon.Phones.Count() > 0 ?
                                                                   scon.Phones.ElementAt(0).Number.Contains(phHome) : false)
                                                                   : true)

                                   && (phWork != "" ? (scon.Phones.Count() > 2 ?
                                                          scon.Phones.ElementAt(2).Number.Contains(phWork)
                                                           || scon.Phones.ElementAt(1).Number.Contains(phWork)
                                                           || scon.Phones.ElementAt(0).Number.Contains(phWork)
                                                           : scon.Phones.Count() > 1 ?
                                                               scon.Phones.ElementAt(1).Number.Contains(phWork)
                                                               || scon.Phones.ElementAt(0).Number.Contains(phWork)
                                                               : scon.Phones.Count() > 0 ?
                                                                   scon.Phones.ElementAt(0).Number.Contains(phWork) : false)
                                                                   : true)
                                      )

                           select scon;

            string result = string.Empty;
            result = "{\"searchResult\":[";

            if (contacts.Count() > 0)
            {
                foreach (var item in contacts)
                {
                    result += "{\"Name\":\"" + item.DisplayName + "\",";
                    result += "\"Address\":\"" + (item.Addresses.Count() > 0 ? item.Addresses.FirstOrDefault().Description + ","
                        + item.Addresses.FirstOrDefault().StreetAddress + ","
                        + item.Addresses.FirstOrDefault().Region + ","
                        + item.Addresses.FirstOrDefault().Country + ","
                        + item.Addresses.FirstOrDefault().PostalCode : "") + "\",";

                    result += "\"mail\":\"" + (item.Emails.Count() > 0 ? (item.Emails.FirstOrDefault()).Address : "") + "\",";

                    result += "\"phoneMobile\":\"";
                    if (item.Phones.Count() > 0)
                        result += item.Phones.ElementAtOrDefault(0).Number + "\",";
                    else
                        result += "" + "\",";
                    result += "\"phoneHome\":\"";
                    if (item.Phones.Count() > 1)
                        result += item.Phones.ElementAtOrDefault(1).Number + "\",";
                    else
                        result += "" + "\",";
                    result += "\"phoneWork\":\"";
                    if (item.Phones.Count() > 2)
                        result += item.Phones.ElementAtOrDefault(2).Number + "\"";
                    else
                        result += "" + "\"";
                    result += "},";

                }
                var len = result.Length - 1;
                result = result.Remove(len, 1) + "]}";

                //string[] invokeParams = { successCallback, result };
                return result;
            }

            else
            {
                result = string.Empty;
                return result;
            }


        }

        public async void ContactsEditPlugin(JObject o)
        {
            string id = o[JsonKey.ID].ToString();
            JObject json = new JObject();
            json[JsonKey.DELETE_CRITERIA] = o[JsonKey.SEARCH_CRITERIA];
            bool deleted = await DeleteContactPlugin(json);

            if (deleted)
            {
                JObject jsonAdd = new JObject();
                jsonAdd[JsonKey.DETAILS] = o[JsonKey.DETAILS];

                bool editContact = await AddContactPlugin(jsonAdd);

                if (editContact)
                {
                    JObject result = new JObject();
                    string r = "Contact edited successfully";
                    result[JsonKey.SUCCESS_MESSAGE] = r;
                    Response.Success(id,result);
                }

                else if (!editContact)
                {
                    // JObject result = new JObject();
                    //string r = "Cannot edit Contact";
                    //  result["successMessage"] = r;
                    Response.Fail(id,ErrorCode.CAN_NOT_EDIT_CONTACT);
                    Log.Error("Can Not Edit Contact");
                }
            }

            else if (!deleted)
            {
                Response.Fail(id, ErrorCode.CAN_NOT_EDIT_CONTACT);
                Log.Error("Can Not Edit Contact");
            }



        }

        public async void ContactsDeletePlugin(JObject o)
        {
            string id = o[JsonKey.ID].ToString();
            bool result = await DeleteContactPlugin(o);
            if (result)
            {
                JObject del = new JObject();
                del[JsonKey.SUCCESS_MESSAGE] = "Contact Deleted";
                Response.Success(id,del);
            }

            else if (!result)
            {
               
                Response.Fail(id,ErrorCode.CONTACT_NOT_FOUND);
                Log.Error("Contact Not Found");
            }

        }

        public async Task<bool> DeleteContactPlugin(JObject o)
        {
          
            var con = o[JsonKey.DELETE_CRITERIA];
            JObject contact = JObject.Parse(con.ToString());

            //bool found = false;
            string firstName = contact[JsonKey.FIRST_NAME].ToString();
            string lastName = contact[JsonKey.LAST_NAME].ToString();
            string phWork = contact[JsonKey.PHONE_WORK].ToString();
            string phMobile = contact[JsonKey.PHONE_MOBILE].ToString();
            string phHome = contact[JsonKey.PHONE_HOME].ToString();

            bool deleted = false;
            try
            {
                Contact searchContact = new Contact();
                ContactStore store = await ContactManager.RequestStoreAsync(ContactStoreAccessType.AppContactsReadWrite);
                ContactStore allAccessStore = await ContactManager.RequestStoreAsync(ContactStoreAccessType.AllContactsReadOnly);
                var lists = await store.FindContactsAsync();
              
                var contacts = from Contact scon in lists
                               where scon.FirstName.Contains(firstName)
                                       && scon.LastName.Contains(lastName)
                                       && ((phMobile != "" ? (scon.Phones.Count() > 0 ?
                                                              scon.Phones.ElementAt(2).Number.Contains(phMobile)
                                                               || scon.Phones.ElementAt(1).Number.Contains(phMobile)
                                                               || scon.Phones.ElementAt(0).Number.Contains(phMobile)
                                                               : scon.Phones.Count() > 1 ?
                                                                   scon.Phones.ElementAt(1).Number.Contains(phMobile)
                                                                   || scon.Phones.ElementAt(0).Number.Contains(phMobile)
                                                                   : scon.Phones.Count() > 0 ?
                                                                       scon.Phones.ElementAt(0).Number.Contains(phMobile) : false)
                                                                       : true)

                                       && (phHome != "" ? (scon.Phones.Count() > 1 ?
                                                              scon.Phones.ElementAt(2).Number.Contains(phHome)
                                                               || scon.Phones.ElementAt(1).Number.Contains(phHome)
                                                               || scon.Phones.ElementAt(0).Number.Contains(phHome)
                                                               : scon.Phones.Count() > 1 ?
                                                                   scon.Phones.ElementAt(1).Number.Contains(phHome)
                                                                   || scon.Phones.ElementAt(0).Number.Contains(phHome)
                                                                   : scon.Phones.Count() > 0 ?
                                                                       scon.Phones.ElementAt(0).Number.Contains(phHome) : false)
                                                                       : true)

                                       && (phWork != "" ? (scon.Phones.Count() > 2 ?
                                                              scon.Phones.ElementAt(2).Number.Contains(phWork)
                                                               || scon.Phones.ElementAt(1).Number.Contains(phWork)
                                                               || scon.Phones.ElementAt(0).Number.Contains(phWork)
                                                               : scon.Phones.Count() > 1 ?
                                                                   scon.Phones.ElementAt(1).Number.Contains(phWork)
                                                                   || scon.Phones.ElementAt(0).Number.Contains(phWork)
                                                                   : scon.Phones.Count() > 0 ?
                                                                       scon.Phones.ElementAt(0).Number.Contains(phWork) : false)
                                                                       : true)
                                          )

                               select scon;
                if (contacts.Count() >= 1)
                {
                    var l = await store.FindContactListsAsync();
                    Contact count = contacts.ElementAt(0);
                    ContactList list = l.FirstOrDefault((x) => x.DisplayName == "myList");
                    foreach (var dummy in l)
                    {
                        //Aggregrate all contacts
                        var rawContact = await allAccessStore.AggregateContactManager.FindRawContactsAsync(count);
                        foreach (var raw in rawContact)
                            await list.DeleteContactAsync(raw);
                    }
                    deleted = true;

                }

                else if (contacts.Count() == 0)
                {
                    deleted = false;
                }

            }
            catch (Exception e)
            {
                deleted = false;
                Log.Debug(e.Message);
            }

            return deleted;
        }
        internal async void FetchContact(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            var picker = new ContactPicker();
            picker.DesiredFieldsWithContactFieldType.Add(ContactFieldType.PhoneNumber);
            Contact icont =  await picker.PickContactAsync();
            if(icont == null)
            {
                Response.Fail(id, ErrorCode.CONTACT_SELECTION_FAIL);
                Log.Error("Contact Selection Fail");
                return;
            }
            ContactStore cmgr = await ContactManager.RequestStoreAsync();
            Contact contact = await cmgr.GetContactAsync(icont.Id);
            if (contact != null)
            {
                try
                {
                    JObject cinfo = new JObject();
                    cinfo[JsonKey.NAME] = contact.DisplayName;
                    string[] phones = new string[contact.Phones.Count];
                    string[] emails = new string[contact.Emails.Count];
                    int i = 0;
                    foreach (var number in contact.Phones)
                    {
                        phones[i] = number.Number;
                        i++;
                    }
                    i = 0;
                    foreach (var mail in contact.Emails)
                    {
                        emails[i] = mail.Address;
                        i++;
                    }
                    cinfo[JsonKey.PHONE_NUMBER] = string.Join(" , ", phones);
                    cinfo[JsonKey.EMAIL] = string.Join(" , ", emails);
                    Response.Success(id, cinfo);
                }
                catch (Exception)
                {
                    Response.Fail(id, ErrorCode.CONTACT_NOT_FOUND);
                    Log.Error("Contact Not Found");
                }
            }
            else
            {
                Response.Fail(id, ErrorCode.CONTACT_SELECTION_FAIL);
                Log.Error("Contact Selection Fail");
            }
        }
    }
}
