// CobbleVision-API.java
// File contains code to communicate with the CobbleVision API

import java.util.concurrent.CompletableFuture
import org.bson.types.ObjectId 
import org.json.simple.JSONObject

// #################################################
// Preparation of Variables and Environment Settings.
// #################################################
public class GlobalVars {
  public Boolean environmentType = false
  public String serverAdress = "https://www.cobblevision.com"
  public Boolean debugging = true
}

public class CobbleVisionAPI{
  private String[] valid_price_categories = ["high", "medium", "low"]
  private String[] valid_job_types = ["QueuedJob"]
  
  if(GlobalVars.environmentType == false || Globalvars.environmentType === "demo"){
    private String BaseURL = "https://www.cobblevision.com"
  }else{
    private String BaseURL = GlobalVars.serverAdress + "/api/"
  }
  
  private String apiUserName = ""
  private String apiToken = ""
  
 // Function allows you to set the Username and Token for CobbleVision
 // @function setApiAuth()
 // @param {String} apiusername
 // @param {String} apitoken
 // @returns {Boolean} Indicating success of setting Api Auth.
  
  public Boolean setApiAuth(apiUserName, apiToken){
    this.apiUserName = apiUserName;
    this.apiToken = apiToken;
    return true;
  }
  
  // # Function allows you to set the debugging variable
  // # @function setDebugging()
  // # @param {Boolean} debugBool
  // # @returns {Boolean} Indicating success of setting Api Auth.
  
  public Boolean setDebugging(debugBool){
    this.debugging = debugBool;
    return true;
  }
  
  // #####################################################
  // # Functions for interacting with the CobbleVision API
  // #####################################################

  // # Return of the following functions is specified within this type description
  // # @typedef {Object} CompletableFuture <HTTPEntity> Entity from URLConnection as Completable Future
  // # @method {String} response returns stream of Content

  // # This function uploads a media file to CobbleVision. You can find it after login in your media storage. Returns a response object with body, response and headers properties, deducted from npm request module
  // # @async
  // # @function uploadMediaFile()  
  // # @param {string} price_category - Either high, medium, low
  // # @param {boolean} publicBool - Make Media available publicly or not?
  // # @param {string} name - Name of Media (Non Unique)
  // # @param {array} tags - Tag Names for Media - Array of Strings
  // # @param {BufferedImage} file - BufferedImage - image to be uploaded
  // # @returns {Response} This return the UploadMediaResponse as HttpEntity. The body is in JSON format.

  @Async
  public CompletableFuture <HTTPEntity> uploadMediaFileAsync(String price_category, Boolean publicBool, String name, String[] tags, BufferedImage file) throws InterruptedException{
    try{
      private String endpoint = "media"
      
      if(this.BaseURL.charAt(this.BaseURL.length - 1) != "/"){
        throw new Exception("Cobble BasePath must end with a slash '/' ")
      }
      
      private String[] keyArray = ["price_category", "publicBool", "name", "tags", "Your Api User Key", "Your Api Token"]
      private Object[] valueArray = [price_category, publicBool, name, tags, apiUserName, apiToken]
      private String[] typeArray = ["String", "Boolean", "String", "Array", "String", "String"]
      
      try{
        checkTypeOfParameter(valueArray, typeArray)
      }catch e as Exception{
        private int err_message = parseInt(e.Message)
        if(err_message instanceof Integer){
          throw new Exception("The provided data is not valid: " + keyArray[err_message] + "is not of type " + typeArray[err_message])
        }else{
          throw new Exception(e.printStackTrace)
        }
      }
      
      if(!check(this.valid_price_categories, price_category)){
        throw new Exception("Price Category is not valid!")
      }
      
      if (!(file instanceof BufferedImage)) throw new Error("File Object is not of type BufferedImage")
      
      private JSONObject obj = new JSONObject()
      obj.put("price_category", price_category)
      obj.put("public", publicBool)
      obj.put("name", name)
      obj.put("tags", tags)
      obj.put("file", new String(file.toString(), "ISO-8859-1"))
      
      private String jsonString = obj.toString()
     
      urlConnection = (HttpURLConnection) ((newURL(this.baseURL+endpoint).openConnection()))
      urlConnection.setDoOutput(true)
      String base64Auth = Base64.encodeToString(new String(apiUserName + ":" + apiToken).getBytes("UTF_8"), Base64.NO_WRAP)
      urlConnection.setRequestProperty("Authorization", "Basic " + base64Auth)
      urlConnection.setRequestProperty("Content-Type", "application/json")
      urlConnection.setRequestProperty("Accept", "application/json")
      urlConnection.setRequestMethod("Post")
      urlConnection.setConnectionTimeout(10000000000000000000000)
      urlConnection.connect()
      
      OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
      wr.write(jsonString);
      wr.close()
      
      // read the response
      InputStream in = new BufferedInputStream(urlConnection.getInputStream());
      String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

      if(GlobalVars.debugging){
        System.out.println(result)
      }
      
      return CompletableFuture.completeFuture(result);
    }catch e as Exception{
    
      if(GlobalVars.debugging){
        System.out.println(e.printStackTrace)
      }
    
      throw new Exception(e.printStackTrace)
    }
  }
  
