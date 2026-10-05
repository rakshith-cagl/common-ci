//--------Contact Plugin files-----------
WinContainer.Contact = (function () {

    function contactAdd(req) {
        try {
            var details = req["details"];
            if (!details["firstName"]) {
                WinContainer.failureCallback(req.id, "APZ-CNT-041")
                return;
            }
            WL.init();
            WL.login({
                scope: ["wl.basic", " wl.emails", "wl.contacts_emails", "wl.contacts_phone_numbers", "wl.offline_access", "wl.contacts_postal_addresses"]
            }).then(function (response) {
                var a = response;
                WL.api({
                    path: "me/contacts",
                    method: "POST",
                    body: {
                        first_name: req.details.firstName,
                        last_name: req.details.lastName,
                        emails: {
                            personal: req.details.mail
                        },
                        phones: {
                            mobile: req.details.phoneMobile,
                            business: req.details.phoneWork,
                            personal: req.details.phoneHome,
                        },
                        addresses: {
                            personal:
                                {
                                    street: req.details.address
                                }
                        }
                    }
                });
                var res = {};
                res.id = req.id;
                WinContainer.successCallback(res);
                WL.logout();
            },
            function (e) {
                WinContainer.failureCallback(req.id, "APZ-CNT-082", e.message);
                WinContainer.Log.error(e.message);
                WL.logout();
            }
        );

        }
        catch (e) {
            WinContainer.failureCallback(req.id, "APZ-CNT-082", e.message);
            WinContainer.Log.error(e.description);
        }
    }
    function contactSearch(req) {
        try {
            var fName = req.searchCriteria.firstName;
            var lName = req.searchCriteria.lastName;
            var mobilePhone = req.searchCriteria.phoneMobile;
            var buisnessPhone = req.searchCriteria.workPhone;
            var personalPhone = req.searchCriteria.homePhone;
            if (fName == null) fName = '';
            if (lName == null) lName = '';
            if (mobilePhone == null) mobilePhone = '';
            if (buisnessPhone == null) buisnessPhone = '';
            if (personalPhone == null) personalPhone = '';

            WL.init();

            var str = "";

            WL.login({
                scope: ["wl.basic", " wl.emails", "wl.contacts_emails", "wl.contacts_phone_numbers", "wl.offline_access", "wl.contacts_postal_addresses"]
            }).then(
           function (response) {

               WL.api({
                   path: "me/contacts",
                   method: "GET"
               }).then(
                   function (response) {
                       var result = {};
                       result["searchResult"] = [];
                       if (response.data.length > 0) {
                           for (var i = 0; i < response.data.length; i++) {
                               try {
                                   if ((response.data[i].first_name.toLowerCase().indexOf(fName.toLowerCase()) >= 0 && response.data[i].last_name.toLowerCase().indexOf(lName.toLowerCase()) >= 0) && response.data[i].phones.mobile.indexOf(mobilePhone) >= 0) {
                                       var objDetail = {};
                                       objDetail["firstName"] = response.data[i].first_name;
                                       objDetail["lastName"] = response.data[i].last_name;
                                       objDetail["mail"] = response.data[i].emails.preferred;
                                       objDetail["phoneMobile"] = response.data[i].phones.mobile;
                                       objDetail["phoneWork"] = response.data[i].phones.business;
                                       objDetail["phoneHome"] = response.data[i].phones.personal;
                                       objDetail["address"] = response.data[i].addresses.personal.street + ", " + response.data[i].addresses.personal.city + ", " + response.data[i].addresses.personal.state + ", " + response.data[i].addresses.personal.region + ", " + response.data[i].addresses.personal.postal_code;
                                       result["searchResult"].push(objDetail);
                                   }
                               }
                               catch (e) {
                                   WinContainer.Log.error(e.description);
                               }
                           }

                           result.id = req.id;
                           WinContainer.successCallback(result);
                       }
                       else {
                           WinContainer.failureCallback(req.id, "APZ-CNT-082");
                       }

                   }, function (e) {
                       WinContainer.failureCallback(req.id, "APZ-CNT-082", e.message);
                       WinContainer.Log.error(e.message);
                   });
           }, function (e) {
               WinContainer.failureCallback(req.id, "APZ-CNT-082", e.error_description);
               WinContainer.Log.error(e.error_description);
           });
        }
        catch (e) {
            WinContainer.failureCallback(req.id, "APZ-CNT-040", e.message);
            WinContainer.Log.error(e.description);
        }
    }
    function contactDelete(req) {
        WinContainer.failureCallback(req.id,"APZ-CNT-022");
    }
    function contactEdit(req) {
        WinContainer.failureCallback(req.id, "APZ-CNT-022");
    }

    function fetchContact(req) {
        var contactPicker = new Windows.ApplicationModel.Contacts.ContactPicker();//.desiredFieldsWithContactFieldType;
       
        contactPicker.pickContactAsync().done(function (contact) {
            var emails = [];
            var phones = [];
            var result = {};
            if (contact != null) {
                try {
                    result["name"] = contact.displayName;
                    for (var i = 0; i < contact.phones.length ; i++)
                        phones.push(contact.phones.getAt(i).number);
                    for (var j = 0; j < contact.emails.length; j++)
                        emails.push(contact.emails.getAt(j).address);
                    result["phoneNo"] = phones;
                    result["email"] = emails;
                    result.id = req.id;
                    WinContainer.successCallback(result);
                }
                catch (e) {
                    WinContainer.failureCallback(req.id, "APZ-CNT-082");
                    WinContainer.Log.error(e.description);
                }
            } else {
                WinContainer.failureCallback(req.id, "APZ-CNT-082");
            }
        }, function (e) {
            WinContainer.failureCallback(req.id, "APZ-CNT-082");
            WinContainer.Log.error(e.message);
        });
    };
    return {
        add: contactAdd,
        search: contactSearch,
        del: contactDelete,
        edit: contactEdit,
        fetch: fetchContact
    }
})();