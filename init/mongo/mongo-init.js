// log the start of script execution
print('START');

// This will create a new database or switch to it if it already exists
db = db.getSiblingDB('MongoSpringSocial');

// Create a user with readWrite permissions on the SpringSocialMongo database
db.createUser({
    user: "admin",
    pwd: "password",
    roles: [
        {
            role: "readWrite",
            db: "MongoSpringSocial"
        }
    ]
});

// Create the 'post' collection if it doesn't exist, as it will be used below
db.createCollection('postdb');

// Define a function to get the current date and time
function NOW() {
    return new Date();
}

// Insert sample posts into the 'post' collection
db.post.insertMany([
    {
        "title": "Test Post 1",
        "content": "This is a test of the first post.",
        "authorId": "user1",
        "createdAt": NOW(),
        "updatedAt": NOW()
    },
    {
        "title": "Test Post 2",
        "content": "This is a test of the second post.",
        "authorId": "user2",
        "createdAt": NOW(),
        "updatedAt": NOW()
    }
]);

// log the end of script execution
print('END');
