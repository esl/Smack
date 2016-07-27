Multi-User Chat Light
=====================

Allows configuration of, participation in, and administration of presence­less  Multi­-User Chats. 
Its feature set is a response to mobile XMPP applications needs and specific environment.

  * Obtain the MUC Light Manager
  * Obtain a MUC Light 
  * Create a new Room
  * Destroy a room
  * Leave a room
  * Change room name
  * Change room subject
  * Set room configurations
  * Manage changes on room name, subject and other configurations
  * Get room information
  * Manage room occupants
  * Manage occupants modifications
  * Discover MUC Light support
  * Get occupied rooms
  * Start a private chat
  * Send message to a room
  * Manage blocking list

**XEP related:** [XEP-xxxx](https://s3.amazonaws.com/uploads.hipchat.com/15025/1268415/d9ByVbihpLmzxBI/XEP-xxxx.Multi.User.Chat.Light.pdf)


Obtain the MUC Light Manager
----------------------------
```
MultiUserChatLightManager multiUserChatLightManager = MultiUserChatLightManager.getInstanceFor(connection);
```

Obtain a MUC Light 
------------------

```
MultiUserChatLight multiUserChatLight = multiUserChatLightManager.getMultiUserChatLight(roomJid);
```
`roomJid` is a Jid


Create a new room
-----------------

```
boolean created = multiUserChatLight.create(roomName, occupants);
```
or
```
boolean created = multiUserChatLight.create(roomName, subject, customConfigs, occupants);
```

*roomName* is a `String`

*subject* is a `String`

*customConfigs* is a `HashMap<String, String>`

*occupants* is a `List<Jid>`


Destroy a room
---------------

```
boolean destroyed = multiUserChatLight.destroy();
```


Leave a room
-------------

```
boolean left = multiUserChatLight.leave();
```


Change room name
----------------

```
boolean roomNameChanged = multiUserChatLight.changeRoomName(roomName);
```
*roomName* is a `String`


Change subject
--------------

```
boolean roomNameChanged = multiUserChatLight.changeSubject(subject);
```
*subject* is a `String`


Set room configurations
-----------------------

```
boolean configsChanged = multiUserChatLight.setRoomConfigs(customConfigs);
```
or
```
boolean configsChanged = multiUserChatLight.setRoomConfigs(roomName, customConfigs);
```
*customConfigs* is a `HashMap<String, String>` (which means [property name, value])

*roomName* is a `String`


Manage changes on room name, subject and other configurations
-------------------------------------------------------------

```
// check if the message is because of a configurations change
if (message.hasExtension(MUCLightElements.ConfigurationsChangeExtension.ELEMENT, MUCLightElements.ConfigurationsChangeExtension.NAMESPACE)) {
  
  // Get the configurations extension
  MUCLightElements.ConfigurationsChangeExtension configurationsChangeExtension = message.getExtension(MUCLightElements.ConfigurationsChangeExtension.ELEMENT, MUCLightElements.ConfigurationsChangeExtension.NAMESPACE);

  // Get new room name
  String roomName = configurationsChangeExtension.getRoomName();

  // Get new subject
  String subject = configurationsChangeExtension.getSubject();

  // Get new custom configurations
  HashMap<String, String> customConfigs = configurationsChangeExtension.getCustomConfigs();

}
```


Get room information
--------------------

**Get configurations** 

```
MUCLightRoomConfiguration configuration = multiUserChatLight.getConfiguration(version);
```
*version* is a `String`

or
```
MUCLightRoomConfiguration configuration = multiUserChatLight.getConfiguration();
```

```
  // Get room name
  String roomName = configuration.getRoomName();

  // Get subject
  String subject = configuration.getSubject();

  // Get custom configurations
  HashMap<String, String> customConfigs = configuration.getCustomConfigs();
```

**Get affiliations**

```
HashMap<Jid, MUCLightAffiliation> affiliations = multiUserChatLight.getAffiliations(version);
```
*version* is a `String`

or
```
HashMap<Jid, MUCLightAffiliation> affiliations = multiUserChatLight.getAffiliations();
```

**Get full information**

```
MUCLightRoomInfo info = multiUserChatLight.getFullInfo(version);
```
*version* is a `String`

or
```
MUCLightRoomInfo info = multiUserChatLight.getFullInfo();
```
```
// Get version
String version = info.getVersion();

// Get room
Jid room = info.getRoom();

// Get configurations
MUCLightRoomConfiguration configuration = info.getConfiguration();

// Get occupants
HashMap<Jid, MUCLightAffiliation> occupants = info.getOccupants();
```


Manage room occupants
---------------------

To change room occupants:
```
boolean affiliationsChanged = multiUserChatLight.changeAffiliations(affiliations);
```
*affiliations* is a `HashMap<Jid, MUCLightAffiliation>`


Manage occupants modifications
------------------------------

```
// check if the message is because of an affiliations change
if (message.hasExtension(MUCLightElements.AffiliationsChangeExtension.ELEMENT, MUCLightElements.AffiliationsChangeExtension.NAMESPACE)) {
  
  // Get the affiliations change extension
  MUCLightElements.AffiliationsChangeExtension affiliationsChangeExtension = message.getExtension(MUCLightElements.AffiliationsChangeExtension.ELEMENT, MUCLightElements.AffiliationsChangeExtension.NAMESPACE);

  // Get the new affiliations
  HashMap<EntityJid, MUCLightAffiliation> affiliations = affiliationsChangeExtension.getAffiliations();

}
```


Discover MUC Light support
--------------------------

**Check if service is enabled**

```
  boolean enabled = multiUserChatLightManager.isServiceEnabled(mucLightService);
```
*mucLightService* is a `DomainBareJid`

**Check if MUC Light feature is supported by the server**

```
boolean isSupported = multiUserChatLightManager.isFeatureSupportedByServer(mucLightService);
```
*mucLightService* is a `DomainBareJid`

**Get MUC Light service domains**

```
List<DomainBareJid> domains = multiUserChatLightManager.getXMPPServiceDomains();
```


Get occupied rooms
------------------

```
List<Jid> occupiedRooms = multiUserChatLightManager.getOccupiedRooms(mucLightService);
```
*mucLightService* is a `DomainBareJid`


Start a private chat
--------------------

```
Chat chat = multiUserChatLight.createPrivateChat(occupant, listener);
```
*occupant* is a `EntityJid`

*listener* is a `ChatMessageListener`


Send message to a room
----------------------

**Create message for an specific MUC Light**

```
Message message = multiUserChatLight.createMessage();
```

**Send a message to an specific MUC Light**

```
multiUserChatLight.sendMessage(message);
```
*message* is a `Message`


Manage blocking list
--------------------

**Get blocked list**

```
// Get users and rooms blocked
List<Jid> jids = multiUserChatLightManager.getUsersAndRoomsBlocked(mucLightService);

// Get rooms blocked
List<Jid> jids = multiUserChatLightManager.getRoomsBlocked(mucLightService);

// Get users blocked
List<Jid> jids = multiUserChatLightManager.getUsersBlocked(mucLightService);
```
*mucLightService* is a `DomainBareJid`

**Block rooms**

```
// Block one room
boolean blocked = multiUserChatLightManager.blockRoom(mucLightService, roomJid);

// Block several rooms
boolean blocked = multiUserChatLightManager.blockRooms(mucLightService, roomsJids);
```
*mucLightService* is a `DomainBareJid`

*roomJid* is a `Jid`

*roomsJids* is a `List<Jid>`

**Block users**

```
// Block one user
boolean blocked = multiUserChatLightManager.blockUser(mucLightService, userJid);

// Block several users
boolean blocked = multiUserChatLightManager.blockUsers(mucLightService, usersJids);
```
*mucLightService* is a `DomainBareJid`

*userJid* is a `Jid`

*usersJids* is a `List<Jid>`

**Unblock rooms**

```
// Unblock one room
boolean unblocked = multiUserChatLightManager.unblockRoom(mucLightService, roomJid);

// Unblock several rooms
boolean unblocked = multiUserChatLightManager.unblockRooms(mucLightService, roomsJids);
```
*mucLightService* is a `DomainBareJid`

*roomJid* is a `Jid`

*roomsJids* is a `List<Jid>`

**Unblock users**

```
// Unblock one user
boolean unblocked = multiUserChatLightManager.unblockUser(mucLightService, userJid);

// Unblock several users
boolean unblocked = multiUserChatLightManager.unblockUsers(mucLightService, usersJids);
```
*mucLightService* is a `DomainBareJid`

*userJid* is a `Jid`

*usersJids* is a `List<Jid>`
