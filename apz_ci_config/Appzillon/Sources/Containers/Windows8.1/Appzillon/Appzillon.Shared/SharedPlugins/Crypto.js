//--------CryptoPlugin files-----------
WinContainer.Crypto = (function () {

     function deriveKey (keyParameter) {

        var derivedKeyBuffer;

        // Initialize the encryption and decryption parameters. 
        var cipherAlgNameString = Windows.Security.Cryptography.Core.SymmetricAlgorithmNames.aesCbcPkcs7,
            algBlockSizeInBytes = 16;

        //Swap first two and last two characters of keyParameter
        var kdfSaltString = keyParameter;
        var firstChar = kdfSaltString.charAt(0);
        var secondChar = kdfSaltString.charAt(1);
        var lastChar = kdfSaltString.charAt(kdfSaltString.length - 1);
        var secLastChar = kdfSaltString.charAt(kdfSaltString.length - 2);

        var midChars = kdfSaltString.substring(2, kdfSaltString.length - 2);
        kdfSaltString = secondChar + firstChar + midChars + lastChar + secLastChar;

        // Initialize the key derivation parameters. 
        var kdfAlgNameString = Windows.Security.Cryptography.Core.KeyDerivationAlgorithmNames.pbkdf2Sha1,
            kdfIterationCount = 2;

        // Convert the keyParameter to binary. 
        var secret = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(keyParameter, Windows.Security.Cryptography.BinaryStringEncoding.utf8);

        // Initialize the key derivation function (PBKDF2) parameters. 
        var salt = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(kdfSaltString, Windows.Security.Cryptography.BinaryStringEncoding.utf8);
        var pbkdf2Params = Windows.Security.Cryptography.Core.KeyDerivationParameters.buildForPbkdf2(salt, kdfIterationCount);

        // Open the PBKDF2_SHA256 algorithm provider. 
        var algorithmProvider = Windows.Security.Cryptography.Core.KeyDerivationAlgorithmProvider.openAlgorithm(kdfAlgNameString);

        // Create a secret key. 
        var secretKey = algorithmProvider.createKey(secret);

        // Peform the derivation. 
        derivedKeyBuffer = Windows.Security.Cryptography.Core.CryptographicEngine.deriveKeyMaterial(secretKey, pbkdf2Params, algBlockSizeInBytes);

        return derivedKeyBuffer;
    };
    function encryptDataBuffer(derivedKeyBuffer, stringToEncrypt, algNameString, keysize, ivBuffer) {
        var encryptedBuffer;

        // Convert the input string, stringToEncrypt, to binary. 
        var inputDataBuffer = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(stringToEncrypt, Windows.Security.Cryptography.BinaryStringEncoding.utf8);

        // Open the algorithm provider specified by the algNameString input parameter. 
        var algorithmProvider = Windows.Security.Cryptography.Core.SymmetricKeyAlgorithmProvider.openAlgorithm(algNameString);

        // Create a symmetric key. 
        var symmetricKey = algorithmProvider.createSymmetricKey(derivedKeyBuffer);

        // Encrypt the input string. 
        encryptedBuffer = Windows.Security.Cryptography.Core.CryptographicEngine.encrypt(symmetricKey, inputDataBuffer, ivBuffer);

        return encryptedBuffer;
    }
    function encryptData(text, key) {
        var cipherAlgNameString = Windows.Security.Cryptography.Core.SymmetricAlgorithmNames.aesCbcPkcs7;
        var reverseKey = '';
        for (var i = key.length - 1; i >= 0; --i) {
            reverseKey += key.charAt(i);
        }

        var bytes = [];

        for (var i = 0; i < reverseKey.length; ++i) {
            bytes.push(reverseKey.charCodeAt(i));
            bytes[i] = bytes[i] >> 1;
        }

        // Derive a key 
        var derivedKeyBuffer = deriveKey(key);

        // Convert the initialization vector string to binary. 
        //var ivBuffer = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(ivBuffer, Windows.Security.Cryptography.BinaryStringEncoding.utf8);
        var reverseIvBuffer = Windows.Security.Cryptography.CryptographicBuffer.createFromByteArray(bytes);

        // Encrypt the input string. 
        var encryptedDataBuffer = encryptDataBuffer(derivedKeyBuffer, text, cipherAlgNameString, derivedKeyBuffer.length, reverseIvBuffer);
        return Windows.Security.Cryptography.CryptographicBuffer.encodeToBase64String(encryptedDataBuffer);
    }
    function decryptDataBuffer(derivedKeyBuffer, stringToDecrypt, algNameString, keysize, ivBuffer) {
        var decryptedBuffer = null;

        try {

            // Convert the input string, stringToDecrypt, to binary. 
            var inputDataBuffer = Windows.Security.Cryptography.CryptographicBuffer.decodeFromBase64String(stringToDecrypt);

            // Open the algorithm provider specified by the algNameString input parameter. 
            var algorithmProvider = Windows.Security.Cryptography.Core.SymmetricKeyAlgorithmProvider.openAlgorithm(algNameString);

            // Create a symmetric key. 
            var symmetricKey = algorithmProvider.createSymmetricKey(derivedKeyBuffer);

            // Decrypt the input string. 
            decryptedBuffer = Windows.Security.Cryptography.Core.CryptographicEngine.decrypt(symmetricKey, inputDataBuffer, ivBuffer);
        }
        catch (e) {
            // Ignore decryption errors 
        }
        return decryptedBuffer;
    }
    function decrypt(stringToDecrypt, key) {
        var cipherAlgNameString = Windows.Security.Cryptography.Core.SymmetricAlgorithmNames.aesCbcPkcs7;
        var reverseKey = '';
        for (var i = key.length - 1; i >= 0; --i) {
            reverseKey += key.charAt(i);
        }

        var bytes = [];

        for (var i = 0; i < reverseKey.length; ++i) {
            bytes.push(reverseKey.charCodeAt(i));
            bytes[i] = bytes[i] >> 1;
        }

        // Derive a key 
        var derivedKeyBuffer = deriveKey(key);

        // Convert the initialization vector string to binary. 
        //var ivBuffer = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(ivString, Windows.Security.Cryptography.BinaryStringEncoding.utf8);
        var reverseIvBuffer = Windows.Security.Cryptography.CryptographicBuffer.createFromByteArray(bytes);

        // Decrypt the data. 
        var decryptedDataBuffer = decryptDataBuffer(derivedKeyBuffer, stringToDecrypt, cipherAlgNameString, derivedKeyBuffer.length, reverseIvBuffer);

        return Windows.Security.Cryptography.CryptographicBuffer.convertBinaryToString(Windows.Security.Cryptography.BinaryStringEncoding.utf8, decryptedDataBuffer);
    }
    function getValidKey(key) {
        var keyLength = key.length;
        if (keyLength > 16) {
            key = key.substring(0, 16);
        }
        else if (keyLength < 16) {
            var count = 16 - keyLength;
            var pad = "";
            for (var i = 0; i < count; i++) {
                pad = pad + "$";
            }
            key = key + pad;
        }
        return key;
    }
    var _Encrypt = function (req) {
        try {
            var id = req.id;
            var data = req.stringToEncrypt;
            var key = getValidKey(req.key);

            var res = {};
            res.id = req.id;
            res.text = encryptData(data, key);
            WinContainer.successCallback(res);

        }
        catch (e) {
            WinContainer.Log.fatal(e.description);
            WinContainer.failureCallback(req.id,"APZ-CNT-048");
        }
    }
    
    var _Decrypt = function (req) {
        try {
            var id = req.id;
            var stringToDecrypt = req.stringToDecrypt;
            var key = getValidKey(req.key);

            var res = {};
            res.id = req.id;
            res.text = decrypt(stringToDecrypt, key);
            WinContainer.successCallback(res);
        } catch (e) {
            WinContainer.Log.fatal(e.description);
            WinContainer.failureCallback(req.id, "APZ-CNT-046");
        }
    }
    return {
        EncryptData: _Encrypt,
        DecryptData: _Decrypt
    }
})();
WinContainer.plugin.fileEncrypt = function (json) {

    //if (!appzillon.plugin.validateFileEncDesc(json))
    //     return;
    var id = json.id;
    var key = json.key;
    var srcFilePath = json.srcFilePath;
    srcFilePath =replaceAll(srcFilePath, "/", "\\");
    var destFilePath = json.destFilePath;
    destFilePath = replaceAll(destFilePath, "/", "\\");
    try {
        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
        var pat = localFolder.path + srcFilePath;
        localFolder.getFileAsync(srcFilePath).done(function (file) {

            file.openAsync(Windows.Storage.FileAccessMode.read).done(function (stream) {

                var keyLength = key.length;
                if (keyLength > 16) {
                    key = key.substring(0, 16);
                } else if (keyLength < 16) {
                    var count = 16 - keyLength;
                    var pad = "";
                    for (var i = 0; i < count; i++) {
                        pad = pad + "$";
                    }
                    key = key + pad;
                }

                var encryptedData;

                // Initialize the encryption and decryption parameters. 
                var cipherAlgNameString = Windows.Security.Cryptography.Core.SymmetricAlgorithmNames.aesCbcPkcs7,
                    algBlockSizeInBytes = 16;
                var inputStream = stream.getInputStreamAt(0);
                var size = stream.size;
                var reader = new Windows.Storage.Streams.DataReader(inputStream);
                reader.loadAsync(size).then(function () {
                    var binary = reader.readBuffer(size);
                    WinContainer.Log.debug("file encryption started");
                    encryptData(binary, key);

                });

                // Encrypt function 
                function encryptData(binaryToEncrypt, key) {

                    var reverseKey = '';
                    for (var i = key.length - 1; i >= 0; --i) {
                        reverseKey += key.charAt(i);
                    }

                    var bytes = [];

                    for (var i = 0; i < reverseKey.length; ++i) {
                        bytes.push(reverseKey.charCodeAt(i));
                        bytes[i] = bytes[i] >> 1;
                    }

                    // Derive a key 
                    var derivedKeyBuffer = deriveKey(key);

                    // Convert the initialization vector string to binary. 
                    //var ivBuffer = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(ivBuffer, Windows.Security.Cryptography.BinaryStringEncoding.utf8);
                    var reverseIvBuffer = Windows.Security.Cryptography.CryptographicBuffer.createFromByteArray(bytes);

                    // Encrypt the input string. 
                    var encryptedDataBuffer = encryptDataBuffer(derivedKeyBuffer, binaryToEncrypt, cipherAlgNameString, derivedKeyBuffer.length, reverseIvBuffer);
                    var encryptedDataString = Windows.Security.Cryptography.CryptographicBuffer.encodeToBase64String(encryptedDataBuffer);
                    localFolder.createFileAsync(destFilePath, Windows.Storage.CreationCollisionOption.replaceExisting).then(function (file) {
                        var fileWrite = Windows.Storage.FileIO.writeTextAsync(file, encryptedDataString).done(function () {
                            WinContainer.Log.debug("file encryption done");
                           // WinContainer.sendAuditLog("FileEncryption", "END");
                            var data = JSON.stringify({
                                successMessage: "File encrypted Successfully."
                            }, null, " ");
                            var json = JSON.parse(data);
                            json.id = id;
                            json.filePath = file.path;
                            WinContainer.successCallback(json);
                        });
                    }, function (e) {
                       // if (auditStartEntry)
                          //  WinContainer.sendAuditLog("FileEncryption", "END");
                       // WinContainer.storeLog(e.description, "E");

                    });
                }

                // Encrypt a data buffer. 
                function encryptDataBuffer(derivedKeyBuffer, binaryToEncrypt, algNameString, keysize, ivBuffer) {
                    var encryptedBuffer;

                    // Convert the input string, stringToEncrypt, to binary. 
                    // var inputDataBuffer = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(stringToEncrypt, Windows.Security.Cryptography.BinaryStringEncoding.utf8);

                    // Open the algorithm provider specified by the algNameString input parameter. 
                    var algorithmProvider = Windows.Security.Cryptography.Core.SymmetricKeyAlgorithmProvider.openAlgorithm(algNameString);

                    // Create a symmetric key. 
                    var symmetricKey = algorithmProvider.createSymmetricKey(derivedKeyBuffer);

                    // Encrypt the input string. 
                    encryptedBuffer = Windows.Security.Cryptography.Core.CryptographicEngine.encrypt(symmetricKey, binaryToEncrypt, ivBuffer);

                    return encryptedBuffer;
                }
            });
        }, function (e) {
            WinContainer.Log.error(e.message);
            WinContainer.failureCallback(id, "APZ-CNT-190");

        });
    } catch (e) {
        WinContainer.Log.error(e.description);
        WinContainer.failureCallback(id, "APZ-CNT-209");


    }

}