  // # This function deletes Media from CobbleVision 
  // # @async
  // # @function deleteMediaFile()  
  // # @param {array} IDArray Array of ID's as Strings
  // # @returns {Response} This return the DeleteMediaResponse. The body is in JSON format.

@Async
  public CompletableFuture <HTTPEntity> deleteMediaFileAsync(String[] IDArray) throws InterruptedException{
    try{
      private String endpoint = "media"
      
      if(this.BaseURL.charAt(this.BaseURL.length - 1) != "/"){
        throw new Exception("Cobble BasePath must end with a slash '/' ")
      }
      
      private String[] keyArray = ["IDArray", "Your Api User Key", "Your Api Token"]
      private Object[] valueArray = [IDArray, apiUserName, apiToken]
      private String[] typeArray = ["Array", "String", "String"]
      
      try{
        checkTypeOfParameter(valueArray, typeArray)
      }catch e as Exception{
        private int err_message = parseInt(e.Message)
        if(err_message instanceof Integer){
          throw new Exception("The provided data is not valid: " + keyArray[err_message] + "is not of type " + typeArray[err_message])
        }else{
          throw new Exception(e.printStackTrace)
        }
      }
      
      public String[] invalidMedia = checkArrayForValidObjectID(IDArray)
      
      if(invalidMedia.length > 0){
        throw new Exception("You supplied a media ID which is invalid in format!")
      }
      
      JSONArray jsArray = new JSONArray();
      for (int i=0; i<IDArray.length; i++){
        jsArray.put(IDArray[i]);
      }
     
      urlConnection = (HttpURLConnection) ((newURL(this.baseURL+endpoint + "?id=" + jsArray.toString()).openConnection()))
      urlConnection.setDoOutput(true)
      String base64Auth = Base64.encodeToString(new String(apiUserName + ":" + apiToken).getBytes("UTF_8"), Base64.NO_WRAP)
      urlConnection.setRequestProperty("Authorization", "Basic " + base64Auth)
      urlConnection.setRequestProperty("Content-Type", "application/json")
      urlConnection.setRequestProperty("Accept", "application/json")
      urlConnection.setRequestMethod("Delete")
      urlConnection.setConnectionTimeout(10000000000000000000000)
      urlConnection.connect()
      
      // read the response
      InputStream in = new BufferedInputStream(urlConnection.getInputStream());
      String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
      
      if(GlobalVars.debugging){
        System.out.println(result)
      }
      
      return CompletableFuture.completeFuture(result);
    }catch e as Exception{
    
      if(GlobalVars.debugging){
        System.out.println(e.printStackTrace)
      }
    
      throw new Exception(e.printStackTrace)
    }
  }
  
  // # Launch a calculation with CobbleVision's Web API. Returns a response object with body, response and headers properties, deducted from npm request module;
  // # @async
  // # @function launchCalculation() 
  // # @param {array} algorithms Array of Algorithm Names
  // # @param {array} media Array of Media ID's  
  // # @param {string} type Type of Job - Currently Always "QueuedJob"
  // # @param {string} [notificationURL] Optional - Notify user upon finishing calculation!
  // # @returns {Response} This returns the LaunchCalculationResponse. The body is in JSON format.  

