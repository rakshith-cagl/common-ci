using Appzillon.Constants;
using Appzillon.Native;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Security.Credentials.UI;

namespace Appzillon.Plugins
{
    class BiometricAuthentication
    {
        #region Singleton Pattern
        private static BiometricAuthentication instance;
        private BiometricAuthentication()
        {
        }
        public static BiometricAuthentication Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new BiometricAuthentication();
                }
                return instance;
            }
        }
        #endregion

        internal async void FingerPrintAuthenticate(JObject obj)
        {
            string id = obj[JsonKey.ID].ToString();
            string availability_staus = await CheckFingerprintAvailability();
            string res = "";
            switch (availability_staus) {
                case JsonKey.FINGERPRINT_VERIFICATION_IS_AVAILABLE:
                 res= await RequestVerificationAsync(JsonKey.USER_MESSAGE);
                    break;
                default:
                    Response.Fail(id,availability_staus);
                    return;
            }
            switch (res) {
                case JsonKey.FINGERPRINT_VERIFIED:
                    Response.Success(id,false);
                    break;
                default:
                    Response.Fail(id,res);
                    break;
            }

        }

        private async Task<string> RequestVerificationAsync(string msg)
        {
            string returnMessage = "";
            try
            {
                UserConsentVerificationResult consentResult = await UserConsentVerifier.RequestVerificationAsync(msg);
                switch (consentResult)
                {
                    case UserConsentVerificationResult.Verified:
                        returnMessage = JsonKey.FINGERPRINT_VERIFIED;
                        break;
                    case UserConsentVerificationResult.DeviceBusy:
                        returnMessage = ErrorCode.BIOMETRIC_DEVICE_IS_BUSY;
                        break;
                    case UserConsentVerificationResult.DeviceNotPresent:
                        returnMessage = ErrorCode.NO_BIOMETRIC_DEVICE_FOUND;
                        break;
                    case UserConsentVerificationResult.DisabledByPolicy:
                        returnMessage = ErrorCode.BIOMETRIC_VERIFICATION_IS_DISABLED_BY_POLICY;
                        break;
                    case UserConsentVerificationResult.NotConfiguredForUser:
                        returnMessage = ErrorCode.NO_FINGERPRINTS_REGISTERED;
                        break;
                    case UserConsentVerificationResult.RetriesExhausted:
                        returnMessage = ErrorCode.TOO_MANY_FAILED_ATTEMPTS;
                        break;
                    case UserConsentVerificationResult.Canceled:
                        returnMessage = ErrorCode.AUTHENTICATION_CANCELLED;
                        break;
                    default:
                        returnMessage = ErrorCode.FINGERPRINTS_VERIFICATION_IS_CURRENTLY_UNAVAILABLE;
                        break;
                }
            }
            catch (Exception e)
            {
                // returnMessage = "Fingerprint authentication failed: " + ex.ToString();
                returnMessage = ErrorCode.AUTHENTICATION_FAILED;
                Log.Error(e.Message);
            }
            return returnMessage;
}

        private async Task<string> CheckFingerprintAvailability()
        {
            string returnMessage = "";
            try
            {
                // Check the availability of fingerprint authentication.
                var ucvAvailability = await UserConsentVerifier.CheckAvailabilityAsync();

                switch (ucvAvailability)
                {
                    case UserConsentVerifierAvailability.Available:
                        returnMessage = JsonKey.FINGERPRINT_VERIFICATION_IS_AVAILABLE;
                        break;
                    case UserConsentVerifierAvailability.DeviceBusy:
                        returnMessage = ErrorCode.BIOMETRIC_DEVICE_IS_BUSY;
                        break;
                    case UserConsentVerifierAvailability.DeviceNotPresent:
                        returnMessage = ErrorCode.NO_BIOMETRIC_DEVICE_FOUND;
                        break;
                    case UserConsentVerifierAvailability.DisabledByPolicy:
                        returnMessage = ErrorCode.BIOMETRIC_VERIFICATION_IS_DISABLED_BY_POLICY;
                        break;
                    case UserConsentVerifierAvailability.NotConfiguredForUser:
                        returnMessage = ErrorCode.NO_FINGERPRINTS_REGISTERED;
                        break;
                    default:
                        returnMessage = ErrorCode.FINGERPRINTS_VERIFICATION_IS_CURRENTLY_UNAVAILABLE;
                        break;
                }
            }
            catch (Exception e)
            {
                // returnMessage = "Fingerprint authentication availability check failed: " + ex.ToString();
                returnMessage = ErrorCode.FINGERPRINT_AUTHENTICATION_AVAILABILITY_CHECK_FAIL;
                Log.Error(e.Message);
            }

            return returnMessage; 
        }
    }
}
