function CordovaGeoLoc() { };

CordovaGeoLoc.getLocationName = function (lat, lng, onSuccess, onError) {
    cordova.exec(onSuccess, onError, 'CordovaGeoLoc', 'getLocationName', [lat, lng]);
};

CordovaGeoLoc.dealerSearch = function (addr, onSuccess) {
    cordova.exec(onSuccess, null, 'CordovaGeoLoc', 'dealerSearch', [addr]);
};

module.exports = CordovaGeoLoc;