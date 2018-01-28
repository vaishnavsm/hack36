/*Created on 01/27/2018 15:31:25.*/

var THRESHOLD = 1;
var w = [1,1,1];
class Panic {
  /**
   * @param {String} userId
   */
  panic( userId ) {
    return Backendless.Data.of("profile").find()
    .then(function(allUsers){
      var currUser = null;
      for(var i=0; i<allUsers.length; ++i){
        if(allUsers[i].ownerId==userId){
          currUser = allUsers[i];
          break;
        }
      }
      if(currUser == null){
        return -1;
      }
      var qualifiedUsers = [];
      for(var i=0;i<allUsers.length; ++i){
        if(allUsers[i].objectId == currUser.objectId) continue;
        var dist = measure(allUsers[i].latitude, allUsers[i].longitude, currUser.latitude, currUser.longitude);
        var karma = allUsers[i].karma;
        if(getKarma(profile.ownerId) != karma){
			return "Corrupt record!";
		}
        var type_reliability = type_check(allUsers[i].types, currUser.types);
        if(karma*w[0] + type_reliability*w[1] + dist*w[2] > THRESHOLD) qualifiedUsers.push(allUsers[i]);
      }
    
      //Time to push for notifications!
      var queryBuilder = Backendless.DataQueryBuilder.create();
      queryBuilder.setRelated( [ "user" ] );
      return Backendless.Data.of("DeviceRegistration").find(queryBuilder)
      .then(function(allDevices){
        var qualifiedDevices = [];
        for(var i = 0; i<allDevices.length; ++i){
          for(var j=0; j<qualifiedUsers.length; ++j){
            if(qualifiedUsers[j].ownerId == allDevices[i].user.objectId){
              qualifiedDevices.push(allDevices[i]);
              break;
            }
          }
        }
        //Now We have all qualified devices
        var qualifiedDeviceIDs = [];
        for(var i=0; i<qualifiedDevices.length; ++i){
          qualifiedDeviceIDs.push(qualifiedDevices[i].deviceId);
        }
        //And Now Their IDs
        var channel = "default";
        var message = JSON.stringify({"latitude":currUser.latitude, "longitude":currUser.longitude});
        var publishOptions = new Backendless.PublishOptions({
                                 headers: {
                                     "android-ticker-text": "Someone Needs You!",
                                     "android-content-title": "Someone is in dire need of a safe space",
                                     "android-content-text": "You can help them out, right?"
                                     }
                                  });
        var deliveryOptions = new Backendless.DeliveryOptions({
                                  pushSinglecast: qualifiedDeviceIDs
                                  }); 
        return Backendless.Messaging.publish( channel, message, publishOptions, deliveryOptions )
       .then( function( messageStatus ) {
         return qualifiedDevices; 
        })
       .catch( function( error ) {
         return "Error at PushNotification, Message: "+error.message+"  , StatusCode: "+error.statusCode;
        });
      })
      .catch(function(error){
        return "Error at DeviceRegistration, Message: "+error.message+"  , StatusCode: "+error.statusCode;
      });
    
    }).catch(function(error){
      return "Error at User Query, Message: "+error.message+"  , StatusCode: "+error.statusCode;
    });
 }
}
 
Backendless.ServerCode.addService( Panic );

function type_check(a, b){
  //Type Reliability Algo Here
  return 1;
}

function measure(lat1, lon1, lat2, lon2){  // generally used geo measurement function
    var R = 6378.137; // Radius of earth in KM
    var dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
    var dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
    Math.sin(dLon/2) * Math.sin(dLon/2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    var d = R * c;
    return d * 1000; // meters
}
