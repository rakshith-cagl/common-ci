//--------Contact Plugin file-----------
WinContainer.Contact = (function () {
    
    function deleteContactStore(req, callback) {
      
        var cont = req.deleteCriteria;
        var remoteID = cont.firstName ? cont.firstName : '';
        remoteID += cont.lastName ? cont.lastName : '';
        remoteID += cont.phoneMobile ? cont.phoneMobile : '';
        remoteID += cont.phoneWork ? cont.phoneWork : '';
        remoteID += cont.phoneHome ? cont.phoneHome : '';

        var pInformation = Windows.Phone.PersonalInformation;
        pInformation.ContactStore.createOrOpenAsync(
            pInformation.ContactStoreSystemAccessMode.readWrite,
            pInformation.ContactStoreApplicationAccessMode.readOnly)
            .then(function (store) {
                store.findContactByRemoteIdAsync(remoteID).then(
                    function (s) {
                        if (!s) {
                            callback(false, "APZ-CNT-044");
                            return;
                        }
                        s.store.deleteContactAsync(s.id).then(
                            function (d) {
                                callback(true);

                            }, function (e) {
                                callback(false, "APZ-CNT-043", e.message);
                                WinContainer.Log.error(e.description);
                            });
                    }, function (e) {
                        callback(false, "APZ-CNT-044", e.message);
                        WinContainer.Log.error(e.description);
                    });
            });
    }
    function searchContactStore(jsonObj, callback) {
        search_req_id = jsonObj.id;
      //  jsonObj.searchCriteria = {};
        var fName = jsonObj.searchCriteria.firstName ? jsonObj.searchCriteria.firstName : '';
        var lName = jsonObj.searchCriteria.lastName ? jsonObj.searchCriteria.lastName : '';
        var mobilePhone = jsonObj.searchCriteria.phoneMobile ? jsonObj.searchCriteria.phoneMobile : '';
        var workPhone = jsonObj.searchCriteria.workPhone ? jsonObj.searchCriteria.phoneWork : '';
        var homePhone = jsonObj.searchCriteria.homePhone ? jsonObj.searchCriteria.phoneHome : '';
        var checkPhone = (mobilePhone.length > 1 || workPhone.length > 1 || homePhone.length > 1) ? true : false;
        Windows.ApplicationModel.Contacts.ContactManager.requestStoreAsync().then(function (s) {
            s.findContactsAsync().then(function (response) {
                try {
                    var result = {};
                    result["searchResult"] = [];
                    if (response.length > 0) {
                        for (var i = 0; i < response.length; i++) {
                            var info = response[i];
                            if (info.firstName.indexOf(fName) >= 0 && info.lastName.indexOf(lName) >= 0) {
                                var objDetail = {};

                                objDetail["firstName"] = info.firstName;
                                objDetail["lastName"] = info.lastName;
                                objDetail["mail"] = info.emails.length > 0 ? info.emails[0].address : '';
                                objDetail["phoneMobile"] = '';
                                objDetail["phoneWork"] = '';
                                objDetail["phoneHome"] = '';
                                if (checkPhone) {
                                    var isHomePresent = true, isWorkPresent = true, isMobilePresent = true;
                                    if (info.phones.length == 0)
                                        continue;
                                    for (var j = 0; j < info.phones.length; j++) {

                                        if (info.phones[j].kind == 0) {
                                            (info.phones[j].number.indexOf(homePhone) >= 0) ? objDetail["phoneHome"] = info.phones[j].number : isHomePresent = false;
                                        }
                                        else if (info.phones[j].kind == 1) {
                                            (info.phones[j].number.indexOf(mobilePhone) >= 0) ? objDetail["phoneMobile"] = info.phones[j].number : isMobilePresent = false;
                                        }
                                        else if (info.phones[j].kind == 2) {
                                            (info.phones[j].number.indexOf(workPhone) >= 0) ? objDetail["phoneWork"] = info.phones[j].number : isWorkPresent = false;
                                        }
                                    }
                                    if (!(isWorkPresent && isMobilePresent && isHomePresent))
                                        continue;
                                } else {
                                    for (var j = 0; j < info.phones.length; j++) {
                                        if (info.phones[j].kind == 0)
                                            objDetail["phoneHome"] = info.phones[j].number;
                                        if (info.phones[j].kind == 1)
                                            objDetail["phoneMobile"] = info.phones[j].number;
                                        if (info.phones[j].kind == 2)
                                            objDetail["phoneWork"] = info.phones[j].number;
                                    }
                                }
                                objDetail["address"] = (info.addresses.length > 0) ?
                                    info.addresses[0].streetAddress + ", "
                                    + info.addresses[0].locality + ", "
                                    + info.addresses[0].region + ", "
                                    + info.addresses[0].country + ','
                                    + info.addresses[0].postalCode
                                    : '';
                                objDetail["website"] = info.website;
                                result["searchResult"].push(objDetail);
                                deleteContactID = info.id;
                            }
                        }
                        //result.id = search_req_id;

                        callback(true, result);
                    }
                    else {
                        callback(false);
                    }
                } catch (e) {
                    callback(false);
                }
            }, function (e) {
                callback(false);
            });
        });
    }
    function addContactStore(req, callback) {
        add_req_id = req.id;
        var details = req["details"];

        if (!details["firstName"]) {
            callback(false, "APZ-CNT-041");
            return;
        }
        var pInformation = Windows.Phone.PersonalInformation;

        var contactStore = pInformation.ContactStore.createOrOpenAsync(
                     pInformation.ContactStoreSystemAccessMode.readWrite,
                      pInformation.ContactStoreApplicationAccessMode.readOnly)
            .then(function (s) {
                var storedContact = new Windows.Phone.PersonalInformation.StoredContact(s);
                storedContact.GivenName = details["firstName"];
                storedContact.getPropertiesAsync().then(function (properties) {
                    var firstName = details["firstName"] ? details["firstName"] : '';
                    properties[pInformation.KnownContactProperties.givenName] = firstName;
                    var lastName = details["lastName"] ? details["lastName"] : '';
                    properties[pInformation.KnownContactProperties.familyName] = lastName;
                    var phoneMobile = details["phoneMobile"] ? details["phoneMobile"] : '';
                    properties[pInformation.KnownContactProperties.mobileTelephone] = phoneMobile;
                    var phoneWork = details["phoneWork"] ? details["phoneWork"] : '';
                    properties[pInformation.KnownContactProperties.workTelephone] = phoneWork;

                    var phoneHome = details["phoneHome"] ? details["phoneHome"] : '';
                    properties[pInformation.KnownContactProperties.telephone] = phoneHome;

                    var email = details["mail"] ? details["mail"] : '';
                    properties[pInformation.KnownContactProperties.email] = email;

                    var add = new Windows.Phone.PersonalInformation.ContactAddress();
                    add.streetAddress = details["address"] ? details["address"] : '';
                    properties[pInformation.KnownContactProperties.address] = add;

                    var web = details["website"] ? details["website"] : "";
                    properties[pInformation.KnownContactProperties.url] = web;
                    storedContact.remoteId = firstName + lastName + phoneMobile + phoneWork + phoneHome;
                }).then(function (s) {
                    storedContact.saveAsync().then(function (s) {
                        callback(true);
                    }, function (e) {
                        callback(false, "APZ-CNT-041", e.message);
                        WinContainer.Log.error(e.description);
                    });
                });
            }, function (e) {
                callback(false, "APZ-CNT-040", e.message);
                WinContainer.Log.error(e.description);
            });
    }

    function contactAdd(req) {
        addContactStore(req, function (s, r, m) {
            if (s) {
                var res = {};
                res.id = req.id;
                WinContainer.successCallback(res);
            } else {
                WinContainer.failureCallback(req.id, r, m);
            }
        });
    }
    function contactSearch(req) {
        searchContactStore(req, function (status, result, msg) {
            if (status) {
                result.id = req.id;
                WinContainer.successCallback(result);
            } else {
                WinContainer.failureCallback(req.id, "APZ-CNT-044");
            }
        });
    }
    function contactDelete(req) {
        delete_req_id = req.id;
        var json = {};
        json.searchCriteria = req.deleteCriteria;
        searchContactStore(json, function (s, j) {
            if (s) {
                if (j.searchResult.length == 1) {
                    var json = {};
                    json.deleteCriteria = j.searchResult[0];
                    deleteContactStore(json, function (s, r, m) {
                        if (s) {
                            var res = {};
                            res.id = req.id;
                            WinContainer.successCallback(res);
                        } else {
                            WinContainer.failureCallback(req.id, r, m);
                        }
                    });
                }
                else {
                    WinContainer.failureCallback(delete_req_id, "APZ-CNT-045");
                }
            } else {
                WinContainer.failureCallback(delete_req_id, "APZ-CNT-044");
            }
        });
    }
    function contactEdit(req) {
    
        var json = {};
        json.searchCriteria = req.searchCriteria;
        
        searchContactStore(json, function (s, j) {
            if (s) {
                if (j.searchResult.length == 1) {
                    var json = {};
                    json.deleteCriteria = j.searchResult[0];
                    deleteContactStore(json, function (s, r, m) {
                        if (s) {
                            addContactStore(req, function (s, r, m) {
                                if (s) {
                                    var res = {};
                                    res.id = req.id;
                                    WinContainer.successCallback(res);
                                } else {
                                    WinContainer.failureCallback(req.id, r, m);
                                }
                            });
                        } else {
                            WinContainer.failureCallback(req.id, r, m);
                        }
                    });
                }
                else {
                    WinContainer.failureCallback(req.id, "APZ-CNT-045");
                }
            } else {
                WinContainer.failureCallback(req.id, "APZ-CNT-044");
            }
        });

    }

    function fetchContact(json) {
        var contactPicker = new Windows.ApplicationModel.Contacts.ContactPicker();
        contactPicker.desiredFieldsWithContactFieldType.append(Windows.ApplicationModel.Contacts.ContactFieldType.phoneNumber);

        contactPicker.pickContactAsync().done(function (cont) {
            if (cont == null) {
                WinContainer.failureCallback(json.id, "APZ-CNT-044");
                return;
            }
            Windows.ApplicationModel.Contacts.ContactManager.requestStoreAsync().then(function (s) {
                s.getContactAsync(cont.id).then(function (contact) {
                    var emails = [];
                    var phones = [];
                    var result = {};
                    result.id = json.id;
                    if (contact != null) {
                        try {
                            result["name"] = contact.displayName;
                            for (var i = 0; i < contact.phones.length ; i++)
                                phones.push(contact.phones.getAt(i).number);
                            for (var j = 0; j < contact.emails.length; j++)
                                emails.push(contact.emails.getAt(j).address);
                            result["phoneNo"] = phones;
                            result["email"] = emails;
                            WinContainer.successCallback(result);
                        }
                        catch (e) {
                            WinContainer.failureCallback(json.id, "APZ-CNT-082", e.message);
                            WinContainer.Log.error(e.description);
                        }
                    } else {
                        WinContainer.failureCallback(json.id, "APZ-CNT-082", e.message);
                    }
                });
            }, function (e) {
                WinContainer.failureCallback(json.id, "APZ-CNT-082", e.message);
                WinContainer.Log.error(e.description);
            });

        }, function (e) {
            WinContainer.failureCallback(json.id, "APZ-CNT-082", e.message);
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

