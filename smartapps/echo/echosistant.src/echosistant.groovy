/**
 * EchoSistant - The Ultimate Voice and Text Messaging Assistant Using Your Alexa Enable Device.
 *		
 *		11/06/2016		Version 1.0.1f	Additional Debug messages and Alexa missing profile Response
 *		11/06/2016		Version 1.0.1d	Debug measures fixed
 *		11/06/2016		Version 1.0.1c  Debug measures added
 *		11/05/2016		Version 1.0.1b	OAuth Fix and Version # update 
 *		11/05/2016 		Version 1.0.1a	OAuth Log error	@ 11:46EST OAuth - Bobby
 *		11/05/2016		Version 1.0.1	OAuth error fix
 *		11/04/2016      Version 1.0		Initial Release
 *
 * ROADMAP
 * - External TTS
 * - External SMS
 * - CoRE integration
 * - Replay last message
 *
 * Credits
 * Thank you to @MichaelS (creator of AskAlexa) for guidance and for letting me use his outstanding Wiki
 * and to begin this project using his code as a jump start.  Thanks goes to Keith @n8xd for his help with  
 * troubleshooting my lambda code. And a huge thank you to @SBDOBRESCU, the co-author of this project, for 
 * jumping on board and helping me expand this project into something more. 
 *
 *  Copyright 2016 Jason Headley
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
/**********************************************************************************************************************************************/
definition(
	name		: "EchoSistant",
	namespace	: "Echo",
	author		: "JH",
	description	: "The Ultimate Voice and Text Messaging Assistant Using Your Alexa Enable Device.",
	category	: "My Apps",
    singleInstance: true,
	iconUrl		: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/app-Echosistant.png",
	iconX2Url	: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/app-Echosistant@2x.png",
	iconX3Url	: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/app-Echosistant@2x.png")
/**********************************************************************************************************************************************/
preferences {  //SHOW MAIN PAGE
	page(name: "mainPage", title: "EchoSistant", install: true, uninstall: false) {
		section {
        	href "profiles", title: "Profiles", description: "Tap here to view and create new profiles....",
            image: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/Echosistant_Config.png"
		}
		section {
			href "about", title: "About EchoSistant", description: "Tap here for App information...Tokens, Version, License...",
            image: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/Echosistant_About.png"
		}
    }
}
	page(name: "profiles", title: "Profiles", install: true, uninstall: false) {
        section {
        	app(name: "Profiles", appName: "echosistantProfile", namespace: "Echo", description: "Create New Profile...", multiple: true)
            image: "https://github.com/BamaRayne/Echosistant/blob/master/smartapps/bamarayne/echosistant.src/Echosistant_Config.png"
	}
	}
page name: "about"
	def about(){
		dynamicPage(name: "about", uninstall: true) {
			section {
            	paragraph "${textAppName()}\n${textVersion()}\n${textCopyright()}",
            	image: "https://raw.githubusercontent.com/BamaRayne/Echosistant/master/smartapps/bamarayne/echosistant.src/app-Echosistant.png"
            }
            section ("Security Tokens - FOR PARENT APP ONLY"){
            	paragraph ("Log into the IDE on your computer and navigate to the Live Logs tab. Leave that window open, come back here, and open this section")
                input "ShowTokens", "bool", title: "Show Security Tokens", default: false, submitOnChange: true
                if (ShowTokens) paragraph "The Security Tokens are now displayed in the Live Logs section of the IDE"
            	def msg = state.accessToken != null ? state.accessToken : "Could not create Access Token. OAuth may not be enabled. "+
				"Go to the SmartApp IDE settings to enable OAuth."	
                if (ShowTokens) log.info "STappID = '${app.id}' , STtoken = '${state.accessToken}'"
		if (ShowTokens) log.info " API url: ${getApiServerUrl()}/api/smartapps/installations/${app.id}/cheat?access_token=${state.accessToken}"
                if (ShowTokens) paragraph "Access token:\n${msg}\n\nApplication ID:\n${app.id}"
			}
			section ("Revoke/Renew Access Token & Application ID"){
				href "Tokens", title: "Revoke/Reset Security Access Token", description: none
			}
			section ("Apache License"){
				input "ShowLicense", "bool", title: "Show License", default: false, submitOnChange: true
				def msg = textLicense()
					if (ShowLicense) paragraph "${msg}"
			}
            section("Debugging") {
            	input "debug", "bool", title: "Enable Debug Logging", default: false, submitOnChange: true 
            //    input "info", "bool", title: "Enable Information Logging", default: false, submitOnChange: true
                if (debug) log.info "${textAppName()}\n${textVersion()}"//\n${textCopyright()}"
                     }
           
			section ("Directions, information, and troubleshooting") { 
			href url:"http://thingsthataresmart.wiki/index.php?title=EchoSistant", title: "EchoSistant Wiki", description: none
            }
			section("Tap below to remove the ${textAppName()} application.  This will remove ALL Profiles and the App from the SmartThings mobile App."){}
		}
}      
page name: "Tokens"
	def Tokens(){
		dynamicPage(name: "Tokens", title: "Security Tokens", uninstall: false){
			section(""){
				paragraph "Tap below to Reset/Renew the Security Token. You must log in to the IDE and open the Live Logs tab before tapping here. "+
				"Copy and paste the displayed tokens into your Amazon Lambda Code."
				if (!state.accessToken) {
                	OAuthToken()
					paragraph "You must enable OAuth via the IDE to setup this app"
				}
            }
					def msg = state.accessToken != null ? state.accessToken : "Could not create Access Token. "+
    				"OAuth may not be enabled. Go to the SmartApp IDE settings to enable OAuth." 
					log.info "STappID = '${app.id}' , STtoken = '${state.accessToken}'"
					log.info " API url: ${getApiServerUrl()}/api/smartapps/installations/${app.id}/cheat?access_token=${state.accessToken}"
			section ("Reset Access Token / Application ID"){
				href "pageConfirmation", title: "Reset Access Token and Application ID", description: none
		}
	}
} 
page name: "pageConfirmation"
	def pageConfirmation(){
		dynamicPage(name: "pageConfirmation", title: "Reset/Renew Access Token Confirmation", uninstall: false){
			section {
				href "pageReset", title: "Reset/Renew Access Token", description: "Tap here to confirm action - READ WARNING BELOW"
				paragraph "PLEASE CONFIRM! By resetting the access token you will disable the ability to interface this SmartApp with your Amazon Echo."+
            	"You will need to copy the new access token to your Amazon Lambda code to re-enable access." +
				"Tap below to go back to the main menu with out resetting the token. You may also tap Done above."
			}
			section(" "){
        		href "mainPage", title: "Cancel And Go Back To Main Menu", description: none 
       	}
	}
}
page name: "pageReset"
	def pageReset(){
		dynamicPage(name: "pageReset", title: "Access Token Reset", uninstall: false){
			section{
				revokeAccessToken()
				state.accessToken = null
				OAuthToken()
				def msg = state.accessToken != null ? "New access token:\n${state.accessToken}\n\n" : "Could not reset Access Token."+
            	"OAuth may not be enabled. Go to the SmartApp IDE settings to enable OAuth."
				paragraph "${msg}"
                paragraph "The new access token and app ID are now displayed in the Live Logs tab of the IDE."
                log.info "STappID = '${app.id}' , STtoken = '${state.accessToken}'"
		log.info " API url: ${getApiServerUrl()}/api/smartapps/installations/${app.id}/cheat?access_token=${state.accessToken}"
			}
			section(" "){ 
        		href "mainPage", title: "Tap Here To Go Back To Main Menu", description: none 
        }
	}
} 	
//************************************************************************************************************
mappings {
      path("/t") {action: [GET: "processTts"]}
      }
//************************************************************************************************************
def installed() {
	if (debug) log.debug "Installed with settings: ${settings}"
	if (debug) log.trace "STappID = '${app.id}' , STtoken = '${state.accessToken}'"
	if (debug) log.info " API url: ${getApiServerUrl()}/api/smartapps/installations/${app.id}/cheat?access_token=${state.accessToken}"
	initialize()
}
def updated() {
	if (debug) log.debug "Updated with settings: ${settings}"
    initialize()
	unsubscribe()    
}
def initialize() {
def children = getChildApps()
if (debug) log.debug "$children.size Profiles installed"
children.each { child ->
//    log.debug "Child app id: $child.id"
}
     
	if (!state.accessToken) {
               OAuthToken()
		paragraph "You must enable OAuth via the IDE to setup this app"
		log.trace "STappID = '${app.id}' , STtoken = '${state.accessToken}'"  
		log.info " API url: ${getApiServerUrl()}/api/smartapps/installations/${app.id}/cheat?access_token=${state.accessToken}"
		}
    }

/*************************************************************************************************************
   CREATE INITIAL TOKEN
*************************************************************************************************************/
def OAuthToken(){
	try {
		createAccessToken()
		log.debug "Creating new Access Token"
	} catch (e) {
		log.error "Access Token not defined. OAuth may not be enabled. Go to the SmartApp IDE settings to enable OAuth."
	}
}
/************************************************************************************************************
   TEXT TO SPEECH PROCESS 
************************************************************************************************************/
def processTts() {
		def ptts = params.ttstext 
       		if (debug) log.debug "#1 Message received from Lambda (ptts) = '${ptts}'"
        def pttx = params.ttstext
        	if (debug) log.debug "#2 Message received from Lambda (pttx) = '${pttx}'"
        def pintentName = params.intentName
			if (debug) log.debug "#3 Profile being called = '${pintentName}'"
        def outputTxt = ''
        def dataSet = [ptts:ptts,pttx:pttx,pintentName:pintentName]        		
            childApps.each {child ->
    			child.profileEvaluate(dataSet)
                }
                
                childApps.each{ child ->
        		def cm = child.label      
          			if (cm == pintentName) {
                            outputTxt = child.outputTxt
                            if (outputTxt) {
                			}
                			else {
                            	outputTxt = "Message sent to ${pintentName} "
                            }
            		}
            }
            if (outputTxt == '' ) {
            	if (debug) log.debug "#4 No matching profile between profile: '${cm}' and intent '${pintentName}'"  
				outputTxt = "Sorry, I was unable to find a profile named ${pintentName}, please check your spelling"
        	}
 			
            if (debug) log.debug "#5 Alexa verbal response = '${outputTxt}'"  
			return ["outputTxt":outputTxt] 
}
/************************************************************************************************************
   Version/Copyright/Information/Help
************************************************************************************************************/
private def textAppName() {
	def text = "EchoSistant"
}	
private def textVersion() {
	def text = "Version 1.0.1f (11/07/2016)"
}
private def textCopyright() {
	def text = "Copyright © 2016 Jason Headley"
}
private def textLicense() {
	def text =
	"Licensed under the Apache License, Version 2.0 (the 'License'); "+
	"you may not use this file except in compliance with the License. "+
	"You may obtain a copy of the License at"+
	"\n\n"+
	" http://www.apache.org/licenses/LICENSE-2.0"+
	"\n\n"+
	"Unless required by applicable law or agreed to in writing, software "+
	"distributed under the License is distributed on an 'AS IS' BASIS, "+
	"WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. "+
	"See the License for the specific language governing permissions and "+
	"limitations under the License."
}
private def textHelp() {
	def text =
		"This smartapp allows you to use an Alexa device to generate a voice or text message on on a different device"
        "See our Wikilinks page for user information!"
}