  @Async
  public CompletableFuture <HTTPEntity> launchCalculationAsync(String[] algorithms, String[] media, String type, String notificationURL) throws InterruptedException{
    try{
      private String endpoint = "calculation"
      
      if(this.BaseURL.charAt(this.BaseURL.length - 1) != "/"){
        throw new Exception("Cobble BasePath must end with a slash '/' ")
      }
      
      private String[] keyArray = ["algorithms", "media", "type", "notificationURL", "Your Api User Key", "Your Api Token"]
      private Object[] valueArray = [algorithms, media, type, notificationURL, apiUserName, apiToken]
      private String[] typeArray = ["Array", "Array", "String", "String", "String", "String"]
      
      try{
        checkTypeOfParameter(valueArray, typeArray)
      }catch e as Exception{
        private int err_message = parseInt(e.Message)
        if(err_message instanceof Integer){
          throw new Exception("The provided data is not valid: " + keyArray[err_message] + "is not of type " + typeArray[err_message])
        }else{
          throw new Exception(e.printStackTrace)
        }
      }
      
      if(!check(this.valid_job_types, type)){
        throw new Exception("Job Type is not valid!")
      }
      
      public String[] invalidMedia = checkArrayForValidObjectID(media)
      if(invalidMedia.length > 0){
        throw new Exception("You supplied a media ID which is invalid in format!")
      }

      public String[] invalidCalcs = checkArrayForValidObjectID(algorithms)
      if(invalidCalcs.length > 0){
        throw new Exception("You supplied a media ID which is invalid in format!")
      }

      private JSONObject obj = new JSONObject()
      obj.put("media", media)
      obj.put("algorithms", algorithms)
      obj.put("type", type)
      obj.put("notificationURL", notificationURL)
      obj.put("file", new String(file.toString(), "ISO-8859-1"))
      
      private String jsonString = obj.toString()
     
      urlConnection = (HttpURLConnection) ((newURL(this.baseURL+endpoint).openConnection()))
      urlConnection.setDoOutput(true)
      String base64Auth = Base64.encodeToString(new String(apiUserName + ":" + apiToken).getBytes("UTF_8"), Base64.NO_WRAP)
      urlConnection.setRequestProperty("Authorization", "Basic " + base64Auth)
      urlConnection.setRequestProperty("Content-Type", "application/json")
      urlConnection.setRequestProperty("Accept", "application/json")
      urlConnection.setRequestMethod("Post")
      urlConnection.setConnectionTimeout(10000000000000000000000)
      urlConnection.connect()
      
      OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
      wr.write(jsonString);
      wr.close()

      // read the response
      InputStream in = new BufferedInputStream(urlConnection.getInputStream());
      String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

      if(GlobalVars.debugging){
        System.out.println(result)
      }
      
      return CompletableFuture.completeFuture(result);
    }catch e as Exception{
    
      if(GlobalVars.debugging){
        System.out.println(e.printStackTrace)
      }
    
      throw new Exception(e.printStackTrace)
    }
  }

