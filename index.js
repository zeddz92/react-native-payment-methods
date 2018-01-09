'use strict';
let {NativeModules, Platform} = require('react-native')
let {PayPal, MFLReactNativePayPal} = NativeModules;

let constants;

if (Platform.OS === 'android') {
    constants = {};
    let constantNames = Object.keys(PayPal).filter(p => p == p.toUpperCase());
    constantNames.forEach(c => constants[c] = PayPal[c]);
} else {
    constants = {
        SANDBOX: 0,
        PRODUCTION: 1,
        NO_NETWORK: 2,

        USER_CANCELLED: 'USER_CANCELLED',
        INVALID_CONFIG: 'INVALID_CONFIG'
    }
}

let functions = {
    paymentRequest(payPalParameters) {
        return new Promise(function(resolve, reject) {
            if (Platform.OS === 'android') {
                PayPal.paymentRequest(payPalParameters, resolve, reject);
            }
        });
    }
};

var exported = {};
Object.assign(exported, constants, functions);

module.exports = exported;