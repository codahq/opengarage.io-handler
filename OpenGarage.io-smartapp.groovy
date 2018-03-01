/**
 *  Copyright 2018 Ben Rimmasch
 *
 * 
 *  This SmartApp code can be found at:
 *  https://github.com/codahq/opengarage.io-handler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

definition(
    name: "OpenGarage HTTP Refresh Endpoint",
    namespace: "me.bendy.opengarage",
    author: "Ben Rimmasch",
    description: "Creates an HTTP refresh endpoint for your OpenGarage",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/garage_contact.png.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/garage_contact@2x.png"
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
    path("/do_update") {
        action: [
            GET: "doUpdate"
        ]
    }
}

void doUpdate() {
    log.debug "Updated with settings: ${settings}"
    og?.refresh()
}

def generateURL() {
    createAccessToken()
    ["https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/do_update", "?access_token=${state.accessToken}"]
}

def viewURL() {
    dynamicPage(name: "viewURL", title: "OpenGarage HTTP Refresh Endpoint", install:!resetOauth, nextPage: resetOauth ? "viewURL" : null) {
        section() {
            generateURL() 
            paragraph "Refresh: https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/do_update?access_token=${state.accessToken}"
            paragraph "Note that your base URL may not be https://graph.api.smartthings.com.  Verify your base API URL!"
        }
    }
}