  // # This function waits until the given calculation ID's are ready to be downloaded!
  // # @async
  // # @function waitForCalculationCompletion() 
  // # @param {array} calculationIDArray Array of Calculation ID's
  // # @returns {Response} This returns the WaitForCalculationResponse. The body is in JSON format.   

@Async
  public CompletableFuture <HTTPEntity> waitForCalculationCompletion(String[] calculationIDArray) throws InterruptedException{
    try{
      private String endpoint = "calculation"
      
      if(this.BaseURL.charAt(this.BaseURL.length - 1) != "/"){
        throw new Exception("Cobble BasePath must end with a slash '/' ")
      }
      
      private String[] keyArray = ["calculationIDArray", "Your Api User Key", "Your Api Token"]
      private Object[] valueArray = [calculationIDArray, apiUserName, apiToken]
      private String[] typeArray = ["Array", "String", "String"]
      
      try{
        checkTypeOfParameter(valueArray, typeArray)
      }catch e as Exception{
        private int err_message = parseInt(e.Message)
        if(err_message instanceof Integer){
          throw new Exception("The provided data is not valid: " + keyArray[err_message] + "is not of type " + typeArray[err_message])
        }else{
          throw new Exception(e.printStackTrace)
        }
      }
      
      public String[] invalidCalcs = checkArrayForValidObjectID(calculationIDArray)
      if(invalidCalcs.length > 0){
        throw new Exception("You supplied a calc ID which is invalid in format!")
      }
      
      JSONArray jsArray = new JSONArray();
      for (int i=0; i < calculationIDArray.length; i++){
        jsArray.put(calculationIDArray[i]);
      }
      
      calculationFinishedBool=false
      while(calculationFinishedBool == False){
        
        urlConnection = (HttpURLConnection) ((newURL(this.baseURL+endpoint + "?id=" + jsArray.toString()).openConnection()))
        urlConnection.setDoOutput(true)
        String base64Auth = Base64.encodeToString(new String(apiUserName + ":" + apiToken).getBytes("UTF_8"), Base64.NO_WRAP)
        urlConnection.setRequestProperty("Authorization", "Basic " + base64Auth)
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.setRequestProperty("Accept", "application/json")
        urlConnection.setRequestMethod("Get")
        urlConnection.setConnectionTimeout(10000000000000000000000)
        urlConnection.connect()

        // read the response
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

        try{
          private JSONArray o = (JSONArray) new JSONTokener(result).nextValue()
        }catch e as Exception{
          private JSONObject o = new JSONObject(result, StandardCharsets.UTF_8)
        }

        if(o instanceof JSONArray){
          for(int v=0; v<o.length, v++){
            if(o[v].status === "finished"){
              calculationFinishedBool=true
            }else{
              calculationFinishedBool=false
              break
            }
          }
        }else{
          if(o.error != null){
            calculationFinishedBool=true
          }
        }

        if(calculationFinishedBool==false){
          wait(3000)
        }

        if(GlobalVars.debugging){
          System.out.println(EntityUtils.toString(result);)
        }
      
        return CompletableFuture.completeFuture(result);
    }catch e as Exception{
    
      if(GlobalVars.debugging){
        System.out.println(e.printStackTrace)
      }
    
      throw new Exception(e.printStackTrace)
    }
  }

  // # This function deletes Result Files or calculations in status "waiting" from CobbleVision. You cannot delete finished jobs beyond their result files, as we keep them for billing purposes.
  // # @async
  // # @function deleteCalculation()
  // # @param {array} IDArray Array of ID's as Strings
  // # @returns {Response} This returns the DeleteCalculationResponse. The body is in JSON format.
  
@Async
  public CompletableFuture <HTTPEntity> deleteCalculation(String[] IDArray) throws InterruptedException{
    try{
      private String endpoint = "calculation"
      
      if(this.BaseURL.charAt(this.BaseURL.length - 1) != "/"){
        throw new Exception("Cobble BasePath must end with a slash '/' ")
      }
      
      private String[] keyArray = ["IDArray", "Your Api User Key", "Your Api Token"]
      private Object[] valueArray = [IDArray, apiUserName, apiToken]
      private String[] typeArray = ["Array", "String", "String"]
      
      try{
        checkTypeOfParameter(valueArray, typeArray)
      }catch e as Exception{
        private int err_message = parseInt(e.Message)
        if(err_message instanceof Integer){
          throw new Exception("The provided data is not valid: " + keyArray[err_message] + "is not of type " + typeArray[err_message])
        }else{
          throw new Exception(e.printStackTrace)
        }
      }
      
      public String[] invalidCalcs = checkArrayForValidObjectID(IDArray)
      if(invalidCalcs.length > 0){
        throw new Exception("You supplied a calc ID which is invalid in format!")
      }
      
      JSONArray jsArray = new JSONArray();
      for (int i=0; i < IDArray.length; i++){
        jsArray.put(IDArray[i]);
      }

      urlConnection = (HttpURLConnection) ((newURL(this.baseURL+endpoint + "?id=" + jsArray.toString()).openConnection()))
      urlConnection.setDoOutput(true)
      String base64Auth = Base64.encodeToString(new String(apiUserName + ":" + apiToken).getBytes("UTF_8"), Base64.NO_WRAP)
      urlConnection.setRequestProperty("Authorization", "Basic " + base64Auth)
      urlConnection.setRequestProperty("Content-Type", "application/json")
      urlConnection.setRequestProperty("Accept", "application/json")
      urlConnection.setRequestMethod("Delete")
      urlConnection.setConnectionTimeout(10000000000000000000000)
      urlConnection.connect()
      
      // read the response
      InputStream in = new BufferedInputStream(urlConnection.getInputStream());
      String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
      
      if(GlobalVars.debugging){
        System.out.println(result)
      }
      
      return CompletableFuture.completeFuture(result);
    }catch e as Exception{
    
      if(GlobalVars.debugging){
        System.out.println(e.printStackTrace)
      }
    
      throw new Exception(e.printStackTrace)
    }
  }

  // # Get Calculation Result with CobbleVision's Web API. Returns a response object with body, response and headers properties, deducted from npm request module;
  // # @async
  // # @function getCalculationResult()
  // # @param {array} IDArray ID of calculation to return result Array 
  // # @param {boolean} returnOnlyStatusBool Return full result or only status? See Doc for more detailed description!
  // # @returns {Response} This returns the GetCalculationResult. The body is in json format.

@Async
  public CompletableFuture <HTTPEntity> getCalculationResult(String[] IDArray, Boolean returnOnlyStatusBool) throws InterruptedException{
    try{
      private String endpoint = "calculation"
      
      if(this.BaseURL.charAt(this.BaseURL.length - 1) != "/"){
        throw new Exception("Cobble BasePath must end with a slash '/' ")
      }
      
      private String[] keyArray = ["IDArray", "returnOnlyStatusBool", "Your Api User Key", "Your Api Token"]
      private Object[] valueArray = [IDArray, returnOnlyStatusBool, apiUserName, apiToken]
      private String[] typeArray = ["Array", "Boolean", "String", "String"]
      
      try{
        checkTypeOfParameter(valueArray, typeArray)
      }catch e as Exception{
        private int err_message = parseInt(e.Message)
        if(err_message instanceof Integer){
          throw new Exception("The provided data is not valid: " + keyArray[err_message] + "is not of type " + typeArray[err_message])
        }else{
          throw new Exception(e.printStackTrace)
        }
      }
      
      public String[] invalidCalcs = checkArrayForValidObjectID(IDArray)
      if(invalidCalcs.length > 0){
        throw new Exception("You supplied a calc ID which is invalid in format!")
      }
      
      JSONArray jsArray = new JSONArray();
      for (int i=0; i < IDArray.length; i++){
        jsArray.put(IDArray[i]);
      }
      
      urlConnection = (HttpURLConnection) ((newURL(this.baseURL+endpoint + "?id=" + jsArray.toString() + "&returnOnlyStatusBool=" + returnOnlyStatusBool.toString()).openConnection()))
      urlConnection.setDoOutput(true)
      String base64Auth = Base64.encodeToString(new String(apiUserName + ":" + apiToken).getBytes("UTF_8"), Base64.NO_WRAP)
      urlConnection.setRequestProperty("Authorization", "Basic " + base64Auth)
      urlConnection.setRequestProperty("Content-Type", "application/json")
      urlConnection.setRequestProperty("Accept", "application/json")
      urlConnection.setRequestMethod("Get")
      urlConnection.setConnectionTimeout(10000000000000000000000)
      urlConnection.connect()

      // read the response
      InputStream in = new BufferedInputStream(urlConnection.getInputStream());
      String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
      
      if(GlobalVars.debugging){
        System.out.println(result)
      }
      
      return CompletableFuture.completeFuture(result);
    }catch e as Exception{
    
      if(GlobalVars.debugging){
        System.out.println(e.printStackTrace)
      }
    
      throw new Exception(e.printStackTrace)
    }
  }

  // # Request your calculation result by ID with the CobbleVision API. Returns a response object with body, response and headers properties, deducted from npm request module;
  // # @async
  // # @function getCalculationVisualization()
  // # @param {string} id ID of calculation to return result/check String
  // # @param {boolean} returnBase64Bool Return Base64 String or image buffer as string?
  // # @param {integer} width target width of visualization file
  // # @param {integer} height target height of visualization file
  // # @returns {Response} This returns the GetCalculationVisualization Result. The body is in binary format.

  @Async
  public CompletableFuture <HTTPEntity> getCalculationVisualization(String id, Boolean returnBase64Bool, Integer width, Integer height) throws InterruptedException{
    try{
      private String endpoint = "calculation/visualization"
      
      if(this.BaseURL.charAt(this.BaseURL.length - 1) != "/"){
        throw new Exception("Cobble BasePath must end with a slash '/' ")
      }
      
      private String[] keyArray = ["id", "returnBase64Bool", "width", "height", "Your Api User Key", "Your Api Token"]
      private Object[] valueArray = [id, returnBase64Bool, width, height, apiUserName, apitoken]
      private String[] typeArray = ["String", "Boolean", "Integer", "Integer", "String", "String"]
      
      try{
        checkTypeOfParameter(valueArray, typeArray)
      }catch e as Exception{
        private int err_message = parseInt(e.Message)
        if(err_message instanceof Integer){
          throw new Exception("The provided data is not valid: " + keyArray[err_message] + "is not of type " + typeArray[err_message])
        }else{
          throw new Exception(e.printStackTrace)
        }
      }
      
      public String[] invalidCalcs = checkArrayForValidObjectID([id])
      if(invalidCalcs.length > 0){
        throw new Exception("You supplied a calc ID which is invalid in format!")
      }

      // read the response
      InputStream in = new BufferedInputStream(urlConnection.getInputStream());
      String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

      if(GlobalVars.debugging){
        System.out.println(result)
      }
      
      return CompletableFuture.completeFuture(result);
    }catch e as Exception{
    
      if(GlobalVars.debugging){
        System.out.println(e.printStackTrace)
      }
    
      throw new Exception(e.printStackTrace)
    }
  }

}

// TypeChecking of Values
// @sync
// @function checktypeOfParameter()
// @param {array} targetArray Array of values to be checked
// @param {array} assertTypeArray Array of types in strings to be checked against
// @returns {boolean} Success of Check

public Boolean checkTypeOfParameter(Object[] targetArray, Object[] assertTypeArray) throws Exception{
  try{
    for(int i=0; i < targetArray.length;i++){
      if(targetArray[i].getClass().getSimpleName() != assertTypeArray[i]){
        if(assertTypeArray[i] === "Array"){
          if(!(targetArray[i].getChars().isArray())){
            return false;
          }
        }else{
          return false;
        }
      }else{
        return true
      }
    }
  }catch Exception as e{
    throw new Error(e.Message)
  }
}

// Check Array of Mongo IDs for Invalid Values
// @sync
// @function checkIDArrayForInvalidValues()
// @param {array} IDArray Array of Mongo IDs
// @returns {boolean} Success of Check

public Boolean checkIDArrayForInvalidValues(String[] IDArray) throws Exception{
  try{
    for(int s = 0; s < IDArray.length; s++){
      ObjectID = org.bson.types.ObjectId(IDArray[s]);
    }
    return true
  }catch Exception as e{
    throw new Error(e.Message)
  }
}

// @sync
// @function wait()
// @param {number} ms time to wait in ms
// @returns {boolean} Success of Wait

public Boolean wait (int ms) throws Exception{
  try{
    Thread.sleep(ms)
    return true
  }catch Exception as e{
    throw new Exception(e.Message)
  }
}

// Check if element part of array
// @sync
// @function check()
// @param {number} ms time to wait in ms
// @returns {boolean} Success of Wait

public Boolean check( Object[] targetArray, String targetKey){
  try{
    List<Object> targetList = Array.asList(targetArray)
    
    if(targetList.contains(targetKey)){
      return true;
    }else{
      return false;
    }
    
  }catch Exception as e{
    throw new Exception(e.message)
  }
}