WinContainer.plugin.fileDecrypt = function (json) {
    var id = json.id;
    var key = json.key;
    var srcFilePath = json.srcFilePath;
    srcFilePath = replaceAll(srcFilePath, "/", "\\");

    var destFilePath = json.destFilePath;
    destFilePath = replaceAll(destFilePath, "/", "\\");
    try {
        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
        localFolder.getFileAsync(srcFilePath).done(function (file) {

            Windows.Storage.FileIO.readTextAsync(file).done(function (base64String) {
                var keyLength = key.length;
                if (keyLength > 16) {
                    key = key.substring(0, 16);
                } else if (keyLength < 16) {
                    var count = 16 - keyLength;
                    var pad = "";
                    for (var i = 0; i < count; i++) {
                        pad = pad + "$";
                    }
                    key = key + pad;
                }

                WinContainer.Log.debug("file decryption start");
                // Initialize the encryption and decryption parameters. 
                var cipherAlgNameString = Windows.Security.Cryptography.Core.SymmetricAlgorithmNames.aesCbcPkcs7,
                    algBlockSizeInBytes = 16;
                decrypt(base64String, key);




                function decrypt(base64String, key) {

                    var reverseKey = '';
                    for (var i = key.length - 1; i >= 0; --i) {
                        reverseKey += key.charAt(i);
                    }

                    var bytes = [];

                    for (var i = 0; i < reverseKey.length; ++i) {
                        bytes.push(reverseKey.charCodeAt(i));
                        bytes[i] = bytes[i] >> 1;
                    }

                    // Derive a key 
                    var derivedKeyBuffer = deriveKey(key);

                    // Convert the initialization vector string to binary. 
                    //  var ivBuffer = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(ivString, Windows.Security.Cryptography.BinaryStringEncoding.utf8);
                    var reverseIvBuffer = Windows.Security.Cryptography.CryptographicBuffer.createFromByteArray(bytes);

                    // Decrypt the data. 
                    var decryptedDataBuffer = decryptDataBuffer(derivedKeyBuffer, base64String, cipherAlgNameString, derivedKeyBuffer.length, reverseIvBuffer);

                    if (!decryptedDataBuffer) {
                        return false;
                    }


                    localFolder.createFileAsync(destFilePath, Windows.Storage.CreationCollisionOption.replaceExisting).then(function (file) {

                        Windows.Storage.FileIO.writeBufferAsync(file, decryptedDataBuffer).done(function () {
                            WinContainer.Log.debug("file decryption done");
                            var data = JSON.stringify({
                                successMessage: "File decrypted Successfully."
                            }, null, " ");
                            var json = JSON.parse(data);
                            json.id = id;
                            json.filePath = file.path;
                            WinContainer.successCallback(json);
                        });

                    }, function (e) {
                        WinContainer.Log.error(e.message);
                        WinContainer.failureCallback(id, "APZ-CNT-082");
                    });


                }

                // Decrypt a data buffer. 
                function decryptDataBuffer(derivedKeyBuffer, base64String, algNameString, keysize, ivBuffer) {
                    var decryptedBuffer = null;

                    try {

                        // Convert the input string, stringToDecrypt, to binary. 
                        var inputDataBuffer = Windows.Security.Cryptography.CryptographicBuffer.decodeFromBase64String(base64String);

                        // Open the algorithm provider specified by the algNameString input parameter. 
                        var algorithmProvider = Windows.Security.Cryptography.Core.SymmetricKeyAlgorithmProvider.openAlgorithm(algNameString);

                        // Create a symmetric key. 
                        var symmetricKey = algorithmProvider.createSymmetricKey(derivedKeyBuffer);

                        // Decrypt the input string. 
                        decryptedBuffer = Windows.Security.Cryptography.Core.CryptographicEngine.decrypt(symmetricKey, inputDataBuffer, ivBuffer);
                    } catch (e) {
                        // Ignore decryption errors 
                    }
                    return decryptedBuffer;
                }
            });
        }, function (e) {
            WinContainer.Log.error(e.message);
            WinContainer.failureCallback(id, "APZ-CNT-082");
        });
    } catch (e) {
        WinContainer.Log.error(e.description);
        var data = JSON.stringify({
            errorDescription: "Failed to decrypt file"
        }, null, " ");
        var json = JSON.parse(data);
        WinContainer.failureCallback(id, "APZ-CNT-210");
    }
}

