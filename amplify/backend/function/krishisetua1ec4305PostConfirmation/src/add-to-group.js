const { // Changed import to require
  CognitoIdentityProviderClient,
  AdminAddUserToGroupCommand
} = require("@aws-sdk/client-cognito-identity-provider");

const client = new CognitoIdentityProviderClient({
  region: process.env.AWS_REGION
});

exports.handler = async (event) => { // CommonJS export style
  console.log("Event:", JSON.stringify(event, null, 2));

  const { userPoolId, userName: username } = event;
  const userAttributes = event.request.userAttributes;

  if (!userAttributes) {
    console.error("User attributes not found in the event request.");
    return event;
  }

  const userRole = userAttributes["custom:role"];

  if (!userRole) {
    console.error("No custom:role attribute found. Cannot assign group.");
    return event;
  }

  let groupName;
  switch (userRole.toLowerCase()) {
    case "farmer":
      groupName = "FarmerGroup";
      break;
    case "buyer":
      groupName = "BuyerGroup";
      break;
    case "delivery":
      groupName = "DeliveryGroup";
      break;
    default:
      console.warn(`Unknown role "${userRole}" — no group will be assigned.`);
      return event;
  }

  const command = new AdminAddUserToGroupCommand({
    GroupName: groupName,
    UserPoolId: userPoolId,
    Username: username
  });

  try {
    await client.send(command);
    console.log(`User ${username} added to group ${groupName}`);
  } catch (error) {
    console.error("Error adding user to group:", error);
  }

  return event;
};