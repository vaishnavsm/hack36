
var SERVER_PRIVATE = "a28fccaa93962ad26492e6615227cd59437cf01f3a1e4e4507622857fa98ec4a";
var SERVER_ADDRESS = "miTkj7N1SdGCcaoQTjTmNv3NQ11k5J2AXf";

var TOKEN = "21a940f1a2674cbba80542fb433b4a0c";
var URL = "https://api.blockcypher.com/v1/btc/test3";

var req = require('request-promise')

class Blockchain {
  /**
   * @param {String} userId
   */
  attach_to_blockchain(userId){
    var opt = {
      method: 'POST',
      uri: 'https://api.blockcypher.com/v1/btc/test3/addrs',
      json: true
    };
    return req(opt)
            .then(function(addr) {
              var queryBuilder = Backendless.DataQueryBuilder.create().setWhereClause("ownerId = "+userId);
              return Backendless.Data.of( "profile" ).find(queryBuilder)
                .then( function( profile ) {
                  profile.address = addr.address;
                  profile.private = addr.private;
                return Backendless.Data.of( "profile" ).save( profile )
                     .then( function( savedObject ) {
                       return addr;
                      })
                     .catch( function( error ) {
                       return "Error Updating User Profile, Message: "+error.message;
                      });
                })
             .catch( function( error ) {
               return "Error Getting User Profile, Message: "+error.message;
              });

            })
            .catch(function(e) { return "Error Creating Address, Message: "+e.message; })
  }
  /**
   * @param {String} userId
   */
   get_karma(user_id){
     var queryBuilder = Backendless.DataQueryBuilder.create().setWhereClause("ownerId = "+userId);
      return Backendless.Data.of( "profile" ).find(queryBuilder)
        .then( function( profile ) {
          var opt = {
            uri: URL+'/addrs/'+profile.address+'/balance',
            headers: {
            'User-Agent': 'Request-Promise'
            },
            json: true
          };
          $.get(opt)
            .then(function(response){
              var net_karma = response.final_balance/10000;
              if(net_karma != profile.karma){
                profile.karma = net_karma;
                Backendless.Data.of( "profile" ).save( profile )
                     .then( function( savedObject ) {})
                     .catch( function( error ) {});
              }
              return net_karma;
            })
            .catch(function(error){
              return "Error Getting Address Balance, Message: "+error.message;
            });
        })
       .catch( function( error ) {
         return "Error Getting User Profile, Message: "+error.message;
        });
   }
  /**
   * @param {String} userId
   */
  set_karma(user_id, n_karma){
    var queryBuilder = Backendless.DataQueryBuilder.create().setWhereClause("ownerId = "+userId);
      return Backendless.Data.of( "profile" ).find(queryBuilder)
        .then( function( profile ) {
          var karma = get_karma(user_id);
          if(karma < n_karma){
            var microtx = {
              from_private: SERVER_PRIVATE,
              to_address: profile.address,
              value_satoshis: 10000*(n_karma-karma)
            }
            var url = URL+'/txs/micro?token='+TOKEN;
            var opt = {
              method: "POST",
              uri: url,
              body: microtx,
              json: true
            }
            req(opt)
              .then(function(d) {});
          }
          if(karma > n_karma){
            var microtx = {
              from_private: profile.private,
              to_address: SERVER_ADDRESS,
              value_satoshis: 10000*(karma-n_karma)
            }
            var url = URL+'/txs/micro?token='+TOKEN;
            $.post(url, JSON.stringify(microtx))
              .then(function(d) {});
          }
          profile.karma = n_karma;
          Backendless.Data.of( "profile" ).save( profile )
               .then( function( savedObject ) {})
               .catch( function( error ) {});
          return "Done";
        })
       .catch( function( error ) {
         return "Error Getting User Profile, Message: "+error.message;
        });
  }
}

Backendless.ServerCode.addService( Blockchain );
