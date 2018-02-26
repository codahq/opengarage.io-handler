/**
 *  OpenGarage SmartApp
 *
 *  Copyright 2018 Ben Rimmasch
 *
 */

definition(
    name: "HTTP Open/Close Endpoint",
    namespace: "me.bendy.opengarage",
    author: "Ben Rimmasch",
    description: "Creates an HTTP motion endpoint for your OpenGarage",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/intruder_motion-cam.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/intruder_motion-cam@2x.png"
)

preferences {
    page(name: "selectDevices", title: "Select the OpenGarage device", install: false, uninstall: true, nextPage: "viewURL") {
        section("Which door controller is the OpenGarage?") {
            input "og", "capability.doorControl", title: "Door Controller", required: true
        }
        section([mobileOnly:true]) {
            label title: "Assign a name", required: false
            mode title: "Set for specific mode(s)", required: false
        }
    }
    page(name: "viewURL", title: "viewURL", install: true)
}

def installed() {
    log.debug "Installed with settings: ${settings}"
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
}

mappings {
    path("/needs_update") {
        action: [
            GET: "openStatus"
        ]
    }
}

void doRefresh() {
    log.debug "Updated with settings: ${settings}"
    og?.refresh()
}

def generateURL() {
    createAccessToken()
    ["https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/open", "?access_token=${state.accessToken}"]
}

def viewURL() {
    dynamicPage(name: "viewURL", title: "HTTP Motion Endpoint", install:!resetOauth, nextPage: resetOauth ? "viewURL" : null) {
        section() {
            generateURL() 
            paragraph "Open: https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/open?access_token=${state.accessToken}"
            paragraph "Close: https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/close?access_token=${state.accessToken}"
        }
    }
}