function deriveKey(keyParameter) {

    var derivedKeyBuffer;

    // Initialize the encryption and decryption parameters. 
    var cipherAlgNameString = Windows.Security.Cryptography.Core.SymmetricAlgorithmNames.aesCbcPkcs7,
        algBlockSizeInBytes = 16;

    //Swap first two and last two characters of keyParameter
    var kdfSaltString = keyParameter;
    var firstChar = kdfSaltString.charAt(0);
    var secondChar = kdfSaltString.charAt(1);
    var lastChar = kdfSaltString.charAt(kdfSaltString.length - 1);
    var secLastChar = kdfSaltString.charAt(kdfSaltString.length - 2);

    var midChars = kdfSaltString.substring(2, kdfSaltString.length - 2);
    kdfSaltString = secondChar + firstChar + midChars + lastChar + secLastChar;

    // Initialize the key derivation parameters. 
    var kdfAlgNameString = Windows.Security.Cryptography.Core.KeyDerivationAlgorithmNames.pbkdf2Sha1,
        kdfIterationCount = 2;

    // Convert the keyParameter to binary. 
    var secret = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(keyParameter, Windows.Security.Cryptography.BinaryStringEncoding.utf8);

    // Initialize the key derivation function (PBKDF2) parameters. 
    var salt = Windows.Security.Cryptography.CryptographicBuffer.convertStringToBinary(kdfSaltString, Windows.Security.Cryptography.BinaryStringEncoding.utf8);
    var pbkdf2Params = Windows.Security.Cryptography.Core.KeyDerivationParameters.buildForPbkdf2(salt, kdfIterationCount);

    // Open the PBKDF2_SHA256 algorithm provider. 
    var algorithmProvider = Windows.Security.Cryptography.Core.KeyDerivationAlgorithmProvider.openAlgorithm(kdfAlgNameString);

    // Create a secret key. 
    var secretKey = algorithmProvider.createKey(secret);

    // Peform the derivation. 
    derivedKeyBuffer = Windows.Security.Cryptography.Core.CryptographicEngine.deriveKeyMaterial(secretKey, pbkdf2Params, algBlockSizeInBytes);

    return derivedKeyBuffer;
};
